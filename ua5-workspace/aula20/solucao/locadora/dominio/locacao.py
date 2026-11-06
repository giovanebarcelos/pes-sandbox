"""Entidade Locacao (antes ficava dentro de servico.py — agora é do domínio)."""

from dataclasses import dataclass


@dataclass(frozen=True)
class Locacao:
    placa: str
    cliente: str
    dias: int
    valor: float
