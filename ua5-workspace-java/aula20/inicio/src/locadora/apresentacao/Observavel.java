package locadora.apresentacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Observer (GoF, Aula 18): guarda um valor e avisa os inscritos quando ele muda.
 * É o "data binding feito à mão" do MVVM (sem JavaFX, Android Jetpack ou Angular).
 */
public class Observavel<T> {
    private T valor;
    private final List<Consumer<T>> inscritos = new ArrayList<>();

    public Observavel(T valorInicial) {
        this.valor = valorInicial;
    }

    /** Inscreve e já entrega o valor atual (a tela nasce desenhada). */
    public void inscrever(Consumer<T> funcao) {
        inscritos.add(funcao);
        funcao.accept(valor);
    }

    public T get() {
        return valor;
    }

    public void set(T novo) {
        if (!Objects.equals(novo, valor)) {
            valor = novo;
            for (Consumer<T> f : inscritos) {
                f.accept(novo);
            }
        }
    }
}
