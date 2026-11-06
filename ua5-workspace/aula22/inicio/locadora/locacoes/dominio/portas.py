"""Portas do contexto de Locações (o que ele precisa do mundo externo)."""

from abc import ABC, abstractmethod


class ServicoFrota(ABC):
    """
    Camada Anticorrupção (ACL): Locações fala com a Frota só por estas 3 operações,
    na SUA linguagem. Implementações: FrotaLocal (Aula 21) e FrotaHttp (Aula 22).
    """

    @abstractmethod
    def preco_base(self, placa, dias): ...

    @abstractmethod
    def reservar(self, placa): ...

    @abstractmethod
    def liberar(self, placa): ...


class EventStore(ABC):
    @abstractmethod
    def anexar(self, id_agregado, eventos, versao_esperada):
        """Grava eventos novos. Falha com ConflitoDeConcorrencia se a versão mudou."""

    @abstractmethod
    def carregar(self, id_agregado):
        """Eventos de um agregado, em ordem."""

    @abstractmethod
    def todos(self):
        """Todos os eventos, em ordem de gravação (para reconstruir projeções)."""
