/*
 * PES1301 - Classe Conta (Encapsulamento)
 * Aula 13: Introdução à UML e Orientação a Objetos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra encapsulamento: atributos privados e acesso via métodos,
 * com validação nas operações (depositar/sacar).
 *
 * Compilar/executar:
 *   javac PES1301-ClasseConta.java && java ClasseContaDemo
 */

class Conta {
    private String titular;   // atributo privado (encapsulado)
    private double saldo;

    public Conta(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }
        saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        saldo -= valor;
    }

    @Override
    public String toString() {
        return String.format("Conta[titular=%s, saldo=R$ %.2f]", titular, saldo);
    }
}

class ClasseContaDemo {
    public static void main(String[] args) {
        Conta conta = new Conta("Ana", 100.0);
        conta.depositar(50.0);
        conta.sacar(30.0);
        System.out.println(conta); // Conta[titular=Ana, saldo=R$ 120,00]
    }
}
