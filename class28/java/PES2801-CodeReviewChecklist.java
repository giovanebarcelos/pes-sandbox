/*
 * PES2801 - Checklist de Code Review e Boas Práticas Colaborativas
 * Aula 28: Práticas Colaborativas, Ética e Encerramento
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Compilar/executar:
 *   javac PES2801-CodeReviewChecklist.java && java CodeReviewChecklist
 */

class CodeReviewChecklist {

    static class CategoriaChecklist {
        String nome;
        String[] itens;

        CategoriaChecklist(String nome, String... itens) {
            this.nome = nome;
            this.itens = itens;
        }
    }

    static final CategoriaChecklist[] CHECKLIST = {
        new CategoriaChecklist("Funcionalidade",
            "O código faz o que foi especificado?",
            "Todos os casos de borda foram tratados?",
            "Há testes automatizados cobrindo a mudança?"),
        new CategoriaChecklist("Legibilidade",
            "Nomes de variáveis/funções são descritivos?",
            "Funções têm tamanho adequado (< 30 linhas)?",
            "Código duplicado foi evitado (DRY)?"),
        new CategoriaChecklist("Segurança",
            "Dados sensíveis estão protegidos?",
            "Validação de entrada está presente?",
            "Não há credenciais hardcoded?"),
        new CategoriaChecklist("Performance",
            "Não há loops desnecessários?",
            "Consultas são eficientes?",
            "Recursos são liberados corretamente?"),
        new CategoriaChecklist("Documentação",
            "Docstrings/Javadoc estão presentes?",
            "README foi atualizado se necessário?",
            "Comentários explicam o 'porquê', não o 'como'?"),
        new CategoriaChecklist("Colaboração",
            "A mensagem de commit é clara e semântica?",
            "O PR tem descrição e link para a issue?",
            "Feedbacks anteriores foram endereçados?"),
    };

    public static void exibirChecklist() {
        System.out.println("=".repeat(60));
        System.out.println("  CHECKLIST DE CODE REVIEW");
        System.out.println("=".repeat(60));
        System.out.println();

        for (CategoriaChecklist cat : CHECKLIST) {
            System.out.println("--- " + cat.nome + " ---");
            for (String item : cat.itens) {
                System.out.println("  ☐ " + item);
            }
            System.out.println();
        }
    }

    public static void exibirBoasPraticas() {
        System.out.println("=".repeat(60));
        System.out.println("  BOAS PRÁTICAS COLABORATIVAS E ÉTICA");
        System.out.println("=".repeat(60));
        String[] praticas = {
            "Respeite opiniões divergentes — diversidade gera melhores soluções",
            "Seja construtivo(a) no feedback: critique o código, não a pessoa",
            "Reconheça contribuições alheias (atribuição justa)",
            "Mantenha confidencialidade de dados de usuários (LGPD/GDPR)",
            "Comunique-se de forma clara, objetiva e respeitosa",
            "Documente decisões importantes para a equipe",
            "Assuma responsabilidade por seus erros e aprenda com eles",
        };
        for (String p : praticas) System.out.println("  • " + p);
    }

    public static void main(String[] args) {
        exibirBoasPraticas();
        System.out.println();
        exibirChecklist();
        System.out.println("✓ Use este checklist em todo Pull Request.");
        System.out.println("✓ Code review é prática colaborativa, não punitiva.");
    }
}
