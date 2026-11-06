package locadora.apresentacao;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Adaptador HTTP (infra fina): traduz a requisição para ControladorWeb.tratar() e devolve o HTML.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.apresentacao.ServidorWeb
 * Abrir:  http://localhost:8019   (porta configurável pela variável PORT)
 */
public final class ServidorWeb {
    private ServidorWeb() {
    }

    public static HttpServer criar(ControladorWeb controlador, String host, int porta) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(host, porta), 0);
        servidor.createContext("/", troca -> responder(controlador, troca));
        servidor.setExecutor(Executors.newCachedThreadPool());
        return servidor;
    }

    private static void responder(ControladorWeb controlador, HttpExchange troca) throws IOException {
        String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        ControladorWeb.Resposta r = controlador.tratar(troca.getRequestMethod(),
                troca.getRequestURI().getPath(), formulario(corpo));
        byte[] dados = r.html().getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        troca.sendResponseHeaders(r.status(), dados.length);
        try (OutputStream out = troca.getResponseBody()) {
            out.write(dados);
        }
    }

    /** "placa=ABC-1234&dias=5" -> {placa=ABC-1234, dias=5} */
    public static Map<String, String> formulario(String corpo) {
        Map<String, String> campos = new HashMap<>();
        for (String par : corpo.split("&")) {
            if (par.isEmpty()) {
                continue;
            }
            String[] kv = par.split("=", 2);
            campos.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return campos;
    }

    public static void main(String[] args) throws IOException {
        int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "8019"));
        criar(new ControladorWeb(Montagem.servicoExemplo(false)), "0.0.0.0", porta).start();
        System.out.println("Locadora MVC em http://localhost:" + porta + "  (Ctrl+C para parar)");
    }
}
