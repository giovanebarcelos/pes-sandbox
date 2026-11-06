/*
 * PES2801 - Checklist de Code Review e Boas Práticas Colaborativas
 * Aula 28: Práticas Colaborativas, Ética e Encerramento
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa um checklist interativo de code review,
 * promovendo práticas colaborativas de qualidade.
 * (Versão Java de class28/python/PES2801-CodeReviewChecklist.py. Diferença: em vez de
 * "descomentar executar_checklist()", o modo interativo é ligado pelo argumento --interativo.)
 *
 * Executar (Java 17+):        java PES2801-CodeReviewChecklist.java
 * Modo interativo (checklist): java PES2801-CodeReviewChecklist.java --interativo
 * Ou compilar:  javac PES2801-CodeReviewChecklist.java && java CodeReviewChecklist [--interativo]
 */

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

class CodeReviewChecklist {

    /** Uma categoria do checklist e as suas perguntas. */
    record Categoria(String nome, List<String> itens) {
    }

    static final List<Categoria> CHECKLIST_CODE_REVIEW = List.of(
        new Categoria("Funcionalidade", List.of(
            "O código faz o que foi especificado?",
            "Todos os casos de borda foram tratados?",
            "Há testes automatizados cobrindo a mudança?")),
        new Categoria("Legibilidade", List.of(
            "Nomes de variáveis/funções são descritivos?",
            "Funções têm tamanho adequado (< 30 linhas)?",
            "Código duplicado foi evitado (DRY)?")),
        new Categoria("Segurança", List.of(
            "Dados sensíveis estão protegidos?",
            "Validação de entrada está presente?",
            "Não há credenciais hardcoded?")),
        new Categoria("Performance", List.of(
            "Não há loops desnecessários?",
            "Consultas são eficientes?",
            "Recursos são liberados corretamente?")),
        new Categoria("Documentação", List.of(
            "Docstrings/Javadoc estão presentes?",
            "README foi atualizado se necessário?",
            "Comentários explicam o 'porquê', não o 'como'?")),
        new Categoria("Colaboração", List.of(
            "A mensagem de commit é clara e semântica?",
            "O PR tem descrição e link para a issue?",
            "Feedbacks anteriores foram endereçados?"))
    );

    static final Set<String> RESPOSTAS_SIM = Set.of("s", "sim", "y", "yes");

    /** Executa o checklist interativo de code review. */
    static void executarChecklist() {
        System.out.println("=".repeat(60));
        System.out.println("  CHECKLIST DE CODE REVIEW");
        System.out.println("=".repeat(60));
        System.out.println();

        Scanner entrada = new Scanner(System.in);
        int total = 0;
        int aprovados = 0;

        for (Categoria categoria : CHECKLIST_CODE_REVIEW) {
            System.out.println("--- " + categoria.nome() + " ---");
            for (String item : categoria.itens()) {
                total++;
                System.out.print("  [" + item + "] (s/n)? ");
                String resposta = entrada.hasNextLine() ? entrada.nextLine().strip().toLowerCase() : "";
                if (RESPOSTAS_SIM.contains(resposta)) {
                    aprovados++;
                    System.out.println("    ✓ OK");
                } else {
                    System.out.println("    ⚠️  Pendente");
                }
            }
            System.out.println();
        }

        double pct = total > 0 ? aprovados * 100.0 / total : 0;
        System.out.printf(Locale.ROOT, "Resultado: %d/%d itens aprovados (%.0f%%)%n", aprovados, total, pct);
        if (pct >= 90) {
            System.out.println("✓ Code Review aprovado! Pronto para merge.");
        } else if (pct >= 70) {
            System.out.println("⚠️  Code Review com ressalvas. Corrija os itens pendentes.");
        } else {
            System.out.println("✗ Code Review reprovado. Resolva os itens antes do merge.");
        }
    }

    /** Exibe princípios de colaboração ética em equipe. */
    static void exibirBoasPraticas() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  BOAS PRÁTICAS COLABORATIVAS E ÉTICA");
        System.out.println("=".repeat(60));
        List<String> praticas = List.of(
            "Respeite opiniões divergentes — diversidade gera melhores soluções",
            "Seja construtivo(a) no feedback: critique o código, não a pessoa",
            "Reconheça contribuições alheias (atribuição justa)",
            "Mantenha confidencialidade de dados de usuários (LGPD/GDPR)",
            "Comunique-se de forma clara, objetiva e respeitosa",
            "Documente decisões importantes para a equipe",
            "Assuma responsabilidade por seus erros e aprenda com eles"
        );
        for (String p : praticas) {
            System.out.println("  • " + p);
        }
    }

    public static void main(String[] args) {
        exibirBoasPraticas();
        System.out.println();
        if (List.of(args).contains("--interativo")) {
            executarChecklist();
        } else {
            System.out.println("(Para usar o checklist interativo, rode com --interativo)");
        }
    }
}
