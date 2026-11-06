package locadora.frota;

import java.util.ArrayList;
import java.util.List;
import locadora.compartilhado.BarramentoEventos;
import locadora.compartilhado.Placa;
import locadora.frota.dominio.Fabrica;
import locadora.frota.dominio.Veiculo;
import locadora.frota.dominio.VeiculoCadastrado;
import locadora.frota.dominio.VeiculoIndisponivel;
import locadora.frota.dominio.VeiculoNaoEncontrado;
import locadora.frota.dominio.VeiculoRepository;

/**
 * Bounded context GESTÃO DE FROTA — "o veículo como ativo": cadastro, estado, preço-base.
 *
 * FrotaAPI é a ÚNICA porta de entrada do contexto (Open Host Service):
 * os outros contextos só falam com a frota por aqui, com tipos simples
 * (String, double, records de dados), nunca com as classes internas de Veiculo.
 */
public class FrotaAPI {
    /** O que a Frota publica sobre um veículo (a "linguagem publicada"). */
    public record VeiculoDados(String placa, String tipo, String modelo, double diaria, boolean disponivel) {
    }

    private final VeiculoRepository repo;
    private final BarramentoEventos barramento;

    public FrotaAPI(VeiculoRepository repositorio, BarramentoEventos barramento) {
        this.repo = repositorio;
        this.barramento = barramento;
    }

    public void cadastrar(String tipo, String placa, String modelo, double diaria) {
        String p = new Placa(placa).valor();
        repo.salvar(Fabrica.criarVeiculo(tipo, p, modelo, diaria));
        if (barramento != null) {
            barramento.publicar(List.of(new VeiculoCadastrado(p, tipo, modelo, diaria)));
        }
    }

    public List<VeiculoDados> listar() {
        List<VeiculoDados> dados = new ArrayList<>();
        for (Veiculo v : repo.listar()) {
            dados.add(new VeiculoDados(v.placa(), v.tipo(), v.modelo(), v.valorDiaria(), v.disponivel()));
        }
        return dados;
    }

    public double precoBase(String placa, int dias) {
        return Math.round(obter(placa).precoBase(dias) * 100) / 100.0;
    }

    public synchronized void reservar(String placa) {
        Veiculo v = obter(placa);
        if (!v.disponivel()) {
            throw new VeiculoIndisponivel(placa);
        }
        v.marcarAlugado();
        repo.salvar(v);
    }

    public synchronized void liberar(String placa) {
        Veiculo v = obter(placa);
        v.marcarDisponivel();
        repo.salvar(v);
    }

    private Veiculo obter(String placa) {
        Veiculo v = repo.buscarPorPlaca(new Placa(placa).valor());
        if (v == null) {
            throw new VeiculoNaoEncontrado(placa);
        }
        return v;
    }
}
