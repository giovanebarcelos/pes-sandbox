package locadora;

/**
 * Notificação separada do caso de uso (SRP) e com interface enxuta (ISP).
 *
 * O legado chamaria um "ServicoExterno" com enviarEmail, enviarSms, gerarPdf...
 * Aqui o serviço só conhece o que usa: notificar(destino, mensagem).
 */
public interface Notificador {
    void notificar(String destino, String mensagem);
}
