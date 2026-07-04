"""
PES1802 - Padrões de Projeto GoF (Singleton, Factory Method, Observer, Strategy)
Aula 18: Princípios de Projeto e Padrões Arquiteturais
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra 4 padrões GoF com exemplos práticos:
- Singleton: gerenciador de configuração (instância única)
- Factory Method: criador de documentos (PDF, CSV, HTML)
- Observer: sistema de notificação (eventos de pedido)
- Strategy: calculadora de frete (PAC, SEDEX, transportadora)
"""

from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List

# ============================================================
# 1. SINGLETON — Gerenciador de Configuração (instância única)
# ============================================================

class ConfigManager:
    """Singleton: garante uma única instância de config global."""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._settings = {
                "app_name": "PES-EAD",
                "version": "1.0.0",
                "language": "pt-BR",
                "theme": "dark",
            }
        return cls._instance

    def get(self, key: str, default=None):
        return self._settings.get(key, default)

    def set(self, key: str, value) -> None:
        self._settings[key] = value

    def list_all(self) -> dict:
        return dict(self._settings)


# ============================================================
# 2. FACTORY METHOD — Criador de Documentos (PDF, CSV, HTML)
# ============================================================

class Document(ABC):
    """Produto abstrato."""

    @abstractmethod
    def render(self, data: list[dict]) -> str:
        ...


class PdfDocument(Document):
    def render(self, data: list[dict]) -> str:
        header = "=" * 40 + "\n  RELATÓRIO PDF\n" + "=" * 40
        rows = "\n".join(f"  {d['id']} | {d['nome']:20s} | R$ {d['valor']:.2f}" for d in data)
        return f"{header}\n\n{rows}\n\n[PDF gerado com sucesso]"


class CsvDocument(Document):
    def render(self, data: list[dict]) -> str:
        rows = "\n".join(f"{d['id']},{d['nome']},{d['valor']:.2f}" for d in data)
        return f"id,nome,valor\n{rows}"


class HtmlDocument(Document):
    def render(self, data: list[dict]) -> str:
        rows = "\n".join(
            f"    <tr><td>{d['id']}</td><td>{d['nome']}</td><td>R$ {d['valor']:.2f}</td></tr>"
            for d in data
        )
        return (
            "<html><body>\n"
            "  <h1>Relatório HTML</h1>\n"
            "  <table border='1'>\n"
            f"{rows}\n"
            "  </table>\n"
            "</body></html>"
        )


class DocumentFactory(ABC):
    """Creator abstrato — Factory Method."""

    @abstractmethod
    def create_document(self) -> Document:
        ...

    def export(self, data: list[dict]) -> str:
        doc = self.create_document()
        return doc.render(data)


class PdfExporter(DocumentFactory):
    def create_document(self) -> Document:
        return PdfDocument()


class CsvExporter(DocumentFactory):
    def create_document(self) -> Document:
        return CsvDocument()


class HtmlExporter(DocumentFactory):
    def create_document(self) -> Document:
        return HtmlDocument()


# ============================================================
# 3. OBSERVER — Sistema de Notificação de Pedidos
# ============================================================

class Subject(ABC):
    """Subject (observável) — notifica observadores."""

    def __init__(self):
        self._observers: List[Observer] = []

    def attach(self, observer: "Observer") -> None:
        self._observers.append(observer)

    def detach(self, observer: "Observer") -> None:
        self._observers.remove(observer)

    def notify(self, event: str, payload: dict) -> None:
        for obs in self._observers:
            obs.update(event, payload)


class Observer(ABC):
    """Interface Observer."""

    @abstractmethod
    def update(self, event: str, payload: dict) -> None:
        ...


class PedidoService(Subject):
    """Serviço de pedidos — notifica observadores sobre eventos."""

    def criar_pedido(self, cliente: str, valor: float) -> dict:
        pedido = {"cliente": cliente, "valor": valor, "status": "CRIADO"}
        self.notify("pedido.criado", pedido)
        return pedido

    def pagar(self, pedido: dict) -> None:
        pedido["status"] = "PAGO"
        self.notify("pedido.pago", pedido)


class EmailNotifier(Observer):
    """Envia e-mail quando pedido é criado/pago."""

    def update(self, event: str, payload: dict) -> None:
        if event == "pedido.criado":
            print(f"  📧 E-MAIL: Confirmação enviada para {payload['cliente']}")
        elif event == "pedido.pago":
            print(f"  📧 E-MAIL: Pagamento confirmado — R$ {payload['valor']:.2f}")


class LogNotifier(Observer):
    """Registra log de todos os eventos."""

    def __init__(self):
        self._log: list[str] = []

    def update(self, event: str, payload: dict) -> None:
        entry = f"  📋 LOG [{event}] cliente={payload['cliente']} valor={payload['valor']}"
        self._log.append(entry)
        print(entry)


class SMSNotifier(Observer):
    """Envia SMS para eventos críticos."""

    def update(self, event: str, payload: dict) -> None:
        if event == "pedido.pago":
            print(f"  📱 SMS: Pagamento de R$ {payload['valor']:.2f} processado!")


# ============================================================
# 4. STRATEGY — Calculadora de Frete (PAC, SEDEX, Transportadora)
# ============================================================

class FreteStrategy(ABC):
    """Interface Strategy para cálculo de frete."""

    @abstractmethod
    def calcular(self, peso_kg: float, distancia_km: float) -> float:
        ...

    @property
    @abstractmethod
    def nome(self) -> str:
        ...


class FretePAC(FreteStrategy):
    """Correios PAC — mais barato, mais lento."""

    @property
    def nome(self) -> str:
        return "PAC (Correios)"

    def calcular(self, peso_kg: float, distancia_km: float) -> float:
        return 12.0 + (peso_kg * 2.5) + (distancia_km * 0.03)


class FreteSEDEX(FreteStrategy):
    """Correios SEDEX — mais rápido, mais caro."""

    @property
    def nome(self) -> str:
        return "SEDEX (Correios)"

    def calcular(self, peso_kg: float, distancia_km: float) -> float:
        return 18.0 + (peso_kg * 3.8) + (distancia_km * 0.06)


class FreteTransportadora(FreteStrategy):
    """Transportadora parceira — custo intermediário."""

    @property
    def nome(self) -> str:
        return "Transportadora Rápida"

    def calcular(self, peso_kg: float, distancia_km: float) -> float:
        return 25.0 + (peso_kg * 1.8) + (distancia_km * 0.04)


class CalculadoraFrete:
    """Contexto — usa uma estratégia de frete."""

    def __init__(self, estrategia: FreteStrategy):
        self._estrategia = estrategia

    def set_estrategia(self, estrategia: FreteStrategy) -> None:
        self._estrategia = estrategia

    def calcular(self, peso_kg: float, distancia_km: float) -> str:
        valor = self._estrategia.calcular(peso_kg, distancia_km)
        return f"Frete {self._estrategia.nome}: R$ {valor:.2f}"


# ============================================================
# DEMONSTRAÇÃO
# ============================================================

def demo_singleton():
    print("\n" + "─" * 55)
    print("  1. SINGLETON — Gerenciador de Configuração")
    print("─" * 55)
    cfg1 = ConfigManager()
    cfg2 = ConfigManager()
    print(f"  Mesma instância? {cfg1 is cfg2}")
    print(f"  Config atual: {cfg1.list_all()}")
    cfg1.set("theme", "light")
    print(f"  Após cfg1.set('theme','light'): cfg2.get('theme') = {cfg2.get('theme')}")


def demo_factory_method():
    print("\n" + "─" * 55)
    print("  2. FACTORY METHOD — Exportação de Documentos")
    print("─" * 55)
    dados = [
        {"id": 1, "nome": "Notebook", "valor": 4500.00},
        {"id": 2, "nome": "Mouse", "valor": 120.00},
    ]
    for exporter_class, label in [
        (PdfExporter, "PDF"),
        (CsvExporter, "CSV"),
        (HtmlExporter, "HTML"),
    ]:
        print(f"\n  --- Exportando {label} ---")
        print(exporter_class().export(dados))


def demo_observer():
    print("\n" + "─" * 55)
    print("  3. OBSERVER — Notificação de Pedidos")
    print("─" * 55)
    service = PedidoService()
    service.attach(EmailNotifier())
    service.attach(LogNotifier())
    service.attach(SMSNotifier())

    pedido = service.criar_pedido("Maria Souza", 250.00)
    service.pagar(pedido)


def demo_strategy():
    print("\n" + "─" * 55)
    print("  4. STRATEGY — Cálculo de Frete")
    print("─" * 55)
    peso, distancia = 5.0, 120.0
    print(f"  Pacote: {peso}kg, Distância: {distancia}km\n")

    calc = CalculadoraFrete(FretePAC())
    print(f"  {calc.calcular(peso, distancia)}")

    calc.set_estrategia(FreteSEDEX())
    print(f"  {calc.calcular(peso, distancia)}")

    calc.set_estrategia(FreteTransportadora())
    print(f"  {calc.calcular(peso, distancia)}")


if __name__ == "__main__":
    print("=" * 55)
    print("  GoF DESIGN PATTERNS — EXEMPLOS PRÁTICOS")
    print("=" * 55)
    demo_singleton()
    demo_factory_method()
    demo_observer()
    demo_strategy()

    print("\n" + "=" * 55)
    print("  ✓ Singleton: instância única (ConfigManager)")
    print("  ✓ Factory Method: criação delegada (Document -> PDF/CSV/HTML)")
    print("  ✓ Observer: notificação em cascata (Pedido -> Email/Log/SMS)")
    print("  ✓ Strategy: algoritmo intercambiável (Frete PAC/SEDEX/Transportadora)")
    print("=" * 55)
