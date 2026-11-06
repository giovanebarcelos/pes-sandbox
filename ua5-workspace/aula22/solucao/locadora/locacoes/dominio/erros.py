"""Erros do contexto de Locações."""


class LocacaoErro(Exception):
    pass


class LocacaoJaEncerrada(LocacaoErro):
    pass


class DataInvalida(LocacaoErro, ValueError):
    pass


class ConflitoDeConcorrencia(LocacaoErro):
    """Duas operações tentaram gravar eventos na mesma versão do agregado."""


# --- erros da Camada Anticorrupção (Aula 22): a Frota vista com as palavras de Locações ---
class VeiculoDesconhecido(LocacaoErro, KeyError):
    pass


class VeiculoNaoDisponivel(LocacaoErro, RuntimeError):
    pass


class FrotaForaDoAr(LocacaoErro):
    """A Frota não respondeu (rede, timeout, serviço parado)."""
