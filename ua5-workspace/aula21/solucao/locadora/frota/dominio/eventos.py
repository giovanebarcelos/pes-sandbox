"""Eventos publicados pelo contexto de Frota."""

from dataclasses import dataclass

from ...compartilhado.eventos import EventoDominio, evento


@evento
@dataclass(frozen=True)
class VeiculoCadastrado(EventoDominio):
    tipo_veiculo: str
    modelo: str
    diaria: float
