package locadora;

import locadora.adaptadores.saida.BancoJdbc;
import locadora.adaptadores.saida.LocacaoJdbcRepository;
import locadora.adaptadores.saida.VeiculoJdbcRepository;
import teste.AfterEach;
import teste.Assumptions;

/** Só roda se houver um driver JDBC (H2 ou SQLite) em lib/ — senão aparece como PULADO. */
class ContratoJdbcTest extends ContratoRepositorioTest {
    private BancoJdbc banco;

    @Override
    protected void criar() {
        String url = JdbcDisponivel.emMemoria();
        Assumptions.assumeTrue(url != null, JdbcDisponivel.SEM_DRIVER);
        banco = new BancoJdbc(url);
        veiculos = new VeiculoJdbcRepository(banco);
        locacoes = new LocacaoJdbcRepository(banco);
    }

    @AfterEach
    void fechar() {
        if (banco != null) {
            banco.close();
        }
    }
}
