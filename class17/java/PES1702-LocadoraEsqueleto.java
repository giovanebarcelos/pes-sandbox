/*
 * PES1702 - Locadora de Veículos (Esqueleto/Contingência)
 * Aula 17: Projeto Parcial — Modelagem UML + Implementação OO
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * CÓDIGO-BASE PARA DISTRIBUIÇÃO EM SALA (contingência de tempo):
 * Este esqueleto contém a classe abstrata Veiculo e a estrutura de
 * Locacao/Locadora prontas. Os alunos precisam apenas:
 *   (1) implementar calcularLocacao() em Carro e Moto (polimorfismo)
 *   (2) cadastrar veículos e simular locações no bloco main()
 *
 * Regras de negócio (do Slide 8 da Aula 17):
 *   - Carro: valor = diária × dias × 1.10 (acréscimo de 10% — taxa de seguro)
 *   - Moto:  valor = diária × dias (sem acréscimo)
 *
 * Compilar/executar:
 *   javac PES1702-LocadoraEsqueleto.java && java LocadoraEsqueleto
 */

import java.util.*;

abstract class Veiculo {
    private String placa, modelo;
    private double valorDiaria;

    Veiculo(String placa, String modelo, double valorDiaria) {
        this.placa = placa;
        this.modelo = modelo;
        this.valorDiaria = valorDiaria;
    }

    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public double getValorDiaria() { return valorDiaria; }

    public abstract double calcularLocacao(int dias);

    @Override
    public String toString() {
        return String.format("%s[%s] %s", getClass().getSimpleName(), placa, modelo);
    }
}

// ============================================================================
// TODO (aluno): implementar calcularLocacao() em Carro e Moto
// ============================================================================

class Carro extends Veiculo {
    private int numPortas;

    Carro(String placa, String modelo, double valorDiaria, int numPortas) {
        super(placa, modelo, valorDiaria);
        this.numPortas = numPortas;
    }

    @Override
    public double calcularLocacao(int dias) {
        // TODO: retornar diária × dias × 1.10
        throw new UnsupportedOperationException("Implemente calcularLocacao para Carro");
    }
}

class Moto extends Veiculo {
    private int cilindradas;

    Moto(String placa, String modelo, double valorDiaria, int cilindradas) {
        super(placa, modelo, valorDiaria);
        this.cilindradas = cilindradas;
    }

    @Override
    public double calcularLocacao(int dias) {
        // TODO: retornar diária × dias
        throw new UnsupportedOperationException("Implemente calcularLocacao para Moto");
    }
}

// ============================================================================
// Classes de apoio (NÃO MODIFICAR — já prontas)
// ============================================================================

class Locacao {
    private Veiculo veiculo;
    private String cliente;
    private int dias;
    private double valorTotal;

    Locacao(Veiculo veiculo, String cliente, int dias) {
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.dias = dias;
        this.valorTotal = veiculo.calcularLocacao(dias);
    }

    public double getValorTotal() { return valorTotal; }

    public void resumo() {
        System.out.println("  Veículo: " + veiculo);
        System.out.println("  Cliente: " + cliente);
        System.out.println("  Dias: " + dias);
        System.out.printf("  Valor Total: R$ %.2f%n", valorTotal);
    }
}

class LocadoraEsqueleto {
    private List<Veiculo> frota = new ArrayList<>();
    private List<Locacao> locacoes = new ArrayList<>();

    public void cadastrar(Veiculo veiculo) {
        frota.add(veiculo);
        System.out.println("  ✓ Cadastrado: " + veiculo);
    }

    public void listarDisponiveis() {
        System.out.printf("%n  --- Frota (%d veículos) ---%n", frota.size());
        for (Veiculo v : frota) {
            System.out.printf("  • %s — R$ %.2f/dia%n", v, v.getValorDiaria());
        }
    }

    public Locacao alugar(String placa, String cliente, int dias) {
        for (Veiculo v : frota) {
            if (v.getPlaca().equals(placa)) {
                Locacao loc = new Locacao(v, cliente, dias);
                locacoes.add(loc);
                System.out.println("\n  ✓ Locação registrada:");
                loc.resumo();
                return loc;
            }
        }
        System.out.println("  ✗ Veículo " + placa + " não encontrado.");
        return null;
    }

    public double faturamentoTotal() {
        double total = 0;
        for (Locacao loc : locacoes) total += loc.getValorTotal();
        return total;
    }

    // ============================================================================
    // TODO (aluno): cadastrar veículos e simular locações
    // ============================================================================

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  LOCADORA DE VEÍCULOS — ESQUELETO (preencha os TODOs)");
        System.out.println("=".repeat(60));

        LocadoraEsqueleto locadora = new LocadoraEsqueleto();

        // TODO: cadastrar 3 veículos (2 carros, 1 moto)
        // locadora.cadastrar(new Carro("ABC-1234", "Fiat Uno", 120.00, 4));
        // locadora.cadastrar(new Carro("DEF-5678", "Honda Civic", 200.00, 4));
        // locadora.cadastrar(new Moto("XYZ-9012", "Honda CG 160", 80.00, 160));

        locadora.listarDisponiveis();

        // TODO: alugar 2 veículos
        // locadora.alugar("ABC-1234", "Ana Silva", 5);
        // locadora.alugar("XYZ-9012", "Carlos Santos", 3);

        // System.out.printf("%n  ✓ Faturamento Total: R$ %.2f%n",
        //                   locadora.faturamentoTotal());
        System.out.println("\n  ⚠ Preencha os TODOs acima para executar o sistema completo.");
    }
}
