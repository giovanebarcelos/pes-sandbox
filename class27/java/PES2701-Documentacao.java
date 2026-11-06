/*
 * PES2701 - Geração Automática de Documentação (Javadoc)
 * Aula 27: Documentação Técnica e Projeto Integrador Final
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra documentação inline com Javadoc e extração automática de
 * informações para documentação, usando como exemplo o cálculo de
 * locação de veículos do Projeto Integrador (Sistema Locadora).
 * (Versão Java de class27/python/PES2701-Documentacao.py.)
 *
 * Diferença em relação ao Python: lá a docstring fica disponível em tempo de
 * execução (inspect.getdoc). No Java o comentário Javadoc NÃO vai para o
 * .class — por isso a extração lê o próprio código-fonte, exatamente como a
 * ferramenta oficial faz:  javadoc -d docs PES2701-Documentacao.java
 *
 * Executar (Java 17+, nesta pasta):  java PES2701-Documentacao.java
 * Ou compilar:  javac PES2701-Documentacao.java && java Documentacao PES2701-Documentacao.java
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Programa principal: calcula locações e extrai a documentação deste arquivo.
 *
 * @author Prof. Giovane Barcelos
 * @version 1.0
 */
class Documentacao {

    /** Um item documentado: a declaração (assinatura) e o texto do Javadoc. */
    record ItemDoc(String declaracao, String javadoc) {
    }

    /**
     * Calcula o valor total da locação de um veículo.
     *
     * @param veiculo  instância de Veiculo (Carro ou Moto)
     * @param dias     número de dias da locação (deve ser >= 1)
     * @return         valor total da locação em reais
     * @throws IllegalArgumentException se dias < 1
     * @throws NullPointerException     se veiculo for null
     *
     * <pre>{@code
     *   Carro carro = new Carro("ABC-1234", "Gol", 100.0, 4);
     *   double total = calcularLocacao(carro, 5); // 500.0
     * }</pre>
     */
    public static double calcularLocacao(Veiculo veiculo, int dias) {
        Objects.requireNonNull(veiculo, "veiculo não pode ser null");
        if (dias < 1) {
            throw new IllegalArgumentException("dias deve ser >= 1");
        }
        return veiculo.calcularLocacao(dias);
    }

    /**
     * Extrai os comentários Javadoc de um arquivo-fonte Java, junto com a
     * declaração (classe, construtor ou método) que vem logo depois de cada um.
     *
     * @param arquivo caminho do arquivo .java a ser lido
     * @return lista com a declaração e o texto de cada Javadoc, na ordem do arquivo
     * @throws IOException se o arquivo não puder ser lido
     */
    public static List<ItemDoc> extrairJavadoc(Path arquivo) throws IOException {
        String fonte = Files.readString(arquivo, StandardCharsets.UTF_8);
        // comentário Javadoc seguido (depois de espaços) pela primeira linha da declaração
        Matcher m = Pattern.compile("/\\*\\*(.*?)\\*/\\s*([^\\n{;]+)", Pattern.DOTALL).matcher(fonte);
        List<ItemDoc> itens = new ArrayList<>();
        while (m.find()) {
            List<String> linhas = new ArrayList<>();
            for (String linha : m.group(1).split("\\R")) {
                linhas.add(linha.replaceFirst("^\\s*\\* ?", "").stripTrailing());
            }
            String texto = String.join("\n", linhas).strip();
            itens.add(new ItemDoc(m.group(2).strip(), texto));
        }
        return itens;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=".repeat(60));
        System.out.println("  DOCUMENTAÇÃO EXTRAÍDA AUTOMATICAMENTE");
        System.out.println("=".repeat(60));

        Carro carro = new Carro("ABC-1234", "Gol", 100.0, 4);
        Moto moto = new Moto("XYZ-5678", "CG 160", 60.0, 160);

        System.out.printf(Locale.ROOT, "%nLocação do carro por 5 dias: R$ %.2f%n", calcularLocacao(carro, 5));
        System.out.printf(Locale.ROOT, "Locação da moto por 3 dias: R$ %.2f%n", calcularLocacao(moto, 3));

        Path arquivo = Path.of(args.length > 0 ? args[0] : "PES2701-Documentacao.java");
        if (!Files.exists(arquivo)) {
            System.out.println("\n(Não achei " + arquivo + ". Rode na pasta class27/java ou passe o caminho do .java.)");
            return;
        }
        List<ItemDoc> docs = extrairJavadoc(arquivo);
        for (ItemDoc item : docs) {
            System.out.println("\n--- " + item.declaracao() + " ---");
            System.out.println("  " + item.javadoc().replace("\n", "\n  "));
        }

        System.out.println("\n✓ " + docs.size() + " elementos documentados.");
        System.out.println("✓ Javadoc bem escrito é a base de uma boa documentação técnica.");
        System.out.println("✓ Gere o HTML oficial com: javadoc -d docs PES2701-Documentacao.java");
    }
}

/**
 * Classe base para veículos disponíveis na locadora.
 *
 * <p>Atributos: placa (identificação do veículo), modelo (nome/modelo do
 * veículo) e valorDiaria (valor cobrado por dia de locação).
 */
class Veiculo {
    final String placa;
    final String modelo;
    final double valorDiaria;

    Veiculo(String placa, String modelo, double valorDiaria) {
        this.placa = placa;
        this.modelo = modelo;
        this.valorDiaria = valorDiaria;
    }

    /**
     * Calcula o valor da locação deste veículo para um número de dias.
     *
     * @param dias número de dias da locação
     * @return valor total (valorDiaria * dias)
     */
    double calcularLocacao(int dias) {
        return valorDiaria * dias;
    }
}

/** Veículo do tipo carro, com número de portas. */
class Carro extends Veiculo {
    final int portas;

    Carro(String placa, String modelo, double valorDiaria, int portas) {
        super(placa, modelo, valorDiaria);
        this.portas = portas;
    }
}

/** Veículo do tipo moto, com cilindrada. */
class Moto extends Veiculo {
    final int cilindrada;

    Moto(String placa, String modelo, double valorDiaria, int cilindrada) {
        super(placa, modelo, valorDiaria);
        this.cilindrada = cilindrada;
    }
}
