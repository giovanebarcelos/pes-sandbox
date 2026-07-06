/*
 * PES2701 - Geração de Documentação (Javadoc)
 * Aula 27: Documentação Técnica e Projeto Integrador Final
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra documentação inline com Javadoc, usando como exemplo o
 * cálculo de locação de veículos do Projeto Integrador (Sistema Locadora).
 *
 * Compilar/executar:
 *   javac PES2701-Documentacao.java && java Documentacao
 */

/** Classe base para veículos disponíveis na locadora. */
class Veiculo {
    String placa;
    String modelo;
    double valorDiaria;

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
    int portas;

    Carro(String placa, String modelo, double valorDiaria, int portas) {
        super(placa, modelo, valorDiaria);
        this.portas = portas;
    }
}

/** Veículo do tipo moto, com cilindrada. */
class Moto extends Veiculo {
    int cilindrada;

    Moto(String placa, String modelo, double valorDiaria, int cilindrada) {
        super(placa, modelo, valorDiaria);
        this.cilindrada = cilindrada;
    }
}

/**
 * Classe que demonstra boas práticas de documentação com Javadoc.
 *
 * <p>Uma boa documentação de código inclui:
 * <ul>
 *   <li>Descrição do propósito da classe/método</li>
 *   <li>Parâmetros com @param</li>
 *   <li>Retorno com @return</li>
 *   <li>Exceções com @throws</li>
 * </ul>
 *
 * @author Prof. Giovane Barcelos
 * @version 1.0
 */
class Documentacao {

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
        if (veiculo == null) {
            throw new NullPointerException("veiculo não pode ser null");
        }
        if (dias < 1) {
            throw new IllegalArgumentException("dias deve ser >= 1");
        }
        return veiculo.calcularLocacao(dias);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  DOCUMENTAÇÃO COM JAVADOC");
        System.out.println("=".repeat(60));

        Carro carro = new Carro("ABC-1234", "Gol", 100.0, 4);
        Moto moto = new Moto("XYZ-5678", "CG 160", 60.0, 160);

        System.out.printf("%n  Locação do carro por 5 dias: R$ %.2f%n", calcularLocacao(carro, 5));
        System.out.printf("  Locação da moto por 3 dias: R$ %.2f%n", calcularLocacao(moto, 3));

        System.out.println("\n✓ Javadoc: documentação integrada ao código.");
        System.out.println("✓ Gere HTML com: javadoc -d docs *.java");
    }
}
