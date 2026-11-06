package locadora.adaptadores.saida;

import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.Notificador;

/** Guarda as mensagens em vez de enviar — um "fake" para testes. */
public class NotificadorMemoria implements Notificador {
    /** Cada item: {destino, mensagem}. */
    public final List<String[]> enviadas = new ArrayList<>();

    @Override
    public void notificar(String destino, String mensagem) {
        enviadas.add(new String[] {destino, mensagem});
    }
}
