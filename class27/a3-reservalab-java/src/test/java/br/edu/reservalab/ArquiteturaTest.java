package br.edu.reservalab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/** RNF-02: a regra de dependência é verificada lendo os imports do código-fonte. */
class ArquiteturaTest {
    private static final Path RAIZ = Path.of("src/main/java/br/edu/reservalab");

    private static List<String> violacoes(String pasta, List<String> proibidos) throws IOException {
        try (Stream<Path> arquivos = Files.list(RAIZ.resolve(pasta))) {
            return arquivos.filter(p -> p.toString().endsWith(".java")).flatMap(p -> {
                try {
                    return Files.readAllLines(p).stream()
                            .filter(l -> l.startsWith("import "))
                            .filter(l -> proibidos.stream().anyMatch(l::contains))
                            .map(l -> p.getFileName() + ": " + l);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }).toList();
        }
    }

    @Test void dominioNaoImportaAplicacaoNemInfra() throws IOException {
        assertEquals(List.of(), violacoes("dominio",
                List.of("br.edu.reservalab.aplicacao", "br.edu.reservalab.infra", "java.sql", "org.sqlite")));
    }

    @Test void aplicacaoNaoImportaInfra() throws IOException {
        assertEquals(List.of(), violacoes("aplicacao",
                List.of("br.edu.reservalab.infra", "java.sql", "org.sqlite")));
    }
}
