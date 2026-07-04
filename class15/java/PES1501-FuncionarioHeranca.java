/*
 * PES1501 - Funcionário com Herança e Polimorfismo
 * Aula 15: Diagrama de Classes e Orientação a Objetos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra herança, polimorfismo e encapsulamento com uma hierarquia
 * Funcionario → Desenvolvedor, Gerente.
 *
 * Compilar/executar:
 *   javac PES1501-FuncionarioHeranca.java && java FuncionarioHeranca
 */

import java.util.*;

abstract class Funcionario {
    private String nome;
    private double salarioBase;

    public Funcionario(String nome, double salarioBase) {
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public String getNome() { return nome; }
    public double getSalarioBase() { return salarioBase; }

    public abstract double calcularSalario();

    @Override
    public String toString() {
        return String.format("%s[nome=%s, salário=R$ %.2f]",
                getClass().getSimpleName(), nome, calcularSalario());
    }
}

class Desenvolvedor extends Funcionario {
    private String linguagem;

    public Desenvolvedor(String nome, double salarioBase, String linguagem) {
        super(nome, salarioBase);
        this.linguagem = linguagem;
    }

    @Override
    public double calcularSalario() {
        return getSalarioBase() * 1.15;
    }
}

class Gerente extends Funcionario {
    private double bonus;

    public Gerente(String nome, double salarioBase, double bonus) {
        super(nome, salarioBase);
        this.bonus = bonus;
    }

    public Gerente(String nome, double salarioBase) {
        this(nome, salarioBase, 2000.0);
    }

    @Override
    public double calcularSalario() {
        return getSalarioBase() + bonus;
    }
}

class FuncionarioHeranca {

    public static void processarFolha(List<Funcionario> funcionarios) {
        System.out.printf("%n%-20s %-18s %15s%n", "Nome", "Cargo", "Salário Final");
        System.out.println("-".repeat(55));
        double total = 0;
        for (Funcionario f : funcionarios) {
            double sal = f.calcularSalario();
            total += sal;
            System.out.printf("%-20s %-18s R$ %12.2f%n",
                    f.getNome(), f.getClass().getSimpleName(), sal);
        }
        System.out.println("-".repeat(55));
        System.out.printf("%-20s %18s R$ %12.2f%n", "TOTAL", " ", total);
    }

    public static void main(String[] args) {
        List<Funcionario> equipe = Arrays.asList(
            new Desenvolvedor("Ana Silva", 8000, "Python"),
            new Desenvolvedor("Carlos Santos", 7500, "Java"),
            new Gerente("Mariana Costa", 12000),
            new Gerente("Roberto Lima", 11000, 2500)
        );

        System.out.println("=".repeat(60));
        System.out.println("  FOLHA DE PAGAMENTO — HERANÇA E POLIMORFISMO");
        System.out.println("=".repeat(60));

        processarFolha(equipe);
        System.out.println("\n✓ Polimorfismo: cada subclasse implementa calcularSalario() de forma diferente.");
    }
}
