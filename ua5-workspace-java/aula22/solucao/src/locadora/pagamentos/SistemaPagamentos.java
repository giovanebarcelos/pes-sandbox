package locadora.pagamentos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

/** Núcleo do microkernel: registro + descoberta automática dos plugins. */
public class SistemaPagamentos {
    /** Resultado de um pagamento: total cobrado (com taxa) + comprovante. */
    public record Pagamento(double total, String comprovante) {
    }

    private final Map<String, PluginPagamento> plugins = new TreeMap<>();

    public SistemaPagamentos registrar(PluginPagamento plugin) {
        if (plugin.nome() == null || plugin.nome().isBlank()) {
            throw new IllegalArgumentException(plugin.getClass().getName() + " não tem nome");
        }
        plugins.put(plugin.nome(), plugin);
        return this;
    }

    /** Registra todo plugin listado em META-INF/services/locadora.pagamentos.PluginPagamento. */
    public SistemaPagamentos descobrir() {
        for (PluginPagamento p : ServiceLoader.load(PluginPagamento.class)) {
            registrar(p);
        }
        return this;
    }

    public List<String> formas() {
        return new ArrayList<>(plugins.keySet());
    }

    public Pagamento pagar(String forma, double valor, String referencia) {
        PluginPagamento plugin = plugins.get(forma);
        if (plugin == null) {
            throw new IllegalArgumentException("Forma de pagamento não suportada: " + forma + ". Aceitas: " + formas());
        }
        double total = Math.round((valor + plugin.taxa(valor)) * 100) / 100.0;
        return new Pagamento(total, plugin.pagar(total, referencia));
    }
}
