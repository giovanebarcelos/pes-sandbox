"""
PES1902 - Padrão MVP (Model-View-Presenter)
Aula 19: MVC, MVP e MVVM
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa o padrão MVP com uma lista de tarefas, tornando a View
completamente passiva: ela não conhece o Model, e todo o fluxo de
apresentação é orquestrado pelo Presenter.
"""

from abc import ABC, abstractmethod


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
# Contrato que a interface real (GUI/CLI) deve implementar.
# O Presenter só conhece essa interface, nunca a implementação concreta.
class TarefaViewInterface(ABC):
    @abstractmethod
    def exibir_disponiveis(self, tarefas):
        pass

    @abstractmethod
    def exibir_mensagem(self, msg):
        pass


class TarefaViewConsole(TarefaViewInterface):
    def exibir_disponiveis(self, tarefas):
        print("\n--- Lista de Tarefas ---")
        if not tarefas:
            print("  (vazia)")
            return
        for t in tarefas:
            print(f"  {t}")
        print(f"  Total: {len(tarefas)} tarefa(s)")

    def exibir_mensagem(self, msg):
        print(f"  {msg}")


# --- Presenter ---
# Toda a lógica de apresentação vive aqui. A View não conhece o Model;
# quem faz a ponte entre os dois é sempre o Presenter.
class TarefaPresenter:
    def __init__(self, model, view: TarefaViewInterface):
        self.model = model
        self.view = view

    def ao_adicionar(self, descricao):
        t = self.model.adicionar(descricao)
        self.view.exibir_mensagem(f"Tarefa #{t.id} adicionada.")

    def ao_solicitar_disponiveis(self):
        self.view.exibir_disponiveis(self.model.listar())

    def ao_concluir(self, id_tarefa):
        t = self.model.concluir(id_tarefa)
        if t:
            self.view.exibir_mensagem(f"Tarefa #{t.id} concluída!")
        else:
            self.view.exibir_mensagem(f"Tarefa #{id_tarefa} não encontrada.")


if __name__ == "__main__":
    print("=" * 50)
    print("  MVP — LISTA DE TAREFAS")
    print("=" * 50)

    presenter = TarefaPresenter(TarefaModel(), TarefaViewConsole())
    presenter.ao_adicionar("Estudar padrão MVP")
    presenter.ao_adicionar("Implementar Presenter em Python")
    presenter.ao_solicitar_disponiveis()
    presenter.ao_concluir(1)
    presenter.ao_solicitar_disponiveis()

    print("\n✓ MVP: View passiva, Presenter concentra toda a lógica de apresentação.")
    print("✓ Diferença-chave do MVC: a View não conhece o Model.")
