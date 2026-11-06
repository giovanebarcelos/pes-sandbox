/*
 * PES2301 - Cálculo de Custo Acumulado por Fase do SDLC
 * Aula 23: Ciclo de Vida de Software
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula o custo acumulado ao longo das fases do ciclo de vida,
 * demonstrando que a manutenção é a fase mais longa e cara.
 * (Versão Java de class23/python/PES2301-CustoCicloVida.py — mesma saída.)
 *
 * Executar (Java 17+):  java PES2301-CustoCicloVida.java
 * Ou compilar:          javac PES2301-CustoCicloVida.java && java CustoCicloVida
 */

import java.util.List;
import java.util.Locale;

class CustoCicloVida {

    /** Uma fase do ciclo de vida: nome, fração do custo total e descrição. */
    record Fase(String nome, double proporcao, String descricao) {
    }

    static final List<Fase> FASES_SDLC = List.of(
        new Fase("Concepção", 0.05, "Viabilidade e escopo inicial"),
        new Fase("Requisitos", 0.10, "Especificação e análise"),
        new Fase("Projeto", 0.10, "Arquitetura e design"),
        new Fase("Implementação", 0.20, "Codificação"),
        new Fase("Testes", 0.15, "Verificação e validação"),
        new Fase("Implantação", 0.05, "Entrega e deploy"),
        new Fase("Manutenção", 0.35, "Correções, adaptações, evolução")
    );

    static void simularCustos(double custoTotal) {
        // Locale.ROOT: separador de milhar "," como no Python, em qualquer computador
        System.out.printf(Locale.ROOT, "%n%-20s %6s %14s %16s%n", "Fase", "%", "Custo (R$)", "Acumulado (R$)");
        System.out.println("-".repeat(60));
        double acumulado = 0;
        for (Fase f : FASES_SDLC) {
            double custo = custoTotal * f.proporcao();
            acumulado += custo;
            System.out.printf(Locale.ROOT, "%-20s %4.0f%% R$ %,12.0f R$ %,14.0f%n",
                    f.nome(), f.proporcao() * 100, custo, acumulado);
        }
        System.out.println("-".repeat(60));
        System.out.printf(Locale.ROOT, "%-20s %6s R$ %,12.0f%n", "TOTAL", "100%", custoTotal);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  CUSTO ACUMULADO POR FASE DO CICLO DE VIDA");
        System.out.println("=".repeat(60));

        simularCustos(500_000);

        System.out.println("\n✓ Manutenção consome ~35% do custo total do ciclo de vida.");
        System.out.println("✓ Custo de corrigir um defeito cresce exponencialmente a cada fase.");
    }
}
