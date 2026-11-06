package locadora;

import java.nio.file.Path;
import java.util.List;
import locadora.adaptadores.saida.BancoJdbc;
import locadora.adaptadores.saida.LocacaoArquivoRepository;
import locadora.adaptadores.saida.LocacaoJdbcRepository;
import locadora.adaptadores.saida.LocacaoMemoriaRepository;
import locadora.adaptadores.saida.NotificadorConsole;
import locadora.adaptadores.saida.NotificadorMemoria;
import locadora.adaptadores.saida.VeiculoArquivoRepository;
import locadora.adaptadores.saida.VeiculoJdbcRepository;
import locadora.adaptadores.saida.VeiculoMemoriaRepository;
import locadora.aplicacao.LocacaoRepository;
import locadora.aplicacao.LocadoraService;
import locadora.aplicacao.Notificador;
import locadora.aplicacao.VeiculoRepository;
import locadora.dominio.DescontoSemanal;
import locadora.dominio.Fabrica;

/**
 * COMPOSITION ROOT — o único lugar que conhece TODOS os adaptadores e os liga ao núcleo.
 *
 * Escolha do adaptador por variável de ambiente (sem mudar código):
 *   LOCADORA_REPO=memoria (padrão) | arquivo | jdbc
 *   LOCADORA_DADOS=dados                              pasta dos CSV (adaptador arquivo)
 *   LOCADORA_JDBC_URL=jdbc:h2:./dados/locadora        URL do banco (adaptador jdbc; driver em lib/)
 */
public final class Config {
    static final List<Object[]> FROTA_EXEMPLO = List.of(
            new Object[] {"carro", "ABC-1234", "Fiat Uno", 120.0},
            new Object[] {"carro", "DEF-5678", "Honda Civic", 200.0},
            new Object[] {"moto", "XYZ-9012", "Honda CG 160", 80.0},
            new Object[] {"caminhao", "CAM-0001", "VW Delivery", 300.0});

    private Config() {
    }

    /** Monta com as variáveis de ambiente. */
    public static LocadoraService montar() {
        String repo = System.getenv().getOrDefault("LOCADORA_REPO", "memoria");
        String destino = repo.equals("jdbc")
                ? System.getenv().getOrDefault("LOCADORA_JDBC_URL", "jdbc:h2:./dados/locadora")
                : System.getenv().getOrDefault("LOCADORA_DADOS", "dados");
        return montar(repo, destino, false, true);
    }

    /**
     * @param repo       "memoria", "arquivo" ou "jdbc"
     * @param destino    pasta (arquivo) ou URL JDBC (jdbc); ignorado em memória
     * @param silencioso true = notificações ficam em memória (testes)
     * @param semear     true = cadastra a frota de exemplo se estiver vazia
     */
    public static LocadoraService montar(String repo, String destino, boolean silencioso, boolean semear) {
        VeiculoRepository veiculos;
        LocacaoRepository locacoes;
        switch (repo) {
            case "memoria":
                veiculos = new VeiculoMemoriaRepository();
                locacoes = new LocacaoMemoriaRepository();
                break;
            case "arquivo":
                veiculos = new VeiculoArquivoRepository(Path.of(destino));
                locacoes = new LocacaoArquivoRepository(Path.of(destino));
                break;
            case "jdbc":
                BancoJdbc banco = new BancoJdbc(destino);
                veiculos = new VeiculoJdbcRepository(banco);
                locacoes = new LocacaoJdbcRepository(banco);
                break;
            default:
                throw new IllegalArgumentException("LOCADORA_REPO inválido: " + repo);
        }
        Notificador notificador = silencioso ? new NotificadorMemoria() : new NotificadorConsole();
        LocadoraService servico = new LocadoraService(veiculos, locacoes, notificador, new DescontoSemanal());
        if (semear && servico.listar().isEmpty()) {
            for (Object[] d : FROTA_EXEMPLO) {
                servico.cadastrar(Fabrica.criarVeiculo((String) d[0], (String) d[1], (String) d[2], (double) d[3]));
            }
        }
        return servico;
    }
}
