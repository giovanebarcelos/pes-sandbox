/*
 * PES2204 - Renderizador Server-Driven UI (SDUI)
 * Aula 22: Microsserviços e Arquiteturas Distribuídas
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra o núcleo de um cliente SDUI (Geração 2 - "JSON de componentes"):
 * o "servidor" envia uma árvore de componentes (aqui montada como Map, como se
 * tivesse chegado por JSON) e o cliente a RENDERIZA. O cliente não conhece
 * nenhuma tela específica - só sabe renderizar os tipos de componente registrados.
 *
 * Conceitos do curso aplicados:
 *   - Factory Method (GoF, Aula 18): mapeia type -> renderizador do componente
 *   - Fallback seguro: tipo desconhecido não quebra a tela (o "paradoxo do SDUI")
 *   - Ação declarativa (Command, Aula 21): o botão carrega a ação como dado
 *
 * Compilar/rodar: javac PES2204-SDUIRenderer.java && java SDUIRendererDemo
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class RenderizadorSDUI {
    // Factory: nome do tipo -> função que renderiza (node, nível) -> String
    private final Map<String, BiFunction<Map<String, Object>, Integer, String>> fabrica = new HashMap<>();

    RenderizadorSDUI() {
        fabrica.put("column", this::renderColumn);
        fabrica.put("text", this::renderText);
        fabrica.put("button", this::renderButton);
    }

    /** Novos componentes precisam ser registrados no cliente (limitação do SDUI). */
    void registrar(String tipo, BiFunction<Map<String, Object>, Integer, String> funcao) {
        fabrica.put(tipo, funcao);
    }

    @SuppressWarnings("unchecked")
    String renderizar(Map<String, Object> node, int nivel) {
        String tipo = (String) node.getOrDefault("type", "");
        BiFunction<Map<String, Object>, Integer, String> render =
                fabrica.getOrDefault(tipo, this::fallback);
        return render.apply(node, nivel);
    }

    @SuppressWarnings("unchecked")
    private String renderColumn(Map<String, Object> node, int nivel) {
        StringBuilder sb = new StringBuilder(ind(nivel) + "[coluna]");
        List<Map<String, Object>> filhos =
                (List<Map<String, Object>>) node.getOrDefault("children", new ArrayList<>());
        for (Map<String, Object> filho : filhos) {
            sb.append("\n").append(renderizar(filho, nivel + 1));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String renderText(Map<String, Object> node, int nivel) {
        Map<String, Object> props = (Map<String, Object>) node.getOrDefault("props", new HashMap<>());
        return ind(nivel) + "texto: " + props.getOrDefault("value", "");
    }

    @SuppressWarnings("unchecked")
    private String renderButton(Map<String, Object> node, int nivel) {
        Map<String, Object> props = (Map<String, Object>) node.getOrDefault("props", new HashMap<>());
        String rotulo = (String) props.getOrDefault("label", "OK");
        Map<String, Object> acao = (Map<String, Object>) props.getOrDefault("action", new HashMap<>());
        return ind(nivel) + "[ " + rotulo + " ] -> acao declarativa: " + descreverAcao(acao);
    }

    /** Tipo desconhecido: não quebra a renderização (fallback seguro). */
    private String fallback(Map<String, Object> node, int nivel) {
        return ind(nivel) + "(componente '" + node.get("type") + "' desconhecido - ignorado)";
    }

    private static String descreverAcao(Map<String, Object> acao) {
        String tipo = (String) acao.getOrDefault("type", "nenhuma");
        if (tipo.equals("navigate")) {
            return "navegar para " + acao.get("route");
        }
        if (tipo.equals("submit")) {
            return "enviar para " + acao.get("endpoint");
        }
        return tipo;
    }

    private static String ind(int nivel) {
        return "  ".repeat(nivel);
    }
}

class SDUIRendererDemo {
    static Map<String, Object> no(String tipo, Map<String, Object> props, List<Map<String, Object>> filhos) {
        Map<String, Object> m = new HashMap<>();
        m.put("type", tipo);
        if (props != null) m.put("props", props);
        if (filhos != null) m.put("children", filhos);
        return m;
    }

    static Map<String, Object> props(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    public static void main(String[] args) {
        // Payload que "chegaria" do servidor como JSON (schema versionado)
        Map<String, Object> acao = props("type", "navigate", "route", "/auction/123");
        List<Map<String, Object>> filhos = new ArrayList<>();
        filhos.add(no("text", props("value", "Leiloes ativos"), null));
        filhos.add(no("button", props("label", "Ver leilao", "action", acao), null));
        filhos.add(no("auction_card", props("id", "abc-123"), null)); // tipo desconhecido

        Map<String, Object> root = no("column", null, filhos);

        RenderizadorSDUI cliente = new RenderizadorSDUI();
        System.out.println("Renderizando tela 'home' (schema v3):\n");
        System.out.println(cliente.renderizar(root, 0));

        System.out.println("\n--- Apos publicar update do app (novo tipo registrado) ---\n");
        cliente.registrar("auction_card", (node, nivel) -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> p = (Map<String, Object>) node.get("props");
            return "  ".repeat(nivel) + "[card de leilao #" + p.get("id") + "]";
        });
        System.out.println(cliente.renderizar(root, 0));
    }
}
