package br.edu.reservalab.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.sqlite.SQLiteConfig;

import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.dominio.Erros.ConflitoDeHorario;
import br.edu.reservalab.dominio.Reserva;
import br.edu.reservalab.dominio.Status;

/** Adaptador SQLite: mesmo contrato do RepositorioMemoria (RNF-03). */
public class RepositorioSqlite implements RepositorioReservas, AutoCloseable {
    private static final String ATIVOS = "('PENDENTE','CONFIRMADA')";
    private static final String ESQUEMA = """
            CREATE TABLE IF NOT EXISTS reservas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recurso_id TEXT NOT NULL,
                matricula TEXT NOT NULL,
                inicio TEXT NOT NULL,
                fim TEXT NOT NULL,
                status TEXT NOT NULL,
                cancelada_em TEXT,
                cancelamento_tardio INTEGER NOT NULL DEFAULT 0
            )""";

    private final Connection con;

    public RepositorioSqlite() {
        this(":memory:");
    }

    public RepositorioSqlite(String caminho) {
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE); // BEGIN IMMEDIATE: trava de escrita
            config.setBusyTimeout(10_000);
            Properties props = config.toProperties();
            con = DriverManager.getConnection("jdbc:sqlite:" + caminho, props);
            try (Statement st = con.createStatement()) {
                st.execute(ESQUEMA);
                st.execute("CREATE INDEX IF NOT EXISTS ix_reservas_recurso ON reservas (recurso_id, inicio)");
                st.execute("CREATE INDEX IF NOT EXISTS ix_reservas_matricula ON reservas (matricula)");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String iso(LocalDateTime d) {
        return d == null ? null : d.truncatedTo(ChronoUnit.MINUTES).toString();
    }

    private static Reserva paraReserva(ResultSet rs) throws SQLException {
        String cancelada = rs.getString("cancelada_em");
        return Reserva.restaurar(rs.getLong("id"), rs.getString("recurso_id"), rs.getString("matricula"),
                LocalDateTime.parse(rs.getString("inicio")), LocalDateTime.parse(rs.getString("fim")),
                Status.valueOf(rs.getString("status")),
                cancelada == null ? null : LocalDateTime.parse(cancelada),
                rs.getInt("cancelamento_tardio") == 1);
    }

    @Override
    public Reserva adicionarSemConflito(Reserva reserva) {
        try {
            con.setAutoCommit(false); // com TransactionMode.IMMEDIATE o BEGIN já é IMMEDIATE
            try {
                try (PreparedStatement q = con.prepareStatement(
                        "SELECT inicio, fim FROM reservas WHERE recurso_id=? AND status IN " + ATIVOS
                                + " AND inicio < ? AND ? < fim LIMIT 1")) {
                    q.setString(1, reserva.recursoId());
                    q.setString(2, iso(reserva.fim()));
                    q.setString(3, iso(reserva.inicio()));
                    try (ResultSet rs = q.executeQuery()) {
                        if (rs.next()) {
                            throw new ConflitoDeHorario(String.format("%s já reservado de %s a %s",
                                    reserva.recursoId(), rs.getString(1).substring(11, 16), rs.getString(2).substring(11, 16)));
                        }
                    }
                }
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO reservas (recurso_id, matricula, inicio, fim, status) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ins.setString(1, reserva.recursoId());
                    ins.setString(2, reserva.matricula());
                    ins.setString(3, iso(reserva.inicio()));
                    ins.setString(4, iso(reserva.fim()));
                    ins.setString(5, reserva.status().name());
                    ins.executeUpdate();
                    try (ResultSet keys = ins.getGeneratedKeys()) {
                        keys.next();
                        reserva.atribuirId(keys.getLong(1));
                    }
                }
                con.commit();
                return reserva;
            } catch (RuntimeException | SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<Reserva> obter(long reservaId) {
        try (PreparedStatement q = con.prepareStatement("SELECT * FROM reservas WHERE id=?")) {
            q.setLong(1, reservaId);
            try (ResultSet rs = q.executeQuery()) {
                return rs.next() ? Optional.of(paraReserva(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void salvar(Reserva reserva) {
        try (PreparedStatement up = con.prepareStatement(
                "UPDATE reservas SET status=?, cancelada_em=?, cancelamento_tardio=? WHERE id=?")) {
            up.setString(1, reserva.status().name());
            up.setString(2, iso(reserva.canceladaEm()));
            up.setInt(3, reserva.cancelamentoTardio() ? 1 : 0);
            up.setLong(4, reserva.id());
            up.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Reserva> ativasDoRecurso(String recursoId, LocalDateTime inicio, LocalDateTime fim) {
        return consultar("SELECT * FROM reservas WHERE recurso_id=? AND status IN " + ATIVOS
                + " AND inicio < ? AND ? < fim", recursoId, iso(fim), iso(inicio));
    }

    @Override
    public List<Reserva> doUsuario(String matricula) {
        return consultar("SELECT * FROM reservas WHERE matricula=?", matricula);
    }

    private List<Reserva> consultar(String sql, String... params) {
        try (PreparedStatement q = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                q.setString(i + 1, params[i]);
            }
            List<Reserva> lista = new ArrayList<>();
            try (ResultSet rs = q.executeQuery()) {
                while (rs.next()) {
                    lista.add(paraReserva(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
