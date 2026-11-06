"""
Adaptador da porta ServicoFrota que chama a FrotaAPI no MESMO processo (monolito modular).

Camada Anticorrupção: traduz os erros da Frota para os erros de Locações,
sem importar nada de `locadora.frota` (a API chega por injeção).
"""

from ..dominio.erros import VeiculoDesconhecido, VeiculoNaoDisponivel
from ..dominio.portas import ServicoFrota


class FrotaLocal(ServicoFrota):
    def __init__(self, frota_api):
        self._api = frota_api

    def preco_base(self, placa, dias):
        return self._traduzir(self._api.preco_base, placa, dias)

    def reservar(self, placa):
        self._traduzir(self._api.reservar, placa)

    def liberar(self, placa):
        self._traduzir(self._api.liberar, placa)

    @staticmethod
    def _traduzir(funcao, *args):
        try:
            return funcao(*args)
        except KeyError as e:
            raise VeiculoDesconhecido(str(e).strip("'\"")) from e
        except RuntimeError as e:
            raise VeiculoNaoDisponivel(str(e)) from e
