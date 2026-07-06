/*
 * PES0602 - Ciclo de Prototipação: Descartável vs Evolutivo
 * Aula 06: Modelos Incremental e Prototipação
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula o ciclo de prototipação (construir -> avaliar com usuário ->
 * refinar) por N iterações, coletando feedback simulado do usuário, e
 * ilustra a diferença de destino entre um protótipo Descartável (jogado
 * fora após validar os requisitos) e um Evolutivo (refinado até virar o
 * sistema real).
 *
 * Compilar/executar:
 *   javac PES0602-CicloPrototipacao.java && java CicloPrototipacao
 */

class CicloPrototipacao {

    enum TipoPrototipo { DESCARTAVEL, EVOLUTIVO }

    static class Prototipo {
        String nome;
        TipoPrototipo tipo;
        double custoConstrucao;
        double custoAvaliacao;

        Prototipo(String nome, TipoPrototipo tipo, double custoConstrucao) {
            this(nome, tipo, custoConstrucao, 500.0);
        }

        Prototipo(String nome, TipoPrototipo tipo, double custoConstrucao, double custoAvaliacao) {
            this.nome = nome;
            this.tipo = tipo;
            this.custoConstrucao = custoConstrucao;
            this.custoAvaliacao = custoAvaliacao;
        }
    }

    static final String[] FEEDBACKS_SIMULADOS = {
        "Falta um campo para filtrar por categoria.",
        "O fluxo de cadastro está confuso, simplifique.",
        "Adicione um gráfico para visualizar o resultado.",
        "Está bom, só falta um botão de exportar."
    };

    /**
     * Simula N iterações do ciclo construir -> avaliar -> refinar.
     *
     * A cada iteração o custo de refino cai para 70% do anterior (o
     * protótipo já validado nas rodadas anteriores exige menos retrabalho).
     * Ao final, aplica o destino do protótipo: descartar e reconstruir do
     * zero, ou evoluir direto para o sistema real. Retorna o custo total.
     */
    static double cicloPrototipacao(Prototipo prototipo, int iteracoes) {
        double custoAcumulado = 0.0;
        System.out.printf("%n--- Ciclo de Prototipação: %s (%s) ---%n", prototipo.nome, prototipo.tipo);
        for (int i = 0; i < iteracoes; i++) {
            double fatorRefino = Math.pow(0.7, i);
            double custoIteracao = prototipo.custoConstrucao * fatorRefino;
            custoAcumulado += custoIteracao + prototipo.custoAvaliacao;
            String feedback = FEEDBACKS_SIMULADOS[i % FEEDBACKS_SIMULADOS.length];
            System.out.printf("  Iteração %d: construir R$ %.2f + avaliar R$ %.2f -> "
                    + "feedback do usuário: \"%s\"%n",
                    i + 1, custoIteracao, prototipo.custoAvaliacao, feedback);
        }

        if (prototipo.tipo == TipoPrototipo.DESCARTAVEL) {
            double custoSistemaReal = prototipo.custoConstrucao * 3;
            System.out.printf("  Decisão: DESCARTAR o protótipo e construir o sistema real "
                    + "do zero (R$ %.2f).%n", custoSistemaReal);
            custoAcumulado += custoSistemaReal;
        } else {
            System.out.println("  Decisão: EVOLUIR o protótipo até virar o sistema real "
                    + "(sem custo extra de reconstrução).");
        }

        System.out.printf("  Custo total do ciclo: R$ %.2f%n", custoAcumulado);
        return custoAcumulado;
    }

    /**
     * Executa o ciclo para um protótipo descartável e um evolutivo,
     * usando o mesmo escopo, para comparar o destino de cada um.
     */
    static void demonstrarPrototipacao() {
        System.out.println("=".repeat(60));
        System.out.println("  CICLO DE PROTOTIPAÇÃO: DESCARTÁVEL x EVOLUTIVO");
        System.out.println("=".repeat(60));

        Prototipo prototipoDescartavel = new Prototipo("Mockup de Navegação", TipoPrototipo.DESCARTAVEL, 1000.0);
        double custoDescartavel = cicloPrototipacao(prototipoDescartavel, 3);

        Prototipo prototipoEvolutivo = new Prototipo("MVP de Cadastro", TipoPrototipo.EVOLUTIVO, 1600.0);
        double custoEvolutivo = cicloPrototipacao(prototipoEvolutivo, 3);

        System.out.println("\n" + "=".repeat(60));
        System.out.printf("Descartável (3 iterações + sistema real do zero): R$ %.2f%n", custoDescartavel);
        System.out.printf("Evolutivo   (3 iterações, vira o sistema real):    R$ %.2f%n", custoEvolutivo);
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        demonstrarPrototipacao();
    }
}
