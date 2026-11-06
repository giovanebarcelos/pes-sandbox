"""
LocadoraService — casos de uso da locadora (porta de ENTRADA / driving port).

Só depende do domínio e das portas. Trocar memória por SQLite, ou terminal
por web, NÃO altera nenhuma linha deste arquivo.
"""

from ..dominio.erros import VeiculoIndisponivel, VeiculoNaoEncontrado
from ..dominio.locacao import Locacao
from ..dominio.precos import SemDesconto


class LocadoraService:
    def __init__(self, veiculos, locacoes, notificador, politica=None):
        self._veiculos = veiculos        # VeiculoRepository
        self._locacoes = locacoes        # LocacaoRepository
        self._notificador = notificador  # Notificador
        self._politica = politica or SemDesconto()

    def cadastrar(self, veiculo):
        self._veiculos.salvar(veiculo)

    def listar(self):
        return self._veiculos.listar()

    def listar_disponiveis(self):
        return [v for v in self._veiculos.listar() if v.disponivel]

    def cotar(self, placa, dias):
        return self._politica.aplicar(self._obter(placa).preco_base(dias), dias)

    def alugar(self, placa, cliente, email, dias):
        veiculo = self._obter(placa)
        if not veiculo.disponivel:
            raise VeiculoIndisponivel(f"Veículo {placa} já está alugado")
        valor = self._politica.aplicar(veiculo.preco_base(dias), dias)
        veiculo.marcar_alugado()
        self._veiculos.salvar(veiculo)
        locacao = Locacao(placa, cliente, dias, valor)
        self._locacoes.adicionar(locacao)
        self._notificador.notificar(
            email, f"Olá {cliente}, sua locação do {veiculo.modelo} "
                   f"por {dias} dia(s) custa R$ {valor:.2f}.")
        return locacao

    def devolver(self, placa):
        veiculo = self._obter(placa)
        veiculo.marcar_disponivel()
        self._veiculos.salvar(veiculo)

    def faturamento_total(self):
        return round(sum(l.valor for l in self._locacoes.listar()), 2)

    def _obter(self, placa):
        veiculo = self._veiculos.buscar_por_placa(placa)
        if veiculo is None:
            raise VeiculoNaoEncontrado(f"Veículo {placa} não encontrado")
        return veiculo
