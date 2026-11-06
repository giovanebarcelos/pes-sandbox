"""Núcleo do microkernel: contrato do plugin + descoberta automática + registro."""

import importlib
import pkgutil
from abc import ABC, abstractmethod


class PluginPagamento(ABC):
    nome = "?"

    @abstractmethod
    def taxa(self, valor):
        """Taxa cobrada pela forma de pagamento, em reais."""

    @abstractmethod
    def pagar(self, valor, referencia):
        """Processa o pagamento e devolve um texto de comprovante."""


class SistemaPagamentos:
    def __init__(self):
        self._plugins = {}

    def registrar(self, plugin):
        if not isinstance(plugin, PluginPagamento):
            raise TypeError(f"{plugin!r} não segue o contrato PluginPagamento")
        self._plugins[plugin.nome] = plugin

    def descobrir(self, pacote="locadora.pagamentos.plugins"):
        """Importa todo módulo do pacote de plugins que tenha a variável PLUGIN."""
        modulo_pacote = importlib.import_module(pacote)
        for info in pkgutil.iter_modules(modulo_pacote.__path__):
            modulo = importlib.import_module(f"{pacote}.{info.name}")
            if hasattr(modulo, "PLUGIN"):
                self.registrar(modulo.PLUGIN)
        return self

    def formas(self):
        return sorted(self._plugins)

    def pagar(self, forma, valor, referencia):
        try:
            plugin = self._plugins[forma]
        except KeyError:
            raise ValueError(f"Forma de pagamento não suportada: {forma}. Aceitas: {self.formas()}") from None
        total = round(valor + plugin.taxa(valor), 2)
        return total, plugin.pagar(total, referencia)
