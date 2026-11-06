"""
Adaptador da porta ServicoFrota que chama a FrotaAPI no MESMO processo (monolito).
Na Aula 22 ele ganha um irmão, FrotaHttp, que faz a mesma coisa pela rede.
"""

from ..dominio.portas import ServicoFrota


class FrotaLocal(ServicoFrota):
    def __init__(self, frota_api):
        self._api = frota_api

    def preco_base(self, placa, dias):
        return self._api.preco_base(placa, dias)

    def reservar(self, placa):
        self._api.reservar(placa)

    def liberar(self, placa):
        self._api.liberar(placa)
