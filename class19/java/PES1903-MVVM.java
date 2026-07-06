/*
 * PES1903 - Padrão MVVM (Model-View-ViewModel)
 * Aula 19: MVC, MVP e MVVM
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa o padrão MVVM com uma lista de tarefas. Em frameworks reais
 * (Angular, WPF, SwiftUI), o data binding é automático — aqui o mecanismo
 * é simulado com uma lista de observadores (Consumer), para fins didáticos.
 *
 * Compilar/executar:
 *   javac PES1903-MVVM.java && java MVVM
 */

import java.util.*;
import java.util.function.Consumer;

class Tarefa {
    int id;
    String descricao;
    boolean concluida;

    Tarefa(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
        this.concluida = false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", concluida ? "✓" : "☐", descricao);
    }
}

class TarefaModel {
    private List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    Tarefa adicionar(String descricao) {
        Tarefa t = new Tarefa(proximoId++, descricao);
        tarefas.add(t);
        return t;
    }

    List<Tarefa> listar() {
        return Collections.unmodifiableList(tarefas);
    }

    Tarefa concluir(int id) {
        for (Tarefa t : tarefas) {
            if (t.id == id) { t.concluida = true; return t; }
        }
        return null;
    }
}

// VIEWMODEL: expõe o estado da UI e notifica observadores quando muda.
// Não conhece a View concreta — quem se inscreve é notificado.
class TarefaViewModel {
    private final TarefaModel model;
    private List<Tarefa> tarefas = new ArrayList<>();
    private final List<Consumer<List<Tarefa>>> observadores = new ArrayList<>();

    TarefaViewModel(TarefaModel model) {
        this.model = model;
    }

    void observar(Consumer<List<Tarefa>> callback) {
        observadores.add(callback);
    }

    private void notificar() {
        for (Consumer<List<Tarefa>> callback : observadores) {
            callback.accept(tarefas);
        }
    }

    void carregar() {
        tarefas = model.listar();
        notificar();
    }

    void adicionar(String descricao) {
        model.adicionar(descricao);
        carregar();
    }

    void concluir(int id) {
        model.concluir(id);
        carregar();
    }
}

class MVVM {
    static void renderizarLista(List<Tarefa> tarefas) {
        System.out.println("\n--- Lista de Tarefas (data binding) ---");
        if (tarefas.isEmpty()) {
            System.out.println("  (vazia)");
            return;
        }
        for (Tarefa t : tarefas) System.out.println("  " + t);
        System.out.println("  Total: " + tarefas.size() + " tarefa(s)");
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  MVVM — LISTA DE TAREFAS");
        System.out.println("=".repeat(50));

        TarefaViewModel viewModel = new TarefaViewModel(new TarefaModel());
        viewModel.observar(MVVM::renderizarLista); // simula o data binding

        viewModel.adicionar("Estudar padrão MVVM");
        viewModel.adicionar("Implementar ViewModel em Java");
        viewModel.concluir(1);

        System.out.println("\n✓ MVVM: ViewModel notifica observadores — a View se atualiza sozinha.");
        System.out.println("✓ Diferença-chave: o ViewModel não conhece a View concreta.");
    }
}
