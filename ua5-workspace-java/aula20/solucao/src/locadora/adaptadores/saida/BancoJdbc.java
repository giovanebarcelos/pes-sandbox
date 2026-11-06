package locadora.adaptadores.saida;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Banco SQL via JDBC — o ÚNICO lugar do sistema que conhece SQL.
 *
 * O JDK traz a API JDBC (java.sql), mas não traz o DRIVER de nenhum banco.
 * Coloque o .jar do driver em ua5-workspace-java/lib/ (ex.: h2-2.2.224.jar ou
 * sqlite-jdbc-*.jar + slf4j-api-*.jar) e use uma URL como:
 *   jdbc:h2:./dados/locadora        jdbc:h2:mem:teste
 *   jdbc:sqlite:dados/locadora.db   jdbc:sqlite::memory:
 * O SQL abaixo é o "comum" que esses bancos aceitam (sem recursos específicos).
 */
public class BancoJdbc implements AutoCloseable {
    private final Connection conexao;

    public BancoJdbc(String url) {
        try {
            conexao = DriverManager.getConnection(url);
            try (Statement st = conexao.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS veiculos (placa VARCHAR(20) PRIMARY KEY, "
                        + "tipo VARCHAR(20) NOT NULL, modelo VARCHAR(100) NOT NULL, "
                        + "diaria DOUBLE PRECISION NOT NULL, disponivel INTEGER NOT NULL, ordem INTEGER NOT NULL)");
                st.execute("CREATE TABLE IF NOT EXISTS locacoes (id INTEGER PRIMARY KEY, placa VARCHAR(20) NOT NULL, "
                        + "cliente VARCHAR(100) NOT NULL, dias INTEGER NOT NULL, valor DOUBLE PRECISION NOT NULL)");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não consegui abrir o banco " + url + ": " + e.getMessage()
                    + " (o driver JDBC está em lib/?)", e);
        }
    }

    /** Há driver JDBC para esta URL no classpath? (usado pelos testes para pular o JDBC). */
    public static boolean temDriver(String url) {
        try {
            DriverManager.getDriver(url);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Uma linha de resultado: os valores das colunas, em ordem. */
    public synchronized List<Object[]> consultar(String sql, Object... parametros) {
        try (PreparedStatement ps = preparar(sql, parametros); ResultSet rs = ps.executeQuery()) {
            List<Object[]> linhas = new ArrayList<>();
            int colunas = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] linha = new Object[colunas];
                for (int i = 0; i < colunas; i++) {
                    linha[i] = rs.getObject(i + 1);
                }
                linhas.add(linha);
            }
            return linhas;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized int executar(String sql, Object... parametros) {
        try (PreparedStatement ps = preparar(sql, parametros)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private PreparedStatement preparar(String sql, Object... parametros) throws SQLException {
        PreparedStatement ps = conexao.prepareStatement(sql);
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
        return ps;
    }

    @Override
    public void close() {
        try {
            conexao.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
