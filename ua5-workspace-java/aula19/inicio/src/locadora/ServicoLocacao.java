package locadora;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Caso de uso "alugar/devolver" — só ORQUESTRA; não calcula, não grava, não imprime.
 *
 * DIP: recebe repositório, notificador e política de preço prontos (injeção de
 * dependência pelo construtor). Nos testes, entram fakes em memória.
 */
public class ServicoLocacao {
    private final RepositorioVeiculos repo;
    private final Notificador notificador;
    private final PoliticaPreco politica;
    private final List<Locacao> locacoes = new ArrayList<>();

    public ServicoLocacao(RepositorioVeiculos repositorio, Notificador notificador) {
        this(repositorio, notificador, new SemDesconto());
    }

    public ServicoLocacao(RepositorioVeiculos repositorio, Notificador notificador, PoliticaPreco politica) {
        this.repo = repositorio;
        this.notificador = notificador;
        this.politica = politica;
    }

    public void cadastrar(Veiculo veiculo) {
        repo.salvar(veiculo);
    }

    public List<Veiculo> listar() {
        return repo.listar();
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
        repo.salvar(veiculo);
        Locacao locacao = new Locacao(placa, cliente, dias, valor);
        locacoes.add(locacao);
        notificador.notificar(email, String.format(Locale.ROOT,
                "Olá %s, sua locação do %s por %d dia(s) custa R$ %.2f.", cliente, veiculo.modelo(), dias, valor));
        return locacao;
    }

    public void devolver(String placa) {
        Veiculo veiculo = obter(placa);
        veiculo.marcarDisponivel();
        repo.salvar(veiculo);
    }

    public double faturamentoTotal() {
        double total = 0;
        for (Locacao l : locacoes) {
            total += l.valor();
        }
        return PoliticaPreco.arredondar(total);
    }

    private Veiculo obter(String placa) {
        Veiculo veiculo = repo.buscar(placa);
        if (veiculo == null) {
            throw new VeiculoNaoEncontrado(placa);
        }
        return veiculo;
    }
}
