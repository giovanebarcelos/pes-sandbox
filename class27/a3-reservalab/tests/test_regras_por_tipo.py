"""RF-03 e RF-05: regras de cada tipo de recurso."""
import pytest

from conftest import dia
from reservalab.dominio.erros import RegraViolada
from reservalab.dominio.reserva import Status


def test_sala_de_estudo_confirma_na_hora(servico, ana):  # RF-03
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    assert r.status is Status.CONFIRMADA and r.id is not None


def test_sala_aceita_exatamente_2h_e_recusa_2h01(servico, ana):  # RF-05, valor-limite
    servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    with pytest.raises(RegraViolada, match="duração máxima de 2h"):
        servico.solicitar(ana, "S101", dia(11, 14), dia(11, 16, 1))


def test_sala_exige_30min_de_antecedencia(servico, ana, relogio):  # RF-05, valor-limite
    relogio.agora = dia(9, 13, 30)
    servico.solicitar(ana, "S101", dia(9, 14), dia(9, 15))  # exatamente 30 min: ok
    with pytest.raises(RegraViolada, match="antecedência"):
        servico.solicitar(ana, "S101", dia(9, 13, 59), dia(9, 14, 30))


def test_fim_antes_do_inicio_e_recusado(servico, ana):
    with pytest.raises(RegraViolada, match="depois do início"):
        servico.solicitar(ana, "S101", dia(10, 16), dia(10, 14))


def test_laboratorio_recusa_aluno(servico, ana):  # RF-05
    with pytest.raises(RegraViolada, match="ALUNO"):
        servico.solicitar(ana, "LAB2", dia(11, 10), dia(11, 12))


def test_laboratorio_exige_24h_de_antecedencia(servico, prof):  # RF-05
    servico.solicitar(prof, "LAB2", dia(10, 9), dia(10, 11))  # exatamente 24h: ok
    with pytest.raises(RegraViolada, match="24h"):
        servico.solicitar(prof, "LAB2", dia(10, 8, 59), dia(10, 10))


def test_auditorio_fica_pendente_ate_aprovacao(servico, prof):  # RF-05
    r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))
    assert r.status is Status.PENDENTE


def test_auditorio_exige_72h(servico, prof):  # RF-05
    with pytest.raises(RegraViolada, match="72h"):
        servico.solicitar(prof, "AUD1", dia(11, 19), dia(11, 21))
