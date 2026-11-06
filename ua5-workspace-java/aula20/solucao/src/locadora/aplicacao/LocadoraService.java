package locadora.aplicacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import locadora.dominio.Locacao;
import locadora.dominio.PoliticaPreco;
import locadora.dominio.SemDesconto;
import locadora.dominio.Veiculo;
import locadora.dominio.VeiculoIndisponivel;
import locadora.dominio.VeiculoNaoEncontrado;

/**
 * LocadoraService — casos de uso da locadora (porta de ENTRADA / driving port).
 *
 * Só depende do domínio e das portas. Trocar memória por arquivo ou JDBC, ou
 * terminal por web, NÃO altera nenhuma linha deste arquivo.
 */
public class LocadoraService {
    private final VeiculoRepository veiculos;
    private final LocacaoRepository locacoes;
    private final Notificador notificador;
    private final PoliticaPreco politica;

    public LocadoraService(VeiculoRepository veiculos, LocacaoRepository locacoes, Notificador notificador) {
        this(veiculos, locacoes, notificador, new SemDesconto());
    }

    public LocadoraService(VeiculoRepository veiculos, LocacaoRepository locacoes, Notificador notificador,
                           PoliticaPreco politica) {
        this.veiculos = veiculos;
        this.locacoes = locacoes;
        this.notificador = notificador;
        this.politica = politica;
    }

    public void cadastrar(Veiculo veiculo) {
        veiculos.salvar(veiculo);
    }

    public List<Veiculo> listar() {
        return veiculos.listar();
    }

    public List<Veiculo> listarDisponiveis() {
        List<Veiculo> livres = new ArrayList<>();
        for (Veiculo v : veiculos.listar()) {
            if (v.disponivel()) {
                livres.add(v);
            }
        }
        return livres;
    }

    public double cotar(String placa, int dias) {
        return politica.aplicar(obter(placa).precoBase(dias), dias);
    }

    public Locacao alugar(String placa, String cliente, String email, int dias) {
        Veiculo veiculo = obter(placa);
        if (!veiculo.disponivel()) {
            throw new VeiculoIndisponivel(placa);
        }
        double valor = politica.aplicar(veiculo.precoBase(dias), dias);
        veiculo.marcarAlugado();
        veiculos.salvar(veiculo);
        Locacao locacao = new Locacao(placa, cliente, dias, valor);
        locacoes.adicionar(locacao);
        notificador.notificar(email, String.format(Locale.ROOT,
                "Olá %s, sua locação do %s por %d dia(s) custa R$ %.2f.", cliente, veiculo.modelo(), dias, valor));
        return locacao;
    }

    public void devolver(String placa) {
        Veiculo veiculo = obter(placa);
        veiculo.marcarDisponivel();
        veiculos.salvar(veiculo);
    }

    public double faturamentoTotal() {
        double total = 0;
        for (Locacao l : locacoes.listar()) {
            total += l.valor();
        }
        return PoliticaPreco.arredondar(total);
    }

    private Veiculo obter(String placa) {
        Veiculo veiculo = veiculos.buscarPorPlaca(placa);
        if (veiculo == null) {
            throw new VeiculoNaoEncontrado(placa);
        }
        return veiculo;
    }
}
