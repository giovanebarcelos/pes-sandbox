/*
 * PES0901 - Registro e Priorização de Requisitos (MoSCoW)
 * Aula 09: Introdução à Engenharia de Requisitos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa um registro de requisitos com priorização MoSCoW
 * (Must have, Should have, Could have, Won't have).
 *
 * Compilar/executar:
 *   javac PES0901-RegistroRequisitos.java && java RegistroRequisitos
 */

import java.util.*;

class RegistroRequisitos {

    enum Prioridade {
        MUST("Must have", 4),
        SHOULD("Should have", 3),
        COULD("Could have", 2),
        WONT("Won't have", 1);

        final String label;
        final int peso;

        Prioridade(String label, int peso) {
            this.label = label;
            this.peso = peso;
        }
    }

    static class Requisito {
        String id, descricao, stakeholder, tipo;
        Prioridade prioridade;

        Requisito(String id, String descricao, Prioridade prioridade, String stakeholder, String tipo) {
            this.id = id;
            this.descricao = descricao;
            this.prioridade = prioridade;
            this.stakeholder = stakeholder;
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (%s) %s — @%s", id, prioridade.label, tipo, descricao, stakeholder);
        }
    }

    static class Registro {
        List<Requisito> requisitos = new ArrayList<>();

        void adicionar(Requisito r) { requisitos.add(r); }

        List<Requisito> listarPorPrioridade() {
            List<Requisito> ordenados = new ArrayList<>(requisitos);
            ordenados.sort((a, b) -> Integer.compare(b.prioridade.peso, a.prioridade.peso));
            return ordenados;
        }

        List<Requisito> filtrarMustShould() {
            List<Requisito> resultado = new ArrayList<>();
            for (Requisito r : requisitos) {
                if (r.prioridade == Prioridade.MUST || r.prioridade == Prioridade.SHOULD) {
                    resultado.add(r);
                }
            }
            return resultado;
        }

        void resumo() {
            System.out.printf("Total: %d requisitos%n", requisitos.size());
            for (Prioridade p : Prioridade.values()) {
                long count = requisitos.stream().filter(r -> r.prioridade == p).count();
                System.out.printf("  %s: %d%n", p.label, count);
            }
        }
    }

    public static void main(String[] args) {
        Registro registro = new Registro();

        registro.adicionar(new Requisito("RF01", "O sistema deve permitir login com email e senha",
                Prioridade.MUST, "Usuário Final", "RF"));
        registro.adicionar(new Requisito("RF02", "O sistema deve enviar email de confirmação de cadastro",
                Prioridade.SHOULD, "Product Owner", "RF"));
        registro.adicionar(new Requisito("RNF01", "O sistema deve responder em até 2 segundos",
                Prioridade.MUST, "Arquiteto", "RNF"));
        registro.adicionar(new Requisito("RF03", "O usuário pode exportar relatório em PDF",
                Prioridade.COULD, "Usuário Final", "RF"));
        registro.adicionar(new Requisito("RF04", "Integração com redes sociais para login",
                Prioridade.WONT, "Marketing", "RF"));

        System.out.println("=".repeat(60));
        System.out.println("  REGISTRO DE REQUISITOS — PRIORIZAÇÃO MOSCOW");
        System.out.println("=".repeat(60));
        System.out.println();

        System.out.println("--- Lista por Prioridade ---");
        for (Requisito r : registro.listarPorPrioridade()) {
            System.out.println("  " + r);
        }

        System.out.printf("%n--- MVP (Must + Should) ---%n");
        for (Requisito r : registro.filtrarMustShould()) {
            System.out.println("  " + r);
        }

        System.out.printf("%n--- Resumo ---%n");
        registro.resumo();
    }
}
