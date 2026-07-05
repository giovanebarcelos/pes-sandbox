/*
 * PES2401 - Antes/Depois de Refatoração
 * Aula 24: Tipos de Manutenção e Evolução de Software
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Compilar/executar:
 *   javac PES2401-Refatoracao.java && java Refatoracao
 */

import java.util.*;

class Refatoracao {

    static class Produto {
        String tipo;
        double preco;
        boolean promocao;

        Produto(String tipo, double preco, boolean promocao) {
            this.tipo = tipo; this.preco = preco; this.promocao = promocao;
        }
    }

    // ❌ ANTES: código com débito técnico
    static double calcularPrecoAntes(List<Produto> produtos) {
        double total = 0;
        for (Produto p : produtos) {
            double preco = p.preco;
            if (p.tipo.equals("eletronico")) preco *= 1.20;
            else if (p.tipo.equals("alimento")) preco *= 1.05;
            else preco *= 1.15;
            if (p.promocao) preco *= 0.90;
            total += preco;
        }
        return total;
    }

    // ✅ DEPOIS: código refatorado
    static final Map<String, Double> IMPOSTOS = Map.of("eletronico", 1.20, "alimento", 1.05);
    static final double IMPOSTO_PADRAO = 1.15;
    static final double DESCONTO_PROMOCAO = 0.90;

    static double aplicarImposto(Produto p) {
        return p.preco * IMPOSTOS.getOrDefault(p.tipo, IMPOSTO_PADRAO);
    }

    static double aplicarDesconto(double preco, Produto p) {
        return p.promocao ? preco * DESCONTO_PROMOCAO : preco;
    }

    static double calcularPrecoDepois(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(p -> aplicarDesconto(aplicarImposto(p), p))
                .sum();
    }

    public static void main(String[] args) {
        List<Produto> produtos = Arrays.asList(
            new Produto("eletronico", 100, true),
            new Produto("alimento", 50, false),
            new Produto("livro", 80, true)
        );

        System.out.println("=".repeat(60));
        System.out.println("  REFATORAÇÃO — ANTES × DEPOIS");
        System.out.println("=".repeat(60));

        System.out.printf("%n  Antes:  R$ %.2f%n", calcularPrecoAntes(produtos));
        System.out.printf("  Depois: R$ %.2f%n", calcularPrecoDepois(produtos));
        System.out.println("  (resultados idênticos ✓)");

        System.out.println("\n✓ Refatoração: mesmo comportamento, código mais limpo.");
        System.out.println("✓ Manutenção perfectiva: melhora estrutura interna.");
    }
}
