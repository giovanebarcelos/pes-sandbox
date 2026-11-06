package locadora.frota.adaptadores;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import locadora.frota.dominio.Veiculo;
import locadora.frota.dominio.VeiculoRepository;

/** Repositório em memória da frota. */
public class VeiculoMemoriaRepository implements VeiculoRepository {
    private final Map<String, Veiculo> dados = new LinkedHashMap<>();

    @Override
    public synchronized void salvar(Veiculo veiculo) {
        dados.put(veiculo.placa(), veiculo);
    }

    @Override
    public synchronized Veiculo buscarPorPlaca(String placa) {
        return dados.get(placa);
    }

    @Override
    public synchronized List<Veiculo> listar() {
        return new ArrayList<>(dados.values());
    }
}
