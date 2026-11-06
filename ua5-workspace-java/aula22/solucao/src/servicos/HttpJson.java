package servicos;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import locadora.infra.Json;

/** Mini-framework HTTP+JSON (só JDK) compartilhado pelos três serviços — o "Spring" da aula. */
public final class HttpJson {
    private HttpJson() {
    }

    /** O que a rota recebe: parâmetros do caminho, da query string e o corpo JSON. */
    public record Requisicao(Map<String, String> caminho, Map<String, String> consulta, Map<String, Object> corpo) {
    }

    /** O que a rota devolve: status HTTP + objeto que vira JSON. */
    public record Resposta(int status, Object corpo) {
    }

    @FunctionalInterface
    public interface Tratador {
        Resposta tratar(Requisicao r);
    }

    public static Resposta resposta(int status, Object... chavesEValores) {
        Map<String, Object> corpo = new java.util.LinkedHashMap<>();
        for (int i = 0; i < chavesEValores.length; i += 2) {
            corpo.put((String) chavesEValores[i], chavesEValores[i + 1]);
        }
        return new Resposta(status, corpo);
    }

    /** Tabela de rotas: método + regex do caminho com grupos nomeados, ex.: /v1/veiculos/(?<placa>[^/]+)/reserva */
    public static class Rotas {
        private record Rota(String metodo, Pattern padrao, List<String> nomes, Tratador tratador) {
        }

        private static final Pattern GRUPO = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
        private final List<Rota> rotas = new ArrayList<>();

        public Rotas adicionar(String metodo, String padrao, Tratador tratador) {
            List<String> nomes = new ArrayList<>();
            Matcher g = GRUPO.matcher(padrao);
            while (g.find()) {
                nomes.add(g.group(1));
            }
            rotas.add(new Rota(metodo, Pattern.compile("^" + padrao + "$"), nomes, tratador));
            return this;
        }

        public Resposta despachar(String metodo, String caminhoComConsulta, Map<String, Object> corpo) {
            String[] partes = caminhoComConsulta.split("\\?", 2);
            Map<String, String> consulta = new HashMap<>();
            if (partes.length > 1) {
                for (String par : partes[1].split("&")) {
                    String[] kv = par.split("=", 2);
                    consulta.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
                }
            }
            for (Rota r : rotas) {
                Matcher m = r.padrao().matcher(partes[0]);
                if (r.metodo().equals(metodo) && m.matches()) {
                    Map<String, String> params = new HashMap<>();
                    for (String nome : r.nomes()) {
                        params.put(nome, URLDecoder.decode(m.group(nome), StandardCharsets.UTF_8));
                    }
                    return r.tratador().tratar(new Requisicao(params, consulta, corpo));
                }
            }
            return resposta(404, "erro", "rota não encontrada: " + metodo + " " + partes[0]);
        }
    }

    /** Cria (sem iniciar) o servidor. Porta 0 = o sistema escolhe uma livre (útil em testes). */
    public static HttpServer criarServidor(Rotas rotas, String host, int porta, String nome, boolean log)
            throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(host, porta), 0);
        servidor.createContext("/", troca -> tratar(rotas, troca, nome, log));
        servidor.setExecutor(Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }));
        return servidor;
    }

    private static void tratar(Rotas rotas, HttpExchange troca, String nome, boolean log) throws IOException {
        Resposta r;
        try {
            String bruto = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> corpo = bruto.isBlank() ? new HashMap<>() : Json.lerObjeto(bruto);
            r = rotas.despachar(troca.getRequestMethod(), troca.getRequestURI().getRawPath()
                    + (troca.getRequestURI().getRawQuery() != null ? "?" + troca.getRequestURI().getRawQuery() : ""), corpo);
        } catch (IllegalArgumentException e) {
            r = resposta(400, "erro", e.getMessage().startsWith("JSON inválido") ? "JSON inválido" : e.getMessage());
        } catch (RuntimeException e) { // devolve 500 em vez de derrubar o serviço
            r = resposta(500, "erro", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        byte[] dados = Json.escrever(r.corpo()).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        troca.sendResponseHeaders(r.status(), dados.length);
        try (OutputStream out = troca.getResponseBody()) {
            out.write(dados);
        }
        if (log) {
            System.out.println("[" + nome + "] " + troca.getRequestMethod() + " " + troca.getRequestURI() + " -> " + r.status());
        }
    }

    // ------------------------------------------------------------- cliente
    private static final HttpClient CLIENTE = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    /** Chama outro serviço. Devolve (status, corpo JSON). Lança IOException se não houver resposta. */
    public static Resposta chamar(String metodo, String url, Object corpo) throws IOException {
        HttpRequest.BodyPublisher publicador = corpo == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(Json.escrever(corpo));
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json").method(metodo, publicador).build();
        try {
            HttpResponse<String> r = CLIENTE.send(req, HttpResponse.BodyHandlers.ofString());
            return new Resposta(r.statusCode(), r.body().isBlank() ? Map.of() : Json.ler(r.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrompido", e);
        }
    }

    /** Lê uma variável de ambiente numérica (porta). */
    static int porta(String padrao) {
        return Integer.parseInt(System.getenv().getOrDefault("PORTA", padrao));
    }
}
