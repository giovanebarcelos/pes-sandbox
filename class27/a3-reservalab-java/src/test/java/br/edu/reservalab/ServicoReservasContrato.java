package br.edu.reservalab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.reservalab.aplicacao.CatalogoRecursos;
import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.aplicacao.ServicoReservas;
import br.edu.reservalab.aplicacao.ServicoReservas.Faixa;
import br.edu.reservalab.dominio.Auditorio;
import br.edu.reservalab.dominio.Erros.ConflitoDeHorario;
import br.edu.reservalab.dominio.Erros.NaoAutorizado;
import br.edu.reservalab.dominio.Erros.NaoEncontrado;
import br.edu.reservalab.dominio.Erros.RegraViolada;
import br.edu.reservalab.dominio.Erros.TransicaoInvalida;
import br.edu.reservalab.dominio.Erros.UsuarioBloqueado;
import br.edu.reservalab.dominio.Laboratorio;
import br.edu.reservalab.dominio.Perfil;
import br.edu.reservalab.dominio.Reserva;
import br.edu.reservalab.dominio.SalaEstudo;
import br.edu.reservalab.dominio.Status;
import br.edu.reservalab.dominio.Usuario;

/**
 * Contrato do sistema (RF-02 a RF-09, RNF-01, RNF-03): a MESMA suíte roda sobre cada repositório.
 * As subclasses só dizem qual repositório criar.
 */
abstract class ServicoReservasContrato {
    static final LocalDateTime AGORA = dia(9, 9, 0); // segunda-feira, 09h

    LocalDateTime agora;
    RepositorioReservas repositorio;
    ServicoReservas servico;
    final Usuario ana = new Usuario("20261001", "Ana", Perfil.ALUNO);
    final Usuario prof = new Usuario("P0042", "Prof. Beto", Perfil.PROFESSOR);
    final Usuario coord = new Usuario("C0001", "Coord. Carla", Perfil.COORDENADOR);

    abstract RepositorioReservas novoRepositorio();

    static LocalDateTime dia(int d, int h, int m) {
        return LocalDateTime.of(2026, 11, d, h, m);
    }

    static LocalDateTime dia(int d, int h) {
        return dia(d, h, 0);
    }

    @BeforeEach
    void preparar() {
        agora = AGORA;
        repositorio = novoRepositorio();
        CatalogoRecursos catalogo = new CatalogoRecursos();
        catalogo.registrar(new SalaEstudo("S101", "Sala de estudo 101", 6));
        catalogo.registrar(new Laboratorio("LAB2", "Laboratório de Redes", 30));
        catalogo.registrar(new Auditorio("AUD1", "Auditório Central", 200));
        servico = new ServicoReservas(repositorio, catalogo, () -> agora);
    }

    // ---- RF-03 e RF-05: regras por tipo ----

    @Test void salaDeEstudoConfirmaNaHora() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        assertEquals(Status.CONFIRMADA, r.status());
        assertNotNull(r.id());
    }

    @Test void salaAceitaExatamente2hERecusa2h01() {
        servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(ana, "S101", dia(11, 14), dia(11, 16, 1)));
        assertTrue(e.getMessage().contains("duração máxima de 2h"));
    }

    @Test void salaExige30minDeAntecedencia() {
        agora = dia(9, 13, 30);
        servico.solicitar(ana, "S101", dia(9, 14), dia(9, 15)); // exatamente 30 min: ok
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(ana, "S101", dia(9, 13, 59), dia(9, 14, 30)));
        assertTrue(e.getMessage().contains("antecedência"));
    }

    @Test void fimAntesDoInicioERecusado() {
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(ana, "S101", dia(10, 16), dia(10, 14)));
        assertTrue(e.getMessage().contains("depois do início"));
    }

    @Test void laboratorioRecusaAluno() {
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(ana, "LAB2", dia(11, 10), dia(11, 12)));
        assertTrue(e.getMessage().contains("ALUNO"));
    }

    @Test void laboratorioExige24hDeAntecedencia() {
        servico.solicitar(prof, "LAB2", dia(10, 9), dia(10, 11)); // exatamente 24h: ok
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(prof, "LAB2", dia(10, 8, 59), dia(10, 10)));
        assertTrue(e.getMessage().contains("24h"));
    }

    @Test void auditorioFicaPendenteAteAprovacao() {
        assertEquals(Status.PENDENTE, servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21)).status());
    }

    @Test void auditorioExige72h() {
        RegraViolada e = assertThrows(RegraViolada.class,
                () -> servico.solicitar(prof, "AUD1", dia(11, 19), dia(11, 21)));
        assertTrue(e.getMessage().contains("72h"));
    }

    // ---- RF-04: conflito ----

    @Test void sobreposicaoParcialERecusada() {
        servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        assertThrows(ConflitoDeHorario.class, () -> servico.solicitar(ana, "S101", dia(10, 15), dia(10, 17)));
    }

    @Test void reservasEncostadasNaoConflitam() { // [início, fim)
        servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        servico.solicitar(ana, "S101", dia(10, 16), dia(10, 18));
        servico.solicitar(ana, "S101", dia(10, 12), dia(10, 14));
    }

    @Test void mesmoHorarioEmRecursosDiferentesEPermitido() {
        servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        servico.solicitar(prof, "LAB2", dia(10, 14), dia(10, 16));
    }

    @Test void reservaCanceladaLiberaOHorario() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        servico.cancelar(ana, r.id());
        servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
    }

    @Test void reservaPendenteTambemOcupaOHorario() {
        servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21));
        assertThrows(ConflitoDeHorario.class, () -> servico.solicitar(prof, "AUD1", dia(13, 20), dia(13, 22)));
    }

    // ---- RF-06, RF-07, RF-08: cancelamento, bloqueio, aprovação ----

    @Test void cancelarComAntecedenciaNaoETardio() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        agora = dia(10, 11, 59); // 2h01 antes
        r = servico.cancelar(ana, r.id());
        assertEquals(Status.CANCELADA, r.status());
        assertFalse(r.cancelamentoTardio());
    }

    @Test void cancelarComMenosDe2hETardio() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        agora = dia(10, 12, 1); // 1h59 antes
        assertTrue(servico.cancelar(ana, r.id()).cancelamentoTardio());
    }

    @Test void exatamente2hAntesNaoETardio() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        agora = dia(10, 12, 0);
        assertFalse(servico.cancelar(ana, r.id()).cancelamentoTardio());
    }

    @Test void naoCancelaReservaJaIniciada() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        agora = dia(10, 14, 0);
        RegraViolada e = assertThrows(RegraViolada.class, () -> servico.cancelar(ana, r.id()));
        assertTrue(e.getMessage().contains("já começou"));
    }

    @Test void naoCancelaDuasVezes() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        servico.cancelar(ana, r.id());
        assertThrows(TransicaoInvalida.class, () -> servico.cancelar(ana, r.id()));
    }

    @Test void outroAlunoNaoCancelaReservaAlheia() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        Usuario bia = new Usuario("999", "Bia", Perfil.ALUNO);
        assertThrows(NaoAutorizado.class, () -> servico.cancelar(bia, r.id()));
    }

    @Test void coordenadorPodeCancelarReservaAlheia() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        assertEquals(Status.CANCELADA, servico.cancelar(coord, r.id()).status());
    }

    private void cancelarTardio(int diaReserva) {
        Reserva r = servico.solicitar(ana, "S101", dia(diaReserva, 14), dia(diaReserva, 15));
        agora = dia(diaReserva, 13, 0); // 1h antes -> tardio
        servico.cancelar(ana, r.id());
    }

    @Test void tresCancelamentosTardiosBloqueiam() {
        for (int d : new int[] {10, 11, 12}) {
            agora = dia(9, 9, 0);
            cancelarTardio(d);
        }
        agora = dia(12, 20, 0);
        UsuarioBloqueado e = assertThrows(UsuarioBloqueado.class,
                () -> servico.solicitar(ana, "S101", dia(16, 14), dia(16, 15)));
        assertTrue(e.getMessage().contains("3 cancelamentos"));
    }

    @Test void doisCancelamentosTardiosNaoBloqueiam() {
        for (int d : new int[] {10, 11}) {
            agora = dia(9, 9, 0);
            cancelarTardio(d);
        }
        agora = dia(11, 20, 0);
        servico.solicitar(ana, "S101", dia(16, 14), dia(16, 15));
    }

    @Test void bloqueioExpiraApos30Dias() {
        for (int d : new int[] {10, 11, 12}) {
            agora = dia(9, 9, 0);
            cancelarTardio(d);
        }
        agora = dia(12, 13, 0).plusDays(30).plusMinutes(1);
        servico.solicitar(ana, "S101", agora.plusHours(1), agora.plusHours(2));
    }

    @Test void coordenadorAprovaReservaPendente() {
        Reserva r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21));
        assertEquals(Status.CONFIRMADA, servico.decidir(coord, r.id(), true).status());
    }

    @Test void coordenadorRejeitaEHorarioFicaLivre() {
        Reserva r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21));
        servico.decidir(coord, r.id(), false);
        servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21));
    }

    @Test void professorNaoAprova() {
        Reserva r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21));
        assertThrows(NaoAutorizado.class, () -> servico.decidir(prof, r.id(), true));
    }

    @Test void naoAprovaReservaJaConfirmada() {
        Reserva r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16));
        assertThrows(TransicaoInvalida.class, () -> servico.decidir(coord, r.id(), true));
    }

    // ---- RF-02 e RF-09: consultas ----

    @Test void diaVazioTemFaixaUnica08a22() {
        assertEquals(List.of(new Faixa(dia(10, 8), dia(10, 22))),
                servico.horariosLivres("S101", LocalDate.of(2026, 11, 10)));
    }

    @Test void horariosLivresDescontamReservas() {
        servico.solicitar(ana, "S101", dia(10, 10), dia(10, 12));
        servico.solicitar(ana, "S101", dia(10, 12), dia(10, 13)); // encostada: faixa livre não se divide
        servico.solicitar(ana, "S101", dia(10, 15), dia(10, 16));
        assertEquals(List.of(new Faixa(dia(10, 8), dia(10, 10)), new Faixa(dia(10, 13), dia(10, 15)),
                new Faixa(dia(10, 16), dia(10, 22))), servico.horariosLivres("S101", LocalDate.of(2026, 11, 10)));
    }

    @Test void recursoInexistente() {
        assertThrows(NaoEncontrado.class, () -> servico.horariosLivres("XXX", LocalDate.of(2026, 11, 10)));
    }

    @Test void minhasReservasOrdenadasPorInicio() {
        servico.solicitar(ana, "S101", dia(11, 10), dia(11, 11));
        servico.solicitar(ana, "S101", dia(10, 10), dia(10, 11));
        assertEquals(List.of(dia(10, 10), dia(11, 10)),
                servico.minhasReservas(ana).stream().map(Reserva::inicio).toList());
    }

    // ---- RNF-01 e RNF-03 ----

    @Test void verificarConflitoEmMenosDe200msCom10MilReservas() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0);
        for (int i = 0; i < 10_000; i++) { // 10 mil reservas encostadas, em dias diferentes, no mesmo recurso
            LocalDateTime inicio = base.plusDays(i / 10).plusHours(i % 10);
            repositorio.adicionarSemConflito(new Reserva("S101", "M" + i, inicio, inicio.plusHours(1), Status.CONFIRMADA));
        }
        Reserva nova = new Reserva("S101", "X", LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 11, 0), Status.CONFIRMADA);
        long t0 = System.nanoTime();
        repositorio.adicionarSemConflito(nova);
        assertTrue(Duration.ofNanos(System.nanoTime() - t0).toMillis() < 200);
    }

    @Test void contratoDoRepositorioIdaEVolta() {
        Reserva r = repositorio.adicionarSemConflito(
                new Reserva("S101", "M1", dia(10, 14), dia(10, 15), Status.CONFIRMADA));
        Reserva lida = repositorio.obter(r.id()).orElseThrow();
        assertEquals("S101", lida.recursoId());
        assertEquals("M1", lida.matricula());
        assertEquals(dia(10, 14), lida.inicio());
        assertEquals(dia(10, 15), lida.fim());
        assertEquals(Status.CONFIRMADA, lida.status());
        assertTrue(repositorio.obter(999).isEmpty());
    }
}
