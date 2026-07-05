/*
 * PES2601 - Simulação de Conflito de Merge e Resolução
 * Aula 26: Fluxos de Trabalho com Git e Colaboração
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Compilar/executar:
 *   javac PES2601-ConflitoMerge.java && java ConflitoMerge
 */

class ConflitoMerge {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  SIMULAÇÃO DE CONFLITO DE MERGE");
        System.out.println("=".repeat(60));
        System.out.println();

        String[] base = {
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        total += item['preco']",
            "    return total",
        };

        String[] desconto = {
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        preco = item['preco'] * 0.9  # 10% desconto",
            "        total += preco",
            "    return total",
        };

        String[] imposto = {
            "def calcular_total(itens):",
            "    total = 0",
            "    for item in itens:",
            "        preco = item['preco'] * 1.15  # 15% imposto",
            "        total += preco",
            "    return total",
        };

        System.out.println("--- Base (main) ---");
        for (String l : base) System.out.println("  " + l);

        System.out.println("\n--- Branch feature/desconto ---");
        for (String l : desconto) System.out.println("  " + l);

        System.out.println("\n--- Branch feature/imposto ---");
        for (String l : imposto) System.out.println("  " + l);

        System.out.println("\n--- CONFLITO na linha 4 ---");
        System.out.println("  <<<<<<< feature/desconto");
        System.out.println("      preco = item['preco'] * 0.9  # 10% desconto");
        System.out.println("  =======");
        System.out.println("      preco = item['preco'] * 1.15  # 15% imposto");
        System.out.println("  >>>>>>> feature/imposto");

        System.out.println("\n--- Resolução (merge manual) ---");
        System.out.println("  preco = item['preco'] * 0.9   # 10% desconto");
        System.out.println("  preco = preco * 1.15           # 15% imposto");

        System.out.println("\n✓ Conflito resolvido mesclando ambas as alterações.");
        System.out.println("✓ Estratégias: aceitar uma, combinar ambas ou reescrever.");
    }
}
