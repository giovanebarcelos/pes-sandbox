package locadora.adaptadores.saida;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import locadora.aplicacao.VeiculoRepository;
import locadora.dominio.Veiculo;

/** Adaptador em memória — usado nos testes (sem banco, sem disco). */
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
