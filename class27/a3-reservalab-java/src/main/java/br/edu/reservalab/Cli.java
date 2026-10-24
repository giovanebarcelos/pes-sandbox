package br.edu.reservalab;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import br.edu.reservalab.aplicacao.CatalogoRecursos;
import br.edu.reservalab.aplicacao.ServicoReservas;
import br.edu.reservalab.dominio.Auditorio;
import br.edu.reservalab.dominio.Erros.ErroDeDominio;
import br.edu.reservalab.dominio.Laboratorio;
import br.edu.reservalab.dominio.Perfil;
import br.edu.reservalab.dominio.Reserva;
import br.edu.reservalab.dominio.SalaEstudo;
import br.edu.reservalab.dominio.Usuario;
import br.edu.reservalab.infra.RepositorioSqlite;

/** Demonstração em linha de comando: {@code java -cp ... br.edu.reservalab.Cli demo}. */
public final class Cli {
    private Cli() { }

    static ServicoReservas montar(String caminhoDb, Supplier<LocalDateTime> relogio) {
        CatalogoRecursos catalogo = new CatalogoRecursos();
        catalogo.registrar(new SalaEstudo("S101", "Sala de estudo 101", 6));
        catalogo.registrar(new Laboratorio("LAB2", "Laboratório de Redes", 30));
        catalogo.registrar(new Auditorio("AUD1", "Auditório Central", 200));
        return new ServicoReservas(new RepositorioSqlite(caminhoDb), catalogo, relogio);
    }

    private static LocalDateTime d(int dia, int hora) {
        return LocalDateTime.of(2026, 11, dia, hora, 0);
    }

    private static void tentar(String texto, Supplier<Reserva> acao) {
        try {
            Reserva r = acao.get();
            System.out.printf("OK    %s -> reserva #%d %s%n", texto, r.id(), r.status());
        } catch (ErroDeDominio e) {
            System.out.printf("ERRO  %s -> %s%n", texto, e.getMessage());
        }
    }

    static void demo() {
        LocalDateTime hoje = LocalDateTime.of(2026, 11, 9, 9, 0); // relógio fixo: saída reproduzível
        ServicoReservas servico = montar(":memory:", () -> hoje);
        Usuario ana = new Usuario("20261001", "Ana", Perfil.ALUNO);
        Usuario prof = new Usuario("P0042", "Prof. Beto", Perfil.PROFESSOR);
        Usuario coord = new Usuario("C0001", "Coord. Carla", Perfil.COORDENADOR);

        tentar("Ana reserva S101 das 14h às 16h (10/11)", () -> servico.solicitar(ana, "S101", d(10, 14), d(10, 16)));
        tentar("Ana tenta 15h-17h na mesma sala", () -> servico.solicitar(ana, "S101", d(10, 15), d(10, 17)));
        tentar("Ana tenta o laboratório", () -> servico.solicitar(ana, "LAB2", d(11, 10), d(11, 12)));
        tentar("Prof. Beto reserva o auditório (13/11)", () -> servico.solicitar(prof, "AUD1", d(13, 19), d(13, 21)));
        System.out.println("Coord. Carla aprova a reserva #2 -> " + servico.decidir(coord, 2, true).status());
        System.out.println("Livres em S101 no dia 10:");
        servico.horariosLivres("S101", LocalDate.of(2026, 11, 10))
                .forEach(f -> System.out.printf("  %tR - %tR%n", f.inicio(), f.fim()));
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("demo")) {
            demo();
        } else {
            System.out.println("uso: Cli demo");
        }
    }
}
