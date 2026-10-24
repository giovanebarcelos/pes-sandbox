"""RF-02 (horários livres) e RF-09 (minhas reservas)."""
from conftest import dia
from reservalab.dominio.erros import NaoEncontrado
import pytest


def test_dia_vazio_tem_faixa_unica_08_22(servico):
    assert servico.horarios_livres("S101", dia(10, 0).date()) == [(dia(10, 8), dia(10, 22))]


def test_horarios_livres_descontam_reservas(servico, ana):
    servico.solicitar(ana, "S101", dia(10, 10), dia(10, 12))
    servico.solicitar(ana, "S101", dia(10, 12), dia(10, 13))  # encostada: faixa livre não se divide
    servico.solicitar(ana, "S101", dia(10, 15), dia(10, 16))
    assert servico.horarios_livres("S101", dia(10, 0).date()) == [
        (dia(10, 8), dia(10, 10)), (dia(10, 13), dia(10, 15)), (dia(10, 16), dia(10, 22)),
    ]


def test_recurso_inexistente(servico):
    with pytest.raises(NaoEncontrado):
        servico.horarios_livres("XXX", dia(10, 0).date())


def test_minhas_reservas_ordenadas_por_inicio(servico, ana):
    servico.solicitar(ana, "S101", dia(11, 10), dia(11, 11))
    servico.solicitar(ana, "S101", dia(10, 10), dia(10, 11))
    assert [r.inicio for r in servico.minhas_reservas(ana)] == [dia(10, 10), dia(11, 10)]
