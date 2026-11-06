package locadora.pagamentos.plugins;

import java.util.Locale;
import locadora.pagamentos.PluginPagamento;

/** Plugin Cartão de crédito: taxa de 3%. */
public class Cartao implements PluginPagamento {
    @Override
    public String nome() {
        return "cartao";
    }

    @Override
    public double taxa(double valor) {
        return Math.round(valor * 0.03 * 100) / 100.0;
    }

    @Override
    public String pagar(double valor, String referencia) {
        return String.format(Locale.ROOT, "Cartão aprovado: R$ %.2f (ref. %s)", valor, referencia);
    }
}
