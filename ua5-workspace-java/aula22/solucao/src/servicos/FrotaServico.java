package servicos;

import static servicos.HttpJson.resposta;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import locadora.Config;
import locadora.compartilhado.BarramentoEventos;
import locadora.frota.FrotaAPI;
import locadora.frota.adaptadores.VeiculoMemoriaRepository;

/**
 * Microsserviço de FROTA — expõe a FrotaAPI (Aula 21) por HTTP.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java servicos.FrotaServico
 * Variáveis: PORTA (padrão 8001)
 *   GET    /saude
 *   GET    /v1/veiculos
 *   GET    /v1/veiculos/{placa}/preco?dias=N
 *   POST   /v1/veiculos/{placa}/reserva     (409 se já alugado)
 *   DELETE /v1/veiculos/{placa}/reserva
 *   GET    /v1/telas/frota                  (bônus SDUI: a TELA vem do servidor)
 */
public final class FrotaServico {
    private static final String PLACA = "(?<placa>[^/?]+)";

    private FrotaServico() {
    }

    private static HttpJson.Resposta erro(RuntimeException e) {
        if (e instanceof NoSuchElementException) {
            return resposta(404, "erro", e.getMessage());
        }
        if (e instanceof IllegalStateException) {
            return resposta(409, "erro", e.getMessage());
        }
        return resposta(400, "erro", e.getMessage());
    }

    public static HttpJson.Rotas criarRotas(FrotaAPI api) {
        HttpJson.Rotas rotas = new HttpJson.Rotas();
        rotas.adicionar("GET", "/saude", r -> resposta(200, "servico", "frota", "status", "ok"));
        rotas.adicionar("GET", "/v1/veiculos", r -> resposta(200, "veiculos", api.listar()));
        rotas.adicionar("GET", "/v1/veiculos/" + PLACA + "/preco", r -> {
            String placa = r.caminho().get("placa");
            try {
                int dias = Integer.parseInt(r.consulta().getOrDefault("dias", "1"));
                return resposta(200, "placa", placa, "preco_base", api.precoBase(placa, dias));
            } catch (RuntimeException e) {
                return erro(e);
            }
        });
        rotas.adicionar("POST", "/v1/veiculos/" + PLACA + "/reserva", r -> {
            try {
                api.reservar(r.caminho().get("placa"));
                return resposta(200, "placa", r.caminho().get("placa"), "disponivel", false);
            } catch (RuntimeException e) {
                return erro(e);
            }
        });
        rotas.adicionar("DELETE", "/v1/veiculos/" + PLACA + "/reserva", r -> {
            try {
                api.liberar(r.caminho().get("placa"));
                return resposta(200, "placa", r.caminho().get("placa"), "disponivel", true);
            } catch (RuntimeException e) {
                return erro(e);
            }
        });
        rotas.adicionar("GET", "/v1/telas/frota", r -> {
            // SDUI (Geração 2 — JSON de componentes), mesmo vocabulário do PES2204
            List<Object> filhos = new ArrayList<>();
            filhos.add(Map.of("type", "text", "props", Map.of("value", "Frota disponível")));
            for (FrotaAPI.VeiculoDados v : api.listar()) {
                if (v.disponivel()) {
                    Map<String, Object> props = new LinkedHashMap<>();
                    props.put("label", String.format(Locale.ROOT, "Alugar %s (%s) — R$ %.2f/dia", v.modelo(), v.placa(), v.diaria()));
                    props.put("action", Map.of("type", "post", "url", "/v1/locacoes", "body", Map.of("placa", v.placa())));
                    filhos.add(Map.of("type", "button", "props", props));
                }
            }
            return resposta(200, "type", "column", "children", filhos);
        });
        return rotas;
    }

    public static FrotaAPI criarApi() {
        FrotaAPI api = new FrotaAPI(new VeiculoMemoriaRepository(), new BarramentoEventos());
        for (Object[] d : Config.FROTA_EXEMPLO) {
            api.cadastrar((String) d[0], (String) d[1], (String) d[2], (double) d[3]);
        }
        return api;
    }

    public static void main(String[] args) throws IOException {
        int porta = HttpJson.porta("8001");
        HttpServer servidor = HttpJson.criarServidor(criarRotas(criarApi()), "0.0.0.0", porta, "frota", true);
        servidor.start();
        System.out.println("[frota] ouvindo em http://localhost:" + porta);
    }
}
