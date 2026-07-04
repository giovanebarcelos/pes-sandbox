/*
 * PES0701 - Cálculo de Velocidade e Burndown de Sprint
 * Aula 07: Metodologias Ágeis — Scrum e XP
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula uma sprint Scrum com cálculo de velocidade da equipe
 * e gráfico de burndown (textual) baseado em story points.
 *
 * Compilar/executar:
 *   javac PES0701-BurndownSprint.java && java BurndownSprint
 */

class BurndownSprint {

    public static int calcularBurndown(int totalStoryPoints, int velocidadeDiaria, int diasSprint) {
        double restante = totalStoryPoints;
        System.out.printf("%n%-6s %10s %10s %10s%n", "Dia", "Planejado", "Realizado", "Restante");
        System.out.println("-".repeat(42));

        for (int dia = 1; dia <= diasSprint; dia++) {
            double planejadoRestante = totalStoryPoints - ((double) totalStoryPoints / diasSprint) * dia;
            double realizado = Math.min(velocidadeDiaria, restante);
            restante -= realizado;
            System.out.printf("%-6d %10.1f %10.1f %10.1f%n", dia,
                    Math.max(planejadoRestante, 0), realizado, Math.max(restante, 0));
        }

        return totalStoryPoints - (int) Math.max(restante, 0);
    }

    public static void main(String[] args) {
        int totalSP = 34;
        int velocidade = 5;
        int dias = 10;

        System.out.println("=".repeat(60));
        System.out.println("  BURNDOWN CHART — SPRINT SCRUM");
        System.out.println("=".repeat(60));
        System.out.printf("  Story Points no Backlog: %d%n", totalSP);
        System.out.printf("  Velocidade diária: %d SP/dia%n", velocidade);
        System.out.printf("  Duração da Sprint: %d dias%n", dias);
        System.out.printf("  Capacidade teórica: %d SP%n", velocidade * dias);

        int entregue = calcularBurndown(totalSP, velocidade, dias);

        System.out.printf("%n  ✓ Story Points entregues: %d/%d%n", entregue, totalSP);
        System.out.printf("  ✓ Velocidade da Sprint: %d SP%n", entregue);
        if (entregue >= totalSP) {
            System.out.println("  🎉 Meta da sprint atingida!");
        } else {
            System.out.printf("  ⚠️  %d SP migrarão para o próximo sprint backlog.%n", totalSP - entregue);
        }
    }
}
