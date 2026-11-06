package locadora.aplicacao;

import java.util.List;
import locadora.dominio.Veiculo;

/**
 * Porta de SAÍDA (driven port): o que o caso de uso PRECISA do mundo externo.
 * Fala a língua do domínio (Veiculo, placa), nunca de SQL, CSV ou HTTP.
 */
public interface VeiculoRepository {
    void salvar(Veiculo veiculo);

    /** Devolve o Veiculo ou null. */
    Veiculo buscarPorPlaca(String placa);

    List<Veiculo> listar();
}
