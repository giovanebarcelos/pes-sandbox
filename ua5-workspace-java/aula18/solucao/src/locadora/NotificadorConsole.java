package locadora;

/** Simula o SMTP do legado imprimindo no terminal. */
public class NotificadorConsole implements Notificador {
    @Override
    public void notificar(String destino, String mensagem) {
        System.out.println("[SMTP] para " + destino + ": " + mensagem);
    }
}
