"""Monta um Model (ServicoLocacao) com dados de exemplo, compartilhado pelas 3 variantes."""

from ..fabrica import criar_veiculo
from ..notificacao import NotificadorConsole, NotificadorMemoria
from ..precos import DescontoSemanal
from ..repositorio import RepositorioMemoria
from ..servico import ServicoLocacao

FROTA_EXEMPLO = [
    ("carro", "ABC-1234", "Fiat Uno", 120),
    ("carro", "DEF-5678", "Honda Civic", 200),
    ("moto", "XYZ-9012", "Honda CG 160", 80),
    ("caminhao", "CAM-0001", "VW Delivery", 300),
]


def servico_exemplo(silencioso=False):
    notificador = NotificadorMemoria() if silencioso else NotificadorConsole()
    servico = ServicoLocacao(RepositorioMemoria(), notificador, DescontoSemanal())
    for tipo, placa, modelo, diaria in FROTA_EXEMPLO:
        servico.cadastrar(criar_veiculo(tipo, placa, modelo, diaria))
    return servico
