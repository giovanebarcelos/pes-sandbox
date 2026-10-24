"""Demonstração em linha de comando: `python -m reservalab.cli demo`."""
import sys
from datetime import datetime

from reservalab.aplicacao.catalogo import CatalogoRecursos
from reservalab.aplicacao.servico_reservas import ServicoReservas
from reservalab.dominio.erros import ErroDeDominio
from reservalab.dominio.recursos import Auditorio, Laboratorio, SalaEstudo
from reservalab.dominio.usuario import Perfil, Usuario
from reservalab.infra.repositorio_sqlite import RepositorioSqlite


def montar(caminho_db: str = ":memory:", relogio=datetime.now) -> ServicoReservas:
    catalogo = CatalogoRecursos()
    catalogo.registrar(SalaEstudo("S101", "Sala de estudo 101", 6))
    catalogo.registrar(Laboratorio("LAB2", "Laboratório de Redes", 30))
    catalogo.registrar(Auditorio("AUD1", "Auditório Central", 200))
    return ServicoReservas(RepositorioSqlite(caminho_db), catalogo, relogio)


def demo() -> None:
    hoje = datetime(2026, 11, 9, 9, 0)  # relógio fixo: a saída é reproduzível
    servico = montar(relogio=lambda: hoje)
    ana = Usuario("20261001", "Ana", Perfil.ALUNO)
    prof = Usuario("P0042", "Prof. Beto", Perfil.PROFESSOR)
    coord = Usuario("C0001", "Coord. Carla", Perfil.COORDENADOR)
    d = lambda dia, h: datetime(2026, 11, dia, h)

    passos = [
        ("Ana reserva S101 das 14h às 16h (10/11)", lambda: servico.solicitar(ana, "S101", d(10, 14), d(10, 16))),
        ("Ana tenta 15h-17h na mesma sala", lambda: servico.solicitar(ana, "S101", d(10, 15), d(10, 17))),
        ("Ana tenta o laboratório", lambda: servico.solicitar(ana, "LAB2", d(11, 10), d(11, 12))),
        ("Prof. Beto reserva o auditório (13/11)", lambda: servico.solicitar(prof, "AUD1", d(13, 19), d(13, 21))),
    ]
    for texto, acao in passos:
        try:
            r = acao()
            print(f"OK    {texto} -> reserva #{r.id} {r.status.value}")
        except ErroDeDominio as e:
            print(f"ERRO  {texto} -> {e}")
    print("Coord. Carla aprova a reserva #2 ->", servico.decidir(coord, 2, True).status.value)
    print("Livres em S101 no dia 10:")
    for ini, fim in servico.horarios_livres("S101", d(10, 8).date()):
        print(f"  {ini:%H:%M} - {fim:%H:%M}")


if __name__ == "__main__":
    if sys.argv[1:] == ["demo"]:
        demo()
    else:
        print("uso: python -m reservalab.cli demo")
