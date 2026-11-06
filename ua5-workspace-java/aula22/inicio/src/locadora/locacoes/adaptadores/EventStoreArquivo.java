package locadora.locacoes.adaptadores;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import locadora.compartilhado.EventoDominio;
import locadora.compartilhado.RegistroEventos;
import locadora.infra.Json;
import locadora.locacoes.dominio.LocacaoIniciada;
import locadora.locacoes.dominio.MultaPorAtrasoAplicada;
import locadora.locacoes.dominio.VeiculoDevolvido;

/** Mesmo comportamento, mas cada evento também vai para uma linha do arquivo .jsonl (JSON Lines). */
public class EventStoreArquivo extends EventStoreMemoria {
    private final Path caminho;

    public EventStoreArquivo(String caminho) {
        RegistroEventos.registrar(LocacaoIniciada.class, VeiculoDevolvido.class, MultaPorAtrasoAplicada.class);
        this.caminho = Path.of(caminho);
        try {
            Path pasta = this.caminho.toAbsolutePath().getParent();
            Files.createDirectories(pasta);
            if (Files.exists(this.caminho)) {
                for (String linha : Files.readAllLines(this.caminho, StandardCharsets.UTF_8)) {
                    if (!linha.isBlank()) {
                        eventos.add(RegistroEventos.deMapa(Json.lerObjeto(linha)));
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized void anexar(String idAgregado, List<? extends EventoDominio> novos, int versaoEsperada) {
        super.anexar(idAgregado, novos, versaoEsperada);
        StringBuilder linhas = new StringBuilder();
        for (EventoDominio e : novos) {
            linhas.append(Json.escrever(RegistroEventos.paraMapa(e))).append('\n');
        }
        try {
            Files.writeString(caminho, linhas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
