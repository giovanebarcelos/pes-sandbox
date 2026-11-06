package locadora.compartilhado;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Barramento de eventos em memória (Observer da Aula 18, agora entre contextos).
 * Quem publica não conhece quem escuta.
 */
public class BarramentoEventos {
    private record Inscricao(Class<?> tipo, Consumer<EventoDominio> funcao) {
    }

    private final List<Inscricao> inscritos = new CopyOnWriteArrayList<>();

    /** Inscreve uma função para um tipo de evento. Use EventoDominio.class para receber todos. */
    @SuppressWarnings("unchecked")
    public <T extends EventoDominio> void inscrever(Class<T> tipo, Consumer<? super T> funcao) {
        inscritos.add(new Inscricao(tipo, e -> ((Consumer<T>) funcao).accept((T) e)));
    }

    public void publicar(List<? extends EventoDominio> eventos) {
        for (EventoDominio e : eventos) {
            for (Inscricao i : inscritos) {
                if (i.tipo().isInstance(e)) {
                    i.funcao().accept(e);
                }
            }
        }
    }
}
