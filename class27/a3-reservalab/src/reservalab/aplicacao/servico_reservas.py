from datetime import date, datetime, timedelta
from typing import List, Tuple

from reservalab.aplicacao.catalogo import CatalogoRecursos
from reservalab.aplicacao.portas import RepositorioReservas, Relogio
from reservalab.dominio.erros import NaoAutorizado, NaoEncontrado, UsuarioBloqueado
from reservalab.dominio.reserva import Reserva, Status
from reservalab.dominio.usuario import Perfil, Usuario

JANELA_PENALIDADE = timedelta(days=30)
LIMITE_TARDIOS = 3
ABERTURA, FECHAMENTO = 8, 22  # horário de funcionamento (RF-02)


class ServicoReservas:
    """Casos de uso do sistema. Não conhece banco nem interface (ADR-001)."""

    def __init__(self, reservas: RepositorioReservas, catalogo: CatalogoRecursos, relogio: Relogio):
        self._reservas = reservas
        self._catalogo = catalogo
        self._agora = relogio

    def solicitar(self, usuario: Usuario, recurso_id: str, inicio: datetime, fim: datetime) -> Reserva:
        """RF-03/04/05/07: valida regras do tipo, bloqueio e conflito; grava."""
        recurso = self._catalogo.obter(recurso_id)
        agora = self._agora()
        self._exigir_nao_bloqueado(usuario, agora)
        recurso.validar(usuario, inicio, fim, agora)
        status = Status.PENDENTE if recurso.exige_aprovacao else Status.CONFIRMADA
        reserva = Reserva(recurso_id, usuario.matricula, inicio, fim, status)
        return self._reservas.adicionar_sem_conflito(reserva)

    def cancelar(self, usuario: Usuario, reserva_id: int) -> Reserva:
        """RF-06: dono ou coordenador cancela; a regra dos 2h mora na Reserva."""
        reserva = self._buscar(reserva_id)
        if reserva.matricula != usuario.matricula and usuario.perfil is not Perfil.COORDENADOR:
            raise NaoAutorizado("só o dono ou um coordenador cancela esta reserva")
        reserva.cancelar(self._agora())
        self._reservas.salvar(reserva)
        return reserva

    def decidir(self, coordenador: Usuario, reserva_id: int, aprovar: bool) -> Reserva:
        """RF-08: só coordenador aprova ou rejeita reserva pendente."""
        if coordenador.perfil is not Perfil.COORDENADOR:
            raise NaoAutorizado("só coordenador aprova ou rejeita reservas")
        reserva = self._buscar(reserva_id)
        reserva.confirmar() if aprovar else reserva.rejeitar()
        self._reservas.salvar(reserva)
        return reserva

    def horarios_livres(self, recurso_id: str, dia: date) -> List[Tuple[datetime, datetime]]:
        """RF-02: faixas livres entre 08h e 22h do dia."""
        self._catalogo.obter(recurso_id)
        abre = datetime(dia.year, dia.month, dia.day, ABERTURA)
        fecha = datetime(dia.year, dia.month, dia.day, FECHAMENTO)
        ocupadas = sorted(self._reservas.ativas_do_recurso(recurso_id, abre, fecha), key=lambda r: r.inicio)
        livres, cursor = [], abre
        for r in ocupadas:
            if r.inicio > cursor:
                livres.append((cursor, r.inicio))
            cursor = max(cursor, r.fim)
        if cursor < fecha:
            livres.append((cursor, fecha))
        return livres

    def minhas_reservas(self, usuario: Usuario) -> List[Reserva]:
        """RF-09."""
        return sorted(self._reservas.do_usuario(usuario.matricula), key=lambda r: r.inicio)

    def _buscar(self, reserva_id: int) -> Reserva:
        reserva = self._reservas.obter(reserva_id)
        if reserva is None:
            raise NaoEncontrado(f"reserva {reserva_id} não existe")
        return reserva

    def _exigir_nao_bloqueado(self, usuario: Usuario, agora: datetime) -> None:
        tardios = [
            r for r in self._reservas.do_usuario(usuario.matricula)
            if r.cancelamento_tardio and r.cancelada_em and agora - r.cancelada_em <= JANELA_PENALIDADE
        ]
        if len(tardios) >= LIMITE_TARDIOS:
            raise UsuarioBloqueado(
                f"{len(tardios)} cancelamentos tardios nos últimos 30 dias: novas reservas bloqueadas"
            )
