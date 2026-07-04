/*
 * PES1401 - Modelo de Atores e Casos de Uso
 * Aula 14: Diagrama de Casos de Uso
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa um modelo simples de atores e casos de uso com descrições,
 * demonstrando a relação entre atores e funcionalidades do sistema.
 *
 * Compilar/executar:
 *   javac PES1401-CasosUso.java && java CasosUso
 */

import java.util.*;

class CasosUso {

    static class Ator {
        String nome, tipo;

        Ator(String nome, String tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }

        @Override
        public String toString() {
            return nome + " (" + tipo + ")";
        }
    }

    static class CasoDeUso {
        String id, nome, preCondicao, posCondicao;
        Ator atorPrincipal;
        List<String> fluxoPrincipal, fluxoAlternativo;

        CasoDeUso(String id, String nome, Ator atorPrincipal, String preCondicao,
                  List<String> fluxoPrincipal, List<String> fluxoAlternativo, String posCondicao) {
            this.id = id;
            this.nome = nome;
            this.atorPrincipal = atorPrincipal;
            this.preCondicao = preCondicao;
            this.fluxoPrincipal = fluxoPrincipal;
            this.fluxoAlternativo = fluxoAlternativo;
            this.posCondicao = posCondicao != null ? posCondicao : "Caso de uso concluído com sucesso";
        }

        void descrever() {
            System.out.printf("%n%s%n", "=".repeat(60));
            System.out.printf("  %s: %s%n", id, nome);
            System.out.printf("%s%n", "=".repeat(60));
            System.out.printf("  Ator Principal: %s%n", atorPrincipal);
            System.out.printf("  Pré-condição: %s%n", preCondicao);
            System.out.printf("  Fluxo Principal:%n");
            for (int i = 0; i < fluxoPrincipal.size(); i++) {
                System.out.printf("    %d. %s%n", i + 1, fluxoPrincipal.get(i));
            }
            if (!fluxoAlternativo.isEmpty()) {
                System.out.printf("  Fluxo Alternativo:%n");
                for (int i = 0; i < fluxoAlternativo.size(); i++) {
                    System.out.printf("    %da. %s%n", i + 1, fluxoAlternativo.get(i));
                }
            }
            System.out.printf("  Pós-condição: %s%n", posCondicao);
        }
    }

    public static void main(String[] args) {
        Ator bibliotecario = new Ator("Bibliotecário", "primário");

        CasoDeUso uc02 = new CasoDeUso(
            "UC02",
            "Realizar empréstimo de livro",
            bibliotecario,
            "Leitor cadastrado e livro disponível",
            Arrays.asList(
                "Bibliotecário identifica o leitor no sistema",
                "Bibliotecário pesquisa o livro desejado",
                "Sistema confirma disponibilidade do exemplar",
                "Bibliotecário confirma o empréstimo",
                "Sistema registra data de empréstimo e devolução prevista"
            ),
            Arrays.asList(
                "Se livro indisponível: sistema exibe mensagem e sugere reserva"
            ),
            "Livro registrado como emprestado ao leitor"
        );

        uc02.descrever();
        System.out.println("\n✓ O diagrama de casos de uso captura a visão funcional do sistema.");
        System.out.println("  Cada UC descreve uma interação ator-sistema que entrega valor.");
    }
}
