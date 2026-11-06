/*
 * PES2601 - Simulação de Conflito de Merge e Resolução
 * Aula 26: Fluxos de Trabalho com Git e Colaboração
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula um cenário de conflito de merge quando duas branches
 * modificam a mesma linha e demonstra a resolução.
 * (Versão Java de class26/python/PES2601-ConflitoMerge.py — mesma saída.)
 *
 * O Python tem o difflib pronto; o Java não. Por isso este arquivo traz um
 * diff unificado mínimo (maior subsequência comum, um bloco só), suficiente
 * para arquivos pequenos. O Git usa um algoritmo mais esperto (Myers), mas a
 * ideia e o formato da saída são os mesmos.
 *
 * Executar (Java 17+):  java PES2601-ConflitoMerge.java
 * Ou compilar:          javac PES2601-ConflitoMerge.java && java ConflitoMerge
 */

import java.util.ArrayList;
import java.util.List;

class ConflitoMerge {

    /** Simula conflito de merge entre duas branches. */
    static void simularConflito() {
        // Versão base (main antes das branches)
        List<String> base = List.of(
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        total += item['preco']",
            "    return total"
        );

        // Branch feature/desconto
        List<String> branchDesconto = List.of(
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        preco = item['preco'] * 0.9  # 10% desconto",
            "        total += preco",
            "    return total"
        );

        // Branch feature/imposto
        List<String> branchImposto = List.of(
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        preco = item['preco'] * 1.15  # 15% imposto",
            "        total += preco",
            "    return total"
        );

        System.out.println("=".repeat(60));
        System.out.println("  SIMULAÇÃO DE CONFLITO DE MERGE");
        System.out.println("=".repeat(60));
        System.out.println();

        System.out.println("--- Base (main) ---");
        imprimir(base);

        System.out.println("\n--- Branch feature/desconto ---");
        imprimir(branchDesconto);

        System.out.println("\n--- Branch feature/imposto ---");
        imprimir(branchImposto);

        System.out.println("\n--- Diff: Base → Desconto ---");
        imprimir(diffUnificado(base, branchDesconto));

        System.out.println("\n--- Diff: Base → Imposto ---");
        imprimir(diffUnificado(base, branchImposto));

        System.out.println("\n--- Resolução (merge manual) ---");
        List<String> resolvido = List.of(
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        preco = item['preco'] * 0.9   # 10% desconto (feature/desconto)",
            "        preco = preco * 1.15           # 15% imposto (feature/imposto)",
            "        total += preco",
            "    return total"
        );
        imprimir(resolvido);

        System.out.println("\n✓ Conflito resolvido mesclando ambas as alterações.");
        System.out.println("✓ Estratégias: aceitar uma, combinar ambas ou reescrever.");
    }

    static void imprimir(List<String> linhas) {
        for (String linha : linhas) {
            System.out.println("  " + linha);
        }
    }

    /**
     * Diff unificado (formato do "git diff") entre duas versões, num bloco só.
     * Linhas: " " igual, "-" só na antiga, "+" só na nova.
     */
    static List<String> diffUnificado(List<String> antes, List<String> depois) {
        int n = antes.size();
        int m = depois.size();
        // lcs[i][j] = tamanho da maior subsequência comum de antes[i..] e depois[j..]
        int[][] lcs = new int[n + 1][m + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                lcs[i][j] = antes.get(i).equals(depois.get(j))
                        ? lcs[i + 1][j + 1] + 1
                        : Math.max(lcs[i + 1][j], lcs[i][j + 1]);
            }
        }
        List<String> saida = new ArrayList<>(List.of("--- ", "+++ ",
                String.format("@@ -1,%d +1,%d @@", n, m)));
        int i = 0;
        int j = 0;
        while (i < n || j < m) {
            if (i < n && j < m && antes.get(i).equals(depois.get(j))) {
                saida.add(" " + antes.get(i++));
                j++;
            } else if (j == m || (i < n && lcs[i + 1][j] >= lcs[i][j + 1])) {
                saida.add("-" + antes.get(i++));   // removida
            } else {
                saida.add("+" + depois.get(j++));  // acrescentada
            }
        }
        return saida;
    }

    public static void main(String[] args) {
        simularConflito();
    }
}
