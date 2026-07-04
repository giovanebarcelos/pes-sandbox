/*
 * PES2301 - Roteiro de Comandos Git Comentado
 * Aula 23: Gerência de Configuração e Introdução ao Git
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Compilar/executar:
 *   javac PES2301-ComandosGit.java && java ComandosGit
 */

class ComandosGit {

    static class ComandoGit {
        String comando, descricao, categoria;

        ComandoGit(String comando, String descricao, String categoria) {
            this.comando = comando; this.descricao = descricao; this.categoria = categoria;
        }
    }

    static final ComandoGit[] ROTEIRO = {
        new ComandoGit("git init", "Inicializa um novo repositório Git", "inicialização"),
        new ComandoGit("git clone <url>", "Clona um repositório remoto", "inicialização"),
        new ComandoGit("git status", "Mostra o estado dos arquivos", "consulta"),
        new ComandoGit("git add <arquivo>", "Adiciona à staging area", "staging"),
        new ComandoGit("git add .", "Adiciona todos os modificados", "staging"),
        new ComandoGit("git commit -m 'msg'", "Registra mudanças no repo local", "commit"),
        new ComandoGit("git log --oneline", "Histórico de commits resumido", "consulta"),
        new ComandoGit("git diff", "Diferenças WD vs staging", "consulta"),
        new ComandoGit("git push origin main", "Envia commits para remoto", "remoto"),
        new ComandoGit("git pull origin main", "Baixa e mescla do remoto", "remoto"),
        new ComandoGit("git branch <nome>", "Cria uma nova branch", "branch"),
        new ComandoGit("git checkout <branch>", "Muda para a branch", "branch"),
        new ComandoGit("git merge <branch>", "Mescla branch na atual", "branch"),
    };

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("  GUIA RÁPIDO DE COMANDOS GIT");
        System.out.println("=".repeat(70));
        System.out.println();

        String categoriaAtual = "";
        for (ComandoGit c : ROTEIRO) {
            if (!c.categoria.equals(categoriaAtual)) {
                categoriaAtual = c.categoria;
                System.out.println("--- " + categoriaAtual.toUpperCase() + " ---");
            }
            System.out.printf("  $ %-32s # %s%n", c.comando, c.descricao);
        }

        System.out.println("\n--- BOAS PRÁTICAS: MENSAGENS DE COMMIT ---");
        System.out.println("  ✓ feat: adiciona endpoint de login");
        System.out.println("  ✓ fix: corrige validação de email nulo");
        System.out.println("  ✗ arrumei umas coisas (vago)");
        System.out.println("  ✗ atualizacao (genérico)");
    }
}
