package locadora.locacoes.adaptadores;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import locadora.infra.Json;
import locadora.locacoes.dominio.FrotaForaDoAr;
import locadora.locacoes.dominio.ServicoFrota;
import locadora.locacoes.dominio.VeiculoDesconhecido;
import locadora.locacoes.dominio.VeiculoNaoDisponivel;

/**
 * Adaptador da porta ServicoFrota que chama o MICROSSERVIÇO de Frota por HTTP (Aula 22).
 *
 * Compare com FrotaLocal: mesma porta, mesmos erros de saída — o
 * ManipuladorComandos não sabe se a Frota está no mesmo processo ou do outro
 * lado da rede. É o retorno do investimento em Hexagonal (Aula 20) + ACL (Aula 21).
 */
public class FrotaHttp implements ServicoFrota {
    private final String url;
    private final Duration timeout;
    private final HttpClient cliente;

    public FrotaHttp(String urlBase) {
        this(urlBase, Duration.ofSeconds(2));
    }

    public FrotaHttp(String urlBase, Duration timeout) {
        this.url = urlBase.replaceAll("/+$", "");
        this.timeout = timeout;
        this.cliente = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    @Override
    public double precoBase(String placa, int dias) {
        Map<String, Object> r = chamar("GET", "/v1/veiculos/" + codificar(placa) + "/preco?dias=" + dias);
        return ((Number) r.get("preco_base")).doubleValue();
    }

    @Override
    public void reservar(String placa) {
        chamar("POST", "/v1/veiculos/" + codificar(placa) + "/reserva");
    }

    @Override
    public void liberar(String placa) {
        chamar("DELETE", "/v1/veiculos/" + codificar(placa) + "/reserva");
    }

    private static String codificar(String placa) {
        return URLEncoder.encode(placa, StandardCharsets.UTF_8);
    }

    private Map<String, Object> chamar(String metodo, String caminho) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url + caminho)).timeout(timeout)
                .method(metodo, HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> r;
        try {
            r = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new FrotaForaDoAr("Frota inacessível em " + url + ": " + e.getClass().getSimpleName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FrotaForaDoAr("chamada à Frota interrompida");
        }
        Map<String, Object> corpo = r.body().isBlank() ? Map.of() : Json.lerObjeto(r.body());
        if (r.statusCode() == 200) {
            return corpo;
        }
        String erro = String.valueOf(corpo.getOrDefault("erro", "HTTP " + r.statusCode()));
        if (r.statusCode() == 404) {
            throw new VeiculoDesconhecido(erro);
        }
        if (r.statusCode() == 409) {
            throw new VeiculoNaoDisponivel(erro);
        }
        throw new FrotaForaDoAr("Frota respondeu " + r.statusCode() + ": " + erro);
    }
}
