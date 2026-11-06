"""Erros do contexto de Locações."""


class LocacaoErro(Exception):
    pass


class LocacaoJaEncerrada(LocacaoErro):
    pass


class DataInvalida(LocacaoErro, ValueError):
    pass


class ConflitoDeConcorrencia(LocacaoErro):
    """Duas operações tentaram gravar eventos na mesma versão do agregado."""
