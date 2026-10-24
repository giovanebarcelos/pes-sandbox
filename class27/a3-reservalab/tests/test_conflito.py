"""RF-04: nunca duas reservas ativas sobrepostas no mesmo recurso."""
import threading

import pytest

from conftest import dia
from reservalab.dominio.erros import ConflitoDeHorario
from reservalab.dominio.reserva import Reserva, Status


def test_sobreposicao_parcial_e_recusada(servico, ana):
    servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    with pytest.raises(ConflitoDeHorario):
        servico.solicitar(ana, "S101", dia(10, 15), dia(10, 17))


def test_reservas_encostadas_nao_conflitam(servico, ana):  # [início, fim)
    servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    servico.solicitar(ana, "S101", dia(10, 16), dia(10, 18))
    servico.solicitar(ana, "S101", dia(10, 12), dia(10, 14))


def test_mesmo_horario_em_recursos_diferentes_e_permitido(servico, ana, prof):
    servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    servico.solicitar(prof, "LAB2", dia(10, 14), dia(10, 16))


def test_reserva_cancelada_libera_o_horario(servico, ana):
    r = servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))
    servico.cancelar(ana, r.id)
    servico.solicitar(ana, "S101", dia(10, 14), dia(10, 16))


def test_reserva_pendente_tambem_ocupa_o_horario(servico, prof):
    servico.solicitar(prof, "AUD1", dia(13, 19), dia(13, 21))
    with pytest.raises(ConflitoDeHorario):
        servico.solicitar(prof, "AUD1", dia(13, 20), dia(13, 22))


def _disputar(criar_repositorio, n=20):
    resultados = []

    def tentar(i):
        r = Reserva("S101", f"M{i}", dia(10, 14), dia(10, 16), Status.CONFIRMADA)
        try:
            criar_repositorio().adicionar_sem_conflito(r)
            resultados.append("ok")
        except ConflitoDeHorario:
            resultados.append("conflito")

    ts = [threading.Thread(target=tentar, args=(i,)) for i in range(n)]
    [t.start() for t in ts]
    [t.join() for t in ts]
    return resultados


def test_memoria_20_threads_no_mesmo_horario_so_uma_vence():  # race condition
    from reservalab.infra.repositorio_memoria import RepositorioMemoria
    repo = RepositorioMemoria()
    resultados = _disputar(lambda: repo)
    assert resultados.count("ok") == 1 and resultados.count("conflito") == 19


def test_sqlite_20_conexoes_no_mesmo_horario_so_uma_vence(tmp_path):  # BEGIN IMMEDIATE
    from reservalab.infra.repositorio_sqlite import RepositorioSqlite
    arquivo = str(tmp_path / "corrida.db")
    resultados = _disputar(lambda: RepositorioSqlite(arquivo))
    assert resultados.count("ok") == 1 and resultados.count("conflito") == 19
