"""
Composição (composition root): o ÚNICO lugar que escolhe as implementações concretas.

Executar (na pasta solucao/):  python -m locadora.main
Arquivo gravado: frota.json (ou o caminho da variável LOCADORA_ARQUIVO)
"""

import os

from .fabrica import criar_veiculo
from .notificacao import NotificadorConsole
from .precos import DescontoSemanal
from .relatorio import relatorio_frota
from .repositorio import RepositorioJson
from .servico import ServicoLocacao


def montar_servico(arquivo=None):
    arquivo = arquivo or os.environ.get("LOCADORA_ARQUIVO", "frota.json")
    if os.path.exists(arquivo):
        os.remove(arquivo)  # demo sempre começa do zero
    return ServicoLocacao(RepositorioJson(arquivo), NotificadorConsole(), DescontoSemanal())


def main():
    servico = montar_servico()
    for tipo, placa, modelo, diaria in [
        ("carro", "ABC-1234", "Fiat Uno", 120),
        ("carro", "DEF-5678", "Honda Civic", 200),
        ("moto", "XYZ-9012", "Honda CG 160", 80),
        ("caminhao", "CAM-0001", "VW Delivery", 300),
    ]:
        servico.cadastrar(criar_veiculo(tipo, placa, modelo, diaria))
    servico.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5)
    servico.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7)
    print(relatorio_frota(servico))


if __name__ == "__main__":
    main()
