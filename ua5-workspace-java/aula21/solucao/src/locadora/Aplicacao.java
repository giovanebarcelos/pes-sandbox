package locadora;

import java.util.List;
import locadora.compartilhado.BarramentoEventos;
import locadora.frota.FrotaAPI;
import locadora.locacoes.aplicacao.ManipuladorComandos;
import locadora.locacoes.aplicacao.Projecao;
import locadora.locacoes.aplicacao.ServicoConsultas;
import locadora.locacoes.dominio.EventStore;

/** Agrupa o que a demo, os serviços e os testes precisam (montado pelo Config). */
public class Aplicacao {
    public final FrotaAPI frota;
    public final ManipuladorComandos comandos;
    public final ServicoConsultas consultas;
    public final EventStore store;
    public final BarramentoEventos barramento;
    private final List<Projecao> projecoes;

    Aplicacao(FrotaAPI frota, ManipuladorComandos comandos, ServicoConsultas consultas, EventStore store,
              BarramentoEventos barramento, List<Projecao> projecoes) {
        this.frota = frota;
        this.comandos = comandos;
        this.consultas = consultas;
        this.store = store;
        this.barramento = barramento;
        this.projecoes = projecoes;
    }

    public void reconstruirProjecoes() {
        for (Projecao p : projecoes) {
            p.reconstruir(store.todos());
        }
    }
}
