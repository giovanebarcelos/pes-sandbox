package servicos;

import static teste.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import teste.AfterEach;
import teste.BeforeEach;
import teste.Test;

/**
 * Testes de integração dos MICROSSERVIÇOS — sobem os 3 servidores dentro do teste
 * (portas livres escolhidas pelo sistema), sem Docker.
 */
class ServicosTest {
    private final List<HttpServer> servidores = new ArrayList<>();
    private int pFrota;
    private int pNotif;
    private HttpServer frota;
    private HttpServer notif;
    private Outbox outbox;
    private String loc;

    static int portaLivre() throws IOException {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

    private HttpServer subir(HttpJson.Rotas rotas, int porta) throws IOException {
        HttpServer srv = HttpJson.criarServidor(rotas, "127.0.0.1", porta, "teste", false);
        srv.start();
        servidores.add(srv);
        return srv;
    }

    private void derrubar(HttpServer srv) {
        srv.stop(0);
        servidores.remove(srv);
    }

    @BeforeEach
    void preparar() throws IOException {
        pFrota = portaLivre();
        pNotif = portaLivre();
        int pLoc = portaLivre();
        frota = subir(FrotaServico.criarRotas(FrotaServico.criarApi()), pFrota);
        notif = subir(NotificacaoServico.criarRotas(), pNotif);
        LocacoesServico.Montado m = LocacoesServico.montarServico("http://127.0.0.1:" + pFrota,
                "http://127.0.0.1:" + pNotif, null, 1000);
        outbox = m.outbox();
        subir(LocacoesServico.criarRotas(m.app(), outbox), pLoc);
        loc = "http://127.0.0.1:" + pLoc;
    }

    @AfterEach
    void limpar() {
        for (HttpServer srv : new ArrayList<>(servidores)) {
            derrubar(srv);
        }
    }

    private HttpJson.Resposta alugar(String placa, String id) throws IOException {
        return HttpJson.chamar("POST", loc + "/v1/locacoes",
                Map.of("id", id, "placa", placa, "cliente", "Ana", "dias", 5, "data_inicio", "2026-11-02"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> corpo(HttpJson.Resposta r) {
        return (Map<String, Object>) r.corpo();
    }

    @SuppressWarnings("unchecked")
    private List<Object> notificacoes() throws IOException {
        return (List<Object>) corpo(HttpJson.chamar("GET", "http://127.0.0.1:" + pNotif + "/v1/notificacoes", null))
                .get("notificacoes");
    }

    // ------------------------------------------------------------ fluxo feliz
    @Test
    @SuppressWarnings("unchecked")
    void fluxo_completo_entre_servicos() throws IOException {
        HttpJson.Resposta r = alugar("ABC-1234", "L-1");
        assertEquals(201, r.status());
        assertEquals(Map.of("id", "L-1", "placa", "ABC-1234", "valor_previsto", 660.0), corpo(r));
        assertEquals(409, alugar("ABC-1234", "L-2").status());
        HttpJson.Resposta dev = HttpJson.chamar("POST", loc + "/v1/locacoes/L-1/devolucao", Map.of("data", "2026-11-09"));
        assertEquals(200, dev.status());
        assertEquals(396.0, corpo(dev).get("multa"));
        assertEquals(0, outbox.entregarPendentes());
        assertEquals(3, notificacoes().size());
        List<Object> veiculos = (List<Object>) corpo(HttpJson.chamar("GET", "http://127.0.0.1:" + pFrota + "/v1/veiculos", null))
                .get("veiculos");
        for (Object v : veiculos) {
            assertEquals(true, ((Map<String, Object>) v).get("disponivel"));
        }
    }

    @Test
    void contrato_de_erros() throws IOException {
        assertEquals(404, alugar("ZZZ-9999", "L-1").status());
        assertEquals(400, HttpJson.chamar("POST", loc + "/v1/locacoes", Map.of("placa", "ABC-1234")).status());
        assertEquals(404, HttpJson.chamar("POST", loc + "/v1/locacoes/NAO-EXISTE/devolucao", Map.of()).status());
        assertEquals(404, HttpJson.chamar("GET", loc + "/v2/locacoes", null).status());
    }

    // ---------------------------------------------------- falhas (resiliência)
    @Test
    void frota_fora_do_ar_responde_503() throws IOException {
        // Chamada SÍNCRONA: sem a Frota, Locações não consegue alugar (e diz isso claramente).
        derrubar(frota);
        HttpJson.Resposta r = alugar("ABC-1234", "L-1");
        assertEquals(503, r.status());
        assertTrue(corpo(r).get("erro").toString().contains("Frota"), corpo(r).toString());
    }

    @Test
    void notificacao_fora_do_ar_nao_impede_locacao() throws IOException {
        // Chamada ASSÍNCRONA: a locação funciona; o aviso espera no outbox e é entregue depois.
        derrubar(notif);
        assertEquals(201, alugar("ABC-1234", "L-1").status());
        assertEquals(1, outbox.entregarPendentes());                       // não entregou
        notif = subir(NotificacaoServico.criarRotas(), pNotif);             // volta
        assertEquals(0, outbox.entregarPendentes());
        assertEquals(1, notificacoes().size());
    }

    @Test
    void notificacao_idempotente() throws IOException {
        String url = "http://127.0.0.1:" + pNotif + "/v1/notificacoes";
        Map<String, Object> msg = Map.of("id", "X-1", "destino", "Ana", "mensagem", "oi");
        HttpJson.Resposta primeira = HttpJson.chamar("POST", url, msg);
        HttpJson.Resposta segunda = HttpJson.chamar("POST", url, msg);
        assertEquals(List.of(201, Map.of("duplicada", false)), List.of(primeira.status(), primeira.corpo()));
        assertEquals(List.of(200, Map.of("duplicada", true)), List.of(segunda.status(), segunda.corpo()));
        assertEquals(1, notificacoes().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void tela_sdui_vem_do_servidor() throws IOException {
        Map<String, Object> tela = corpo(HttpJson.chamar("GET", "http://127.0.0.1:" + pFrota + "/v1/telas/frota", null));
        assertEquals("column", tela.get("type"));
        long botoes = ((List<Object>) tela.get("children")).stream()
                .filter(c -> "button".equals(((Map<String, Object>) c).get("type"))).count();
        assertEquals(4, botoes);
    }
}
