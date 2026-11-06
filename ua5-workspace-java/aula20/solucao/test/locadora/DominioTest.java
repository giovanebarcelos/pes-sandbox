package locadora;

import static teste.Assertions.*;

import java.util.List;
import locadora.dominio.Caminhao;
import locadora.dominio.Carro;
import locadora.dominio.DescontoSemanal;
import locadora.dominio.Fabrica;
import locadora.dominio.Moto;
import locadora.dominio.Veiculo;
import teste.Test;

/** Domínio puro: nenhum banco, nenhum adaptador, nenhum serviço. */
class DominioTest {
    @Test
    void precos_base() {
        assertEquals(660.0, new Carro("A", "M", 120).precoBase(5), 1e-9);
        assertEquals(240.0, new Moto("B", "M", 80).precoBase(3), 1e-9);
        assertEquals(710.0, new Caminhao("C", "M", 300).precoBase(2), 1e-9);
    }

    @Test
    void desconto_semanal() {
        assertEquals(504.0, new DescontoSemanal().aplicar(560.0, 7));
        assertEquals(480.0, new DescontoSemanal().aplicar(480.0, 6));
    }

    @Test
    void fabrica() {
        assertTrue(Fabrica.criarVeiculo("moto", "X", "CG", 80) instanceof Moto);
        assertEquals(List.of("caminhao", "carro", "moto"), Fabrica.tiposConhecidos());
    }

    @Test
    void contrato_lsp_de_todos_os_tipos() {
        for (String tipo : Fabrica.tiposConhecidos()) {
            Veiculo v = Fabrica.criarVeiculo(tipo, "AAA-0001", "Modelo", 100);
            assertTrue(v.precoBase(1) >= 0 && v.precoBase(1) < v.precoBase(2), tipo);
        }
    }
}
