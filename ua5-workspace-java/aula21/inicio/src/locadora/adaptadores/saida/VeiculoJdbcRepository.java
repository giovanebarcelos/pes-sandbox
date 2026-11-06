package locadora.adaptadores.saida;

import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.VeiculoRepository;
import locadora.dominio.Fabrica;
import locadora.dominio.Veiculo;

/** Adaptador SQL da porta VeiculoRepository. */
public class VeiculoJdbcRepository implements VeiculoRepository {
    private static final String COLUNAS = "SELECT tipo, placa, modelo, diaria, disponivel FROM veiculos";
    private final BancoJdbc db;

    public VeiculoJdbcRepository(BancoJdbc banco) {
        this.db = banco;
    }

    @Override
    public void salvar(Veiculo v) {
        synchronized (db) {
            int alterados = db.executar("UPDATE veiculos SET disponivel = ? WHERE placa = ?",
                    v.disponivel() ? 1 : 0, v.placa());
            if (alterados == 0) {
                int ordem = ((Number) db.consultar("SELECT COUNT(*) FROM veiculos").get(0)[0]).intValue() + 1;
                db.executar("INSERT INTO veiculos (placa, tipo, modelo, diaria, disponivel, ordem) VALUES (?,?,?,?,?,?)",
                        v.placa(), v.tipo(), v.modelo(), v.valorDiaria(), v.disponivel() ? 1 : 0, ordem);
            }
        }
    }

    @Override
    public Veiculo buscarPorPlaca(String placa) {
        List<Object[]> linhas = db.consultar(COLUNAS + " WHERE placa = ?", placa);
        return linhas.isEmpty() ? null : paraVeiculo(linhas.get(0));
    }

    @Override
    public List<Veiculo> listar() {
        List<Veiculo> veiculos = new ArrayList<>();
        for (Object[] linha : db.consultar(COLUNAS + " ORDER BY ordem")) {
            veiculos.add(paraVeiculo(linha));
        }
        return veiculos;
    }

    private static Veiculo paraVeiculo(Object[] l) {
        Veiculo v = Fabrica.criarVeiculo((String) l[0], (String) l[1], (String) l[2], ((Number) l[3]).doubleValue());
        if (((Number) l[4]).intValue() == 0) {
            v.marcarAlugado();
        }
        return v;
    }
}
