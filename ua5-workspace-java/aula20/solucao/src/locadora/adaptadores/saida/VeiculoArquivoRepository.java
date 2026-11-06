package locadora.adaptadores.saida;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.VeiculoRepository;
import locadora.dominio.Fabrica;
import locadora.dominio.Veiculo;

/**
 * Adaptador em ARQUIVO (CSV) — persiste entre execuções usando só o JDK.
 * Arquivo: <pasta>/veiculos.csv com "tipo;placa;modelo;diaria;disponivel".
 */
public class VeiculoArquivoRepository implements VeiculoRepository {
    private final Path arquivo;

    public VeiculoArquivoRepository(Path pasta) {
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.arquivo = pasta.resolve("veiculos.csv");
    }

    @Override
    public synchronized void salvar(Veiculo veiculo) {
        List<Veiculo> todos = listar();
        boolean achou = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).placa().equals(veiculo.placa())) {
                todos.set(i, veiculo);
                achou = true;
            }
        }
        if (!achou) {
            todos.add(veiculo);
        }
        StringBuilder csv = new StringBuilder();
        for (Veiculo v : todos) {
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
    public synchronized Veiculo buscarPorPlaca(String placa) {
        for (Veiculo v : listar()) {
            if (v.placa().equals(placa)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Veiculo> listar() {
        List<Veiculo> veiculos = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return veiculos;
        }
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String[] c = linha.split(";");
                Veiculo v = Fabrica.criarVeiculo(c[0], c[1], c[2], Double.parseDouble(c[3]));
                if (!Boolean.parseBoolean(c[4])) {
                    v.marcarAlugado();
                }
                veiculos.add(v);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return veiculos;
    }
}
