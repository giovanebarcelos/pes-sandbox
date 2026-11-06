"""Adaptadores em memória — usados nos testes (sem banco, sem disco)."""

from ...aplicacao.portas import LocacaoRepository, Notificador, VeiculoRepository


class VeiculoMemoriaRepository(VeiculoRepository):
    def __init__(self):
        self._dados = {}

    def salvar(self, veiculo):
        self._dados[veiculo.placa] = veiculo

    def buscar_por_placa(self, placa):
        return self._dados.get(placa)

    def listar(self):
        return list(self._dados.values())


class LocacaoMemoriaRepository(LocacaoRepository):
    def __init__(self):
        self._dados = []

    def adicionar(self, locacao):
        self._dados.append(locacao)

    def listar(self):
        return list(self._dados)


class NotificadorMemoria(Notificador):
    def __init__(self):
        self.enviadas = []

    def notificar(self, destino, mensagem):
        self.enviadas.append((destino, mensagem))
