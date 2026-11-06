"""
LADO DE ESCRITA (Command) do CQRS.

Comando = intenção ("IniciarLocacao"), pode ser recusado.
Evento  = fato consumado ("LocacaoIniciada"), não se discute.
"""

from dataclasses import dataclass
from datetime import date

from ...compartilhado.valor import Dinheiro
from ..dominio.locacao import Locacao
from ..dominio.precos import DescontoSemanal


@dataclass(frozen=True)
class IniciarLocacao:
    id_locacao: str
    placa: str
    cliente: str
    data_inicio: date
    dias_previstos: int


@dataclass(frozen=True)
class RegistrarDevolucao:
    id_locacao: str
    data_devolucao: date


class ManipuladorComandos:
    def __init__(self, event_store, frota, barramento, politica=None):
        self._store = event_store
        self._frota = frota
        self._barramento = barramento
        self._politica = politica or DescontoSemanal()

    def executar(self, comando):
        if isinstance(comando, IniciarLocacao):
            return self._iniciar(comando)
        if isinstance(comando, RegistrarDevolucao):
            return self._devolver(comando)
        raise TypeError(f"Comando desconhecido: {comando!r}")

    def _iniciar(self, c):
        base = self._frota.preco_base(c.placa, c.dias_previstos)
        valor = Dinheiro.de_reais(self._politica.aplicar(base, c.dias_previstos))
        locacao = Locacao.iniciar(c.id_locacao, c.placa, c.cliente, c.data_inicio,
                                  c.dias_previstos, valor)
        self._frota.reservar(locacao.placa)   # síncrono: precisa saber se está livre
        self._salvar(locacao, versao_anterior=0)
        return locacao

    def _devolver(self, c):
        locacao = Locacao.reconstituir(c.id_locacao, self._store.carregar(c.id_locacao))
        if locacao.versao == 0:
            raise KeyError(f"Locação {c.id_locacao} não existe")
        versao_anterior = locacao.versao
        locacao.registrar_devolucao(c.data_devolucao)
        self._salvar(locacao, versao_anterior)
        return locacao

    def _salvar(self, locacao, versao_anterior):
        self._store.anexar(locacao.id, locacao.pendentes, versao_anterior)
        self._barramento.publicar(locacao.pendentes)
        locacao.pendentes = []
