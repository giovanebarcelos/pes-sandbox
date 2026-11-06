package locadora;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/** Pastas temporárias para os testes (o TemporaryDirectory do Python). */
final class Pastas {
    private Pastas() {
    }

    static Path temporaria() {
        try {
            return Files.createTempDirectory("locadora");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void apagar(Path pasta) {
        try (Stream<Path> s = Files.walk(pasta)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
