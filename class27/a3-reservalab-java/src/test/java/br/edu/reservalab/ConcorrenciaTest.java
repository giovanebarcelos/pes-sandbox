package br.edu.reservalab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.dominio.Erros.ConflitoDeHorario;
import br.edu.reservalab.dominio.Reserva;
import br.edu.reservalab.dominio.Status;
import br.edu.reservalab.infra.RepositorioMemoria;
import br.edu.reservalab.infra.RepositorioSqlite;

/** RNF-04: sob 20 solicitações simultâneas do mesmo horário, exatamente 1 é aceita. */
class ConcorrenciaTest {

    private static List<String> disputar(Supplier<RepositorioReservas> criar, int n) throws InterruptedException {
        List<String> resultados = new CopyOnWriteArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final int k = i;
            Thread t = new Thread(() -> {
                Reserva r = new Reserva("S101", "M" + k, LocalDateTime.of(2026, 11, 10, 14, 0),
                        LocalDateTime.of(2026, 11, 10, 16, 0), Status.CONFIRMADA);
                try {
                    criar.get().adicionarSemConflito(r);
                    resultados.add("ok");
                } catch (ConflitoDeHorario e) {
                    resultados.add("conflito");
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        return resultados;
    }

    @Test void memoria20ThreadsNoMesmoHorarioSoUmaVence() throws InterruptedException {
        RepositorioMemoria repo = new RepositorioMemoria();
        List<String> r = disputar(() -> repo, 20);
        assertEquals(1, r.stream().filter("ok"::equals).count());
        assertEquals(19, r.stream().filter("conflito"::equals).count());
    }

    @Test void sqlite20ConexoesNoMesmoHorarioSoUmaVence(@TempDir Path dir) throws InterruptedException {
        String arquivo = dir.resolve("corrida.db").toString();
        new RepositorioSqlite(arquivo).close(); // cria o esquema antes da disputa
        List<String> r = disputar(() -> new RepositorioSqlite(arquivo), 20);
        assertEquals(1, r.stream().filter("ok"::equals).count());
        assertEquals(19, r.stream().filter("conflito"::equals).count());
    }
}
