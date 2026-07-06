/*
 * PES0801 - Métrica de Lead Time e Limite de WIP (Kanban)
 * Aula 08: Kanban e Comparação de Abordagens
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Calcula métricas de fluxo Kanban: lead time, cycle time e
 * simula o efeito do limite de WIP (Work In Progress).
 *
 * Compilar/executar:
 *   javac PES0801-LeadTimeWIP.java && java LeadTimeWIP
 */

class LeadTimeWIP {

    static class Tarefa {
        String nome;
        int leadTime;
        int cycleTime;

        Tarefa(String nome, int leadTime, int cycleTime) {
            this.nome = nome;
            this.leadTime = leadTime;
            this.cycleTime = cycleTime;
        }
    }

    public static double[] calcularMetricas(Tarefa[] tarefas) {
        System.out.printf("%-10s %14s %15s%n", "Tarefa", "Lead Time (d)", "Cycle Time (d)");
        System.out.println("-".repeat(42));
        double totalLead = 0, totalCycle = 0;
        for (Tarefa t : tarefas) {
            totalLead += t.leadTime;
            totalCycle += t.cycleTime;
            System.out.printf("%-10s %14d %15d%n", t.nome, t.leadTime, t.cycleTime);
        }
        int n = tarefas.length;
        double avgLead = totalLead / n;
        double avgCycle = totalCycle / n;
        System.out.println("-".repeat(42));
        System.out.printf("%-10s %14.1f %15.1f%n", "MÉDIA", avgLead, avgCycle);
        System.out.printf("%nThroughput: %d tarefas ≈ %.2f tarefas/dia (considerando período)%n",
                n, n / 16.0);
        return new double[]{avgLead, avgCycle};
    }

    public static void simularWIP(int limiteWIP, int totalTarefas, double tempoMedio, int capacidadeEquipe) {
        // Lei de Little: Lead Time = WIP / Throughput. O throughput é limitado pela
        // capacidade da equipe — WIP acima dela só represa tarefas, não acelera a entrega.
        int wipProcessavel = Math.min(limiteWIP, capacidadeEquipe);
        double throughputMax = wipProcessavel / tempoMedio;
        double leadTimeEst = throughputMax > 0 ? limiteWIP / throughputMax : Double.POSITIVE_INFINITY;
        System.out.printf("  WIP=%2d: Throughput máx=%.2f tar/dia, Lead Time est.=%.1f dias%n",
                limiteWIP, throughputMax, leadTimeEst);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  MÉTRICAS KANBAN — LEAD TIME E WIP");
        System.out.println("=".repeat(60));

        Tarefa[] tarefas = {
            new Tarefa("US-01", 5, 5),
            new Tarefa("US-02", 8, 5),
            new Tarefa("US-03", 10, 5),
            new Tarefa("US-04", 10, 5),
            new Tarefa("US-05", 12, 7),
        };

        System.out.printf("%n--- Métricas de Fluxo ---%n");
        double[] medias = calcularMetricas(tarefas);

        int capacidadeEquipe = 3; // nº de tarefas que a equipe processa em paralelo
        System.out.printf("%n--- Simulação de Limite de WIP (capacidade da equipe: %d) ---%n", capacidadeEquipe);
        for (int wip : new int[]{2, 3, 5, 10}) {
            simularWIP(wip, 20, medias[1], capacidadeEquipe);
        }

        System.out.printf("%n✓ Conclusão: WIP acima da capacidade da equipe não aumenta o throughput,%n"
                + "  apenas represa tarefas e aumenta o lead time — por isso reduzir o WIP%n"
                + "  (até o limite da capacidade) reduz o lead time e expõe gargalos.%n");
    }
}
