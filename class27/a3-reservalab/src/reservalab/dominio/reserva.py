from dataclasses import dataclass
from datetime import datetime, timedelta
from enum import Enum
from typing import Optional

from reservalab.dominio.erros import RegraViolada, TransicaoInvalida

LIMITE_CANCELAMENTO_TARDIO = timedelta(hours=2)


class Status(Enum):
    PENDENTE = "PENDENTE"
    CONFIRMADA = "CONFIRMADA"
    REJEITADA = "REJEITADA"
    CANCELADA = "CANCELADA"


ATIVOS = (Status.PENDENTE, Status.CONFIRMADA)


@dataclass
class Reserva:
    recurso_id: str
    matricula: str
    inicio: datetime
    fim: datetime
    status: Status
    id: Optional[int] = None
    cancelada_em: Optional[datetime] = None
    cancelamento_tardio: bool = False

    @property
    def ativa(self) -> bool:
        return self.status in ATIVOS

    def conflita_com(self, inicio: datetime, fim: datetime) -> bool:
        """Intervalos semiabertos [início, fim): reservas encostadas NÃO conflitam."""
        return self.inicio < fim and inicio < self.fim

    def confirmar(self) -> None:
        self._exigir(Status.PENDENTE, "confirmar")
        self.status = Status.CONFIRMADA

    def rejeitar(self) -> None:
        self._exigir(Status.PENDENTE, "rejeitar")
        self.status = Status.REJEITADA

    def cancelar(self, agora: datetime) -> None:
        """Cancela antes do início; menos de 2h antes conta como tardio."""
        if not self.ativa:
            raise TransicaoInvalida(f"reserva {self.status.value} não pode ser cancelada")
        if agora >= self.inicio:
            raise RegraViolada("a reserva já começou e não pode ser cancelada")
        self.cancelamento_tardio = (self.inicio - agora) < LIMITE_CANCELAMENTO_TARDIO
        self.cancelada_em = agora
        self.status = Status.CANCELADA

    def _exigir(self, esperado: Status, acao: str) -> None:
        if self.status is not esperado:
            raise TransicaoInvalida(f"só reserva {esperado.value} pode {acao} (está {self.status.value})")
