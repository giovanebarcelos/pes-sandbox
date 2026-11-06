"""Adaptador de notificação que imprime no terminal (simula o SMTP)."""

from ...aplicacao.portas import Notificador


class NotificadorConsole(Notificador):
    def notificar(self, destino, mensagem):
        print(f"[SMTP] para {destino}: {mensagem}")
