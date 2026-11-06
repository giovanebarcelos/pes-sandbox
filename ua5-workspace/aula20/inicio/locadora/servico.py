"""
Caso de uso "alugar/devolver" — só ORQUESTRA; não calcula, não grava, não imprime.

DIP: recebe repositório, notificador e política de preço prontos (injeção de
dependência pelo construtor). Nos testes, entram fakes em memória.
"""

from dataclasses import dataclass

from .erros import VeiculoIndisponivel, VeiculoNaoEncontrado
from .precos import SemDesconto


@dataclass(frozen=True)
class Locacao:
    placa: str
    cliente: str
    dias: int
    valor: float


class ServicoLocacao:
    def __init__(self, repositorio, notificador, politica=None):
        self._repo = repositorio
        self._notificador = notificador
        self._politica = politica or SemDesconto()
        self._locacoes = []

    def cadastrar(self, veiculo):
        self._repo.salvar(veiculo)

    def listar(self):
        return self._repo.listar()

    def cotar(self, placa, dias):
        veiculo = self._obter(placa)
        return self._politica.aplicar(veiculo.preco_base(dias), dias)

    def alugar(self, placa, cliente, email, dias):
        veiculo = self._obter(placa)
        if not veiculo.disponivel:
            raise VeiculoIndisponivel(f"Veículo {placa} já está alugado")
        valor = self._politica.aplicar(veiculo.preco_base(dias), dias)
        veiculo.marcar_alugado()
        self._repo.salvar(veiculo)
        locacao = Locacao(placa, cliente, dias, valor)
        self._locacoes.append(locacao)
        self._notificador.notificar(
            email,
            f"Olá {cliente}, sua locação do {veiculo.modelo} "
            f"por {dias} dia(s) custa R$ {valor:.2f}.",
        )
        return locacao

    def devolver(self, placa):
        veiculo = self._obter(placa)
        veiculo.marcar_disponivel()
        self._repo.salvar(veiculo)

    def faturamento_total(self):
        return round(sum(l.valor for l in self._locacoes), 2)

    def _obter(self, placa):
        veiculo = self._repo.buscar(placa)
        if veiculo is None:
            raise VeiculoNaoEncontrado(f"Veículo {placa} não encontrado")
        return veiculo
