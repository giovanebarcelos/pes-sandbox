package locadora;

import java.util.List;

/**
 * Persistência separada da regra de negócio (SRP + DIP).
 *
 * O serviço depende da ABSTRAÇÃO RepositorioVeiculos; quem decide se é
 * memória (testes) ou arquivo CSV (produção) é o Main.
 */
public interface RepositorioVeiculos {
    void salvar(Veiculo veiculo);

    /** Devolve o veículo ou null. */
    Veiculo buscar(String placa);

    List<Veiculo> listar();
}
