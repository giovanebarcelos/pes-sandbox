"""
PES1902 - Arquitetura Hexagonal (Ports & Adapters)
Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra Ports & Adapters: o domínio define portas (interfaces),
os adaptadores implementam detalhes técnicos (DB, API).
"""

from abc import ABC, abstractmethod


# ===== Porta (interface no domínio) =====
class RepositorioProduto(ABC):
    """Porta de saída: o domínio define O QUE precisa, não COMO."""

    @abstractmethod
    def buscar_por_id(self, id_produto):
        pass

    @abstractmethod
    def salvar(self, produto):
        pass


# ===== Domínio (Core) =====
class Produto:
    def __init__(self, id_produto, nome, preco):
        self.id = id_produto
        self.nome = nome
        self.preco = preco

    def __repr__(self):
        return f"Produto[{self.id}] {self.nome} — R$ {self.preco:.2f}"


class CatalogoService:
    """Serviço de domínio: depende da abstração (porta), não da implementação."""

    def __init__(self, repositorio: RepositorioProduto):
        self.repositorio = repositorio

    def obter_produto(self, id_produto):
        return self.repositorio.buscar_por_id(id_produto)

    def cadastrar_produto(self, id_produto, nome, preco):
        produto = Produto(id_produto, nome, preco)
        self.repositorio.salvar(produto)
        return produto


# ===== Adaptadores (implementações concretas) =====
class RepositorioMemoria(RepositorioProduto):
    """Adaptador: implementa a porta com armazenamento em memória."""

    def __init__(self):
        self._dados = {}

    def buscar_por_id(self, id_produto):
        return self._dados.get(id_produto)

    def salvar(self, produto):
        self._dados[produto.id] = produto


# ===== Aplicação (adaptador primário / driving) =====
def main_hexagonal():
    print("=" * 60)
    print("  ARQUITETURA HEXAGONAL — PORTS & ADAPTERS")
    print("=" * 60)

    # Injetar adaptador concreto na porta
    repo = RepositorioMemoria()
    catalogo = CatalogoService(repo)

    catalogo.cadastrar_produto(1, "Notebook", 4500.00)
    catalogo.cadastrar_produto(2, "Mouse", 120.00)

    p = catalogo.obter_produto(1)
    print(f"\n  Produto encontrado: {p}")

    print("\n✓ Domínio isolado: troque o adaptador sem alterar a lógica de negócio.")
    print("✓ Porta = interface; Adaptador = implementação concreta (DB, API, etc.).")


if __name__ == "__main__":
    main_hexagonal()
