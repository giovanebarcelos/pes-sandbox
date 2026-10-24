from abc import ABC
from datetime import datetime, timedelta

from reservalab.dominio.erros import RegraViolada
from reservalab.dominio.usuario import Perfil, Usuario


class Recurso(ABC):
    """Algo reservável. Cada tipo declara suas regras; a validação é única.

    Polimorfismo por dados + Template Method: subclasses só redefinem os
    atributos de classe; `validar` nunca precisa de um `if tipo == ...`.
    """

    tipo: str
    duracao_maxima: timedelta
    antecedencia_minima: timedelta
    exige_aprovacao: bool = False
    perfis_permitidos: frozenset = frozenset(Perfil)

    def __init__(self, id: str, nome: str, capacidade: int):
        if capacidade < 1:
            raise ValueError("capacidade deve ser >= 1")
        self.id = id
        self.nome = nome
        self.capacidade = capacidade

    def validar(self, usuario: Usuario, inicio: datetime, fim: datetime, agora: datetime) -> None:
        """Valida o pedido contra as regras do tipo.

        Raises:
            RegraViolada: perfil, intervalo, duração ou antecedência inválidos.
        """
        if usuario.perfil not in self.perfis_permitidos:
            raise RegraViolada(f"{self.tipo}: perfil {usuario.perfil.value} não pode reservar")
        if fim <= inicio:
            raise RegraViolada("o fim da reserva deve ser depois do início")
        if fim - inicio > self.duracao_maxima:
            horas = self.duracao_maxima.total_seconds() / 3600
            raise RegraViolada(f"{self.tipo}: duração máxima de {horas:g}h")
        if inicio - agora < self.antecedencia_minima:
            horas = self.antecedencia_minima.total_seconds() / 3600
            raise RegraViolada(f"{self.tipo}: reserve com pelo menos {horas:g}h de antecedência")


class SalaEstudo(Recurso):
    tipo = "Sala de estudo"
    duracao_maxima = timedelta(hours=2)
    antecedencia_minima = timedelta(minutes=30)


class Laboratorio(Recurso):
    tipo = "Laboratório"
    duracao_maxima = timedelta(hours=4)
    antecedencia_minima = timedelta(hours=24)
    perfis_permitidos = frozenset({Perfil.PROFESSOR, Perfil.COORDENADOR})


class Auditorio(Recurso):
    tipo = "Auditório"
    duracao_maxima = timedelta(hours=6)
    antecedencia_minima = timedelta(hours=72)
    exige_aprovacao = True
    perfis_permitidos = frozenset({Perfil.PROFESSOR, Perfil.COORDENADOR})
