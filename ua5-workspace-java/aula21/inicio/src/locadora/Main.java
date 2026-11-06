package locadora;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import locadora.adaptadores.saida.BancoJdbc;
import locadora.aplicacao.LocadoraService;
import locadora.dominio.Veiculo;

/**
 * Demonstração: o MESMO roteiro rodando com adaptadores diferentes.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.Main
 */
public class Main {
    static void roteiro(LocadoraService servico) {
        servico.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5);
        servico.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7);
        List<String> livres = new ArrayList<>();
        for (Veiculo v : servico.listarDisponiveis()) {
            livres.add(v.placa());
        }
        System.out.println("  Disponíveis: " + livres);
        System.out.printf(Locale.ROOT, "  Faturamento: R$ %.2f%n", servico.faturamentoTotal());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("--- Adaptador: memória ---");
        roteiro(Config.montar("memoria", null, true, true));
        Path pasta = Files.createTempDirectory("locadora");
        System.out.println("--- Adaptador: arquivo CSV (" + pasta + ") ---");
        roteiro(Config.montar("arquivo", pasta.toString(), true, true));
        for (String arq : new String[] {"veiculos.csv", "locacoes.csv", ""}) {
            Files.deleteIfExists(pasta.resolve(arq)); // a demo não deixa lixo na pasta temporária
        }
        String url = BancoJdbc.temDriver("jdbc:h2:mem:demo") ? "jdbc:h2:mem:demo" : "jdbc:sqlite::memory:";
        if (BancoJdbc.temDriver(url)) {
            System.out.println("--- Adaptador: JDBC (" + url + ") ---");
            roteiro(Config.montar("jdbc", url, true, true));
        } else {
            System.out.println("--- Adaptador: JDBC — pulado (sem driver em lib/; veja o README) ---");
        }
        System.out.println("LocadoraService não mudou nenhuma linha entre as execuções.");
    }
}
