/*
 * PES1902 - Padrão MVP (Model-View-Presenter)
 * Aula 19: MVC, MVP e MVVM
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa o padrão MVP com uma lista de tarefas, tornando a View
 * completamente passiva: ela não conhece o Model, e todo o fluxo de
 * apresentação é orquestrado pelo Presenter.
 *
 * Compilar/executar:
 *   javac PES1902-MVP.java && java MVP
 */

import java.util.*;

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

// VIEW: contrato que a interface real (GUI/CLI) deve implementar.
// O Presenter só conhece essa interface, nunca a implementação concreta.
interface TarefaViewInterface {
    void exibirDisponiveis(List<Tarefa> tarefas);
    void exibirMensagem(String msg);
}

class TarefaViewConsole implements TarefaViewInterface {
    @Override
    public void exibirDisponiveis(List<Tarefa> tarefas) {
        System.out.println("\n--- Lista de Tarefas ---");
        if (tarefas.isEmpty()) {
            System.out.println("  (vazia)");
            return;
        }
        for (Tarefa t : tarefas) System.out.println("  " + t);
        System.out.println("  Total: " + tarefas.size() + " tarefa(s)");
    }

    @Override
    public void exibirMensagem(String msg) {
        System.out.println("  " + msg);
    }
}

// PRESENTER: toda a lógica de apresentação, sem conhecer a UI concreta.
// A View não conhece o Model — o Presenter é o intermediário total.
class TarefaPresenter {
    private final TarefaModel model;
    private final TarefaViewInterface view;

    TarefaPresenter(TarefaModel model, TarefaViewInterface view) {
        this.model = model;
        this.view = view;
    }

    void aoAdicionar(String descricao) {
        Tarefa t = model.adicionar(descricao);
        view.exibirMensagem("Tarefa #" + t.id + " adicionada.");
    }

    void aoSolicitarDisponiveis() {
        view.exibirDisponiveis(model.listar());
    }

    void aoConcluir(int id) {
        Tarefa t = model.concluir(id);
        if (t != null) view.exibirMensagem("Tarefa #" + id + " concluída!");
        else view.exibirMensagem("Tarefa #" + id + " não encontrada.");
    }
}

class MVP {
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  MVP — LISTA DE TAREFAS");
        System.out.println("=".repeat(50));

        TarefaPresenter presenter = new TarefaPresenter(new TarefaModel(), new TarefaViewConsole());
        presenter.aoAdicionar("Estudar padrão MVP");
        presenter.aoAdicionar("Implementar Presenter em Java");
        presenter.aoSolicitarDisponiveis();
        presenter.aoConcluir(1);
        presenter.aoSolicitarDisponiveis();

        System.out.println("\n✓ MVP: View passiva, Presenter concentra toda a lógica de apresentação.");
        System.out.println("✓ Diferença-chave do MVC: a View não conhece o Model.");
    }
}
