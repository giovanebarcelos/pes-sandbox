"""
PES2001 - DDD: Entidade, Objeto de Valor e Agregado
Aula 20: DDD, Microserviços, Componentes e Interfaces
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra os blocos táticos do DDD: Entity, Value Object, Aggregate.
Domínio: Pedido (agregado) com itens (entidades) e preço (value object).
"""

from dataclasses import dataclass


# ===== Value Object (imutável, identificado por seus atributos) =====
@dataclass(frozen=True)
class Dinheiro:
    """Value Object: imutável, comparado por valor."""
    valor: float

    def __add__(self, other):
        return Dinheiro(round(self.valor + other.valor, 2))

    def __mul__(self, quantidade):
        return Dinheiro(round(self.valor * quantidade, 2))

    def __repr__(self):
        return f"R$ {self.valor:.2f}"


# ===== Entity (tem identidade própria) =====
class ItemPedido:
    """Entity: identificado por produto_id, mutável."""

    def __init__(self, produto_id, nome, preco_unitario, quantidade):
        self.produto_id = produto_id            # identidade
        self.nome = nome
        self.preco_unitario = preco_unitario    # Value Object
        self.quantidade = quantidade

    def subtotal(self):
        return self.preco_unitario * self.quantidade

    def __repr__(self):
        return f"Item[{self.produto_id}] {self.nome} x{self.quantidade} = {self.subtotal()}"


# ===== Aggregate Root (garante consistência do agregado) =====
class Pedido:
    """Aggregate Root: controla o ciclo de vida dos itens."""

    def __init__(self, pedido_id, cliente):
        self.pedido_id = pedido_id
        self.cliente = cliente
        self._itens = []
        self._status = "ABERTO"

    def adicionar_item(self, produto_id, nome, preco, quantidade=1):
        if self._status != "ABERTO":
            raise ValueError("Pedido não está aberto para alterações.")
        item = ItemPedido(produto_id, nome, Dinheiro(preco), quantidade)
        self._itens.append(item)

    def total(self):
        return sum((item.subtotal() for item in self._itens), Dinheiro(0.0))

    def fechar(self):
        if not self._itens:
            raise ValueError("Pedido sem itens.")
        self._status = "FECHADO"

    @property
    def status(self):
        return self._status

    @property
    def itens(self):
        return list(self._itens)

    def __repr__(self):
        return f"Pedido[{self.pedido_id}] {self.cliente} — {self._status} — {self.total()}"


if __name__ == "__main__":
    print("=" * 60)
    print("  DDD — ENTIDADE, VALUE OBJECT E AGREGADO")
    print("=" * 60)

    pedido = Pedido(1001, "Ana Silva")
    pedido.adicionar_item("P001", "Notebook", 4500.00, 1)
    pedido.adicionar_item("P002", "Mouse", 120.00, 2)
    pedido.adicionar_item("P003", "Teclado", 250.00, 1)

    print(f"\n  {pedido}")
    print("  Itens:")
    for item in pedido.itens:
        print(f"    {item}")
    print(f"  Total: {pedido.total()}")

    pedido.fechar()
    print(f"\n  Status após fechar: {pedido.status}")

    print("\n✓ Entity: tem identidade (produto_id, pedido_id).")
    print("✓ Value Object: imutável, comparado por valor (Dinheiro).")
    print("✓ Aggregate: Pedido garante consistência dos itens.")
