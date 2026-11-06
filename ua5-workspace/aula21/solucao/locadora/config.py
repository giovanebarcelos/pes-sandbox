"""
Composition root: liga os dois contextos.

Integração entre contextos por EVENTO: quando Locações publica VeiculoDevolvido,
a política abaixo manda a Frota liberar o veículo. Locações não chama
`liberar` diretamente no comando de devolução.
"""

import os

from .compartilhado.eventos import BarramentoEventos
from .frota.adaptadores.memoria import VeiculoMemoriaRepository
from .frota.api import FrotaAPI
from .locacoes.adaptadores.event_store import EventStoreArquivo, EventStoreMemoria
from .locacoes.adaptadores.frota_local import FrotaLocal
from .locacoes.aplicacao.comandos import ManipuladorComandos
from .locacoes.aplicacao.consultas import (TIPOS_DE_EVENTO, Faturamento, LocacoesAtivas,
                                           RankingMensal, ServicoConsultas)
from .locacoes.dominio.eventos import VeiculoDevolvido

FROTA_EXEMPLO = [
    ("carro", "ABC-1234", "Fiat Uno", 120),
    ("carro", "DEF-5678", "Honda Civic", 200),
    ("moto", "XYZ-9012", "Honda CG 160", 80),
    ("caminhao", "CAM-0001", "VW Delivery", 300),
]


class Aplicacao:
    """Agrupa o que a demo, a CLI e os testes precisam."""

    def __init__(self, frota, comandos, consultas, store, barramento, projecoes):
        self.frota, self.comandos, self.consultas = frota, comandos, consultas
        self.store, self.barramento, self.projecoes = store, barramento, projecoes

    def reconstruir_projecoes(self):
        for p in self.projecoes:
            p.reconstruir(self.store.todos())


def montar(caminho_eventos=None, servico_frota=None, frota_api=None):
    caminho_eventos = caminho_eventos or os.environ.get("LOCADORA_EVENTOS")
    barramento = BarramentoEventos()
    if frota_api is None:
        frota_api = FrotaAPI(VeiculoMemoriaRepository(), barramento)
        for dados in FROTA_EXEMPLO:
            frota_api.cadastrar(*dados)
    servico_frota = servico_frota or FrotaLocal(frota_api)
    store = EventStoreArquivo(caminho_eventos) if caminho_eventos else EventStoreMemoria()

    projecoes = [LocacoesAtivas(), RankingMensal(), Faturamento()]
    for p in projecoes:
        p.reconstruir(store.todos())          # retoma o estado de execuções anteriores
        for tipo in TIPOS_DE_EVENTO:
            barramento.inscrever(tipo, p.tratar)
    barramento.inscrever(VeiculoDevolvido, lambda e: servico_frota.liberar(e.placa))

    comandos = ManipuladorComandos(store, servico_frota, barramento)
    return Aplicacao(frota_api, comandos, ServicoConsultas(*projecoes), store, barramento, projecoes)
