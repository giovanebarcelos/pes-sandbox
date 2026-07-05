/*
 * PES2301 - Cálculo de Custo Acumulado por Fase do SDLC
 * Aula 23: Ciclo de Vida de Software
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Compilar/executar:
 *   javac PES2301-CustoCicloVida.java && java CustoCicloVida
 */

class CustoCicloVida {

    static class Fase {
        String nome;
        double proporcao;

        Fase(String nome, double proporcao) { this.nome = nome; this.proporcao = proporcao; }
    }

    static final Fase[] FASES = {
        new Fase("Concepção", 0.05),
        new Fase("Requisitos", 0.10),
        new Fase("Projeto", 0.10),
        new Fase("Implementação", 0.20),
        new Fase("Testes", 0.15),
        new Fase("Implantação", 0.05),
        new Fase("Manutenção", 0.35),
    };

    static void simularCustos(double custoTotal) {
        System.out.printf("%n%-20s %6s %14s %16s%n", "Fase", "%", "Custo (R$)", "Acumulado (R$)");
        System.out.println("-".repeat(60));
        double acumulado = 0;
        for (Fase f : FASES) {
            double custo = custoTotal * f.proporcao;
            acumulado += custo;
            System.out.printf("%-20s %5.0f%% R$ %,12.0f R$ %,14.0f%n",
                    f.nome, f.proporcao * 100, custo, acumulado);
        }
        System.out.println("-".repeat(60));
        System.out.printf("%-20s %6s R$ %,12.0f%n", "TOTAL", "100%", custoTotal);
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
