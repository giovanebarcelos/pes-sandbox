"""
Eventos de domínio do contexto de Locações — a FONTE DA VERDADE (Event Sourcing).

Datas vão como texto ISO ("2026-11-06") e dinheiro como centavos (int),
para o evento poder ser gravado em JSON e lido de volta sem perda.
"""

from dataclasses import dataclass

from ...compartilhado.eventos import EventoDominio, evento


@evento
@dataclass(frozen=True)
class LocacaoIniciada(EventoDominio):
    placa: str
    cliente: str
    data_inicio: str
    dias_previstos: int
    valor_previsto_centavos: int


@evento
@dataclass(frozen=True)
class VeiculoDevolvido(EventoDominio):
    placa: str
    data_devolucao: str
    dias_utilizados: int


@evento
@dataclass(frozen=True)
class MultaPorAtrasoAplicada(EventoDominio):
    dias_atraso: int
    valor_centavos: int
