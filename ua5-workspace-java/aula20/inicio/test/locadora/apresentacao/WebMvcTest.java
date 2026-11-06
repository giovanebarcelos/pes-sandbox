package locadora.apresentacao;

import static teste.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import teste.BeforeEach;
import teste.Test;

/** MVC web: Controller testado sem servidor + um teste de fumaça com servidor real. */
class WebMvcTest {
    private ControladorWeb c;

    @BeforeEach
    void preparar() {
        c = new ControladorWeb(Montagem.servicoExemplo(true));
    }

    @Test
    void get_lista_frota() {
        ControladorWeb.Resposta r = c.tratar("GET", "/", Map.of());
        assertEquals(200, r.status());
        assertTrue(r.html().contains("Fiat Uno"));
    }

    @Test
    void post_alugar() {
        ControladorWeb.Resposta r = c.tratar("POST", "/alugar",
                Map.of("placa", "abc-1234", "dias", "5", "cliente", "Ana", "email", "a@x"));
        assertEquals(200, r.status());
        assertTrue(r.html().contains("R$ 660.00"));
    }

    @Test
    void post_alugar_invalido() {
        assertEquals(400, c.tratar("POST", "/alugar", Map.of("placa", "ZZZ", "dias", "1", "cliente", "A", "email", "a@x")).status());
        assertEquals(400, c.tratar("POST", "/alugar", Map.of("placa", "ABC-1234")).status());
    }

    @Test
    void rota_inexistente() {
        assertEquals(404, c.tratar("GET", "/nada", Map.of()).status());
    }

    @Test
    void fumaca_com_servidor_real() throws Exception {
        HttpServer srv = ServidorWeb.criar(c, "127.0.0.1", 0);
        srv.start();
        try {
            String url = "http://127.0.0.1:" + srv.getAddress().getPort() + "/alugar";
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("placa=XYZ-9012&dias=3&cliente=Bia&email=b%40x")).build();
            HttpResponse<String> r = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            assertTrue(r.body().contains("R$ 240.00"), r.body());
        } finally {
            srv.stop(0);
        }
    }
}
