package locadora;

import static teste.Assertions.*;

import java.util.List;
import locadora.aplicacao.LocacaoRepository;
import locadora.aplicacao.VeiculoRepository;
import locadora.dominio.Fabrica;
import locadora.dominio.Locacao;
import locadora.dominio.Veiculo;
import teste.BeforeEach;
import teste.Test;

/**
 * Teste de CONTRATO das portas VeiculoRepository/LocacaoRepository.
 *
 * A mesma bateria roda contra todos os adaptadores (subclasses abaixo). Se um adaptador
 * novo (ex.: PostgreSQL) passar aqui, ele pode substituir os outros (LSP aplicado
 * à arquitetura).
 */
abstract class ContratoRepositorioTest {
    protected VeiculoRepository veiculos;
    protected LocacaoRepository locacoes;

    /** As subclasses só dizem COMO criar os dois repositórios. */
    protected abstract void criar();

    @BeforeEach
    void preparar() {
        criar();
    }

    @Test
    void salvar_e_buscar() {
        veiculos.salvar(Fabrica.criarVeiculo("carro", "ABC-1234", "Uno", 120));
        Veiculo v = veiculos.buscarPorPlaca("ABC-1234");
        assertEquals(List.of("ABC-1234", "Uno", 120.0, "carro"), List.of(v.placa(), v.modelo(), v.valorDiaria(), v.tipo()));
    }

    @Test
    void buscar_inexistente_devolve_null() {
        assertNull(veiculos.buscarPorPlaca("NAO-0000"));
    }

    @Test
    void salvar_de_novo_atualiza_disponibilidade() {
        Veiculo v = Fabrica.criarVeiculo("moto", "XYZ-9012", "CG", 80);
        veiculos.salvar(v);
        v.marcarAlugado();
        veiculos.salvar(v);
        assertFalse(veiculos.buscarPorPlaca("XYZ-9012").disponivel());
        assertEquals(1, veiculos.listar().size());
    }

    @Test
    void locacoes() {
        locacoes.adicionar(new Locacao("ABC-1234", "Ana", 5, 660.0));
        assertEquals(List.of(new Locacao("ABC-1234", "Ana", 5, 660.0)), locacoes.listar());
    }
}
