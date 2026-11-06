package locadora.aplicacao;

import java.util.List;
import locadora.dominio.Locacao;

/** Porta de SAÍDA para guardar as locações feitas. */
public interface LocacaoRepository {
    void adicionar(Locacao locacao);

    List<Locacao> listar();
}
