"""
Políticas de preço — padrão Strategy (GoF).

O desconto deixou de ser um `if dias >= 7` escondido em `calcular()`:
agora é uma estratégia trocável, injetada no serviço (DIP).
"""

from abc import ABC, abstractmethod


class PoliticaPreco(ABC):
    @abstractmethod
    def aplicar(self, valor, dias):
        """Recebe o preço-base e devolve o preço final."""


class SemDesconto(PoliticaPreco):
    def aplicar(self, valor, dias):
        return round(valor, 2)


class DescontoSemanal(PoliticaPreco):
    """Regra do legado: 7 dias ou mais ganham 10% de desconto."""

    def __init__(self, dias_minimos=7, percentual=0.10):
        self.dias_minimos = dias_minimos
        self.percentual = percentual

    def aplicar(self, valor, dias):
        if dias >= self.dias_minimos:
            valor = valor * (1 - self.percentual)
        return round(valor, 2)
