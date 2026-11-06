package locadora.adaptadores.saida;

import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.LocacaoRepository;
import locadora.dominio.Locacao;

public class LocacaoMemoriaRepository implements LocacaoRepository {
    private final List<Locacao> dados = new ArrayList<>();

    @Override
    public synchronized void adicionar(Locacao locacao) {
        dados.add(locacao);
    }

    @Override
    public synchronized List<Locacao> listar() {
        return new ArrayList<>(dados);
    }
}
