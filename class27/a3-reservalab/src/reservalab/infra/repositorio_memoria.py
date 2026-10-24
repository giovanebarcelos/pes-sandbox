import copy
import threading
from datetime import datetime
from typing import Dict, List, Optional

from reservalab.dominio.erros import ConflitoDeHorario
from reservalab.dominio.reserva import Reserva


class RepositorioMemoria:
    """Adaptador em memória: usado nos testes e como referência de comportamento."""

    def __init__(self) -> None:
        self._por_id: Dict[int, Reserva] = {}
        self._por_recurso: Dict[str, List[Reserva]] = {}
        self._proximo = 1
        self._trava = threading.Lock()

    def adicionar_sem_conflito(self, reserva: Reserva) -> Reserva:
        with self._trava:
            for existente in self._por_recurso.get(reserva.recurso_id, []):
                if existente.ativa and existente.conflita_com(reserva.inicio, reserva.fim):
                    raise ConflitoDeHorario(
                        f"{reserva.recurso_id} já reservado de {existente.inicio:%H:%M} a {existente.fim:%H:%M}"
                    )
            reserva.id = self._proximo
            self._proximo += 1
            self._por_id[reserva.id] = copy.copy(reserva)
            self._por_recurso.setdefault(reserva.recurso_id, []).append(self._por_id[reserva.id])
            return reserva

    def obter(self, reserva_id: int) -> Optional[Reserva]:
        r = self._por_id.get(reserva_id)
        return copy.copy(r) if r else None

    def salvar(self, reserva: Reserva) -> None:
        with self._trava:
            atual = self._por_id[reserva.id]
            atual.status = reserva.status
            atual.cancelada_em = reserva.cancelada_em
            atual.cancelamento_tardio = reserva.cancelamento_tardio

    def ativas_do_recurso(self, recurso_id: str, inicio: datetime, fim: datetime) -> List[Reserva]:
        return [copy.copy(r) for r in self._por_recurso.get(recurso_id, []) if r.ativa and r.conflita_com(inicio, fim)]

    def do_usuario(self, matricula: str) -> List[Reserva]:
        return [copy.copy(r) for r in self._por_id.values() if r.matricula == matricula]
