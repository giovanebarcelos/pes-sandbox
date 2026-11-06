package locadora.apresentacao;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import locadora.Locacao;
import locadora.ServicoLocacao;

/**
 * MVC na web, só com o JDK (com.sun.net.httpserver) — sem Spring.
 *
 *   Model      -> ServicoLocacao
 *   View       -> PaginasWeb (funções que devolvem HTML)
 *   Controller -> ControladorWeb.tratar(metodo, caminho, formulario)
 *                 (testável sem subir servidor — ver test/.../WebMvcTest.java)
 *
 * O ServidorWeb é só um adaptador fino entre a rede e este Controller.
 */
public class ControladorWeb {
    /** Resposta HTTP: status + HTML. */
    public record Resposta(int status, String html) {
    }

    private final ServicoLocacao modelo;

    public ControladorWeb(ServicoLocacao modelo) {
        this.modelo = modelo;
    }

    public Resposta tratar(String metodo, String caminho, Map<String, String> formulario) {
        if (metodo.equals("GET") && caminho.equals("/")) {
            return new Resposta(200, PaginasWeb.frota(modelo.listar(), ""));
        }
        try {
            if (metodo.equals("POST") && caminho.equals("/alugar")) {
                Locacao loc = modelo.alugar(campo(formulario, "placa").trim().toUpperCase(), campo(formulario, "cliente"),
                        campo(formulario, "email"), Integer.parseInt(campo(formulario, "dias")));
                return new Resposta(200, PaginasWeb.frota(modelo.listar(),
                        String.format(Locale.ROOT, "%s alugado para %s: R$ %.2f", loc.placa(), loc.cliente(), loc.valor())));
            }
            if (metodo.equals("POST") && caminho.equals("/devolver")) {
                modelo.devolver(campo(formulario, "placa"));
                return new Resposta(200, PaginasWeb.frota(modelo.listar(), campo(formulario, "placa") + " devolvido"));
            }
        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            return new Resposta(400, PaginasWeb.frota(modelo.listar(), "Erro: " + e.getMessage()));
        }
        return new Resposta(404, "<h1>404</h1>");
    }

    private static String campo(Map<String, String> formulario, String nome) {
        String valor = formulario.get(nome);
        if (valor == null) {
            throw new IllegalArgumentException("campo obrigatório: " + nome);
        }
        return valor;
    }
}
