"""Erros do contexto de Frota."""


class FrotaErro(Exception):
    pass


class VeiculoNaoEncontrado(FrotaErro, KeyError):
    pass


class VeiculoIndisponivel(FrotaErro, RuntimeError):
    pass
