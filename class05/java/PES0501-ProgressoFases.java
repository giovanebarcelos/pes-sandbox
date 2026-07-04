/*
 * PES0501 - Simulação de Progresso por Fases (Modelos Tradicionais)
 * Aula 05: Modelos Tradicionais — Cascata e Espiral
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula o progresso de um projeto seguindo as fases do modelo cascata.
 * Demonstra como as fases são sequenciais e como o esforço se distribui.
 *
 * Compilar/executar:
 *   javac PES0501-ProgressoFases.java && java ProgressoFases
 */

class ProgressoFases {

    static class Fase {
        String nome;
        double proporcao;
        String entregavel;

        Fase(String nome, double proporcao, String entregavel) {
            this.nome = nome;
            this.proporcao = proporcao;
            this.entregavel = entregavel;
        }
    }

    static final Fase[] FASES_CASCATA = {
        new Fase("Requisitos", 0.15, "Documento de requisitos aprovado"),
        new Fase("Projeto (Design)", 0.20, "Arquitetura e design detalhado"),
        new Fase("Implementação", 0.35, "Código fonte completo"),
        new Fase("Testes", 0.20, "Relatório de testes e bugs corrigidos"),
        new Fase("Manutenção", 0.10, "Sistema em produção"),
    };

    public static void simularProgresso(double esforcoTotalHoras) {
        System.out.println("=".repeat(60));
        System.out.printf("  SIMULAÇÃO DE PROJETO — MODELO CASCATA%n");
        System.out.printf("  Esforço total estimado: %.0fh%n", esforcoTotalHoras);
        System.out.println("=".repeat(60));
        System.out.println();

        double acumulado = 0;
        for (Fase f : FASES_CASCATA) {
            double horasFase = esforcoTotalHoras * f.proporcao;
            acumulado += f.proporcao;
            System.out.printf("  📌 %s%n", f.nome);
            System.out.printf("     Horas estimadas: %.0fh (%.0f%%)%n", horasFase, f.proporcao * 100);
            System.out.printf("     Progresso acumulado: %.0f%%%n", acumulado * 100);
            System.out.printf("     Entregável: %s%n", f.entregavel);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        simularProgresso(500);
        System.out.println("⚠️  Atenção: no modelo cascata, mudanças tardias são caras.");
        System.out.println("   Cada fase deve ser concluída antes de iniciar a próxima.");
    }
}
