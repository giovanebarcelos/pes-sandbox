"""
PES2003 - Arquiteturas Avançadas: Modular Monolith, Microkernel, P2P
Aula 20: DDD, Microserviços, Componentes e Interfaces
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Referência comparativa demonstrando três arquiteturas avançadas:
- Modular Monolith: módulos isolados, único deploy, chamadas in-process
- Microkernel/Plugin: core mínimo + plugins extensíveis
- Peer-to-Peer: rede descentralizada de nós autônomos
"""

from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List, Dict


# ============================================================
# 1. MODULAR MONOLITH
#    Módulos com APIs internas bem definidas, comunicação in-process,
#    schemas de banco isolados, único deploy.
# ============================================================

class ModuloBase(ABC):
    """Base para módulos do monólito modular."""

    def __init__(self, nome: str):
        self.nome = nome
        self._eventos: List[str] = []

    @abstractmethod
    def processar(self, dados: dict) -> dict:
        ...

    def registrar_evento(self, evento: str) -> None:
        self._eventos.append(evento)


class ModuloVendas(ModuloBase):
    """Módulo de Vendas — API interna."""

    def __init__(self):
        super().__init__("Vendas")

    def processar(self, dados: dict) -> dict:
        pedido_id = dados.get("pedido_id", 0)
        self.registrar_evento(f"Pedido #{pedido_id} criado")
        return {"modulo": "Vendas", "status": "OK", "pedido_id": pedido_id}

    def consultar_pedido(self, pedido_id: int) -> dict:
        return {"pedido_id": pedido_id, "cliente": "Ana Silva", "total": 250.00}


class ModuloEstoque(ModuloBase):
    """Módulo de Estoque — API interna."""

    def __init__(self):
        super().__init__("Estoque")

    def processar(self, dados: dict) -> dict:
        produto_id = dados.get("produto_id", "")
        qtd = dados.get("quantidade", 0)
        self.registrar_evento(f"Reserva de {qtd} un. do produto {produto_id}")
        return {"modulo": "Estoque", "status": "RESERVADO", "produto_id": produto_id}


class ModuloPagamento(ModuloBase):
    """Módulo de Pagamento — API interna."""

    def __init__(self):
        super().__init__("Pagamento")

    def processar(self, dados: dict) -> dict:
        valor = dados.get("valor", 0.0)
        self.registrar_evento(f"Pagamento de R$ {valor:.2f} processado")
        return {"modulo": "Pagamento", "status": "APROVADO", "valor": valor}


class ModularMonolith:
    """Orquestrador: coordena módulos via chamadas in-process."""

    def __init__(self):
        self.vendas = ModuloVendas()
        self.estoque = ModuloEstoque()
        self.pagamento = ModuloPagamento()

    def fluxo_pedido(self, pedido_id: int, produto_id: str, qtd: int, valor: float) -> dict:
        r_vendas = self.vendas.processar({"pedido_id": pedido_id})
        r_estoque = self.estoque.processar({"produto_id": produto_id, "quantidade": qtd})
        r_pagto = self.pagamento.processar({"valor": valor})
        return {
            "fluxo": "pedido_completo",
            "vendas": r_vendas,
            "estoque": r_estoque,
            "pagamento": r_pagto,
        }


# ============================================================
# 2. MICROKERNEL / PLUGIN ARCHITECTURE
#    Core mínimo + plugins que implementam contratos.
# ============================================================

class IExportPlugin(ABC):
    """Contrato para plugins de exportação."""

    @abstractmethod
    def exportar(self, dados: List[dict]) -> str:
        ...

    @property
    @abstractmethod
    def extensao(self) -> str:
        ...


class PluginPDF(IExportPlugin):
    @property
    def extensao(self) -> str:
        return ".pdf"

    def exportar(self, dados: List[dict]) -> str:
        linhas = "\n".join(
            f"  {d['id']} | {d['nome']:15s} | R$ {d['valor']:.2f}" for d in dados
        )
        return f"[PDF]\n{'=' * 30}\n{linhas}\n{'=' * 30}"


class PluginCSV(IExportPlugin):
    @property
    def extensao(self) -> str:
        return ".csv"

    def exportar(self, dados: List[dict]) -> str:
        linhas = "\n".join(f"{d['id']},{d['nome']},{d['valor']:.2f}" for d in dados)
        return f"id,nome,valor\n{linhas}"


class PluginJSON(IExportPlugin):
    @property
    def extensao(self) -> str:
        return ".json"

    def exportar(self, dados: List[dict]) -> str:
        import json
        return json.dumps(dados, indent=2, ensure_ascii=False)


class PluginRegistry:
    """Core: gerencia plugins registrados dinamicamente."""

    def __init__(self):
        self._plugins: Dict[str, IExportPlugin] = {}

    def registrar(self, plugin: IExportPlugin) -> None:
        self._plugins[plugin.extensao] = plugin

    def exportar(self, formato: str, dados: List[dict]) -> str:
        plugin = self._plugins.get(formato)
        if plugin is None:
            return f"Formato '{formato}' não suportado. Disponíveis: {list(self._plugins.keys())}"
        return plugin.exportar(dados)

    @property
    def formatos_disponiveis(self) -> List[str]:
        return list(self._plugins.keys())


# ============================================================
# 3. PEER-TO-PEER (P2P)
#    Rede descentralizada — cada nó é cliente e servidor.
# ============================================================

class NoP2P:
    """Nó de rede P2P — autônomo, com recursos e conexões."""

    def __init__(self, node_id: str, recurso: str):
        self.node_id = node_id
        self.recurso = recurso
        self._vizinhos: List[NoP2P] = []
        self._mensagens: List[str] = []

    def conectar(self, outro: "NoP2P") -> None:
        if outro not in self._vizinhos and outro is not self:
            self._vizinhos.append(outro)
            outro._vizinhos.append(self)  # Conexão bidirecional

    @property
    def vizinhos(self) -> List[str]:
        return [v.node_id for v in self._vizinhos]

    def compartilhar(self, mensagem: str) -> None:
        """Propaga mensagem para todos os vizinhos (flooding simples)."""
        self._mensagens.append(f"[local] {mensagem}")
        for vizinho in self._vizinhos:
            vizinho.receber(self.node_id, mensagem)

    def receber(self, origem: str, mensagem: str) -> None:
        self._mensagens.append(f"[de {origem}] {mensagem}")

    def resumo(self) -> dict:
        return {
            "no": self.node_id,
            "recurso": self.recurso,
            "vizinhos": self.vizinhos,
            "mensagens": self._mensagens,
        }


# ============================================================
# DEMONSTRAÇÃO
# ============================================================

def demo_modular_monolith():
    print("\n" + "─" * 55)
    print("  1. MODULAR MONOLITH")
    print("     Módulos isolados — comunicação in-process — deploy único")
    print("─" * 55)

    app = ModularMonolith()
    resultado = app.fluxo_pedido(1001, "P-ABC", 2, 250.00)
    for chave, valor in resultado.items():
        print(f"    {chave}: {valor}")

    print("  ✓ Módulos: Vendas, Estoque, Pagamento")
    print("  ✓ Vantagem: simplicidade de deploy com isolamento lógico")


def demo_microkernel():
    print("\n" + "─" * 55)
    print("  2. MICROKERNEL / PLUGIN ARCHITECTURE")
    print("     Core mínimo + plugins dinâmicos")
    print("─" * 55)

    registry = PluginRegistry()
    registry.registrar(PluginPDF())
    registry.registrar(PluginCSV())
    registry.registrar(PluginJSON())

    dados = [
        {"id": 1, "nome": "Notebook", "valor": 4500.00},
        {"id": 2, "nome": "Monitor", "valor": 1200.00},
    ]

    print(f"    Formatos disponíveis: {registry.formatos_disponiveis}")
    for fmt in [".pdf", ".csv", ".json"]:
        print(f"\n    --- Exportando {fmt} ---")
        print(registry.exportar(fmt, dados))

    print("  ✓ Core (Registry) é estável, plugins são extensíveis")


def demo_p2p():
    print("\n" + "─" * 55)
    print("  3. PEER-TO-PEER (P2P)")
    print("     Rede descentralizada — nós autônomos")
    print("─" * 55)

    a = NoP2P("Nó-A", "Arquivo: projeto.pdf")
    b = NoP2P("Nó-B", "Arquivo: especificacao.docx")
    c = NoP2P("Nó-C", "Música: jazz.mp3")
    d = NoP2P("Nó-D", "Vídeo: tutorial.mp4")

    a.conectar(b)
    b.conectar(c)
    c.conectar(d)
    a.conectar(c)

    print("    Topologia: A—B—C—D e A—C")
    print(f"    Vizinhos de A: {a.vizinhos}")
    print(f"    Vizinhos de B: {b.vizinhos}")
    print(f"    Vizinhos de C: {c.vizinhos}")

    a.compartilhar("Novo arquivo disponível: projeto.pdf")
    print(f"\n    Mensagens em A: {a._mensagens}")
    print(f"    Mensagens em B: {b._mensagens}")

    print("  ✓ Cada nó é autônomo (cliente + servidor)")
    print("  ✓ Sem ponto central de falha")


if __name__ == "__main__":
    print("=" * 60)
    print("  ARQUITETURAS AVANÇADAS")
    print("  Modular Monolith | Microkernel | Peer-to-Peer")
    print("=" * 60)

    demo_modular_monolith()
    demo_microkernel()
    demo_p2p()

    print("\n" + "=" * 60)
    print("  COMPARATIVO:")
    print("  ┌──────────────────┬─────────────────────────────────┐")
    print("  │ Modular Monolith │ Módulos isolados, deploy único  │")
    print("  │ Microkernel      │ Core estável + plugins flexíveis│")
    print("  │ P2P              │ Nós autônomos descentralizados  │")
    print("  ├──────────────────┼─────────────────────────────────┤")
    print("  │ Quando usar cada │ Modular: transição p/ µserviços │")
    print("  │                  │ Microkernel: IDEs, navegadores  │")
    print("  │                  │ P2P: blockchain, torrent, IoT   │")
    print("  └──────────────────┴─────────────────────────────────┘")
    print("=" * 60)
