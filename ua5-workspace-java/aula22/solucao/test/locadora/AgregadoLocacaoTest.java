package locadora;

import static teste.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import locadora.compartilhado.Dinheiro;
import locadora.locacoes.dominio.DataInvalida;
import locadora.locacoes.dominio.Locacao;
import locadora.locacoes.dominio.LocacaoIniciada;
import locadora.locacoes.dominio.LocacaoJaEncerrada;
import locadora.locacoes.dominio.MultaPorAtrasoAplicada;
import locadora.locacoes.dominio.VeiculoDevolvido;
import teste.Test;

/**
 * Testes no formato DADOS estes eventos / QUANDO este comando / ENTÃO estes eventos.
 * O agregado é testado sem banco, sem frota e sem barramento.
 */
class AgregadoLocacaoTest {
    static final LocacaoIniciada INICIADA = new LocacaoIniciada("L-1", "ABC-1234", "Ana", "2026-11-02", 5, 66000);

    static LocalDate d(int dia) {
        return LocalDate.of(2026, 11, dia);
    }

    @Test
    void iniciar_gera_evento() {
        Locacao loc = Locacao.iniciar("L-1", "abc-1234", "Ana", d(2), 5, new Dinheiro(66000));
        assertEquals(List.of(INICIADA), loc.pendentes());
    }

    @Test
    void devolucao_no_prazo_nao_gera_multa() {
        Locacao loc = Locacao.reconstituir("L-1", List.of(INICIADA));
        loc.registrarDevolucao(d(7));
        assertEquals(List.of(new VeiculoDevolvido("L-1", "ABC-1234", "2026-11-07", 5)), loc.pendentes());
    }

    @Test
    void devolucao_atrasada_gera_multa() {
        Locacao loc = Locacao.reconstituir("L-1", List.of(INICIADA));
        loc.registrarDevolucao(d(9));                      // 7 dias usados, 2 de atraso
        // diária média 132,00 × 1,5 × 2 = 396,00
        List<?> pendentes = loc.pendentes();
        assertEquals(new MultaPorAtrasoAplicada("L-1", 2, 39600), pendentes.get(pendentes.size() - 1));
        assertEquals(new Dinheiro(105600), loc.valorTotal());
    }

    @Test
    void nao_devolve_duas_vezes() {
        Locacao loc = Locacao.reconstituir("L-1",
                List.of(INICIADA, new VeiculoDevolvido("L-1", "ABC-1234", "2026-11-07", 5)));
        assertThrows(LocacaoJaEncerrada.class, () -> loc.registrarDevolucao(d(8)));
    }

    @Test
    void invariantes_de_data() {
        assertThrows(DataInvalida.class, () -> Locacao.iniciar("L-2", "ABC-1234", "Ana", d(2), 0, new Dinheiro(1)));
        Locacao loc = Locacao.reconstituir("L-1", List.of(INICIADA));
        assertThrows(DataInvalida.class, () -> loc.registrarDevolucao(d(1)));
    }

    @Test
    void reconstituir_chega_ao_mesmo_estado() {
        Locacao original = Locacao.iniciar("L-1", "ABC-1234", "Ana", d(2), 5, new Dinheiro(66000));
        original.registrarDevolucao(d(9));
        Locacao copia = Locacao.reconstituir("L-1", original.pendentes());
        assertEquals(List.of(original.encerrada(), original.valorTotal(), original.versao()),
                List.of(copia.encerrada(), copia.valorTotal(), copia.versao()));
    }
}
