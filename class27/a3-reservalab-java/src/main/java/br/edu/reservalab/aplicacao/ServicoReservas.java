package br.edu.reservalab.aplicacao;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import br.edu.reservalab.dominio.Erros.NaoAutorizado;
import br.edu.reservalab.dominio.Erros.NaoEncontrado;
import br.edu.reservalab.dominio.Erros.UsuarioBloqueado;
import br.edu.reservalab.dominio.Perfil;
import br.edu.reservalab.dominio.Recurso;
import br.edu.reservalab.dominio.Reserva;
import br.edu.reservalab.dominio.Status;
import br.edu.reservalab.dominio.Usuario;

/** Casos de uso do sistema. Não conhece banco nem interface (ADR-001). */
public class ServicoReservas {
    public static final Duration JANELA_PENALIDADE = Duration.ofDays(30);
    public static final int LIMITE_TARDIOS = 3;
    public static final int ABERTURA = 8;
    public static final int FECHAMENTO = 22;

    /** Faixa livre [inicio, fim). */
    public record Faixa(LocalDateTime inicio, LocalDateTime fim) { }

    private final RepositorioReservas reservas;
    private final CatalogoRecursos catalogo;
    private final Supplier<LocalDateTime> relogio;

    public ServicoReservas(RepositorioReservas reservas, CatalogoRecursos catalogo, Supplier<LocalDateTime> relogio) {
        this.reservas = reservas;
        this.catalogo = catalogo;
        this.relogio = relogio;
    }

    /** RF-03/04/05/07: valida regras do tipo, bloqueio e conflito; grava. */
    public Reserva solicitar(Usuario usuario, String recursoId, LocalDateTime inicio, LocalDateTime fim) {
        Recurso recurso = catalogo.obter(recursoId);
        LocalDateTime agora = relogio.get();
        exigirNaoBloqueado(usuario, agora);
        recurso.validar(usuario, inicio, fim, agora);
        Status status = recurso.exigeAprovacao() ? Status.PENDENTE : Status.CONFIRMADA;
        return reservas.adicionarSemConflito(new Reserva(recursoId, usuario.matricula(), inicio, fim, status));
    }

    /** RF-06: dono ou coordenador cancela; a regra das 2h mora na Reserva. */
    public Reserva cancelar(Usuario usuario, long reservaId) {
        Reserva reserva = buscar(reservaId);
        if (!reserva.matricula().equals(usuario.matricula()) && usuario.perfil() != Perfil.COORDENADOR) {
            throw new NaoAutorizado("só o dono ou um coordenador cancela esta reserva");
        }
        reserva.cancelar(relogio.get());
        reservas.salvar(reserva);
        return reserva;
    }

    /** RF-08: só coordenador aprova ou rejeita reserva pendente. */
    public Reserva decidir(Usuario coordenador, long reservaId, boolean aprovar) {
        if (coordenador.perfil() != Perfil.COORDENADOR) {
            throw new NaoAutorizado("só coordenador aprova ou rejeita reservas");
        }
        Reserva reserva = buscar(reservaId);
        if (aprovar) {
            reserva.confirmar();
        } else {
            reserva.rejeitar();
        }
        reservas.salvar(reserva);
        return reserva;
    }

    /** RF-02: faixas livres entre 08h e 22h do dia. */
    public List<Faixa> horariosLivres(String recursoId, LocalDate dia) {
        catalogo.obter(recursoId);
        LocalDateTime abre = dia.atTime(LocalTime.of(ABERTURA, 0));
        LocalDateTime fecha = dia.atTime(LocalTime.of(FECHAMENTO, 0));
        List<Reserva> ocupadas = new ArrayList<>(reservas.ativasDoRecurso(recursoId, abre, fecha));
        ocupadas.sort(Comparator.comparing(Reserva::inicio));
        List<Faixa> livres = new ArrayList<>();
        LocalDateTime cursor = abre;
        for (Reserva r : ocupadas) {
            if (r.inicio().isAfter(cursor)) {
                livres.add(new Faixa(cursor, r.inicio()));
            }
            if (r.fim().isAfter(cursor)) {
                cursor = r.fim();
            }
        }
        if (cursor.isBefore(fecha)) {
            livres.add(new Faixa(cursor, fecha));
        }
        return livres;
    }

    /** RF-09. */
    public List<Reserva> minhasReservas(Usuario usuario) {
        List<Reserva> lista = new ArrayList<>(reservas.doUsuario(usuario.matricula()));
        lista.sort(Comparator.comparing(Reserva::inicio));
        return lista;
    }

    private Reserva buscar(long reservaId) {
        return reservas.obter(reservaId)
                .orElseThrow(() -> new NaoEncontrado("reserva " + reservaId + " não existe"));
    }

    private void exigirNaoBloqueado(Usuario usuario, LocalDateTime agora) {
        long tardios = reservas.doUsuario(usuario.matricula()).stream()
                .filter(r -> r.cancelamentoTardio() && r.canceladaEm() != null
                        && Duration.between(r.canceladaEm(), agora).compareTo(JANELA_PENALIDADE) <= 0)
                .count();
        if (tardios >= LIMITE_TARDIOS) {
            throw new UsuarioBloqueado(
                    tardios + " cancelamentos tardios nos últimos 30 dias: novas reservas bloqueadas");
        }
    }
}
