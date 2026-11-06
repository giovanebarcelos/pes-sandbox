package locadora;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioMemoria implements RepositorioVeiculos {
    private final Map<String, Veiculo> dados = new LinkedHashMap<>();

    @Override
    public void salvar(Veiculo veiculo) {
        dados.put(veiculo.placa(), veiculo);
    }

    @Override
    public Veiculo buscar(String placa) {
        return dados.get(placa);
    }

    @Override
    public List<Veiculo> listar() {
        return new ArrayList<>(dados.values());
    }
}
