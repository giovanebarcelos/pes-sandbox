"""
COMPOSITION ROOT — o único módulo que conhece TODOS os adaptadores e os liga ao núcleo.

Escolha do adaptador por variável de ambiente (sem mudar código):
  LOCADORA_REPO=memoria  (padrão)  |  LOCADORA_REPO=sqlite
  LOCADORA_DB=dados/locadora.db    (caminho do arquivo SQLite)
"""

import os

from .adaptadores.saida.console import NotificadorConsole
from .adaptadores.saida.memoria import (LocacaoMemoriaRepository, NotificadorMemoria,
                                        VeiculoMemoriaRepository)
from .adaptadores.saida.sqlite import BancoSqlite, LocacaoSQLRepository, VeiculoSQLRepository
from .aplicacao.servico import LocadoraService
from .dominio.fabrica import criar_veiculo
from .dominio.precos import DescontoSemanal

FROTA_EXEMPLO = [
    ("carro", "ABC-1234", "Fiat Uno", 120),
    ("carro", "DEF-5678", "Honda Civic", 200),
    ("moto", "XYZ-9012", "Honda CG 160", 80),
    ("caminhao", "CAM-0001", "VW Delivery", 300),
]


def montar(repo=None, caminho_db=None, silencioso=False, semear=True):
    repo = repo or os.environ.get("LOCADORA_REPO", "memoria")
    if repo == "sqlite":
        banco = BancoSqlite(caminho_db or os.environ.get("LOCADORA_DB", "dados/locadora.db"))
        veiculos, locacoes = VeiculoSQLRepository(banco), LocacaoSQLRepository(banco)
    elif repo == "memoria":
        veiculos, locacoes = VeiculoMemoriaRepository(), LocacaoMemoriaRepository()
    else:
        raise ValueError(f"LOCADORA_REPO inválido: {repo}")
    notificador = NotificadorMemoria() if silencioso else NotificadorConsole()
    servico = LocadoraService(veiculos, locacoes, notificador, DescontoSemanal())
    if semear and not servico.listar():
        for tipo, placa, modelo, diaria in FROTA_EXEMPLO:
            servico.cadastrar(criar_veiculo(tipo, placa, modelo, diaria))
    return servico
