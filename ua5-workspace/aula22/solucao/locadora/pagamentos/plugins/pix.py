"""Plugin PIX: sem taxa."""

from ..nucleo import PluginPagamento


class Pix(PluginPagamento):
    nome = "pix"

    def taxa(self, valor):
        return 0.0

    def pagar(self, valor, referencia):
        return f"PIX recebido: R$ {valor:.2f} (ref. {referencia})"


PLUGIN = Pix()
