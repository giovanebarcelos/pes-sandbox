package locadora;

import static teste.Assertions.*;

import locadora.pagamentos.PluginPagamento;
import locadora.pagamentos.SistemaPagamentos;
import teste.BeforeEach;
import teste.Test;

/** Microkernel: o núcleo descobre os plugins sozinho e aceita plugins novos sem mudar. */
class PluginsTest {
    private SistemaPagamentos sistema;

    @BeforeEach
    void preparar() {
        sistema = new SistemaPagamentos().descobrir();
    }

    @Test
    void descobre_plugins_pelo_service_loader() {
        assertTrue(sistema.formas().contains("pix"), sistema.formas().toString());
        assertTrue(sistema.formas().contains("cartao"), sistema.formas().toString());
    }

    @Test
    void cada_plugin_aplica_sua_taxa() {
        assertEquals(660.0, sistema.pagar("pix", 660.0, "L-1").total());
        assertEquals(679.8, sistema.pagar("cartao", 660.0, "L-1").total());
    }

    @Test
    void forma_nao_suportada() {
        assertThrows(IllegalArgumentException.class, () -> sistema.pagar("cheque", 10.0, "L-1"));
    }

    @Test
    void plugin_registrado_em_tempo_de_execucao() {
        sistema.registrar(new PluginPagamento() {
            public String nome() { return "cripto"; }
            public double taxa(double valor) { return 1.0; }
            public String pagar(double valor, String referencia) { return "ok"; }
        });
        assertEquals(new SistemaPagamentos.Pagamento(11.0, "ok"), sistema.pagar("cripto", 10.0, "L-1"));
    }

    @Test
    void rejeita_plugin_sem_nome() {
        assertThrows(IllegalArgumentException.class, () -> sistema.registrar(new PluginPagamento() {
            public String nome() { return " "; }
            public double taxa(double valor) { return 0; }
            public String pagar(double valor, String referencia) { return ""; }
        }));
    }
}
