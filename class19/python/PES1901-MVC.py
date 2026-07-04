"""
PES1901 - Padrão MVC (Model-View-Controller)
Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa o padrão MVC com uma lista de tarefas,
separando Model (dados), View (apresentação) e Controller (lógica).
"""


# --- Model ---
class Tarefa:
    def __init__(self, id_tarefa, descricao, concluida=False):
        self.id = id_tarefa
        self.descricao = descricao
        self.concluida = concluida

    def __repr__(self):
        status = "✓" if self.concluida else "☐"
        return f"[{status}] {self.descricao}"


class TarefaModel:
    def __init__(self):
        self._tarefas = []
        self._proximo_id = 1

    def adicionar(self, descricao):
        t = Tarefa(self._proximo_id, descricao)
        self._tarefas.append(t)
        self._proximo_id += 1
        return t

    def listar(self):
        return list(self._tarefas)

    def concluir(self, id_tarefa):
        for t in self._tarefas:
            if t.id == id_tarefa:
                t.concluida = True
                return t
        return None


# --- View ---
class TarefaView:
    @staticmethod
    def listar(tarefas):
        print("\n--- Lista de Tarefas ---")
        if not tarefas:
            print("  (vazia)")
            return
        for t in tarefas:
            print(f"  {t}")
        print(f"  Total: {len(tarefas)} tarefa(s)")

    @staticmethod
    def mensagem(texto):
        print(f"  {texto}")


# --- Controller ---
class TarefaController:
    def __init__(self):
        self.model = TarefaModel()
        self.view = TarefaView()

    def adicionar(self, descricao):
        t = self.model.adicionar(descricao)
        self.view.mensagem(f"Tarefa #{t.id} adicionada.")

    def listar(self):
        self.view.listar(self.model.listar())

    def concluir(self, id_tarefa):
        t = self.model.concluir(id_tarefa)
        if t:
            self.view.mensagem(f"Tarefa #{t.id} concluída!")
        else:
            self.view.mensagem(f"Tarefa #{id_tarefa} não encontrada.")


if __name__ == "__main__":
    print("=" * 50)
    print("  MVC — LISTA DE TAREFAS")
    print("=" * 50)

    controller = TarefaController()
    controller.adicionar("Estudar diagrama de classes UML")
    controller.adicionar("Implementar padrão MVC em Python")
    controller.listar()
    controller.concluir(1)
    controller.listar()

    print("\n✓ MVC: Model (dados), View (apresentação), Controller (lógica).")
