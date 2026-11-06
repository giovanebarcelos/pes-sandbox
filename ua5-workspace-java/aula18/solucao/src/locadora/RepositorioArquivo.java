package locadora;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Mesmo formato de arquivo do legado: CSV "tipo;placa;modelo;diaria;disponivel". */
public class RepositorioArquivo implements RepositorioVeiculos {
    private final Path arquivo;
    private final Map<String, Veiculo> dados = new LinkedHashMap<>();

    public RepositorioArquivo(String arquivo) {
        this.arquivo = Path.of(arquivo);
        if (Files.exists(this.arquivo)) {
            try {
                List<String> linhas = Files.readAllLines(this.arquivo, StandardCharsets.UTF_8);
                for (String linha : linhas.subList(1, linhas.size())) {
                    String[] c = linha.split(";");
                    Veiculo v = Fabrica.criarVeiculo(c[0], c[1], c[2], Double.parseDouble(c[3]));
                    if (!Boolean.parseBoolean(c[4])) {
                        v.marcarAlugado();
                    }
                    dados.put(v.placa(), v);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void salvar(Veiculo veiculo) {
        dados.put(veiculo.placa(), veiculo);
        StringBuilder csv = new StringBuilder("tipo;placa;modelo;diaria;disponivel\n");
        for (Veiculo v : dados.values()) {
            csv.append(v.tipo()).append(';').append(v.placa()).append(';').append(v.modelo()).append(';')
               .append(v.valorDiaria()).append(';').append(v.disponivel()).append('\n');
        }
        try {
            Files.writeString(arquivo, csv, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
