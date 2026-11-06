package locadora.adaptadores.saida;

import locadora.aplicacao.Notificador;

/** Adaptador de notificação que imprime no terminal (simula o SMTP). */
public class NotificadorConsole implements Notificador {
    @Override
    public void notificar(String destino, String mensagem) {
        System.out.println("[SMTP] para " + destino + ": " + mensagem);
    }
}
