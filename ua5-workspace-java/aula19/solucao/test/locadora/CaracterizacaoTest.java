package locadora;

import static teste.Assertions.*;

import java.util.NoSuchElementException;
import teste.BeforeEach;
import teste.Test;

/**
 * Os MESMOS cenários de aula18/inicio/test/CaracterizacaoTest.java, agora contra o
 * código refatorado. Se estes passam, a refatoração preservou o comportamento.
 */
class CaracterizacaoTest {
    private NotificadorMemoria notificador;
    private ServicoLocacao servico;

    @BeforeEach
    void preparar() {
        notificador = new NotificadorMemoria();
        servico = new ServicoLocacao(new RepositorioMemoria(), notificador, new DescontoSemanal());
        servico.cadastrar(Fabrica.criarVeiculo("carro", "ABC-1234", "Fiat Uno", 120));
        servico.cadastrar(Fabrica.criarVeiculo("moto", "XYZ-9012", "Honda CG 160", 80));
    }

    private double valor(String placa, int dias) {
        return servico.alugar(placa, "Cliente", "c@x.com", dias).valor();
    }

    @Test
    void carro_5_dias_tem_10_por_cento_de_seguro() {
        assertEquals(660.00, valor("ABC-1234", 5));
    }

    @Test
    void moto_3_dias_sem_seguro() {
        assertEquals(240.00, valor("XYZ-9012", 3));
    }

    @Test
    void sete_dias_ou_mais_tem_10_por_cento_de_desconto() {
        assertEquals(504.00, valor("XYZ-9012", 7));
        assertEquals(1188.00, valor("ABC-1234", 10));
    }

    @Test
    void veiculo_alugado_fica_indisponivel() {
        valor("ABC-1234", 2);
        assertThrows(IllegalStateException.class, () -> valor("ABC-1234", 2));
    }

    @Test
    void devolver_libera_o_veiculo() {
        valor("ABC-1234", 2);
        servico.devolver("ABC-1234");
        assertEquals(132.00, valor("ABC-1234", 1));
    }

    @Test
    void placa_inexistente() {
        assertThrows(NoSuchElementException.class, () -> valor("ZZZ-0000", 1));
    }

    @Test
    void envia_notificacao_de_confirmacao() {
        servico.alugar("ABC-1234", "Ana", "a@x.com", 5);
        String[] ultima = notificador.enviadas.get(notificador.enviadas.size() - 1);
        assertEquals("a@x.com", ultima[0]);
        assertTrue(ultima[1].contains("R$ 660.00"), ultima[1]);
    }

    @Test
    void relatorio_mostra_faturamento() {
        valor("ABC-1234", 5);
        valor("XYZ-9012", 3);
        assertTrue(Relatorio.frota(servico).contains("Faturamento: R$ 900.00"));
    }
}
