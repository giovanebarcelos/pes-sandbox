package locadora.frota.dominio;

import java.util.List;

/** Porta do contexto de Frota (o adaptador SQL da Aula 20 poderia ser plugado aqui). */
public interface VeiculoRepository {
    void salvar(Veiculo veiculo);

    Veiculo buscarPorPlaca(String placa);

    List<Veiculo> listar();
}
