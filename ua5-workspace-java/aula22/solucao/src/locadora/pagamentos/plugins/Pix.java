package locadora.pagamentos.plugins;

import java.util.Locale;
import locadora.pagamentos.PluginPagamento;

/** Plugin PIX: sem taxa. */
public class Pix implements PluginPagamento {
    @Override
    public String nome() {
        return "pix";
    }

    @Override
    public double taxa(double valor) {
        return 0.0;
    }

    @Override
    public String pagar(double valor, String referencia) {
        return String.format(Locale.ROOT, "PIX recebido: R$ %.2f (ref. %s)", valor, referencia);
    }
}
