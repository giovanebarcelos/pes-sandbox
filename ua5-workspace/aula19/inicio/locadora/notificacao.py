"""
Notificação separada do caso de uso (SRP) e com interface enxuta (ISP).

O legado chamaria um "ServicoExterno" com enviar_email, enviar_sms, gerar_pdf...
Aqui o serviço só conhece o que usa: `notificar(destino, mensagem)`.
"""

from abc import ABC, abstractmethod


class Notificador(ABC):
    @abstractmethod
    def notificar(self, destino, mensagem): ...


class NotificadorConsole(Notificador):
    """Simula o SMTP do legado imprimindo no terminal."""

    def notificar(self, destino, mensagem):
        print(f"[SMTP] para {destino}: {mensagem}")


class NotificadorMemoria(Notificador):
    """Guarda as mensagens — útil em testes (é um 'fake')."""

    def __init__(self):
        self.enviadas = []

    def notificar(self, destino, mensagem):
        self.enviadas.append((destino, mensagem))
