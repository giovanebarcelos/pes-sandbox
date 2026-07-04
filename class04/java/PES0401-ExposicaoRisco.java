/*
 * PES0401 - Cálculo de Exposição a Risco e Indicadores
 * Aula 04: Acompanhamento, Riscos e Ferramentas de Gestão
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Calcula a exposição a risco (probabilidade × impacto financeiro)
 * e exibe indicadores de progresso do projeto (EV, PV, AC simplificados).
 *
 * Compilar/executar:
 *   javac PES0401-ExposicaoRisco.java && java ExposicaoRisco
 */

import java.util.*;

class ExposicaoRisco {

    static class Risco {
        String nome;
        double probabilidade;
        double impactoFinanceiro;

        Risco(String nome, double probabilidade, double impactoFinanceiro) {
            this.nome = nome;
            this.probabilidade = probabilidade;
            this.impactoFinanceiro = impactoFinanceiro;
        }

        double getExposicao() {
            return probabilidade * impactoFinanceiro;
        }

        String getClassificacao() {
            double exp = getExposicao();
            if (exp > 50_000) return "ALTO  🔴";
            else if (exp > 10_000) return "MÉDIO 🟡";
            else return "BAIXO 🟢";
        }
    }

    static class EVMResult {
        double spi, cpi, sv, cv;
        String statusPrazo, statusCusto;

        EVMResult(double spi, double cpi, double sv, double cv, String statusPrazo, String statusCusto) {
            this.spi = spi; this.cpi = cpi; this.sv = sv; this.cv = cv;
            this.statusPrazo = statusPrazo; this.statusCusto = statusCusto;
        }
    }

    public static EVMResult calcularEVM(double valorPlanejado, double valorAgregado, double custoReal) {
        double spi = valorPlanejado > 0 ? valorAgregado / valorPlanejado : 0;
        double cpi = custoReal > 0 ? valorAgregado / custoReal : 1.0;
        double sv = valorAgregado - valorPlanejado;
        double cv = valorAgregado - custoReal;
        String statusPrazo = spi >= 1.0 ? "No prazo" : "Atrasado";
        String statusCusto = cpi >= 1.0 ? "Dentro do orçamento" : "Acima do orçamento";
        return new EVMResult(spi, cpi, sv, cv, statusPrazo, statusCusto);
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GESTÃO DE RISCOS E INDICADORES (EVM)");
        System.out.println("=".repeat(60));

        List<Risco> riscos = Arrays.asList(
            new Risco("Atraso em dependência externa", 0.7, 80_000),
            new Risco("Saída de desenvolvedor chave", 0.5, 120_000),
            new Risco("Mudança de requisitos tardia", 0.4, 25_000),
            new Risco("Falha de integração", 0.3, 60_000),
            new Risco("Indisponibilidade de servidor", 0.1, 5_000)
        );

        System.out.printf("%n--- Matriz de Riscos ---%n");
        System.out.printf("%-35s %6s %12s %12s %s%n", "Risco", "Prob", "Impacto", "Exposição", "Clas.");
        System.out.println("-".repeat(80));
        for (Risco r : riscos) {
            System.out.printf("%-35s %5.0f%% R$%,10.0f R$%,10.0f %s%n",
                    r.nome, r.probabilidade * 100, r.impactoFinanceiro,
                    r.getExposicao(), r.getClassificacao());
        }

        System.out.printf("%n--- Indicadores de Progresso (EVM Simplificado) ---%n");
        EVMResult evm = calcularEVM(50000, 42000, 48000);
        System.out.printf("SPI: %.2f (%s)%n", evm.spi, evm.statusPrazo);
        System.out.printf("CPI: %.2f (%s)%n", evm.cpi, evm.statusCusto);
        System.out.printf("SV (Schedule Variance): R$ %,.2f%n", evm.sv);
        System.out.printf("CV (Cost Variance): R$ %,.2f%n", evm.cv);
    }
}
