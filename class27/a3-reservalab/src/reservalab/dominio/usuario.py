from dataclasses import dataclass
from enum import Enum


class Perfil(Enum):
    ALUNO = "ALUNO"
    PROFESSOR = "PROFESSOR"
    COORDENADOR = "COORDENADOR"


@dataclass(frozen=True)
class Usuario:
    """Só matrícula, nome e perfil: nada além do necessário (LGPD, RNF-05)."""

    matricula: str
    nome: str
    perfil: Perfil
