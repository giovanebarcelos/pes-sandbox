package br.edu.reservalab.infra;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.dominio.Erros.ConflitoDeHorario;
import br.edu.reservalab.dominio.Reserva;

/** Adaptador em memória: usado nos testes e como referência de comportamento. */
public class RepositorioMemoria implements RepositorioReservas {
    private final Map<Long, Reserva> porId = new HashMap<>();
    private final Map<String, List<Reserva>> porRecurso = new HashMap<>();
    private long proximo = 1;

    @Override
    public synchronized Reserva adicionarSemConflito(Reserva reserva) {
        for (Reserva existente : porRecurso.getOrDefault(reserva.recursoId(), List.of())) {
            if (existente.ativa() && existente.conflitaCom(reserva.inicio(), reserva.fim())) {
                throw new ConflitoDeHorario(String.format("%s já reservado de %tR a %tR",
                        reserva.recursoId(), existente.inicio(), existente.fim()));
            }
        }
        reserva.atribuirId(proximo++);
        Reserva guardada = reserva.copia();
        porId.put(guardada.id(), guardada);
        porRecurso.computeIfAbsent(reserva.recursoId(), k -> new ArrayList<>()).add(guardada);
        return reserva;
    }

    @Override
    public synchronized Optional<Reserva> obter(long reservaId) {
        return Optional.ofNullable(porId.get(reservaId)).map(Reserva::copia);
    }

    @Override
    public synchronized void salvar(Reserva reserva) {
        // substitui a guardada (e a referência na lista por recurso) pelo novo estado
        Reserva antiga = porId.get(reserva.id());
        Reserva nova = reserva.copia();
        porId.put(nova.id(), nova);
        List<Reserva> lista = porRecurso.get(antiga.recursoId());
        lista.set(lista.indexOf(antiga), nova);
    }

    @Override
    public synchronized List<Reserva> ativasDoRecurso(String recursoId, LocalDateTime inicio, LocalDateTime fim) {
        return porRecurso.getOrDefault(recursoId, List.of()).stream()
                .filter(r -> r.ativa() && r.conflitaCom(inicio, fim)).map(Reserva::copia).toList();
    }

    @Override
    public synchronized List<Reserva> doUsuario(String matricula) {
        return porId.values().stream().filter(r -> r.matricula().equals(matricula)).map(Reserva::copia).toList();
    }
}
