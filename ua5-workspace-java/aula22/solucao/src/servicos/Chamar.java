package servicos;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import locadora.infra.Json;

/**
 * Mini "curl" multiplataforma para os experimentos do tutorial (Windows, Linux, macOS).
 *
 * Uso (na pasta solucao/ ou meu/):
 *   java ../../ferramentas/Rodar.java servicos.Chamar GET  http://localhost:8001/v1/veiculos
 *   java ../../ferramentas/Rodar.java servicos.Chamar POST http://localhost:8002/v1/locacoes placa=ABC-1234 cliente=Ana dias=5
 *   java ../../ferramentas/Rodar.java servicos.Chamar POST http://localhost:8002/v1/locacoes "{\"placa\":\"ABC-1234\",\"cliente\":\"Ana\",\"dias\":5}"
 */
public final class Chamar {
    private Chamar() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: servicos.Chamar METODO URL [chave=valor ... | JSON]");
            System.exit(2);
        }
        String metodo = args[0].toUpperCase();
        String url = args[1];
        String[] resto = Arrays.copyOfRange(args, 2, args.length);
        Object corpo = null;
        if (resto.length > 0) {
            if (resto[0].strip().startsWith("{")) {
                corpo = Json.lerObjeto(String.join(" ", resto));
            } else { // formato chave=valor (mais fácil de digitar no PowerShell)
                Map<String, Object> campos = new LinkedHashMap<>();
                for (String par : resto) {
                    String[] kv = par.split("=", 2);
                    campos.put(kv[0], kv[1].matches("\\d+") ? Long.parseLong(kv[1]) : kv[1]);
                }
                corpo = campos;
            }
        } else if (!metodo.equals("GET")) {
            corpo = Map.of();
        }
        try {
            HttpJson.Resposta r = HttpJson.chamar(metodo, url, corpo);
            System.out.println("HTTP " + r.status());
            System.out.println(Json.escrever(r.corpo()));
        } catch (IOException e) {
            System.err.println("Sem resposta de " + url + ": " + e.getClass().getSimpleName());
            System.exit(1);
        }
    }
}
