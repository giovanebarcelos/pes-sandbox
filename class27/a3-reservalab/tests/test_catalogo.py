"""RF-01: catálogo de recursos."""
import pytest

from reservalab.aplicacao.catalogo import CatalogoRecursos
from reservalab.dominio.erros import NaoEncontrado
from reservalab.dominio.recursos import Laboratorio, SalaEstudo


def test_registrar_e_listar_ordenado_por_id():
    c = CatalogoRecursos()
    c.registrar(Laboratorio("LAB2", "Laboratório de Redes", 30))
    c.registrar(SalaEstudo("S101", "Sala de estudo 101", 6))
    assert [r.id for r in c.listar()] == ["LAB2", "S101"]


def test_registrar_id_repetido_e_recusado():
    c = CatalogoRecursos()
    c.registrar(SalaEstudo("S101", "Sala 101", 6))
    with pytest.raises(ValueError, match="já registrado"):
        c.registrar(SalaEstudo("S101", "Outra sala", 4))


def test_capacidade_deve_ser_positiva():
    with pytest.raises(ValueError, match="capacidade"):
        SalaEstudo("S102", "Sala 102", 0)


def test_obter_inexistente():
    with pytest.raises(NaoEncontrado):
        CatalogoRecursos().obter("XXX")
