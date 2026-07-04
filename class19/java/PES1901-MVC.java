/*
 * PES1901 - Padrão MVC (Model-View-Controller)
 * Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa o padrão MVC com uma lista de tarefas.
 *
 * Compilar/executar:
 *   javac PES1901-MVC.java && java MVC
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

class TarefaView {
    void listar(List<Tarefa> tarefas) {
        System.out.println("\n--- Lista de Tarefas ---");
        if (tarefas.isEmpty()) {
            System.out.println("  (vazia)");
            return;
        }
        for (Tarefa t : tarefas) System.out.println("  " + t);
        System.out.println("  Total: " + tarefas.size() + " tarefa(s)");
    }

    void mensagem(String texto) {
        System.out.println("  " + texto);
    }
}

class TarefaController {
    private TarefaModel model = new TarefaModel();
    private TarefaView view = new TarefaView();

    void adicionar(String descricao) {
        Tarefa t = model.adicionar(descricao);
        view.mensagem("Tarefa #" + t.id + " adicionada.");
    }

    void listar() { view.listar(model.listar()); }

    void concluir(int id) {
        Tarefa t = model.concluir(id);
        if (t != null) view.mensagem("Tarefa #" + id + " concluída!");
        else view.mensagem("Tarefa #" + id + " não encontrada.");
    }
}

class MVC {
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  MVC — LISTA DE TAREFAS");
        System.out.println("=".repeat(50));

        TarefaController controller = new TarefaController();
        controller.adicionar("Estudar diagrama de classes UML");
        controller.adicionar("Implementar padrão MVC em Java");
        controller.listar();
        controller.concluir(1);
        controller.listar();

        System.out.println("\n✓ MVC: Model (dados), View (apresentação), Controller (lógica).");
    }
}
