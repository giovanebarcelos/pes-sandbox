"""
Persistência separada da regra de negócio (SRP + DIP).

O serviço depende da ABSTRAÇÃO RepositorioVeiculos; quem decide se é
memória (testes) ou arquivo JSON (produção) é o `main.py`.
"""

import json
from abc import ABC, abstractmethod

from .fabrica import criar_veiculo


class RepositorioVeiculos(ABC):
    @abstractmethod
    def salvar(self, veiculo): ...

    @abstractmethod
    def buscar(self, placa):
        """Devolve o veículo ou None."""

    @abstractmethod
    def listar(self): ...


class RepositorioMemoria(RepositorioVeiculos):
    def __init__(self):
        self._dados = {}

    def salvar(self, veiculo):
        self._dados[veiculo.placa] = veiculo

    def buscar(self, placa):
        return self._dados.get(placa)

    def listar(self):
        return list(self._dados.values())


class RepositorioJson(RepositorioVeiculos):
    """Mesmo formato de arquivo do legado: lista de dicionários."""

    def __init__(self, arquivo):
        self._arquivo = arquivo
        self._dados = {}
        try:
            with open(arquivo, encoding="utf-8") as f:
                for d in json.load(f):
                    v = criar_veiculo(d["tipo"], d["placa"], d["modelo"], d["diaria"])
                    if not d.get("disponivel", True):
                        v.marcar_alugado()
                    self._dados[v.placa] = v
        except FileNotFoundError:
            pass

    def salvar(self, veiculo):
        self._dados[veiculo.placa] = veiculo
        with open(self._arquivo, "w", encoding="utf-8") as f:
            json.dump([
                {"tipo": v.tipo, "placa": v.placa, "modelo": v.modelo,
                 "diaria": v.valor_diaria, "disponivel": v.disponivel}
                for v in self._dados.values()
            ], f, ensure_ascii=False, indent=2)

    def buscar(self, placa):
        return self._dados.get(placa)

    def listar(self):
        return list(self._dados.values())
