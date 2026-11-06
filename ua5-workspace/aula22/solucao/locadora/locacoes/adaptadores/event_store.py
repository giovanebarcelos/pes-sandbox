"""
Event stores: em memória (testes) e em arquivo JSON Lines (um evento por linha, só ACRESCENTA).
"""

import json
import os
import threading

from ...compartilhado.eventos import evento_de_dict
from ..dominio.erros import ConflitoDeConcorrencia
from ..dominio.portas import EventStore


class EventStoreMemoria(EventStore):
    def __init__(self):
        self._eventos = []   # [(id_agregado, evento)]
        self._trava = threading.Lock()

    def anexar(self, id_agregado, eventos, versao_esperada):
        with self._trava:
            atual = sum(1 for i, _ in self._eventos if i == id_agregado)
            if atual != versao_esperada:
                raise ConflitoDeConcorrencia(
                    f"{id_agregado}: esperava versão {versao_esperada}, está em {atual}")
            self._eventos += [(id_agregado, e) for e in eventos]

    def carregar(self, id_agregado):
        return [e for i, e in self._eventos if i == id_agregado]

    def todos(self):
        return [e for _, e in self._eventos]


class EventStoreArquivo(EventStoreMemoria):
    """Mesmo comportamento, mas cada evento também vai para uma linha do arquivo .jsonl."""

    def __init__(self, caminho):
        super().__init__()
        self._caminho = caminho
        os.makedirs(os.path.dirname(caminho) or ".", exist_ok=True)
        if os.path.exists(caminho):
            with open(caminho, encoding="utf-8") as f:
                for linha in f:
                    if linha.strip():
                        e = evento_de_dict(json.loads(linha))
                        self._eventos.append((e.id_agregado, e))

    def anexar(self, id_agregado, eventos, versao_esperada):
        super().anexar(id_agregado, eventos, versao_esperada)
        with open(self._caminho, "a", encoding="utf-8") as f:
            for e in eventos:
                f.write(json.dumps(e.para_dict(), ensure_ascii=False) + "\n")
