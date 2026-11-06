"""Plugin Cartão de crédito: taxa de 3%."""

from ..nucleo import PluginPagamento


class Cartao(PluginPagamento):
    nome = "cartao"

    def taxa(self, valor):
        return round(valor * 0.03, 2)

    def pagar(self, valor, referencia):
        return f"Cartão aprovado: R$ {valor:.2f} (ref. {referencia})"


PLUGIN = Cartao()
