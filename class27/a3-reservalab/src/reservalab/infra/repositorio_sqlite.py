import sqlite3
from datetime import datetime
from typing import List, Optional

from reservalab.dominio.erros import ConflitoDeHorario
from reservalab.dominio.reserva import Reserva, Status

_ATIVOS = "('PENDENTE','CONFIRMADA')"
_ESQUEMA = """
CREATE TABLE IF NOT EXISTS reservas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    recurso_id TEXT NOT NULL,
    matricula TEXT NOT NULL,
    inicio TEXT NOT NULL,
    fim TEXT NOT NULL,
    status TEXT NOT NULL,
    cancelada_em TEXT,
    cancelamento_tardio INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS ix_reservas_recurso ON reservas (recurso_id, inicio);
CREATE INDEX IF NOT EXISTS ix_reservas_matricula ON reservas (matricula);
"""


def _iso(d: Optional[datetime]) -> Optional[str]:
    return d.isoformat(timespec="minutes") if d else None


def _para_reserva(linha) -> Reserva:
    return Reserva(
        id=linha[0], recurso_id=linha[1], matricula=linha[2],
        inicio=datetime.fromisoformat(linha[3]), fim=datetime.fromisoformat(linha[4]),
        status=Status(linha[5]),
        cancelada_em=datetime.fromisoformat(linha[6]) if linha[6] else None,
        cancelamento_tardio=bool(linha[7]),
    )


class RepositorioSqlite:
    """Adaptador SQLite: mesmo contrato do RepositorioMemoria (RNF-03)."""

    def __init__(self, caminho: str = ":memory:") -> None:
        self._con = sqlite3.connect(caminho, isolation_level=None)  # transações manuais
        self._con.executescript(_ESQUEMA)

    def adicionar_sem_conflito(self, reserva: Reserva) -> Reserva:
        self._con.execute("BEGIN IMMEDIATE")  # trava de escrita: fecha a janela de corrida
        try:
            linha = self._con.execute(
                f"SELECT inicio, fim FROM reservas WHERE recurso_id=? AND status IN {_ATIVOS} "
                "AND inicio < ? AND ? < fim LIMIT 1",
                (reserva.recurso_id, _iso(reserva.fim), _iso(reserva.inicio)),
            ).fetchone()
            if linha:
                raise ConflitoDeHorario(
                    f"{reserva.recurso_id} já reservado de {linha[0][11:16]} a {linha[1][11:16]}"
                )
            cur = self._con.execute(
                "INSERT INTO reservas (recurso_id, matricula, inicio, fim, status) VALUES (?,?,?,?,?)",
                (reserva.recurso_id, reserva.matricula, _iso(reserva.inicio), _iso(reserva.fim), reserva.status.value),
            )
            reserva.id = cur.lastrowid
            self._con.execute("COMMIT")
            return reserva
        except BaseException:
            self._con.execute("ROLLBACK")
            raise

    def obter(self, reserva_id: int) -> Optional[Reserva]:
        linha = self._con.execute("SELECT * FROM reservas WHERE id=?", (reserva_id,)).fetchone()
        return _para_reserva(linha) if linha else None

    def salvar(self, reserva: Reserva) -> None:
        self._con.execute(
            "UPDATE reservas SET status=?, cancelada_em=?, cancelamento_tardio=? WHERE id=?",
            (reserva.status.value, _iso(reserva.cancelada_em), int(reserva.cancelamento_tardio), reserva.id),
        )

    def ativas_do_recurso(self, recurso_id: str, inicio: datetime, fim: datetime) -> List[Reserva]:
        linhas = self._con.execute(
            f"SELECT * FROM reservas WHERE recurso_id=? AND status IN {_ATIVOS} AND inicio < ? AND ? < fim",
            (recurso_id, _iso(fim), _iso(inicio)),
        ).fetchall()
        return [_para_reserva(l) for l in linhas]

    def do_usuario(self, matricula: str) -> List[Reserva]:
        linhas = self._con.execute("SELECT * FROM reservas WHERE matricula=?", (matricula,)).fetchall()
        return [_para_reserva(l) for l in linhas]
