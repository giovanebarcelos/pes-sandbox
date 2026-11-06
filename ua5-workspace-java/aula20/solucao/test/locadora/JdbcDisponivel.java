package locadora;

import java.nio.file.Path;
import locadora.adaptadores.saida.BancoJdbc;

/** Descobre qual driver JDBC está em lib/ (H2 ou SQLite). Sem driver: os testes JDBC são PULADOS. */
final class JdbcDisponivel {
    private static int contador;

    private JdbcDisponivel() {
    }

    /** URL de um banco novo em memória, ou null se não há driver. */
    static String emMemoria() {
        String h2 = "jdbc:h2:mem:teste" + (++contador);
        if (BancoJdbc.temDriver(h2)) {
            return h2;
        }
        return BancoJdbc.temDriver("jdbc:sqlite::memory:") ? "jdbc:sqlite::memory:" : null;
    }

    /** URL de um banco em arquivo dentro da pasta, ou null se não há driver. */
    static String emArquivo(Path pasta) {
        String h2 = "jdbc:h2:" + pasta.resolve("locadora").toAbsolutePath();
        if (BancoJdbc.temDriver(h2)) {
            return h2;
        }
        String sqlite = "jdbc:sqlite:" + pasta.resolve("locadora.db").toAbsolutePath();
        return BancoJdbc.temDriver(sqlite) ? sqlite : null;
    }

    static final String SEM_DRIVER = "sem driver JDBC em lib/ (opcional — veja o README, Aula 20)";
}
