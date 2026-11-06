"""
OUTBOX — comunicação ASSÍNCRONA com nova tentativa.

O caso de uso grava o que precisa ser avisado/feito em outro serviço numa
fila local e responde ao usuário NA HORA. Um "entregador" em segundo plano
tenta enviar; se o outro serviço estiver fora do ar, tenta de novo depois.
Resultado: Notificação parada NÃO impede uma locação (sem falha em cascata).
"""

import threading
import time


class Outbox:
    def __init__(self, entregar, intervalo=1.0):
        """entregar(tarefa) deve lançar exceção se não conseguiu."""
        self._entregar = entregar
        self._intervalo = intervalo
        self._pendentes = []
        self._ids = set()
        self._trava = threading.Lock()
        self.ultimo_erro = None

    def adicionar(self, tarefa):
        with self._trava:
            if tarefa["id"] not in self._ids:
                self._ids.add(tarefa["id"])
                self._pendentes.append(tarefa)

    def pendentes(self):
        with self._trava:
            return list(self._pendentes)

    def entregar_pendentes(self):
        for tarefa in self.pendentes():
            try:
                self._entregar(tarefa)
            except Exception as e:  # noqa: BLE001 — fica na fila para a próxima rodada
                self.ultimo_erro = f"{tarefa['id']}: {e}"
                continue
            with self._trava:
                self._pendentes.remove(tarefa)
        restantes = len(self.pendentes())
        if restantes == 0:
            self.ultimo_erro = None
        return restantes

    def iniciar(self):
        def laco():
            while True:
                self.entregar_pendentes()
                time.sleep(self._intervalo)

        threading.Thread(target=laco, daemon=True).start()
        return self
