"""
PES1903 - Padrão MVVM (Model-View-ViewModel)
Aula 19: MVC, MVP e MVVM
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa o padrão MVVM com uma lista de tarefas. Sem um framework de
data binding real, o Python expõe o estado do ViewModel e notifica
observadores (aqui simplificado com uma lista de callbacks), simulando
o mecanismo de binding automático de frameworks como Angular ou WPF.
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


# --- ViewModel ---
class TarefaViewModel:
    """Expõe o estado da UI (tarefas) e notifica observadores quando muda.
    Não conhece a View — quem se inscreve para ser notificado é ela."""

    def __init__(self, model):
        self.model = model
        self.tarefas = []
        self._observadores = []

    def observar(self, callback):
        self._observadores.append(callback)

    def _notificar(self):
        for callback in self._observadores:
            callback(self.tarefas)

    def carregar(self):
        self.tarefas = self.model.listar()
        self._notificar()

    def adicionar(self, descricao):
        self.model.adicionar(descricao)
        self.carregar()

    def concluir(self, id_tarefa):
        self.model.concluir(id_tarefa)
        self.carregar()


# --- View (bindada ao ViewModel via callback) ---
def renderizar_lista(tarefas):
    print("\n--- Lista de Tarefas (data binding) ---")
    if not tarefas:
        print("  (vazia)")
        return
    for t in tarefas:
        print(f"  {t}")
    print(f"  Total: {len(tarefas)} tarefa(s)")


if __name__ == "__main__":
    print("=" * 50)
    print("  MVVM — LISTA DE TAREFAS")
    print("=" * 50)

    view_model = TarefaViewModel(TarefaModel())
    view_model.observar(renderizar_lista)  # simula o data binding

    view_model.adicionar("Estudar padrão MVVM")
    view_model.adicionar("Implementar ViewModel em Python")
    view_model.concluir(1)

    print("\n✓ MVVM: ViewModel notifica observadores — a View se atualiza sozinha.")
    print("✓ Diferença-chave: o ViewModel não conhece a View concreta.")
