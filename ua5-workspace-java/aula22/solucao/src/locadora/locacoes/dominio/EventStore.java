package locadora.locacoes.dominio;

import java.util.List;
import locadora.compartilhado.EventoDominio;

/** Porta do armazenamento de eventos (a "fonte da verdade" do Event Sourcing). */
public interface EventStore {
    /** Grava eventos novos. Falha com ConflitoDeConcorrencia se a versão mudou. */
    void anexar(String idAgregado, List<? extends EventoDominio> eventos, int versaoEsperada);

    /** Eventos de um agregado, em ordem. */
    List<EventoDominio> carregar(String idAgregado);

    /** Todos os eventos, em ordem de gravação (para reconstruir projeções). */
    List<EventoDominio> todos();
}
