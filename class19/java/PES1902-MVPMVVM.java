/*
 * PES1902 - Padrões MVP e MVVM (Model-View-Presenter / Model-View-ViewModel)
 * Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra MVP e MVVM com um app de lista de tarefas (Todo).
 * - MVP: View passiva, Presenter orquestra toda lógica de apresentação
 * - MVVM: ViewModel expõe estado + comandos, View faz data binding
 *
 * Compilar/executar:
 *   javac PES1902-MVPMVVM.java && java MVPMVVM
 */

import java.util.*;

// ============================================================
// MODEL — Compartilhado entre MVP e MVVM
// ============================================================

class Tarefa {
    private static int contador = 0;
    final int id;
    final String descricao;
    boolean concluida;

    Tarefa(String descricao) {
        this.id = ++contador;
        this.descricao = descricao;
        this.concluida = false;
    }

    void marcarConcluida() { this.concluida = true; }

    @Override
    public String toString() {
        return String.format("[%s] #%d %s", concluida ? "✅" : "⬜", id, descricao);
    }
}

class TarefaRepository {
    private final List<Tarefa> tarefas = new ArrayList<>();

    Tarefa adicionar(String descricao) {
        Tarefa t = new Tarefa(descricao);
        tarefas.add(t);
        return t;
    }

    List<Tarefa> listar() { return Collections.unmodifiableList(tarefas); }

    Tarefa concluir(int id) {
        for (Tarefa t : tarefas) {
            if (t.id == id) { t.marcarConcluida(); return t; }
        }
        return null;
    }

    int total() { return tarefas.size(); }
    int concluidas() { return (int) tarefas.stream().filter(t -> t.concluida).count(); }
}

// ============================================================
// 1. MVP — Model-View-Presenter
// ============================================================

interface ViewMVP {
    void mostrarTarefas(List<Tarefa> tarefas);
    void mostrarMensagem(String msg);
}

class ConsoleViewMVP implements ViewMVP {
    public void mostrarTarefas(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) System.out.println("    (nenhuma tarefa)");
        else tarefas.forEach(t -> System.out.println("    " + t));
    }

    public void mostrarMensagem(String msg) {
        System.out.println("    → " + msg);
    }
}

class TarefaPresenter {
    private final ViewMVP view;
    private final TarefaRepository model;

    TarefaPresenter(ViewMVP view, TarefaRepository model) {
        this.view = view;
        this.model = model;
    }

    void adicionarTarefa(String descricao) {
        Tarefa t = model.adicionar(descricao);
        view.mostrarMensagem("Tarefa '#" + t.id + "' adicionada (MVP).");
    }

    void listarTarefas() { view.mostrarTarefas(model.listar()); }

    void concluirTarefa(int id) {
        Tarefa t = model.concluir(id);
        if (t != null) view.mostrarMensagem("Tarefa '#" + t.id + "' concluída (MVP).");
        else view.mostrarMensagem("Tarefa #" + id + " não encontrada.");
    }

    void mostrarResumo() {
        view.mostrarMensagem("Resumo: " + model.concluidas() + "/" + model.total() + " tarefas concluídas.");
    }
}

// ============================================================
// 2. MVVM — Model-View-ViewModel
// ============================================================

interface ObservableListener {
    void onChange();
}

class Observable {
    private final List<ObservableListener> listeners = new ArrayList<>();

    void bind(ObservableListener listener) { listeners.add(listener); }

    void notifyChange() { listeners.forEach(ObservableListener::onChange); }
}

class TarefaViewModel {
    private final TarefaRepository model;
    final Observable observable = new Observable();

    TarefaViewModel(TarefaRepository model) { this.model = model; }

    void adicionarTarefa(String descricao) {
        model.adicionar(descricao);
        observable.notifyChange();
    }

    void concluirTarefa(int id) {
        model.concluir(id);
        observable.notifyChange();
    }

    List<Tarefa> getTarefas() { return model.listar(); }

    String getResumo() { return model.concluidas() + "/" + model.total() + " tarefas concluídas."; }
}

class ConsoleViewMVVM implements ObservableListener {
    private final TarefaViewModel vm;

    ConsoleViewMVVM(TarefaViewModel vm) {
        this.vm = vm;
        vm.observable.bind(this);
    }

    void render() {
        System.out.println("    ── MVVM (data binding) ──");
        List<Tarefa> tarefas = vm.getTarefas();
        if (tarefas.isEmpty()) System.out.println("    (nenhuma tarefa)");
        else tarefas.forEach(t -> System.out.println("    " + t));
        System.out.println("    📊 " + vm.getResumo());
    }

    @Override
    public void onChange() { render(); }
}

// ============================================================
// DEMONSTRAÇÃO
// ============================================================

class MVPMVVM {

    static void demoMVP() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  1. MVP — Model-View-Presenter");
        System.out.println("─".repeat(55));

        TarefaRepository model = new TarefaRepository();
        ConsoleViewMVP view = new ConsoleViewMVP();
        TarefaPresenter presenter = new TarefaPresenter(view, model);

        presenter.adicionarTarefa("Estudar diagramas UML");
        presenter.adicionarTarefa("Implementar MVP em Java");
        presenter.adicionarTarefa("Revisar padrões de projeto");
        presenter.listarTarefas();
        presenter.concluirTarefa(1);
        presenter.listarTarefas();
        presenter.mostrarResumo();
    }

    static void demoMVVM() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  2. MVVM — Model-View-ViewModel (com data binding)");
        System.out.println("─".repeat(55));

        TarefaRepository model = new TarefaRepository();
        TarefaViewModel vm = new TarefaViewModel(model);
        ConsoleViewMVVM view = new ConsoleViewMVVM(vm);

        System.out.println("    Adicionando tarefas...");
        vm.adicionarTarefa("Preparar apresentação sobre MVVM");
        vm.adicionarTarefa("Configurar ambiente de desenvolvimento");
        System.out.println("    Concluindo tarefa #1...");
        vm.concluirTarefa(1);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(55));
        System.out.println("  MVP & MVVM — LISTA DE TAREFAS");
        System.out.println("=".repeat(55));

        demoMVP();
        demoMVVM();

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  ✓ MVP: View passiva → Presenter orquestra tudo");
        System.out.println("  ✓ MVVM: ViewModel observável + data binding → View reage");
        System.out.println("  ✓ Ambos separam responsabilidades e facilitam testes");
        System.out.println("=".repeat(55));
    }
}
