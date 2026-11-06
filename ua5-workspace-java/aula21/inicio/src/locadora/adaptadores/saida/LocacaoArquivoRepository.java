package locadora.adaptadores.saida;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.LocacaoRepository;
import locadora.dominio.Locacao;

/** Locações em <pasta>/locacoes.csv — uma linha por locação, só acrescenta. */
public class LocacaoArquivoRepository implements LocacaoRepository {
    private final Path arquivo;

    public LocacaoArquivoRepository(Path pasta) {
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.arquivo = pasta.resolve("locacoes.csv");
    }

    @Override
    public synchronized void adicionar(Locacao l) {
        String linha = l.placa() + ";" + l.cliente() + ";" + l.dias() + ";" + l.valor() + "\n";
        try {
            Files.writeString(arquivo, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized List<Locacao> listar() {
        List<Locacao> locacoes = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return locacoes;
        }
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String[] c = linha.split(";");
                locacoes.add(new Locacao(c[0], c[1], Integer.parseInt(c[2]), Double.parseDouble(c[3])));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return locacoes;
    }
}
