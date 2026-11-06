package locadora.locacoes.adaptadores;

import java.util.ArrayList;
import java.util.List;
import locadora.compartilhado.EventoDominio;
import locadora.locacoes.dominio.ConflitoDeConcorrencia;
import locadora.locacoes.dominio.EventStore;

/** Event store em memória (testes). Só ACRESCENTA — nunca altera nem apaga um evento. */
public class EventStoreMemoria implements EventStore {
    protected final List<EventoDominio> eventos = new ArrayList<>();

    @Override
    public synchronized void anexar(String idAgregado, List<? extends EventoDominio> novos, int versaoEsperada) {
        int atual = carregar(idAgregado).size();
        if (atual != versaoEsperada) {
            throw new ConflitoDeConcorrencia(idAgregado + ": esperava versão " + versaoEsperada + ", está em " + atual);
        }
        eventos.addAll(novos);
    }

    @Override
    public synchronized List<EventoDominio> carregar(String idAgregado) {
        List<EventoDominio> doAgregado = new ArrayList<>();
        for (EventoDominio e : eventos) {
            if (e.idAgregado().equals(idAgregado)) {
                doAgregado.add(e);
            }
        }
        return doAgregado;
    }

    @Override
    public synchronized List<EventoDominio> todos() {
        return new ArrayList<>(eventos);
    }
}
