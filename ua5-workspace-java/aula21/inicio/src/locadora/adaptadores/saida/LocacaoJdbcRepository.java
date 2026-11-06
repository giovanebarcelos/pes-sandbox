package locadora.adaptadores.saida;

import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.LocacaoRepository;
import locadora.dominio.Locacao;

/** Adaptador SQL da porta LocacaoRepository. */
public class LocacaoJdbcRepository implements LocacaoRepository {
    private final BancoJdbc db;

    public LocacaoJdbcRepository(BancoJdbc banco) {
        this.db = banco;
    }

    @Override
    public void adicionar(Locacao l) {
        synchronized (db) {
            int id = ((Number) db.consultar("SELECT COUNT(*) FROM locacoes").get(0)[0]).intValue() + 1;
            db.executar("INSERT INTO locacoes (id, placa, cliente, dias, valor) VALUES (?,?,?,?,?)",
                    id, l.placa(), l.cliente(), l.dias(), l.valor());
        }
    }

    @Override
    public List<Locacao> listar() {
        List<Locacao> locacoes = new ArrayList<>();
        for (Object[] r : db.consultar("SELECT placa, cliente, dias, valor FROM locacoes ORDER BY id")) {
            locacoes.add(new Locacao((String) r[0], (String) r[1], ((Number) r[2]).intValue(),
                    ((Number) r[3]).doubleValue()));
        }
        return locacoes;
    }
}
