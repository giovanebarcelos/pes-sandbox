/*
 * PES0201 - Calculadora de Restrição Tripla
 * Aula 02: Fundamentos de Gerenciamento de Projetos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra a relação entre escopo, prazo e custo (restrição tripla).
 * Dado um escopo base, calcula o esforço estimado e permite simular
 * variações de prazo e equipe.
 *
 * Compilar/executar:
 *   javac PES0201-RestricaoTripla.java && java RestricaoTripla
 */

class RestricaoTripla {

    public static class ResultadoEsforco {
        public final double capacidade;
        public final double esforcoPorPessoa;

        public ResultadoEsforco(double capacidade, double esforcoPorPessoa) {
            this.capacidade = capacidade;
            this.esforcoPorPessoa = esforcoPorPessoa;
        }
    }

    public static ResultadoEsforco calcularEsforco(int escopoPontos, int prazoSemanas, int tamanhoEquipe) {
        if (prazoSemanas <= 0 || tamanhoEquipe <= 0) {
            throw new IllegalArgumentException("Prazo e tamanho da equipe devem ser positivos.");
        }
        double capacidade = (double) prazoSemanas * tamanhoEquipe;
        double esforcoPorPessoa = escopoPontos / capacidade;
        return new ResultadoEsforco(capacidade, esforcoPorPessoa);
    }

    public static void simularCenario(int escopo, int prazo, int equipe, String nome) {
        ResultadoEsforco r = calcularEsforco(escopo, prazo, equipe);
        System.out.printf("  Cenário%s:%n", nome.isEmpty() ? "" : " (" + nome + ")");
        System.out.printf("    Escopo: %d pts | Prazo: %d sem | Equipe: %d pessoas%n", escopo, prazo, equipe);
        System.out.printf("    Capacidade: %.1f pessoa-semanas | Esforço/pessoa: %.2f pts/sem%n", r.capacidade, r.esforcoPorPessoa);
        if (r.esforcoPorPessoa > 10) {
            System.out.println("    ⚠️  Alta carga (>10 pts/sem) — risco de atraso!");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  CALCULADORA DE RESTRIÇÃO TRIPLA");
        System.out.println("  Escopo × Prazo × Custo (Equipe)");
        System.out.println("=".repeat(60));
        System.out.println();

        simularCenario(100, 10, 5, "Base");
        simularCenario(100, 5, 5, "Prazo reduzido");
        simularCenario(100, 10, 8, "Equipe maior");

        System.out.println("Conclusão: alterar um vértice do triângulo afeta os demais.");
    }
}
