"""RNF-01 (desempenho), RNF-02 (domínio puro) e RNF-03 (persistência trocável)."""
import ast
import pathlib
import time
from datetime import datetime, timedelta

from reservalab.dominio.reserva import Reserva, Status

RAIZ = pathlib.Path(__file__).resolve().parents[1] / "src" / "reservalab"


def _imports(pasta: str):
    for arq in (RAIZ / pasta).glob("*.py"):
        for no in ast.walk(ast.parse(arq.read_text())):
            if isinstance(no, ast.Import):
                yield arq.name, [a.name for a in no.names]
            elif isinstance(no, ast.ImportFrom):
                yield arq.name, [no.module or ""]


def test_dominio_nao_importa_aplicacao_nem_infra():  # RNF-02, regra de dependência
    proibidos = ("reservalab.aplicacao", "reservalab.infra", "sqlite3")
    violacoes = [(a, m) for a, mods in _imports("dominio") for m in mods if m.startswith(proibidos)]
    assert violacoes == []


def test_aplicacao_nao_importa_infra():  # RNF-02
    violacoes = [(a, m) for a, mods in _imports("aplicacao") for m in mods
                 if m.startswith(("reservalab.infra", "sqlite3"))]
    assert violacoes == []


def _popular(repositorio, n=10_000):
    base = datetime(2026, 1, 1, 8)
    for i in range(n):  # 10 mil reservas encostadas, em dias diferentes, no mesmo recurso
        inicio = base + timedelta(days=i // 10, hours=i % 10)
        repositorio.adicionar_sem_conflito(Reserva("S101", f"M{i}", inicio, inicio + timedelta(hours=1), Status.CONFIRMADA))


def test_verificar_conflito_em_menos_de_200ms_com_10_mil_reservas(repositorio):  # RNF-01
    _popular(repositorio)
    nova = Reserva("S101", "X", datetime(2030, 1, 1, 10), datetime(2030, 1, 1, 11), Status.CONFIRMADA)
    t0 = time.perf_counter()
    repositorio.adicionar_sem_conflito(nova)
    assert time.perf_counter() - t0 < 0.2


def test_contrato_do_repositorio_ida_e_volta(repositorio):  # RNF-03
    r = repositorio.adicionar_sem_conflito(
        Reserva("S101", "M1", datetime(2026, 11, 10, 14), datetime(2026, 11, 10, 15), Status.CONFIRMADA))
    lida = repositorio.obter(r.id)
    assert (lida.recurso_id, lida.matricula, lida.inicio, lida.fim, lida.status) == \
        ("S101", "M1", r.inicio, r.fim, Status.CONFIRMADA)
    assert repositorio.obter(999) is None
