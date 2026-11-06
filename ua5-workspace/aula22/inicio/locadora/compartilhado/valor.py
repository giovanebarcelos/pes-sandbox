"""
Objetos de Valor (Value Objects) — imutáveis, sem identidade, iguais quando os atributos são iguais.
"""

import re
from dataclasses import dataclass

_PLACA = re.compile(r"^[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}$")


@dataclass(frozen=True)
class Placa:
    valor: str

    def __post_init__(self):
        normalizada = self.valor.strip().upper()
        if not _PLACA.match(normalizada):
            raise ValueError(f"Placa inválida: {self.valor!r}")
        object.__setattr__(self, "valor", normalizada)

    def __str__(self):
        return self.valor


@dataclass(frozen=True)
class Dinheiro:
    centavos: int

    @classmethod
    def de_reais(cls, reais):
        return cls(int(round(float(reais) * 100)))

    def __add__(self, outro):
        return Dinheiro(self.centavos + outro.centavos)

    def vezes(self, fator):
        return Dinheiro(int(round(self.centavos * fator)))

    @property
    def reais(self):
        return self.centavos / 100

    def __str__(self):
        return f"R$ {self.reais:.2f}"
