"""
PES1903 - Clean, Onion e Hexagonal: Comparativo com o Mesmo Domínio
Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Exemplo comparativo: o mesmo domínio (Pedido) estruturado em três
estilos arquiteturais — Clean, Onion e Hexagonal — mostrando como
cada um organiza as dependências e camadas.

Domínio comum: Gerenciamento de pedidos com ItemPedido e cálculo de total.
"""

from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List


# ============================================================
# DOMÍNIO COMPARTILHADO (entidades, value objects, regras)
# ============================================================

@dataclass(frozen=True)
class Dinheiro:
    """Value Object imutável."""
    valor: float

    def somar(self, outro: "Dinheiro") -> "Dinheiro":
        return Dinheiro(self.valor + outro.valor)

    def __repr__(self) -> str:
        return f"R$ {self.valor:,.2f}"


@dataclass(frozen=True)
class ItemPedido:
    """Entity — com identidade de produto."""
    produto_id: str
    nome: str
    preco: Dinheiro
    quantidade: int

    def subtotal(self) -> Dinheiro:
        return Dinheiro(self.preco.valor * self.quantidade)


class Pedido:
    """Aggregate Root — entidade raiz do agregado."""

    def __init__(self, pedido_id: int, cliente: str):
        self.pedido_id = pedido_id
        self.cliente = cliente
        self._itens: List[ItemPedido] = []
        self._status = "ABERTO"

    def adicionar_item(self, produto_id: str, nome: str, preco: float, quantidade: int) -> None:
        self._itens.append(ItemPedido(produto_id, nome, Dinheiro(preco), quantidade))

    def total(self) -> Dinheiro:
        t = Dinheiro(0.0)
        for item in self._itens:
            t = t.somar(item.subtotal())
        return t

    @property
    def status(self) -> str:
        return self._status

    @property
    def itens(self) -> List[ItemPedido]:
        return list(self._itens)

    def fechar(self) -> None:
        if not self._itens:
            raise ValueError("Pedido sem itens não pode ser fechado.")
        self._status = "FECHADO"


# ============================================================
# INTERFACE DE REPOSITÓRIO (porta — usada por todos os estilos)
# ============================================================

class PedidoRepository(ABC):
    """Porta: abstração de persistência (usada em todos os estilos)."""

    @abstractmethod
    def salvar(self, pedido: Pedido) -> None:
        ...

    @abstractmethod
    def buscar_por_id(self, pedido_id: int) -> Pedido | None:
        ...


# ============================================================
# 1. CLEAN ARCHITECTURE
#    Estrutura: Entities → Use Cases → Interface Adapters → Frameworks
# ============================================================

# -- Caso de Uso (Application Layer) --
class CriarPedidoUseCase:
    """Caso de uso: orquestra a criação de pedido."""

    def __init__(self, repo: PedidoRepository):
        self._repo = repo

    def executar(self, pedido_id: int, cliente: str, itens: List[tuple]) -> Pedido:
        pedido = Pedido(pedido_id, cliente)
        for prod_id, nome, preco, qtd in itens:
            pedido.adicionar_item(prod_id, nome, preco, qtd)
        pedido.fechar()
        self._repo.salvar(pedido)
        return pedido


# -- Adaptador de Interface (Controller) --
class PedidoController:
    """Controller: adapta entrada externa para o caso de uso."""

    def __init__(self, usecase: CriarPedidoUseCase):
        self._usecase = usecase

    def criar_pedido(self, pedido_id: int, cliente: str, itens: List[dict]) -> dict:
        tuplas = [(i["id"], i["nome"], i["preco"], i["qtd"]) for i in itens]
        pedido = self._usecase.executar(pedido_id, cliente, tuplas)
        return {
            "id": pedido.pedido_id,
            "cliente": pedido.cliente,
            "total": str(pedido.total()),
            "status": pedido.status,
        }


# -- Adaptador de Framework (Repository concreto) --
class PedidoRepositoryEmMemoria(PedidoRepository):
    """Implementação concreta (em memória) — camada de infraestrutura."""

    def __init__(self):
        self._db: dict[int, Pedido] = {}

    def salvar(self, pedido: Pedido) -> None:
        self._db[pedido.pedido_id] = pedido

    def buscar_por_id(self, pedido_id: int) -> Pedido | None:
        return self._db.get(pedido_id)


# ============================================================
# 2. ONION ARCHITECTURE
#    Estrutura: Domain → Application → Infrastructure → Presentation
#    Mesma lógica, organização diferente (ênfase em camadas concêntricas)
# ============================================================

class OnionPedidoService:
    """
    Onion Application Service.
    Na Onion, Application depende apenas de Domain (interfaces).
    """

    def __init__(self, repo: PedidoRepository):
        self._repo = repo

    def criar_e_fechar(self, pedido_id: int, cliente: str, itens: List[tuple]) -> Pedido:
        pedido = Pedido(pedido_id, cliente)
        for pid, nome, preco, qtd in itens:
            pedido.adicionar_item(pid, nome, preco, qtd)
        pedido.fechar()
        self._repo.salvar(pedido)
        return pedido

    def resumo(self, pedido_id: int) -> str | None:
        pedido = self._repo.buscar_por_id(pedido_id)
        if pedido is None:
            return None
        return (
            f"[Onion] Pedido #{pedido.pedido_id} — {pedido.cliente} — "
            f"{pedido.total()} ({pedido.status})"
        )


# ============================================================
# 3. HEXAGONAL ARCHITECTURE (Ports & Adapters)
#    Portas (interfaces) já definidas: PedidoRepository
#    Adaptadores concretos conectam o domínio ao mundo externo
# ============================================================

class HexagonalPedidoHandler:
    """
    Handler hexagonal: recebe comando pela porta e processa no domínio.
    O domínio não sabe nada sobre o exterior.
    """

    def __init__(self, repo: PedidoRepository):
        self._repo = repo

    def handle_criar_pedido(self, command: dict) -> dict:
        pedido = Pedido(command["pedido_id"], command["cliente"])
        for item in command["itens"]:
            pedido.adicionar_item(
                item["id"], item["nome"], item["preco"], item["qtd"]
            )
        pedido.fechar()
        self._repo.salvar(pedido)

        # Projeção de resposta (adapter de saída)
        return {
            "pedido_id": pedido.pedido_id,
            "cliente": pedido.cliente,
            "total": str(pedido.total()),
            "status": pedido.status,
        }

    def handle_consultar_pedido(self, pedido_id: int) -> dict | None:
        pedido = self._repo.buscar_por_id(pedido_id)
        if pedido is None:
            return None
        return {
            "pedido_id": pedido.pedido_id,
            "cliente": pedido.cliente,
            "total": str(pedido.total()),
            "status": pedido.status,
            "itens": [f"{i.nome} x{i.quantidade}" for i in pedido.itens],
        }


# ============================================================
# DEMONSTRAÇÃO COMPARATIVA
# ============================================================

def demo_clean():
    print("\n" + "─" * 55)
    print("  1. CLEAN ARCHITECTURE")
    print("     Entities → Use Cases → Controllers → Repositories")
    print("─" * 55)

    repo = PedidoRepositoryEmMemoria()
    usecase = CriarPedidoUseCase(repo)
    controller = PedidoController(usecase)

    result = controller.criar_pedido(1001, "Carlos Oliveira", [
        {"id": "P01", "nome": "Monitor 24'", "preco": 1200.00, "qtd": 1},
        {"id": "P02", "nome": "Teclado Mecânico", "preco": 350.00, "qtd": 2},
    ])
    print(f"  Resultado: {result}")
    print("  ✓ Domínio isolado → Controller → UseCase → Repository")


def demo_onion():
    print("\n" + "─" * 55)
    print("  2. ONION ARCHITECTURE")
    print("     Domain ← Application ← Infrastructure ← Presentation")
    print("─" * 55)

    repo = PedidoRepositoryEmMemoria()
    service = OnionPedidoService(repo)

    service.criar_e_fechar(2001, "Ana Beatriz", [
        ("P03", "Notebook", 4500.00, 1),
        ("P04", "Mouse", 120.00, 1),
    ])
    print(f"  {service.resumo(2001)}")
    print("  ✓ Camadas concêntricas: dependência sempre para dentro")


def demo_hexagonal():
    print("\n" + "─" * 55)
    print("  3. HEXAGONAL (PORTS & ADAPTERS)")
    print("     Portas (interfaces) + Adaptadores (concretos)")
    print("─" * 55)

    repo = PedidoRepositoryEmMemoria()
    handler = HexagonalPedidoHandler(repo)

    cmd = {
        "pedido_id": 3001,
        "cliente": "Roberto Lima",
        "itens": [
            {"id": "P05", "nome": "SSD 1TB", "preco": 600.00, "qtd": 2},
            {"id": "P06", "nome": "Cabo HDMI", "preco": 45.00, "qtd": 3},
        ],
    }
    result = handler.handle_criar_pedido(cmd)
    print(f"  Comando executado: {result}")
    consulta = handler.handle_consultar_pedido(3001)
    print(f"  Consulta: {consulta}")
    print("  ✓ Domínio 100% isolado por portas; adaptadores trocáveis")


if __name__ == "__main__":
    print("=" * 60)
    print("  CLEAN × ONION × HEXAGONAL — MESMO DOMÍNIO")
    print("=" * 60)
    print("\n  Domínio comum: Pedido + ItemPedido + Dinheiro (Value Object)")

    demo_clean()
    demo_onion()
    demo_hexagonal()

    print("\n" + "=" * 60)
    print("  COMPARATIVO:")
    print("  ┌──────────┬──────────────────────────────────┐")
    print("  │ Clean    │ Entities → UseCases → Adapters   │")
    print("  │ Onion    │ Domain ← App ← Infra ← Present.  │")
    print("  │ Hexagonal│ Ports (interfaces) + Adapters     │")
    print("  ├──────────┼──────────────────────────────────┤")
    print("  │ TODOS    │ Domínio isolado de frameworks     │")
    print("  │ TODOS    │ Dependência aponta para dentro    │")
    print("  │ TODOS    │ Testabilidade e troca de infra    │")
    print("  └──────────┴──────────────────────────────────┘")
    print("=" * 60)
