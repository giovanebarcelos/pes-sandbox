"""Portas (interfaces) que a aplicação exige do mundo externo."""
from datetime import datetime
from typing import Callable, List, Optional, Protocol

from reservalab.dominio.reserva import Reserva

Relogio = Callable[[], datetime]


class RepositorioReservas(Protocol):
    def adicionar_sem_conflito(self, reserva: Reserva) -> Reserva:
        """Grava a reserva SE não houver conflito, de forma atômica.

        Verificar e gravar em chamadas separadas permitiria duas reservas
        simultâneas no mesmo horário (race condition) — por isso é uma operação só.

        Raises:
            ConflitoDeHorario: já existe reserva ativa sobrepondo o intervalo.
        """

    def obter(self, reserva_id: int) -> Optional[Reserva]: ...

    def salvar(self, reserva: Reserva) -> None: ...

    def ativas_do_recurso(self, recurso_id: str, inicio: datetime, fim: datetime) -> List[Reserva]: ...

    def do_usuario(self, matricula: str) -> List[Reserva]: ...
