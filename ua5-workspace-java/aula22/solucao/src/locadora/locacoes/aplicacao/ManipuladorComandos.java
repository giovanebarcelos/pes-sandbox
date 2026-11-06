package locadora.locacoes.aplicacao;

import java.util.NoSuchElementException;
import locadora.compartilhado.BarramentoEventos;
import locadora.compartilhado.Dinheiro;
import locadora.locacoes.dominio.DescontoSemanal;
import locadora.locacoes.dominio.EventStore;
import locadora.locacoes.dominio.Locacao;
import locadora.locacoes.dominio.PoliticaPreco;
import locadora.locacoes.dominio.ServicoFrota;

/** Executa os comandos: carrega o agregado, chama a regra, grava os eventos e os publica. */
public class ManipuladorComandos {
    private final EventStore store;
    private final ServicoFrota frota;
    private final BarramentoEventos barramento;
    private final PoliticaPreco politica;

    public ManipuladorComandos(EventStore store, ServicoFrota frota, BarramentoEventos barramento) {
        this(store, frota, barramento, new DescontoSemanal());
    }

    public ManipuladorComandos(EventStore store, ServicoFrota frota, BarramentoEventos barramento,
                               PoliticaPreco politica) {
        this.store = store;
        this.frota = frota;
        this.barramento = barramento;
        this.politica = politica;
    }

    public Locacao executar(Comando comando) {
        if (comando instanceof IniciarLocacao c) {
            return iniciar(c);
        }
        if (comando instanceof RegistrarDevolucao c) {
            return devolver(c);
        }
        throw new IllegalArgumentException("Comando desconhecido: " + comando);
    }

    private Locacao iniciar(IniciarLocacao c) {
        double base = frota.precoBase(c.placa(), c.diasPrevistos());
        Dinheiro valor = Dinheiro.deReais(politica.aplicar(base, c.diasPrevistos()));
        Locacao locacao = Locacao.iniciar(c.idLocacao(), c.placa(), c.cliente(), c.dataInicio(),
                c.diasPrevistos(), valor);
        frota.reservar(locacao.placa()); // síncrono: precisa saber se está livre
        salvar(locacao, 0);
        return locacao;
    }

    private Locacao devolver(RegistrarDevolucao c) {
        Locacao locacao = Locacao.reconstituir(c.idLocacao(), store.carregar(c.idLocacao()));
        if (locacao.versao() == 0) {
            throw new NoSuchElementException("Locação " + c.idLocacao() + " não existe");
        }
        int versaoAnterior = locacao.versao();
        locacao.registrarDevolucao(c.dataDevolucao());
        salvar(locacao, versaoAnterior);
        return locacao;
    }

    private void salvar(Locacao locacao, int versaoAnterior) {
        store.anexar(locacao.id(), locacao.pendentes(), versaoAnterior);
        barramento.publicar(locacao.pendentes());
        locacao.limparPendentes();
    }
}
