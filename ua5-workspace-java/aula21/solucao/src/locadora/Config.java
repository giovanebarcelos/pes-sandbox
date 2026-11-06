package locadora;

import java.util.List;
import locadora.compartilhado.BarramentoEventos;
import locadora.compartilhado.EventoDominio;
import locadora.frota.FrotaAPI;
import locadora.frota.adaptadores.VeiculoMemoriaRepository;
import locadora.locacoes.adaptadores.EventStoreArquivo;
import locadora.locacoes.adaptadores.EventStoreMemoria;
import locadora.locacoes.adaptadores.FrotaLocal;
import locadora.locacoes.aplicacao.Faturamento;
import locadora.locacoes.aplicacao.LocacoesAtivas;
import locadora.locacoes.aplicacao.ManipuladorComandos;
import locadora.locacoes.aplicacao.Projecao;
import locadora.locacoes.aplicacao.RankingMensal;
import locadora.locacoes.aplicacao.ServicoConsultas;
import locadora.locacoes.dominio.EventStore;
import locadora.locacoes.dominio.ServicoFrota;
import locadora.locacoes.dominio.VeiculoDevolvido;

/**
 * Composition root: liga os dois contextos.
 *
 * Integração entre contextos por EVENTO: quando Locações publica VeiculoDevolvido,
 * a política abaixo manda a Frota liberar o veículo. Locações não chama
 * liberar() diretamente no comando de devolução.
 *
 * Event store: em memória, ou em arquivo se LOCADORA_EVENTOS=dados/eventos.jsonl
 */
public final class Config {
    public static final List<Object[]> FROTA_EXEMPLO = List.of(
            new Object[] {"carro", "ABC-1234", "Fiat Uno", 120.0},
            new Object[] {"carro", "DEF-5678", "Honda Civic", 200.0},
            new Object[] {"moto", "XYZ-9012", "Honda CG 160", 80.0},
            new Object[] {"caminhao", "CAM-0001", "VW Delivery", 300.0});

    private Config() {
    }

    public static Aplicacao montar() {
        return montar(System.getenv("LOCADORA_EVENTOS"));
    }

    /** @param caminhoEventos arquivo .jsonl do event store, ou null para memória */
    public static Aplicacao montar(String caminhoEventos) {
        BarramentoEventos barramento = new BarramentoEventos();
        FrotaAPI frotaApi = new FrotaAPI(new VeiculoMemoriaRepository(), barramento);
        for (Object[] d : FROTA_EXEMPLO) {
            frotaApi.cadastrar((String) d[0], (String) d[1], (String) d[2], (double) d[3]);
        }
        ServicoFrota servicoFrota = new FrotaLocal(frotaApi::precoBase, frotaApi::reservar, frotaApi::liberar);
        EventStore store = caminhoEventos != null ? new EventStoreArquivo(caminhoEventos) : new EventStoreMemoria();
        LocacoesAtivas ativas = new LocacoesAtivas();
        RankingMensal ranking = new RankingMensal();
        Faturamento faturamento = new Faturamento();
        List<Projecao> projecoes = List.of(ativas, ranking, faturamento);
        for (Projecao p : projecoes) {
            p.reconstruir(store.todos());                       // retoma o estado de execuções anteriores
            barramento.inscrever(EventoDominio.class, p::tratar);
        }
        barramento.inscrever(VeiculoDevolvido.class, e -> servicoFrota.liberar(e.placa()));
        ManipuladorComandos comandos = new ManipuladorComandos(store, servicoFrota, barramento);
        return new Aplicacao(frotaApi, comandos, new ServicoConsultas(ativas, ranking, faturamento), store,
                barramento, projecoes);
    }
}
