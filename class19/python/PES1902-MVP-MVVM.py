"""
PES1902 - Padrões MVP e MVVM (Model-View-Presenter / Model-View-ViewModel)
Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra MVP e MVVM com um app de lista de tarefas (Todo).
- MVP: View passiva, Presenter orquestra toda lógica de apresentação
- MVVM: ViewModel expõe estado + comandos, View faz data binding
"""

from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List, Callable


# ============================================================
# MODEL — Compartilhado entre MVP e MVVM
# ============================================================

class Tarefa:
    """Entidade simples de tarefa."""

    _contador: int = 0

    def __init__(self, descricao: str):
        Tarefa._contador += 1
        self.id: int = Tarefa._contador
        self.descricao: str = descricao
        self.concluida: bool = False

    def marcar_concluida(self) -> None:
        self.concluida = True

    def __repr__(self) -> str:
        status = "✅" if self.concluida else "⬜"
        return f"[{status}] #{self.id} {self.descricao}"


class TarefaRepository:
    """Model: gerencia a coleção de tarefas (dados + regras)."""

    def __init__(self):
        self._tarefas: List[Tarefa] = []

    def adicionar(self, descricao: str) -> Tarefa:
        t = Tarefa(descricao)
        self._tarefas.append(t)
        return t

    def listar(self) -> List[Tarefa]:
        return list(self._tarefas)

    def concluir(self, id_tarefa: int) -> Tarefa | None:
        for t in self._tarefas:
            if t.id == id_tarefa:
                t.marcar_concluida()
                return t
        return None

    @property
    def total(self) -> int:
        return len(self._tarefas)

    @property
    def concluidas(self) -> int:
        return sum(1 for t in self._tarefas if t.concluida)


# ============================================================
# 1. MVP — Model-View-Presenter
# ============================================================

class ViewMVP(ABC):
    """View passiva — apenas exibe, não tem lógica."""

    @abstractmethod
    def mostrar_tarefas(self, tarefas: List[Tarefa]) -> None:
        ...

    @abstractmethod
    def mostrar_mensagem(self, msg: str) -> None:
        ...


class ConsoleViewMVP(ViewMVP):
    """Implementação console da View MVP."""

    def mostrar_tarefas(self, tarefas: List[Tarefa]) -> None:
        if not tarefas:
            print("    (nenhuma tarefa)")
        for t in tarefas:
            print(f"    {t}")

    def mostrar_mensagem(self, msg: str) -> None:
        print(f"    → {msg}")


class TarefaPresenter:
    """Presenter: toda lógica de apresentação fica aqui."""

    def __init__(self, view: ViewMVP, model: TarefaRepository):
        self._view = view
        self._model = model

    def adicionar_tarefa(self, descricao: str) -> None:
        t = self._model.adicionar(descricao)
        self._view.mostrar_mensagem(f"Tarefa '#{t.id}' adicionada (MVP).")

    def listar_tarefas(self) -> None:
        self._view.mostrar_tarefas(self._model.listar())

    def concluir_tarefa(self, id_tarefa: int) -> None:
        t = self._model.concluir(id_tarefa)
        if t:
            self._view.mostrar_mensagem(f"Tarefa '#{t.id}' concluída (MVP).")
        else:
            self._view.mostrar_mensagem(f"Tarefa #{id_tarefa} não encontrada.")

    def mostrar_resumo(self) -> None:
        self._view.mostrar_mensagem(
            f"Resumo: {self._model.concluidas}/{self._model.total} tarefas concluídas."
        )


# ============================================================
# 2. MVVM — Model-View-ViewModel
# ============================================================

class Observable:
    """Mini sistema de data binding — notifica listeners."""

    def __init__(self):
        self._listeners: List[Callable] = []

    def bind(self, listener: Callable) -> None:
        """Registra callback para quando o estado mudar."""
        self._listeners.append(listener)

    def notify(self) -> None:
        for listener in self._listeners:
            listener()


class TarefaViewModel:
    """ViewModel: estado observável + comandos."""

    def __init__(self, model: TarefaRepository):
        self._model = model
        self.observable = Observable()

    def adicionar_tarefa(self, descricao: str) -> None:
        self._model.adicionar(descricao)
        self.observable.notify()

    def concluir_tarefa(self, id_tarefa: int) -> None:
        self._model.concluir(id_tarefa)
        self.observable.notify()

    @property
    def tarefas(self) -> List[Tarefa]:
        return self._model.listar()

    @property
    def resumo(self) -> str:
        return f"{self._model.concluidas}/{self._model.total} tarefas concluídas."


class ConsoleViewMVVM:
    """View MVVM — faz data binding com o ViewModel."""

    def __init__(self, viewmodel: TarefaViewModel):
        self._vm = viewmodel
        self._vm.observable.bind(self._render)

    def _render(self) -> None:
        """Re-renderiza toda vez que o ViewModel notifica mudança."""
        print("    ── MVVM (data binding) ──")
        if not self._vm.tarefas:
            print("    (nenhuma tarefa)")
        for t in self._vm.tarefas:
            print(f"    {t}")
        print(f"    📊 {self._vm.resumo}")


# ============================================================
# DEMONSTRAÇÃO
# ============================================================

def demo_mvp():
    print("\n" + "─" * 55)
    print("  1. MVP — Model-View-Presenter")
    print("─" * 55)
    model = TarefaRepository()
    view = ConsoleViewMVP()
    presenter = TarefaPresenter(view, model)

    presenter.adicionar_tarefa("Estudar diagramas UML")
    presenter.adicionar_tarefa("Implementar MVP em Python")
    presenter.adicionar_tarefa("Revisar padrões de projeto")
    presenter.listar_tarefas()
    presenter.concluir_tarefa(1)
    presenter.listar_tarefas()
    presenter.mostrar_resumo()


def demo_mvvm():
    print("\n" + "─" * 55)
    print("  2. MVVM — Model-View-ViewModel (com data binding)")
    print("─" * 55)
    model = TarefaRepository()
    vm = TarefaViewModel(model)
    view = ConsoleViewMVVM(vm)

    print("    Adicionando tarefas...")
    vm.adicionar_tarefa("Preparar apresentação sobre MVVM")
    vm.adicionar_tarefa("Configurar ambiente de desenvolvimento")
    print("    Concluindo tarefa #1...")
    vm.concluir_tarefa(1)


if __name__ == "__main__":
    print("=" * 55)
    print("  MVP & MVVM — LISTA DE TAREFAS")
    print("=" * 55)
    demo_mvp()
    demo_mvvm()

    print("\n" + "=" * 55)
    print("  ✓ MVP: View passiva → Presenter orquestra tudo")
    print("  ✓ MVVM: ViewModel observável + data binding → View reage")
    print("  ✓ Ambos separam responsabilidades e facilitam testes")
    print("=" * 55)
