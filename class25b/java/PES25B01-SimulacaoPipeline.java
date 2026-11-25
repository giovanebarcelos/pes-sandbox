/*
 * PES25B01 - Simulação de Pipeline de CI/CD
 * Aula 25B (complementar): Integração Contínua e Entrega Contínua (CI/CD)
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Executa em sequência as etapas típicas de um pipeline (Slide 7 da Aula 25B)
 * aplicando as três regras centrais da aula:
 *
 *   1. FAIL FAST     -- se uma etapa falha, as seguintes NÃO são executadas;
 *   2. QUALITY GATE  -- a cobertura de testes abaixo do mínimo reprova o pipeline,
 *                       mesmo com todos os testes passando;
 *   3. CD DELIVERY x CD DEPLOY -- na Entrega Contínua a produção espera uma
 *                       aprovação humana; no Deploy Contínuo ela é automática.
 *
 * Logicamente equivalente a PES25B01-SimulacaoPipeline.py: mesmas etapas,
 * mesmos cenários, mesma saída. Resultados fixos (determinísticos);
 * duração = tempo SIMULADO de um pipeline real.
 *
 * Compilar/executar (Java 17+):
 *   javac PES25B01-SimulacaoPipeline.java && java SimulacaoPipeline
 */

import java.util.List;
import java.util.Locale;

class SimulacaoPipeline {

    static final double COBERTURA_MINIMA = 80.0;

    /** Resultado da execução de uma etapa. */
    record Resultado(boolean ok, String mensagem) {}

    /** Uma etapa (stage/job) do pipeline. */
    static class Etapa {
        final String nome;
        final int duracaoS;
        final boolean passa;

        Etapa(String nome, int duracaoS, boolean passa) {
            this.nome = nome;
            this.duracaoS = duracaoS;
            this.passa = passa;
        }

        Etapa(String nome, int duracaoS) {
            this(nome, duracaoS, true);
        }

        Resultado executar() {
            return new Resultado(passa, passa ? "OK" : "FALHOU");
        }
    }

    /** Etapa que reprova o pipeline se a cobertura ficar abaixo do mínimo. */
    static class QualityGate extends Etapa {
        final double cobertura;

        QualityGate(double cobertura) {
            super(String.format("Quality Gate (cobertura >= %.0f%%)", COBERTURA_MINIMA), 20);
            this.cobertura = cobertura;
        }

        @Override
        Resultado executar() {
            boolean ok = cobertura >= COBERTURA_MINIMA;
            return new Resultado(ok, String.format("%s - cobertura %.1f%%", ok ? "OK" : "FALHOU", cobertura));
        }
    }

    /** Última etapa: em "delivery" espera aprovação humana; em "deploy" é automática. */
    static class DeployProducao extends Etapa {
        final String modo;
        final boolean aprovado;

        DeployProducao(String modo, boolean aprovado) {
            super("Deploy em produção", 40);
            this.modo = modo;
            this.aprovado = aprovado;
        }

        @Override
        Resultado executar() {
            if (modo.equals("deploy")) return new Resultado(true, "OK - automático (CD Deploy)");
            if (aprovado) return new Resultado(true, "OK - aprovado manualmente (CD Delivery)");
            return new Resultado(false, "PENDENTE - aguardando aprovação (CD Delivery)");
        }
    }

    /** Executa as etapas em sequência, com fail fast. */
    static boolean executar(String nome, List<Etapa> etapas) {
        System.out.println("=".repeat(64));
        System.out.println("  PIPELINE: " + nome);
        System.out.println("=".repeat(64));
        int tempoTotal = 0;
        for (int i = 0; i < etapas.size(); i++) {
            Etapa etapa = etapas.get(i);
            Resultado r = etapa.executar();
            tempoTotal += etapa.duracaoS;
            System.out.printf("[%d/%d] %-40s [%s] (%ds)%n", i + 1, etapas.size(), etapa.nome, r.mensagem(), etapa.duracaoS);
            if (!r.ok()) {
                int restantes = etapas.size() - (i + 1);
                if (etapa instanceof DeployProducao) {
                    System.out.println("\n>> Artefato pronto e homologado; produção espera um clique humano.");
                } else {
                    System.out.printf("%n>> FAIL FAST: '%s' falhou; %d etapa(s) NÃO executada(s).%n", etapa.nome, restantes);
                }
                System.out.printf("   Feedback em %ds (tempo simulado)%n", tempoTotal);
                System.out.println("   RESULTADO: PIPELINE INTERROMPIDO");
                return false;
            }
        }
        System.out.printf("%n   Commit em produção em %ds (tempo simulado)%n", tempoTotal);
        System.out.println("   RESULTADO: PIPELINE APROVADO");
        return true;
    }

    static List<Etapa> etapasPadrao(boolean testesPassam, double cobertura, String modo, boolean aprovado) {
        return List.of(
            new Etapa("Checkout", 5),
            new Etapa("Build (compilar / instalar deps)", 40),
            new Etapa("Testes unitários", 60, testesPassam),
            new Etapa("Testes de integração", 90),
            new QualityGate(cobertura),
            new Etapa("Package (imagem Docker)", 45),
            new Etapa("Deploy em staging", 30),
            new Etapa("Testes de API/E2E em staging", 120),
            new DeployProducao(modo, aprovado)
        );
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT); // "86.4%" (ponto), igual ao Python, em qualquer idioma do sistema
        record Cenario(String nome, List<Etapa> etapas) {}
        List<Cenario> cenarios = List.of(
            new Cenario("reservalab-api | main | CD Delivery, aprovado",
                etapasPadrao(true, 86.4, "delivery", true)),
            new Cenario("reservalab-api | main | CD Delivery, sem aprovação",
                etapasPadrao(true, 86.4, "delivery", false)),
            new Cenario("reservalab-api | main | CD Deploy (automático)",
                etapasPadrao(true, 86.4, "deploy", false)),
            new Cenario("reservalab-api | feature/multa | teste quebrado",
                etapasPadrao(false, 86.4, "delivery", true)),
            new Cenario("reservalab-api | feature/relatorio | testes verdes, cobertura baixa",
                etapasPadrao(true, 72.5, "delivery", true))
        );
        for (Cenario c : cenarios) {
            executar(c.nome(), c.etapas());
            System.out.println();
        }
    }
}
