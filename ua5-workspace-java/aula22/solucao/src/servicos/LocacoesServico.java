package servicos;

import static servicos.HttpJson.resposta;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;
import locadora.Aplicacao;
import locadora.Config;
import locadora.locacoes.adaptadores.FrotaHttp;
import locadora.locacoes.aplicacao.IniciarLocacao;
import locadora.locacoes.aplicacao.RankingMensal;
import locadora.locacoes.aplicacao.RegistrarDevolucao;
import locadora.locacoes.dominio.FrotaForaDoAr;
import locadora.locacoes.dominio.Locacao;
import locadora.locacoes.dominio.LocacaoErro;
import locadora.locacoes.dominio.LocacaoIniciada;
import locadora.locacoes.dominio.MultaPorAtrasoAplicada;
import locadora.locacoes.dominio.VeiculoDesconhecido;
import locadora.locacoes.dominio.VeiculoDevolvido;
import locadora.locacoes.dominio.VeiculoNaoDisponivel;

/**
 * Microsserviço de LOCAÇÕES — o contexto da Aula 21 (CQRS + Event Sourcing) exposto por HTTP.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java servicos.LocacoesServico
 * Variáveis:
 *   PORTA            (padrão 8002)
 *   FROTA_URL        (padrão http://localhost:8001)  -> chamada SÍNCRONA
 *   NOTIFICACAO_URL  (padrão http://localhost:8003)  -> entrega ASSÍNCRONA (outbox)
 *   LOCADORA_EVENTOS (padrão dados/eventos.jsonl)    -> event store deste serviço
 *
 *   GET  /saude
 *   POST /v1/locacoes                   {"placa","cliente","dias"[, "data_inicio", "id"]}
 *   POST /v1/locacoes/{id}/devolucao    {["data"]}
 *   GET  /v1/locacoes/ativas
 *   GET  /v1/relatorios/faturamento?mes=AAAA-MM
 *   GET  /v1/relatorios/mais-alugados?mes=AAAA-MM
 *   GET  /v1/outbox                     (o que ainda não foi entregue)
 */
public final class LocacoesServico {
    /** O serviço montado: a aplicação (Aula 21) + o outbox. */
    public record Montado(Aplicacao app, Outbox outbox) {
    }

    private LocacoesServico() {
    }

    static Outbox.Entregador criarEntregador(FrotaHttp frota, String urlNotificacao) {
        return tarefa -> {
            if ("liberar".equals(tarefa.get("tipo"))) {
                frota.liberar((String) tarefa.get("placa"));
            } else {
                HttpJson.Resposta r = HttpJson.chamar("POST", urlNotificacao.replaceAll("/+$", "") + "/v1/notificacoes", tarefa);
                if (r.status() >= 300) {
                    throw new IOException("Notificação respondeu " + r.status());
                }
            }
        };
    }

    private static Map<String, Object> tarefa(Object... chavesEValores) {
        Map<String, Object> t = new LinkedHashMap<>();
        for (int i = 0; i < chavesEValores.length; i += 2) {
            t.put((String) chavesEValores[i], chavesEValores[i + 1]);
        }
        return t;
    }

    /** Assina os eventos do barramento e transforma cada um em tarefa assíncrona. */
    static void ligarOutbox(Aplicacao app, Outbox outbox) {
        app.barramento.inscrever(LocacaoIniciada.class, e -> outbox.adicionar(tarefa(
                "id", e.idAgregado() + ":iniciada", "tipo", "notificar", "destino", e.cliente(),
                "mensagem", String.format(Locale.ROOT, "Locação %s do %s confirmada: R$ %.2f por %d dia(s).",
                        e.idAgregado(), e.placa(), e.valorPrevistoCentavos() / 100.0, e.diasPrevistos()))));
        app.barramento.inscrever(VeiculoDevolvido.class, e -> {
            outbox.adicionar(tarefa("id", e.idAgregado() + ":liberar", "tipo", "liberar", "placa", e.placa()));
            outbox.adicionar(tarefa("id", e.idAgregado() + ":devolvido", "tipo", "notificar",
                    "destino", "cliente da " + e.idAgregado(),
                    "mensagem", "Veículo " + e.placa() + " devolvido (locação " + e.idAgregado() + ")."));
        });
        app.barramento.inscrever(MultaPorAtrasoAplicada.class, e -> outbox.adicionar(tarefa(
                "id", e.idAgregado() + ":multa", "tipo", "notificar", "destino", "cliente da " + e.idAgregado(),
                "mensagem", String.format(Locale.ROOT, "Multa por %d dia(s) de atraso: R$ %.2f.",
                        e.diasAtraso(), e.valorCentavos() / 100.0))));
    }

    /** Executa um comando e traduz os erros de Locações para status HTTP. */
    private static HttpJson.Resposta executar(Supplier<HttpJson.Resposta> comando) {
        try {
            return comando.get();
        } catch (VeiculoDesconhecido e) {
            return resposta(404, "erro", e.getMessage());
        } catch (VeiculoNaoDisponivel e) {
            return resposta(409, "erro", e.getMessage());
        } catch (FrotaForaDoAr e) {
            return resposta(503, "erro", e.getMessage());
        } catch (NoSuchElementException e) {
            return resposta(404, "erro", e.getMessage());
        } catch (LocacaoErro | IllegalArgumentException e) {
            return resposta(400, "erro", e.getMessage());
        }
    }

    private static String mes(Map<String, String> consulta) {
        return consulta.getOrDefault("mes", LocalDate.now().toString().substring(0, 7));
    }

    public static HttpJson.Rotas criarRotas(Aplicacao app, Outbox outbox) {
        HttpJson.Rotas rotas = new HttpJson.Rotas();
        rotas.adicionar("GET", "/saude", r -> resposta(200, "servico", "locacoes", "status", "ok",
                "outbox_pendentes", outbox.pendentes().size()));
        rotas.adicionar("POST", "/v1/locacoes", r -> {
            IniciarLocacao comando;
            try {
                Map<String, Object> c = r.corpo();
                for (String campo : List.of("placa", "cliente", "dias")) {
                    if (!c.containsKey(campo)) {
                        throw new IllegalArgumentException("falta o campo '" + campo + "'");
                    }
                }
                Object id = c.get("id");
                Object inicio = c.get("data_inicio");
                comando = new IniciarLocacao(id != null ? id.toString() : "L-" + UUID.randomUUID().toString().substring(0, 6),
                        c.get("placa").toString(), c.get("cliente").toString(),
                        inicio != null ? LocalDate.parse(inicio.toString()) : LocalDate.now(),
                        Integer.parseInt(c.get("dias").toString()));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                return resposta(400, "erro", "corpo inválido: " + e.getMessage());
            }
            return executar(() -> {
                Locacao loc = app.comandos.executar(comando);
                return resposta(201, "id", loc.id(), "placa", loc.placa(), "valor_previsto", loc.valorPrevisto().reais());
            });
        });
        rotas.adicionar("POST", "/v1/locacoes/(?<id>[^/]+)/devolucao", r -> {
            LocalDate data;
            try {
                Object d = r.corpo().get("data");
                data = d != null ? LocalDate.parse(d.toString()) : LocalDate.now();
            } catch (DateTimeParseException e) {
                return resposta(400, "erro", e.getMessage());
            }
            return executar(() -> {
                Locacao loc = app.comandos.executar(new RegistrarDevolucao(r.caminho().get("id"), data));
                return resposta(200, "id", loc.id(), "multa", loc.multa().reais(), "valor_total", loc.valorTotal().reais());
            });
        });
        rotas.adicionar("GET", "/v1/locacoes/ativas", r -> resposta(200, "ativas", app.consultas.locacoesAtivas()));
        rotas.adicionar("GET", "/v1/relatorios/faturamento", r -> resposta(200, "mes", mes(r.consulta()),
                "faturamento", app.consultas.faturamentoDoMes(mes(r.consulta()))));
        rotas.adicionar("GET", "/v1/relatorios/mais-alugados", r -> {
            List<Object> ranking = new ArrayList<>(); // [[placa, n], ...] — mesmo formato da versão Python
            for (RankingMensal.Contagem c : app.consultas.maisAlugados(mes(r.consulta()))) {
                ranking.add(List.of(c.placa(), c.locacoes()));
            }
            return resposta(200, "mes", mes(r.consulta()), "ranking", ranking);
        });
        rotas.adicionar("GET", "/v1/outbox", r -> resposta(200, "pendentes", outbox.pendentes(),
                "ultimo_erro", outbox.ultimoErro()));
        return rotas;
    }

    public static Montado montarServico(String urlFrota, String urlNotificacao, String caminhoEventos, long intervaloMs) {
        FrotaHttp frota = new FrotaHttp(urlFrota);
        Aplicacao app = Config.montar(caminhoEventos, frota, false);
        Outbox outbox = new Outbox(criarEntregador(frota, urlNotificacao), intervaloMs);
        ligarOutbox(app, outbox);
        return new Montado(app, outbox);
    }

    public static void main(String[] args) throws IOException {
        int porta = HttpJson.porta("8002");
        Map<String, String> env = System.getenv();
        Montado m = montarServico(env.getOrDefault("FROTA_URL", "http://localhost:8001"),
                env.getOrDefault("NOTIFICACAO_URL", "http://localhost:8003"),
                env.getOrDefault("LOCADORA_EVENTOS", "dados/eventos.jsonl"), 1000);
        m.outbox().iniciar();
        HttpServer servidor = HttpJson.criarServidor(criarRotas(m.app(), m.outbox()), "0.0.0.0", porta, "locacoes", true);
        servidor.start();
        System.out.println("[locacoes] ouvindo em http://localhost:" + porta);
    }
}
