from datetime import datetime

import pytest

from reservalab.aplicacao.catalogo import CatalogoRecursos
from reservalab.aplicacao.servico_reservas import ServicoReservas
from reservalab.dominio.recursos import Auditorio, Laboratorio, SalaEstudo
from reservalab.dominio.usuario import Perfil, Usuario
from reservalab.infra.repositorio_memoria import RepositorioMemoria
from reservalab.infra.repositorio_sqlite import RepositorioSqlite

AGORA = datetime(2026, 11, 9, 9, 0)  # segunda-feira, 09h


def dia(d: int, h: int, m: int = 0) -> datetime:
    return datetime(2026, 11, d, h, m)


class Relogio:
    """Relógio controlável: o teste decide que horas são."""

    def __init__(self, agora: datetime = AGORA):
        self.agora = agora

    def __call__(self) -> datetime:
        return self.agora


@pytest.fixture(params=[RepositorioMemoria, RepositorioSqlite], ids=["memoria", "sqlite"])
def repositorio(request):
    return request.param()


@pytest.fixture
def relogio():
    return Relogio()


@pytest.fixture
def servico(repositorio, relogio):
    catalogo = CatalogoRecursos()
    catalogo.registrar(SalaEstudo("S101", "Sala de estudo 101", 6))
    catalogo.registrar(Laboratorio("LAB2", "Laboratório de Redes", 30))
    catalogo.registrar(Auditorio("AUD1", "Auditório Central", 200))
    return ServicoReservas(repositorio, catalogo, relogio)


@pytest.fixture
def ana():
    return Usuario("20261001", "Ana", Perfil.ALUNO)


@pytest.fixture
def prof():
    return Usuario("P0042", "Prof. Beto", Perfil.PROFESSOR)


@pytest.fixture
def coord():
    return Usuario("C0001", "Coord. Carla", Perfil.COORDENADOR)
