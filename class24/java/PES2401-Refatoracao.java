/*
 * PES2401 - Antes/Depois de Refatoração
 * Aula 24: Tipos de Manutenção e Evolução de Software
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra refatoração: código com débito técnico (antes)
 * vs. código limpo e modular (depois), mantendo mesmo comportamento.
 * (Versão Java de class24/python/PES2401-Refatoracao.py — mesma saída.)
 *
 * Executar (Java 17+):  java PES2401-Refatoracao.java
 * Ou compilar:          javac PES2401-Refatoracao.java && java Refatoracao
 */

import java.util.List;
import java.util.Locale;
import java.util.Map;

class Refatoracao {

    /** Produto: tipo, preço e se está em promoção. */
    record Produto(String tipo, double preco, boolean promocao) {
    }

    // ============================================================
    // ❌ ANTES: código com débito técnico
    // ============================================================
    /** Código com cheiro: método longo, muitos ifs, difícil de estender. */
    static double calcularPrecoAntes(List<Produto> produtos) {
        double total = 0;
        for (Produto p : produtos) {
            double preco = p.preco();
            if (p.tipo().equals("eletronico")) {
                preco = preco * 1.20;  // imposto
            } else if (p.tipo().equals("alimento")) {
                preco = preco * 1.05;
            } else {
                preco = preco * 1.15;
            }
            if (p.promocao()) {
                preco = preco * 0.90;
            }
            total += preco;
        }
        return total;
    }

    // ============================================================
    // ✅ DEPOIS: código refatorado (Clean Code)
    // ============================================================
    static final Map<String, Double> IMPOSTOS = Map.of("eletronico", 1.20, "alimento", 1.05);
    static final double IMPOSTO_PADRAO = 1.15;
    static final double DESCONTO_PROMOCAO = 0.90;

    static double aplicarImposto(Produto produto) {
        double taxa = IMPOSTOS.getOrDefault(produto.tipo(), IMPOSTO_PADRAO);
        return produto.preco() * taxa;
    }

    static double aplicarDesconto(double preco, Produto produto) {
        return produto.promocao() ? preco * DESCONTO_PROMOCAO : preco;
    }

    /** Código refatorado: métodos pequenos, fácil de estender. */
    static double calcularPrecoDepois(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(p -> aplicarDesconto(aplicarImposto(p), p))
                .sum();
    }

    public static void main(String[] args) {
        List<Produto> produtos = List.of(
            new Produto("eletronico", 100, true),
            new Produto("alimento", 50, false),
            new Produto("livro", 80, true)
        );

        System.out.println("=".repeat(60));
        System.out.println("  REFATORAÇÃO — ANTES × DEPOIS");
        System.out.println("=".repeat(60));

        System.out.printf(Locale.ROOT, "%n  Antes:  R$ %.2f%n", calcularPrecoAntes(produtos));
        System.out.printf(Locale.ROOT, "  Depois: R$ %.2f%n", calcularPrecoDepois(produtos));
        System.out.println("  (resultados idênticos ✓)");

        System.out.println("\n✓ Refatoração: mesmo comportamento, código mais limpo.");
        System.out.println("✓ Manutenção perfectiva: melhora estrutura interna sem alterar externo.");
    }
}
