from typing import Dict, List

from reservalab.dominio.erros import NaoEncontrado
from reservalab.dominio.recursos import Recurso


class CatalogoRecursos:
    """Recursos reserváveis do campus (RF-01). Em memória: ver SDR-001, fora de escopo."""

    def __init__(self) -> None:
        self._recursos: Dict[str, Recurso] = {}

    def registrar(self, recurso: Recurso) -> None:
        if recurso.id in self._recursos:
            raise ValueError(f"recurso {recurso.id} já registrado")
        self._recursos[recurso.id] = recurso

    def obter(self, recurso_id: str) -> Recurso:
        try:
            return self._recursos[recurso_id]
        except KeyError:
            raise NaoEncontrado(f"recurso {recurso_id} não existe") from None

    def listar(self) -> List[Recurso]:
        return sorted(self._recursos.values(), key=lambda r: r.id)
