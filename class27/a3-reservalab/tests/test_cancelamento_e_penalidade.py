"""RF-06, RF-07 e RF-08: cancelamento, bloqueio e aprovação."""
from datetime import timedelta

import pytest

from conftest import dia
from reservalab.dominio.erros import NaoAutorizado, RegraViolada, TransicaoInvalida, UsuarioBloqueado
from reservalab.dominio.reserva import Status


def test_cancelar_com_antecedencia_nao_e_tardio(servico, ana, relogio):  # RF-06
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    relogio.agora = dia(10, 11, 59)  # 2h01 antes
    r = servico.cancelar(ana, r.id)
    assert r.status is Status.CANCELADA and not r.cancelamento_tardio


def test_cancelar_com_menos_de_2h_e_tardio(servico, ana, relogio):  # RF-06, valor-limite
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    relogio.agora = dia(10, 12, 1)  # 1h59 antes
    assert servico.cancelar(ana, r.id).cancelamento_tardio


def test_exatamente_2h_antes_nao_e_tardio(servico, ana, relogio):  # RF-06, valor-limite
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    relogio.agora = dia(10, 12, 0)
    assert not servico.cancelar(ana, r.id).cancelamento_tardio


def test_nao_cancela_reserva_ja_iniciada(servico, ana, relogio):
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    relogio.agora = dia(10, 14, 0)
    with pytest.raises(RegraViolada, match="já começou"):
        servico.cancelar(ana, r.id)


def test_nao_cancela_duas_vezes(servico, ana):
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    servico.cancelar(ana, r.id)
    with pytest.raises(TransicaoInvalida):
        servico.cancelar(ana, r.id)


def test_outro_aluno_nao_cancela_reserva_alheia(servico, ana, prof):
    from reservalab.dominio.usuario import Perfil, Usuario
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    with pytest.raises(NaoAutorizado):
        servico.cancelar(Usuario("999", "Bia", Perfil.ALUNO), r.id)


def test_coordenador_pode_cancelar_reserva_alheia(servico, ana, coord):
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    assert servico.cancelar(coord, r.id).status is Status.CANCELADA


def _cancelar_tardio(servico, ana, relogio, dia_reserva):
    r = servico.solicitar(ana, "S101", dia(dia_reserva, 14), dia(dia_reserva, 15))
    relogio.agora = dia(dia_reserva, 13, 0)  # 1h antes -> tardio
    servico.cancelar(ana, r.id)


def test_tres_cancelamentos_tardios_bloqueiam(servico, ana, relogio):  # RF-07
    for d in (10, 11, 12):
        relogio.agora = dia(9, 9, 0)
        _cancelar_tardio(servico, ana, relogio, d)
    relogio.agora = dia(12, 20, 0)
    with pytest.raises(UsuarioBloqueado, match="3 cancelamentos"):
        servico.solicitar(ana, "S101", dia(16, 14), dia(16, 15))


def test_dois_cancelamentos_tardios_nao_bloqueiam(servico, ana, relogio):  # RF-07, valor-limite
    for d in (10, 11):
        relogio.agora = dia(9, 9, 0)
        _cancelar_tardio(servico, ana, relogio, d)
    relogio.agora = dia(11, 20, 0)
    servico.solicitar(ana, "S101", dia(16, 14), dia(16, 15))


def test_bloqueio_expira_apos_30_dias(servico, ana, relogio):  # RF-07
    for d in (10, 11, 12):
        relogio.agora = dia(9, 9, 0)
        _cancelar_tardio(servico, ana, relogio, d)
    relogio.agora = dia(12, 13, 0) + timedelta(days=30, minutes=1)
    servico.solicitar(ana, "S101", relogio.agora + timedelta(hours=1), relogio.agora + timedelta(hours=2))


def test_coordenador_aprova_reserva_pendente(servico, prof, coord):  # RF-08
    r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))
    assert servico.decidir(coord, r.id, aprovar=True).status is Status.CONFIRMADA


def test_coordenador_rejeita_e_horario_fica_livre(servico, prof, coord):  # RF-08
    r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))
    servico.decidir(coord, r.id, aprovar=False)
    servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))


def test_professor_nao_aprova(servico, prof):  # RF-08
    r = servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))
    with pytest.raises(NaoAutorizado):
        servico.decidir(prof, r.id, aprovar=True)


def test_nao_aprova_reserva_ja_confirmada(servico, ana, coord):
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    with pytest.raises(TransicaoInvalida):
        servico.decidir(coord, r.id, aprovar=True)
