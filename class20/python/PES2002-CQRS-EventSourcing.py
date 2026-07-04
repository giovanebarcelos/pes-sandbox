"""
PES2002 - CQRS + Event Sourcing: Conta Bancária
Aula 20: DDD, Microserviços, Componentes e Interfaces
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra CQRS (Command Query Responsibility Segregation) combinado
com Event Sourcing usando o domínio de conta bancária.

- Commands: CriarConta, Depositar, Sacar (operações de escrita)
- Queries: ConsultarSaldo, Extrato (operações de leitura)
- Event Sourcing: cada operação gera um evento imutável;
  o estado atual é reconstruído reprocessando o log de eventos.
"""

from __future__ import annotations
from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from datetime import datetime
from typing import List


# ============================================================
# DOMAIN EVENTS (imutáveis — fonte da verdade)
# ============================================================

@dataclass(frozen=True)
class DomainEvent(ABC):
    """Evento de domínio base."""
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())

    @abstractmethod
    def aplicar(self, estado: "ContaState") -> "ContaState":
        ...


@dataclass(frozen=True)
class ContaCriada(DomainEvent):
    conta_id: str = ""
    titular: str = ""

    def aplicar(self, estado: "ContaState") -> "ContaState":
        return ContaState(conta_id=self.conta_id, titular=self.titular, saldo=0.0, ativa=True)


@dataclass(frozen=True)
class DepositoRealizado(DomainEvent):
    valor: float = 0.0

    def aplicar(self, estado: "ContaState") -> "ContaState":
        return ContaState(
            conta_id=estado.conta_id,
            titular=estado.titular,
            saldo=estado.saldo + self.valor,
            ativa=estado.ativa,
        )


@dataclass(frozen=True)
class SaqueRealizado(DomainEvent):
    valor: float = 0.0

    def aplicar(self, estado: "ContaState") -> "ContaState":
        return ContaState(
            conta_id=estado.conta_id,
            titular=estado.titular,
            saldo=estado.saldo - self.valor,
            ativa=estado.ativa,
        )


@dataclass(frozen=True)
class ContaState:
    """Projeção do estado atual da conta (read model)."""
    conta_id: str = ""
    titular: str = ""
    saldo: float = 0.0
    ativa: bool = False


# ============================================================
# EVENT STORE (log imutável de eventos)
# ============================================================

class EventStore:
    """Armazena eventos em log append-only."""

    def __init__(self):
        self._streams: dict[str, List[DomainEvent]] = {}

    def append(self, conta_id: str, events: List[DomainEvent]) -> None:
        if conta_id not in self._streams:
            self._streams[conta_id] = []
        self._streams[conta_id].extend(events)

    def load(self, conta_id: str) -> List[DomainEvent]:
        return list(self._streams.get(conta_id, []))

    def all_streams(self) -> List[str]:
        return list(self._streams.keys())


# ============================================================
# COMMAND SIDE (escrita)
# ============================================================

class ContaCommandHandler:
    """Command Handler: processa comandos e gera eventos."""

    def __init__(self, store: EventStore):
        self._store = store

    def criar_conta(self, conta_id: str, titular: str) -> List[DomainEvent]:
        if self._store.load(conta_id):
            raise ValueError(f"Conta '{conta_id}' já existe.")
        eventos = [ContaCriada(conta_id=conta_id, titular=titular)]
        self._store.append(conta_id, eventos)
        return eventos

    def depositar(self, conta_id: str, valor: float) -> List[DomainEvent]:
        if valor <= 0:
            raise ValueError("Valor de depósito deve ser positivo.")
        estado = self._reconstruir_estado(conta_id)
        if not estado.ativa:
            raise ValueError(f"Conta '{conta_id}' não encontrada.")
        eventos = [DepositoRealizado(valor=valor)]
        self._store.append(conta_id, eventos)
        return eventos

    def sacar(self, conta_id: str, valor: float) -> List[DomainEvent]:
        if valor <= 0:
            raise ValueError("Valor de saque deve ser positivo.")
        estado = self._reconstruir_estado(conta_id)
        if not estado.ativa:
            raise ValueError(f"Conta '{conta_id}' não encontrada.")
        if estado.saldo < valor:
            raise ValueError(f"Saldo insuficiente: {estado.saldo:.2f} < {valor:.2f}")
        eventos = [SaqueRealizado(valor=valor)]
        self._store.append(conta_id, eventos)
        return eventos

    def _reconstruir_estado(self, conta_id: str) -> ContaState:
        """Reconstrói estado atual a partir do log de eventos."""
        eventos = self._store.load(conta_id)
        if not eventos:
            return ContaState()
        estado = ContaState()
        for evt in eventos:
            estado = evt.aplicar(estado)
        return estado


# ============================================================
# QUERY SIDE (leitura) — Read Model otimizado
# ============================================================

class ContaQueryHandler:
    """Query Handler: consulta projeções (read-only)."""

    def __init__(self, store: EventStore):
        self._store = store

    def consultar_saldo(self, conta_id: str) -> ContaState:
        """Reconstrói estado sob demanda (pode ser cacheado)."""
        eventos = self._store.load(conta_id)
        estado = ContaState()
        for evt in eventos:
            estado = evt.aplicar(estado)
        return estado

    def extrato(self, conta_id: str) -> List[dict]:
        """Retorna extrato de eventos como projeção legível."""
        eventos = self._store.load(conta_id)
        resultado = []
        saldo = 0.0
        for evt in eventos:
            if isinstance(evt, ContaCriada):
                resultado.append({
                    "tipo": "ABERTURA",
                    "detalhe": f"Conta criada — titular: {evt.titular}",
                    "valor": 0.0,
                })
            elif isinstance(evt, DepositoRealizado):
                saldo += evt.valor
                resultado.append({
                    "tipo": "DEPÓSITO",
                    "detalhe": f"+ R$ {evt.valor:,.2f}",
                    "valor": evt.valor,
                    "saldo_parcial": saldo,
                })
            elif isinstance(evt, SaqueRealizado):
                saldo -= evt.valor
                resultado.append({
                    "tipo": "SAQUE",
                    "detalhe": f"- R$ {evt.valor:,.2f}",
                    "valor": -evt.valor,
                    "saldo_parcial": saldo,
                })
        return resultado


# ============================================================
# DEMONSTRAÇÃO
# ============================================================

if __name__ == "__main__":
    print("=" * 60)
    print("  CQRS + EVENT SOURCING — CONTA BANCÁRIA")
    print("=" * 60)

    store = EventStore()
    cmd = ContaCommandHandler(store)
    qry = ContaQueryHandler(store)

    # --- COMMANDS (escrita) ---
    print("\n  📤 COMMANDS (escrita):")
    cmd.criar_conta("C001", "João da Silva")
    print("    ✓ Conta 'C001' criada para João da Silva")

    cmd.depositar("C001", 500.00)
    print("    ✓ Depósito de R$ 500,00")

    cmd.depositar("C001", 200.00)
    print("    ✓ Depósito de R$ 200,00")

    cmd.sacar("C001", 150.00)
    print("    ✓ Saque de R$ 150,00")

    cmd.depositar("C001", 100.00)
    print("    ✓ Depósito de R$ 100,00")

    # --- QUERIES (leitura) ---
    print("\n  📥 QUERIES (leitura):")
    saldo = qry.consultar_saldo("C001")
    print(f"    Saldo atual: R$ {saldo.saldo:,.2f}")

    print("\n  📋 EXTRATO (projeção do event log):")
    print(f"    {'Tipo':<12} {'Detalhe':<30} {'Valor'}")
    print(f"    {'─' * 12} {'─' * 30} {'─' * 10}")
    for linha in qry.extrato("C001"):
        print(f"    {linha['tipo']:<12} {linha['detalhe']:<30}", end="")
        if "saldo_parcial" in linha:
            print(f" → saldo: R$ {linha['saldo_parcial']:,.2f}")
        else:
            print()

    # --- EVENT LOG bruto ---
    print("\n  📜 EVENT LOG (fonte da verdade — imutável):")
    for evt in store.load("C001"):
        print(f"    {evt}")

    print("\n" + "=" * 60)
    print("  ✓ CQRS: Commands (escrita) separados de Queries (leitura)")
    print("  ✓ Event Sourcing: estado reconstruído do log de eventos")
    print("  ✓ Event Store: append-only, imutável, auditável")
    print("=" * 60)
