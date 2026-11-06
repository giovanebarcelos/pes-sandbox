"""
Factory Method (GoF): cria o veículo certo a partir de um texto ("carro", "moto"...).

Quem lê dados de fora (arquivo, formulário, API) só conhece o nome do tipo;
a fábrica é o ÚNICO lugar que liga nome -> classe. Para um tipo novo,
basta registrá-lo (OCP) — nenhum if/elif espalhado pelo sistema.
"""

from .veiculos import Caminhao, Carro, Moto

_TIPOS = {"carro": Carro, "moto": Moto, "caminhao": Caminhao}


def registrar_tipo(nome, classe):
    _TIPOS[nome] = classe


def tipos_conhecidos():
    return sorted(_TIPOS)


def criar_veiculo(tipo, placa, modelo, diaria):
    try:
        classe = _TIPOS[tipo]
    except KeyError:
        raise ValueError(f"Tipo desconhecido: {tipo}") from None
    return classe(placa, modelo, diaria)
