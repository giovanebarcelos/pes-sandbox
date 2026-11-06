package locadora;

import java.util.ArrayList;
import java.util.List;

/** Guarda as mensagens — útil em testes (é um "fake"). */
public class NotificadorMemoria implements Notificador {
    /** Cada item: {destino, mensagem}. */
    public final List<String[]> enviadas = new ArrayList<>();

    @Override
    public void notificar(String destino, String mensagem) {
        enviadas.add(new String[] {destino, mensagem});
    }
}
