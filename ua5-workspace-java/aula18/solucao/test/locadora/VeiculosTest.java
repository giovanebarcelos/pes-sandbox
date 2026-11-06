package locadora;

import static teste.Assertions.*;

import java.util.List;
import teste.Test;

/** Testes de OCP, LSP e Factory Method. */
class VeiculosTest {
    /**
     * LSP: QUALQUER tipo de veículo deve passar neste mesmo teste.
     * Ele percorre todos os tipos registrados na fábrica — um tipo novo entra
     * automaticamente no contrato.
     */
    @Test
    void todo_tipo_cumpre_o_contrato() {
        for (String tipo : Fabrica.tiposConhecidos()) {
            Veiculo v = Fabrica.criarVeiculo(tipo, "AAA-0001", "Modelo", 100);
            assertTrue(v.disponivel(), tipo);
            for (int dias : new int[] {1, 2, 7, 30}) {
                assertTrue(v.precoBase(dias) >= 0, tipo + ": preço negativo");
            }
            // preço cresce com os dias (ninguém "inverte" a regra)
            assertTrue(v.precoBase(1) < v.precoBase(2), tipo + ": preço não cresce com os dias");
            v.marcarAlugado();
            assertFalse(v.disponivel(), tipo);
        }
    }

    @Test
    void fabrica_cria_cada_tipo() {
        assertTrue(Fabrica.criarVeiculo("carro", "A", "M", 10) instanceof Carro);
        assertTrue(Fabrica.criarVeiculo("moto", "B", "M", 10) instanceof Moto);
        assertTrue(Fabrica.criarVeiculo("caminhao", "C", "M", 10) instanceof Caminhao);
    }

    @Test
    void fabrica_tipo_desconhecido() {
        assertThrows(IllegalArgumentException.class, () -> Fabrica.criarVeiculo("navio", "D", "M", 10));
    }

    @Test
    void fabrica_tipos_conhecidos() {
        assertEquals(List.of("caminhao", "carro", "moto"), Fabrica.tiposConhecidos());
    }

    @Test
    void caminhao_tem_seguro_e_taxa_fixa() {
        assertEquals(710.0, new Caminhao("C", "M", 300).precoBase(2), 1e-9);
    }

    @Test
    void diaria_precisa_ser_positiva() {
        assertThrows(IllegalArgumentException.class, () -> new Moto("M", "CG", 0));
    }
}
