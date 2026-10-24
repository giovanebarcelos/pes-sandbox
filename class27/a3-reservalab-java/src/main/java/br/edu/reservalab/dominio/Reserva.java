package br.edu.reservalab.dominio;

import java.time.Duration;
import java.time.LocalDateTime;

import br.edu.reservalab.dominio.Erros.RegraViolada;
import br.edu.reservalab.dominio.Erros.TransicaoInvalida;

/** Reserva com máquina de estados: o status só muda pelos métodos desta classe. */
public class Reserva {
    public static final Duration LIMITE_CANCELAMENTO_TARDIO = Duration.ofHours(2);

    private Long id;
    private final String recursoId;
    private final String matricula;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private Status status;
    private LocalDateTime canceladaEm;
    private boolean cancelamentoTardio;

    public Reserva(String recursoId, String matricula, LocalDateTime inicio, LocalDateTime fim, Status status) {
        this.recursoId = recursoId;
        this.matricula = matricula;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
    }

    /** Reconstrói uma reserva já gravada (usado pelos repositórios). */
    public static Reserva restaurar(long id, String recursoId, String matricula, LocalDateTime inicio,
                                    LocalDateTime fim, Status status, LocalDateTime canceladaEm, boolean tardio) {
        Reserva r = new Reserva(recursoId, matricula, inicio, fim, status);
        r.id = id;
        r.canceladaEm = canceladaEm;
        r.cancelamentoTardio = tardio;
        return r;
    }

    public Long id() { return id; }
    public void atribuirId(long id) { this.id = id; }
    public String recursoId() { return recursoId; }
    public String matricula() { return matricula; }
    public LocalDateTime inicio() { return inicio; }
    public LocalDateTime fim() { return fim; }
    public Status status() { return status; }
    public LocalDateTime canceladaEm() { return canceladaEm; }
    public boolean cancelamentoTardio() { return cancelamentoTardio; }
    public boolean ativa() { return status.ativo(); }

    /** Intervalos semiabertos [início, fim): reservas encostadas NÃO conflitam. */
    public boolean conflitaCom(LocalDateTime outroInicio, LocalDateTime outroFim) {
        return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
    }

    public void confirmar() {
        exigir(Status.PENDENTE, "confirmar");
        status = Status.CONFIRMADA;
    }

    public void rejeitar() {
        exigir(Status.PENDENTE, "rejeitar");
        status = Status.REJEITADA;
    }

    /** Cancela antes do início; menos de 2h antes conta como tardio. */
    public void cancelar(LocalDateTime agora) {
        if (!ativa()) {
            throw new TransicaoInvalida("reserva " + status + " não pode ser cancelada");
        }
        if (!agora.isBefore(inicio)) {
            throw new RegraViolada("a reserva já começou e não pode ser cancelada");
        }
        cancelamentoTardio = Duration.between(agora, inicio).compareTo(LIMITE_CANCELAMENTO_TARDIO) < 0;
        canceladaEm = agora;
        status = Status.CANCELADA;
    }

    /** Cópia independente, para os repositórios não vazarem estado interno. */
    public Reserva copia() {
        return restaurar(id == null ? 0 : id, recursoId, matricula, inicio, fim, status, canceladaEm, cancelamentoTardio);
    }

    private void exigir(Status esperado, String acao) {
        if (status != esperado) {
            throw new TransicaoInvalida("só reserva " + esperado + " pode " + acao + " (está " + status + ")");
        }
    }
}
