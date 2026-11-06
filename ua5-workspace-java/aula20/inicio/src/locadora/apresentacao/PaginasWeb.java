package locadora.apresentacao;

import java.util.List;
import java.util.Locale;
import locadora.Veiculo;

/** MVC web — VIEW: funções que devolvem HTML. Não chamam o Model, só recebem dados. */
public final class PaginasWeb {
    private PaginasWeb() {
    }

    public static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }

    public static String frota(List<Veiculo> veiculos, String mensagem) {
        StringBuilder linhas = new StringBuilder();
        for (Veiculo v : veiculos) {
            linhas.append(String.format(Locale.ROOT, "<tr><td>%s</td><td>%s</td><td>R$ %.2f</td><td>%s</td><td>%s</td></tr>",
                    escapar(v.placa()), escapar(v.modelo()), v.valorDiaria(),
                    v.disponivel() ? "disponível" : "alugado", botaoDevolver(v)));
        }
        String aviso = mensagem.isEmpty() ? "" : "<p class='msg'>" + escapar(mensagem) + "</p>";
        return "<!doctype html><html lang=\"pt-br\"><head><meta charset=\"utf-8\">\n"
                + "<title>Locadora — MVC</title>\n"
                + "<style>body{font-family:sans-serif;max-width:760px;margin:2rem auto;padding:0 1rem}\n"
                + "table{border-collapse:collapse;width:100%}td,th{border:1px solid #ccc;padding:.4rem}\n"
                + ".msg{background:#eef;padding:.5rem}form.inline{display:inline}</style></head>\n"
                + "<body><h1>Locadora de Veículos</h1>" + aviso + "\n"
                + "<table><tr><th>Placa</th><th>Modelo</th><th>Diária</th><th>Status</th><th></th></tr>" + linhas + "</table>\n"
                + "<h2>Alugar</h2>\n<form method=\"post\" action=\"/alugar\">\n"
                + "<input name=\"placa\" placeholder=\"Placa\" required> <input name=\"dias\" type=\"number\" min=\"1\" value=\"1\">\n"
                + "<input name=\"cliente\" placeholder=\"Cliente\" required> <input name=\"email\" type=\"email\" placeholder=\"E-mail\" required>\n"
                + "<button>Alugar</button></form></body></html>";
    }

    private static String botaoDevolver(Veiculo v) {
        if (v.disponivel()) {
            return "";
        }
        return "<form class='inline' method='post' action='/devolver'>"
                + "<input type='hidden' name='placa' value='" + escapar(v.placa()) + "'>"
                + "<button>Devolver</button></form>";
    }
}
