package locadora.aplicacao;

/** Porta de SAÍDA para avisar o cliente (e-mail, SMS, console...). */
public interface Notificador {
    void notificar(String destino, String mensagem);
}
