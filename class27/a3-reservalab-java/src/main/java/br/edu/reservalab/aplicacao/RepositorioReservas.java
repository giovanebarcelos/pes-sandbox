package br.edu.reservalab.aplicacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.edu.reservalab.dominio.Reserva;

/** Porta que a aplicação exige da persistência. */
public interface RepositorioReservas {

    /**
     * Grava a reserva SE não houver conflito, de forma atômica.
     *
     * <p>Verificar e gravar em chamadas separadas permitiria duas reservas simultâneas
     * no mesmo horário (condição de corrida); por isso é uma operação só (ADR-002).
     *
     * @throws br.edu.reservalab.dominio.Erros.ConflitoDeHorario já existe reserva ativa sobrepondo o intervalo
     */
    Reserva adicionarSemConflito(Reserva reserva);

    Optional<Reserva> obter(long reservaId);

    void salvar(Reserva reserva);

    List<Reserva> ativasDoRecurso(String recursoId, LocalDateTime inicio, LocalDateTime fim);

    List<Reserva> doUsuario(String matricula);
}
