"""
LADO DE LEITURA (Query) do CQRS — PROJEÇÕES.

Cada projeção é um "modelo de leitura" montado a partir dos eventos,
no formato exato que a consulta precisa. Nenhuma consulta lê o agregado.
Se uma projeção estragar ou mudar de formato, basta reconstruí-la a
partir do event store (`reconstruir`).
"""

from collections import Counter, defaultdict

from ..dominio.eventos import LocacaoIniciada, MultaPorAtrasoAplicada, VeiculoDevolvido


class Projecao:
    def tratar(self, evento):
        metodo = getattr(self, f"ao_{type(evento).__name__}", None)
        if metodo:
            metodo(evento)

    def reconstruir(self, eventos):
        self.__init__()
        for e in eventos:
            self.tratar(e)


class LocacoesAtivas(Projecao):
    def __init__(self):
        self.ativas = {}

    def ao_LocacaoIniciada(self, e):
        self.ativas[e.id_agregado] = {"placa": e.placa, "cliente": e.cliente,
                                      "inicio": e.data_inicio, "dias": e.dias_previstos}

    def ao_VeiculoDevolvido(self, e):
        self.ativas.pop(e.id_agregado, None)


class RankingMensal(Projecao):
    """Bônus do Slide 30: 'veículos mais alugados no mês'."""

    def __init__(self):
        self.por_mes = defaultdict(Counter)

    def ao_LocacaoIniciada(self, e):
        self.por_mes[e.data_inicio[:7]][e.placa] += 1

    def mais_alugados(self, mes, limite=3):
        return self.por_mes[mes].most_common(limite)


class Faturamento(Projecao):
    def __init__(self):
        self.mes_da_locacao = {}
        self.centavos_por_mes = defaultdict(int)

    def ao_LocacaoIniciada(self, e):
        mes = e.data_inicio[:7]
        self.mes_da_locacao[e.id_agregado] = mes
        self.centavos_por_mes[mes] += e.valor_previsto_centavos

    def ao_MultaPorAtrasoAplicada(self, e):
        self.centavos_por_mes[self.mes_da_locacao[e.id_agregado]] += e.valor_centavos

    def do_mes(self, mes):
        return self.centavos_por_mes[mes] / 100


class ServicoConsultas:
    """Fachada de leitura: só lê projeções, nunca o event store nem agregados."""

    def __init__(self, ativas, ranking, faturamento):
        self.ativas, self.ranking, self.faturamento = ativas, ranking, faturamento

    def locacoes_ativas(self):
        return dict(self.ativas.ativas)

    def mais_alugados(self, mes, limite=3):
        return self.ranking.mais_alugados(mes, limite)

    def faturamento_do_mes(self, mes):
        return self.faturamento.do_mes(mes)


TIPOS_DE_EVENTO = (LocacaoIniciada, VeiculoDevolvido, MultaPorAtrasoAplicada)
