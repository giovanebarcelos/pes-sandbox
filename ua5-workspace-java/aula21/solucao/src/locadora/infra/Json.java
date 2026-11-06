package locadora.infra;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON mínimo (ler e escrever) — o JDK não traz um; em projeto real use Jackson ou Gson.
 * Escreve: Map, List/array, String, Number, Boolean, null e records (como objetos).
 * Lê:      objetos viram LinkedHashMap, arrays viram ArrayList, números inteiros
 *          viram Long e os demais Double.
 *
 * É INFRAESTRUTURA: o domínio nunca importa esta classe (ver FronteirasTest).
 */
public final class Json {
    private final String texto;
    private int pos;

    private Json(String texto) {
        this.texto = texto;
    }

    // ------------------------------------------------------------------ escrever
    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escrever(valor, sb);
        return sb.toString();
    }

    private static void escrever(Object v, StringBuilder sb) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof String s) {
            texto(s, sb);
        } else if (v instanceof Number || v instanceof Boolean) {
            sb.append(v);
        } else if (v instanceof Map<?, ?> m) {
            sb.append('{');
            boolean primeiro = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!primeiro) {
                    sb.append(", ");
                }
                primeiro = false;
                texto(String.valueOf(e.getKey()), sb);
                sb.append(": ");
                escrever(e.getValue(), sb);
            }
            sb.append('}');
        } else if (v instanceof Iterable<?> lista) {
            sb.append('[');
            boolean primeiro = true;
            for (Object item : lista) {
                if (!primeiro) {
                    sb.append(", ");
                }
                primeiro = false;
                escrever(item, sb);
            }
            sb.append(']');
        } else if (v instanceof Object[] array) {
            escrever(List.of(array), sb);
        } else if (v.getClass().isRecord()) {
            Map<String, Object> campos = new LinkedHashMap<>();
            try {
                for (RecordComponent c : v.getClass().getRecordComponents()) {
                    c.getAccessor().setAccessible(true);
                    campos.put(c.getName(), c.getAccessor().invoke(v));
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
            escrever(campos, sb);
        } else {
            texto(v.toString(), sb);
        }
    }

    private static void texto(String s, StringBuilder sb) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ---------------------------------------------------------------------- ler
    /** Lê um texto JSON. Lança IllegalArgumentException se for inválido. */
    public static Object ler(String texto) {
        Json j = new Json(texto);
        j.espacos();
        Object v = j.valor();
        j.espacos();
        if (j.pos != texto.length()) {
            throw j.erro("texto sobrando");
        }
        return v;
    }

    /** Atalho para quando o JSON tem que ser um objeto {...}. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> lerObjeto(String texto) {
        Object v = ler(texto);
        if (!(v instanceof Map)) {
            throw new IllegalArgumentException("JSON inválido: esperava um objeto {...}");
        }
        return (Map<String, Object>) v;
    }

    private Object valor() {
        if (pos >= texto.length()) {
            throw erro("fim inesperado");
        }
        char c = texto.charAt(pos);
        if (c == '{') {
            return objeto();
        }
        if (c == '[') {
            return lista();
        }
        if (c == '"') {
            return texto();
        }
        if (texto.startsWith("true", pos)) {
            pos += 4;
            return true;
        }
        if (texto.startsWith("false", pos)) {
            pos += 5;
            return false;
        }
        if (texto.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        return numero();
    }

    private Map<String, Object> objeto() {
        Map<String, Object> m = new LinkedHashMap<>();
        pos++;
        espacos();
        if (texto.charAt(pos) == '}') {
            pos++;
            return m;
        }
        while (true) {
            espacos();
            if (texto.charAt(pos) != '"') {
                throw erro("esperava nome entre aspas");
            }
            String chave = texto();
            espacos();
            esperar(':');
            espacos();
            m.put(chave, valor());
            espacos();
            if (texto.charAt(pos) == ',') {
                pos++;
            } else {
                esperar('}');
                return m;
            }
        }
    }

    private List<Object> lista() {
        List<Object> l = new ArrayList<>();
        pos++;
        espacos();
        if (texto.charAt(pos) == ']') {
            pos++;
            return l;
        }
        while (true) {
            espacos();
            l.add(valor());
            espacos();
            if (texto.charAt(pos) == ',') {
                pos++;
            } else {
                esperar(']');
                return l;
            }
        }
    }

    private String texto() {
        StringBuilder sb = new StringBuilder();
        pos++;
        while (true) {
            if (pos >= texto.length()) {
                throw erro("texto sem aspas de fechamento");
            }
            char c = texto.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                char e = texto.charAt(pos++);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        sb.append((char) Integer.parseInt(texto.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default: sb.append(e);
                }
            } else {
                sb.append(c);
            }
        }
    }

    private Number numero() {
        int inicio = pos;
        while (pos < texto.length() && "+-0123456789.eE".indexOf(texto.charAt(pos)) >= 0) {
            pos++;
        }
        String n = texto.substring(inicio, pos);
        if (n.isEmpty()) {
            throw erro("valor inesperado");
        }
        try {
            if (n.contains(".") || n.contains("e") || n.contains("E")) {
                return Double.parseDouble(n);
            }
            return Long.parseLong(n);
        } catch (NumberFormatException e) {
            throw erro("número inválido: " + n);
        }
    }

    private void espacos() {
        while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
            pos++;
        }
    }

    private void esperar(char c) {
        if (pos >= texto.length() || texto.charAt(pos) != c) {
            throw erro("esperava '" + c + "'");
        }
        pos++;
    }

    private IllegalArgumentException erro(String motivo) {
        return new IllegalArgumentException("JSON inválido (posição " + pos + "): " + motivo);
    }
}
