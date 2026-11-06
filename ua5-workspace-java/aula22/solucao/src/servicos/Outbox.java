package servicos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * OUTBOX — comunicação ASSÍNCRONA com nova tentativa.
 *
 * O caso de uso grava o que precisa ser avisado/feito em outro serviço numa
 * fila local e responde ao usuário NA HORA. Um "entregador" em segundo plano
 * tenta enviar; se o outro serviço estiver fora do ar, tenta de novo depois.
 * Resultado: Notificação parada NÃO impede uma locação (sem falha em cascata).
 */
public class Outbox {
    /** Entrega uma tarefa; deve lançar exceção se não conseguiu. */
    @FunctionalInterface
    public interface Entregador {
        void entregar(Map<String, Object> tarefa) throws Exception;
    }

    private final Entregador entregador;
    private final long intervaloMs;
    private final List<Map<String, Object>> pendentes = new ArrayList<>();
    private final Set<Object> ids = new HashSet<>();
    private volatile String ultimoErro;

    public Outbox(Entregador entregador, long intervaloMs) {
        this.entregador = entregador;
        this.intervaloMs = intervaloMs;
    }

    public synchronized void adicionar(Map<String, Object> tarefa) {
        if (ids.add(tarefa.get("id"))) {
            pendentes.add(tarefa);
        }
    }

    public synchronized List<Map<String, Object>> pendentes() {
        return new ArrayList<>(pendentes);
    }

    public String ultimoErro() {
        return ultimoErro;
    }

    /** Tenta entregar tudo o que está na fila; devolve quantas tarefas continuam pendentes. */
    public int entregarPendentes() {
        for (Map<String, Object> tarefa : pendentes()) {
            try {
                entregador.entregar(tarefa);
            } catch (Exception e) { // fica na fila para a próxima rodada
                ultimoErro = tarefa.get("id") + ": " + e.getClass().getSimpleName()
                        + (e.getMessage() != null ? " " + e.getMessage() : "");
                continue;
            }
            synchronized (this) {
                pendentes.remove(tarefa);
            }
        }
        int restantes = pendentes().size();
        if (restantes == 0) {
            ultimoErro = null;
        }
        return restantes;
    }

    public Outbox iniciar() {
        Thread t = new Thread(() -> {
            while (true) {
                entregarPendentes();
                try {
                    Thread.sleep(intervaloMs);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "outbox");
        t.setDaemon(true);
        t.start();
        return this;
    }
}
