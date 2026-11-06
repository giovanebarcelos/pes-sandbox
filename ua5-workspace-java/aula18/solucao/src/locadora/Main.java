package locadora;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Composição (composition root): o ÚNICO lugar que escolhe as implementações concretas.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.Main
 * Arquivo gravado: frota.csv (ou o caminho da variável LOCADORA_ARQUIVO)
 */
public class Main {
    static ServicoLocacao montarServico(String arquivo) throws IOException {
        Files.deleteIfExists(Path.of(arquivo)); // demo sempre começa do zero
        return new ServicoLocacao(new RepositorioArquivo(arquivo), new NotificadorConsole(), new DescontoSemanal());
    }

    public static void main(String[] args) throws IOException {
        String arquivo = System.getenv().getOrDefault("LOCADORA_ARQUIVO", "frota.csv");
        ServicoLocacao servico = montarServico(arquivo);
        servico.cadastrar(Fabrica.criarVeiculo("carro", "ABC-1234", "Fiat Uno", 120));
        servico.cadastrar(Fabrica.criarVeiculo("carro", "DEF-5678", "Honda Civic", 200));
        servico.cadastrar(Fabrica.criarVeiculo("moto", "XYZ-9012", "Honda CG 160", 80));
        servico.cadastrar(Fabrica.criarVeiculo("caminhao", "CAM-0001", "VW Delivery", 300));
        servico.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5);
        servico.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7);
        System.out.println(Relatorio.frota(servico));
    }
}
