"""Erros de negócio da locadora (substituem KeyError/RuntimeError genéricos do legado)."""


class LocadoraErro(Exception):
    """Base para todos os erros de regra de negócio."""


class VeiculoNaoEncontrado(LocadoraErro, KeyError):
    """Herda de KeyError para manter compatível com o código legado."""


class VeiculoIndisponivel(LocadoraErro, RuntimeError):
    """Herda de RuntimeError para manter compatível com o código legado."""
