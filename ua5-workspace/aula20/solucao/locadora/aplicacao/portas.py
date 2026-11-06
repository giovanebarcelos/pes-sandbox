"""
Portas de SAÍDA (driven ports): o que o caso de uso PRECISA do mundo externo.

Repare que as portas falam a língua do domínio (Veiculo, Locacao, placa),
nunca de SQL, JSON ou HTTP.
"""

from abc import ABC, abstractmethod


class VeiculoRepository(ABC):
    @abstractmethod
    def salvar(self, veiculo): ...

    @abstractmethod
    def buscar_por_placa(self, placa):
        """Devolve Veiculo ou None."""

    @abstractmethod
    def listar(self): ...


class LocacaoRepository(ABC):
    @abstractmethod
    def adicionar(self, locacao): ...

    @abstractmethod
    def listar(self): ...


class Notificador(ABC):
    @abstractmethod
    def notificar(self, destino, mensagem): ...
