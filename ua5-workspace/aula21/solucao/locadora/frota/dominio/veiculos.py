"""
Hierarquia de veículos.

SRP: cada classe só sabe o próprio preço-base e o próprio estado.
OCP: um novo tipo (ex.: Caminhao) é uma NOVA classe — nenhum if/elif é alterado.
LSP: toda subclasse cumpre o contrato de Veiculo (preço >= 0 para dias >= 1,
     sem lançar exceção nova) — ver tests/test_veiculos.py.
"""

from abc import ABC, abstractmethod


class Veiculo(ABC):
    tipo = "veiculo"

    def __init__(self, placa, modelo, valor_diaria):
        if valor_diaria <= 0:
            raise ValueError("A diária deve ser positiva")
        self._placa = placa
        self._modelo = modelo
        self._valor_diaria = float(valor_diaria)
        self._disponivel = True

    @property
    def placa(self):
        return self._placa

    @property
    def modelo(self):
        return self._modelo

    @property
    def valor_diaria(self):
        return self._valor_diaria

    @property
    def disponivel(self):
        return self._disponivel

    def marcar_alugado(self):
        self._disponivel = False

    def marcar_disponivel(self):
        self._disponivel = True

    @abstractmethod
    def preco_base(self, dias):
        """Preço sem descontos — cada tipo tem a sua regra (polimorfismo, Aula 17)."""

    def __repr__(self):
        return f"{self.__class__.__name__}[{self._placa}] {self._modelo}"


class Carro(Veiculo):
    tipo = "carro"

    def preco_base(self, dias):
        return self.valor_diaria * dias * 1.10  # +10% de seguro


class Moto(Veiculo):
    tipo = "moto"

    def preco_base(self, dias):
        return self.valor_diaria * dias


class Caminhao(Veiculo):
    """Extensão feita no Passo 4 (OCP): +10% de seguro e taxa fixa de R$ 50,00."""

    tipo = "caminhao"

    def preco_base(self, dias):
        return self.valor_diaria * dias * 1.10 + 50.0
