/*
 * PES0301 - Cálculo de Caminho Crítico e Estimativa de Custo
 * Aula 03: Planejamento — Escopo, Prazo, Custo e Qualidade
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa um algoritmo simplificado de caminho crítico (CPM) para um
 * conjunto de atividades com dependências e calcula o custo total estimado.
 *
 * Compilar/executar:
 *   javac PES0301-CaminhoCritico.java && java CaminhoCritico
 */

import java.util.*;

class CaminhoCritico {

    static class Atividade {
        String nome;
        int duracao;
        double custoPorDia;
        List<String> dependencias;
        int inicioCedo = 0;
        int fimCedo = 0;

        Atividade(String nome, int duracao, double custoPorDia, String... dependencias) {
            this.nome = nome;
            this.duracao = duracao;
            this.custoPorDia = custoPorDia;
            this.dependencias = Arrays.asList(dependencias);
        }

        double getCustoTotal() {
            return duracao * custoPorDia;
        }

        @Override
        public String toString() {
            return String.format("%s[dur=%dd, ini=%d, fim=%d, custo=R$%.0f]",
                    nome, duracao, inicioCedo, fimCedo, getCustoTotal());
        }
    }

    static class ResultadoCPM {
        int duracaoTotal;
        double custoTotal;
        List<Atividade> criticas;

        ResultadoCPM(int duracaoTotal, double custoTotal, List<Atividade> criticas) {
            this.duracaoTotal = duracaoTotal;
            this.custoTotal = custoTotal;
            this.criticas = criticas;
        }
    }

    public static ResultadoCPM calcularCaminhoCritico(List<Atividade> atividades) {
        Map<String, Atividade> resolvidas = new LinkedHashMap<>();
        List<Atividade> pendentes = new ArrayList<>(atividades);

        while (!pendentes.isEmpty()) {
            boolean progresso = false;
            for (Iterator<Atividade> it = pendentes.iterator(); it.hasNext(); ) {
                Atividade atv = it.next();
                boolean depsResolvidas = atv.dependencias.stream().allMatch(resolvidas::containsKey);
                if (depsResolvidas) {
                    int inicio = 0;
                    for (String dep : atv.dependencias) {
                        inicio = Math.max(inicio, resolvidas.get(dep).fimCedo);
                    }
                    atv.inicioCedo = inicio;
                    atv.fimCedo = inicio + atv.duracao;
                    resolvidas.put(atv.nome, atv);
                    it.remove();
                    progresso = true;
                    break;
                }
            }
            if (!progresso) throw new IllegalStateException("Dependência circular ou não resolvida.");
        }

        int duracaoTotal = atividades.stream().mapToInt(a -> a.fimCedo).max().orElse(0);
        double custoTotal = atividades.stream().mapToDouble(Atividade::getCustoTotal).sum();

        List<Atividade> criticas = new ArrayList<>();
        for (Atividade a : atividades) {
            if (a.fimCedo == duracaoTotal ||
                atividades.stream().anyMatch(dep -> dep.dependencias.contains(a.nome) && dep.fimCedo == duracaoTotal)) {
                criticas.add(a);
            }
        }

        return new ResultadoCPM(duracaoTotal, custoTotal, criticas);
    }

    public static void main(String[] args) {
        List<Atividade> atvs = Arrays.asList(
            new Atividade("Plano de Projeto", 7, 200),
            new Atividade("Entrevistas", 7, 150, "Plano de Projeto"),
            new Atividade("DRS", 10, 180, "Entrevistas"),
            new Atividade("Back-end", 21, 250, "DRS"),
            new Atividade("Front-end", 21, 250, "DRS"),
            new Atividade("Banco de Dados", 14, 220, "DRS"),
            new Atividade("Testes Unitários", 7, 200, "Back-end", "Front-end"),
            new Atividade("Testes Integrados", 7, 200, "Testes Unitários", "Banco de Dados"),
            new Atividade("Implantação", 3, 300, "Testes Integrados")
        );

        ResultadoCPM r = calcularCaminhoCritico(atvs);

        System.out.println("=".repeat(60));
        System.out.println("  CAMINHO CRÍTICO E ESTIMATIVA DE CUSTO");
        System.out.println("=".repeat(60));
        System.out.printf("%nDuração total do projeto: %d dias%n", r.duracaoTotal);
        System.out.printf("Custo total estimado: R$ %,.2f%n", r.custoTotal);
        System.out.printf("%nAtividades no caminho crítico:%n");
        for (Atividade a : r.criticas) {
            System.out.printf("  • %s (%dd, R$ %,.2f)%n", a.nome, a.duracao, a.getCustoTotal());
        }
    }
}
