/*
 * PES1701 - Mini-Sistema Locadora de Veículos
 * Aula 17: Projeto Parcial — Modelagem UML + Implementação OO
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Sistema completo demonstrando herança, polimorfismo e encapsulamento
 * em um domínio de locadora de veículos (Carro, Moto).
 *
 * Compilar/executar:
 *   javac PES1701-Locadora.java && java Locadora
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

class Carro extends Veiculo {
    private int numPortas;

    Carro(String placa, String modelo, double valorDiaria, int numPortas) {
        super(placa, modelo, valorDiaria);
        this.numPortas = numPortas;
    }

    @Override
    public double calcularLocacao(int dias) {
        return getValorDiaria() * dias * 1.10;
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
        return getValorDiaria() * dias;
    }
}

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

    double getValorTotal() { return valorTotal; }

    void resumo() {
        System.out.printf("  Veículo: %s%n", veiculo);
        System.out.printf("  Cliente: %s%n", cliente);
        System.out.printf("  Dias: %d%n", dias);
        System.out.printf("  Valor Total: R$ %.2f%n", valorTotal);
    }
}

class Locadora {
    private List<Veiculo> frota = new ArrayList<>();
    private List<Locacao> locacoes = new ArrayList<>();

    void cadastrar(Veiculo veiculo) {
        frota.add(veiculo);
        System.out.printf("  ✓ Cadastrado: %s%n", veiculo);
    }

    void listarDisponiveis() {
        System.out.printf("%n  --- Frota (%d veículos) ---%n", frota.size());
        for (Veiculo v : frota) {
            System.out.printf("  • %s — R$ %.2f/dia%n", v, v.getValorDiaria());
        }
    }

    Locacao alugar(String placa, String cliente, int dias) {
        for (Veiculo v : frota) {
            if (v.getPlaca().equals(placa)) {
                Locacao loc = new Locacao(v, cliente, dias);
                locacoes.add(loc);
                System.out.printf("%n  ✓ Locação registrada:%n");
                loc.resumo();
                return loc;
            }
        }
        System.out.printf("  ✗ Veículo %s não encontrado.%n", placa);
        return null;
    }

    double faturamentoTotal() {
        return locacoes.stream().mapToDouble(Locacao::getValorTotal).sum();
    }
}

class LocadoraDemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  LOCADORA DE VEÍCULOS — SISTEMA OO");
        System.out.println("=".repeat(60));

        Locadora locadora = new Locadora();

        System.out.printf("%n--- Cadastro de Veículos ---%n");
        locadora.cadastrar(new Carro("ABC-1234", "Fiat Uno", 120.00, 4));
        locadora.cadastrar(new Carro("DEF-5678", "Honda Civic", 200.00, 4));
        locadora.cadastrar(new Moto("XYZ-9012", "Honda CG 160", 80.00, 160));

        locadora.listarDisponiveis();

        System.out.printf("%n--- Locações ---%n");
        locadora.alugar("ABC-1234", "Ana Silva", 5);
        locadora.alugar("XYZ-9012", "Carlos Santos", 3);

        System.out.printf("%n  ✓ Faturamento Total: R$ %.2f%n", locadora.faturamentoTotal());
        System.out.printf("%n✓ Sistema completo com herança, polimorfismo e encapsulamento.%n");
    }
}
