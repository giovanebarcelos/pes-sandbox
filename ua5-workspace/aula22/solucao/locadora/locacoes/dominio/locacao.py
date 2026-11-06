"""
Agregado LOCACAO (raiz do agregado), reconstruído a partir de eventos.

Regra de ouro do Event Sourcing:
  1. o método de comando VALIDA a invariante e GERA eventos (não muda estado direto);
  2. `_aplicar(evento)` é o ÚNICO lugar que muda o estado;
  3. carregar = aplicar, em ordem, todos os eventos gravados (`reconstituir`).

Invariantes que esta raiz protege:
  - dias previstos >= 1
  - não se devolve uma locação já encerrada
  - a data de devolução não pode ser anterior à de início
  - atraso gera multa: 1,5 × diária média × dias de atraso
"""

from datetime import date

from ...compartilhado.valor import Dinheiro, Placa
from .erros import DataInvalida, LocacaoJaEncerrada
from .eventos import LocacaoIniciada, MultaPorAtrasoAplicada, VeiculoDevolvido

FATOR_MULTA = 1.5


class Locacao:
    def __init__(self, id_locacao):
        self.id = id_locacao
        self.versao = 0               # quantos eventos já foram aplicados
        self.pendentes = []           # eventos novos, ainda não gravados
        self.placa = None
        self.cliente = None
        self.data_inicio = None
        self.dias_previstos = 0
        self.valor_previsto = Dinheiro(0)
        self.multa = Dinheiro(0)
        self.encerrada = False

    # ------------------------- comandos -------------------------
    @classmethod
    def iniciar(cls, id_locacao, placa, cliente, data_inicio, dias_previstos, valor_previsto):
        if dias_previstos < 1:
            raise DataInvalida("A locação precisa de pelo menos 1 dia")
        locacao = cls(id_locacao)
        locacao._registrar(LocacaoIniciada(
            id_locacao, str(Placa(placa)), cliente, data_inicio.isoformat(),
            int(dias_previstos), valor_previsto.centavos))
        return locacao

    def registrar_devolucao(self, data_devolucao):
        if self.encerrada:
            raise LocacaoJaEncerrada(f"Locação {self.id} já foi encerrada")
        if data_devolucao < self.data_inicio:
            raise DataInvalida("Devolução antes do início da locação")
        dias_utilizados = max(1, (data_devolucao - self.data_inicio).days)
        self._registrar(VeiculoDevolvido(self.id, self.placa, data_devolucao.isoformat(),
                                         dias_utilizados))
        atraso = dias_utilizados - self.dias_previstos
        if atraso > 0:
            diaria_media = self.valor_previsto.vezes(1 / self.dias_previstos)
            multa = diaria_media.vezes(FATOR_MULTA * atraso)
            self._registrar(MultaPorAtrasoAplicada(self.id, atraso, multa.centavos))

    @property
    def valor_total(self):
        return self.valor_previsto + self.multa

    # --------------------- event sourcing ---------------------
    @classmethod
    def reconstituir(cls, id_locacao, eventos):
        locacao = cls(id_locacao)
        for e in eventos:
            locacao._aplicar(e)
        return locacao

    def _registrar(self, evento):
        self._aplicar(evento)
        self.pendentes.append(evento)

    def _aplicar(self, e):
        if isinstance(e, LocacaoIniciada):
            self.placa, self.cliente = e.placa, e.cliente
            self.data_inicio = date.fromisoformat(e.data_inicio)
            self.dias_previstos = e.dias_previstos
            self.valor_previsto = Dinheiro(e.valor_previsto_centavos)
        elif isinstance(e, VeiculoDevolvido):
            self.encerrada = True
        elif isinstance(e, MultaPorAtrasoAplicada):
            self.multa = Dinheiro(e.valor_centavos)
        self.versao += 1
