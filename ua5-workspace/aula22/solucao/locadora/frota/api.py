"""
API PÚBLICA do contexto de Frota (Open Host Service).

Devolve dados simples (dict, float), nunca objetos internos do domínio — assim
o outro contexto não fica acoplado às classes da frota. Na Aula 22 esta mesma
API vira um serviço HTTP sem que Locações perceba a diferença.
"""

from ..compartilhado.valor import Placa
from .dominio.erros import VeiculoIndisponivel, VeiculoNaoEncontrado
from .dominio.eventos import VeiculoCadastrado
from .dominio.fabrica import criar_veiculo


class FrotaAPI:
    def __init__(self, repositorio, barramento=None):
        self._repo = repositorio
        self._barramento = barramento

    def cadastrar(self, tipo, placa, modelo, diaria):
        placa = str(Placa(placa))
        self._repo.salvar(criar_veiculo(tipo, placa, modelo, diaria))
        if self._barramento:
            self._barramento.publicar([VeiculoCadastrado(placa, tipo, modelo, float(diaria))])

    def listar(self):
        return [{"placa": v.placa, "tipo": v.tipo, "modelo": v.modelo,
                 "diaria": v.valor_diaria, "disponivel": v.disponivel}
                for v in self._repo.listar()]

    def preco_base(self, placa, dias):
        return round(self._obter(placa).preco_base(dias), 2)

    def reservar(self, placa):
        v = self._obter(placa)
        if not v.disponivel:
            raise VeiculoIndisponivel(f"Veículo {placa} já está alugado")
        v.marcar_alugado()
        self._repo.salvar(v)

    def liberar(self, placa):
        v = self._obter(placa)
        v.marcar_disponivel()
        self._repo.salvar(v)

    def _obter(self, placa):
        v = self._repo.buscar_por_placa(str(Placa(placa)))
        if v is None:
            raise VeiculoNaoEncontrado(f"Veículo {placa} não encontrado")
        return v
