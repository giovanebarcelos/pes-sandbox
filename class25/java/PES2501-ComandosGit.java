/*
 * PES2501 - Roteiro de Comandos Git Executável
 * Aula 25: Gerência de Configuração e Introdução ao Git
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Executa DE VERDADE a sequência de comandos Git mais usada no dia a dia,
 * usando repositórios temporários criados exclusivamente para esta
 * demonstração (nunca o repositório real da disciplina). Cada comando é
 * disparado via ProcessBuilder contra um repositório real em um diretório
 * temporário, e a saída exibida é a saída REAL do Git -- nada aqui é apenas
 * texto impresso. Ao final, todos os diretórios temporários são removidos.
 *
 * Compilar/executar:
 *   javac PES2501-ComandosGit.java && java ComandosGit
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

class ComandosGit {

    static void rodar(List<String> comando, Path cwd) {
        System.out.println("  $ " + String.join(" ", comando));
        try {
            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.directory(cwd.toFile());
            pb.redirectErrorStream(true);
            Process processo = pb.start();
            String saida = new String(processo.getInputStream().readAllBytes()).strip();
            processo.waitFor();
            if (!saida.isEmpty()) {
                for (String linha : saida.split("\n")) {
                    System.out.println("    " + linha);
                }
            }
            System.out.println();
        } catch (IOException | InterruptedException e) {
            System.out.println("    Erro ao executar: " + e.getMessage());
        }
    }

    static void escrever(Path arquivo, String conteudo) throws IOException {
        Files.createDirectories(arquivo.getParent());
        Files.writeString(arquivo, conteudo);
    }

    static void removerDiretorio(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (var stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder())
                  .forEach(p -> p.toFile().delete());
        }
    }

    static void demoRoteiroGit() throws IOException {
        Path baseDir = Files.createTempDirectory("pes25-demo-");
        Path repoDir = baseDir.resolve("meu-projeto");
        Path originDir = baseDir.resolve("origin").resolve("meu-projeto.git");
        Path clonePaiDir = baseDir.resolve("colega");
        Path cloneDir = clonePaiDir.resolve("meu-projeto");
        Files.createDirectories(repoDir);

        try {
            System.out.println("=".repeat(70));
            System.out.println("  ROTEIRO DE COMANDOS GIT (execução real em repositório temporário)");
            System.out.println("  Repositório de trabalho: " + repoDir);
            System.out.println("=".repeat(70));
            System.out.println();

            System.out.println("--- INICIALIZAÇÃO ---");
            rodar(List.of("git", "init", "-b", "main"), repoDir);
            rodar(List.of("git", "config", "user.name", "PES Aula 25"), repoDir);
            rodar(List.of("git", "config", "user.email", "pes25@exemplo.edu"), repoDir);

            System.out.println("--- STAGING E COMMIT ---");
            escrever(repoDir.resolve("README.md"), "# Meu Projeto\n");
            rodar(List.of("git", "add", "README.md"), repoDir);
            rodar(List.of("git", "commit", "-m", "docs: adiciona README inicial"), repoDir);

            escrever(repoDir.resolve("main.py"), "print(\"Hello, Git!\")\n");
            rodar(List.of("git", "add", "."), repoDir);
            rodar(List.of("git", "commit", "-m", "feat: adiciona script principal"), repoDir);

            System.out.println("--- CONSULTA ---");
            rodar(List.of("git", "status"), repoDir);
            rodar(List.of("git", "log", "--oneline"), repoDir);

            escrever(repoDir.resolve("main.py"), "print(\"Hello, Git!\")\nprint(\"Versão 2\")\n");
            rodar(List.of("git", "diff"), repoDir);

            rodar(List.of("git", "add", "main.py"), repoDir);
            rodar(List.of("git", "commit", "-m", "feat: adiciona mensagem de versão"), repoDir);

            System.out.println("--- BRANCH ---");
            rodar(List.of("git", "branch", "feature-login"), repoDir);
            rodar(List.of("git", "checkout", "feature-login"), repoDir);
            escrever(repoDir.resolve("login.py"), "def login():\n    pass\n");
            rodar(List.of("git", "add", "login.py"), repoDir);
            rodar(List.of("git", "commit", "-m", "feat: adiciona esqueleto de login"), repoDir);
            rodar(List.of("git", "checkout", "main"), repoDir);
            rodar(List.of("git", "merge", "feature-login"), repoDir);

            System.out.println("--- CONFIGURAÇÃO (.gitignore) ---");
            escrever(repoDir.resolve(".gitignore"), "__pycache__/\n*.pyc\n");
            escrever(repoDir.resolve("temp.pyc"), "lixo\n");
            rodar(List.of("git", "add", ".gitignore"), repoDir);
            rodar(List.of("git", "commit", "-m", "chore: adiciona .gitignore"), repoDir);
            rodar(List.of("git", "status", "--ignored"), repoDir);

            System.out.println("--- REMOTO ---");
            Files.createDirectories(originDir);
            rodar(List.of("git", "init", "--bare", "-b", "main"), originDir);
            rodar(List.of("git", "remote", "add", "origin", originDir.toString()), repoDir);
            rodar(List.of("git", "push", "-u", "origin", "main"), repoDir);

            Files.createDirectories(clonePaiDir);
            rodar(List.of("git", "clone", originDir.toString(), cloneDir.toString()), clonePaiDir);
            escrever(cloneDir.resolve("CONTRIBUTING.md"), "# Como contribuir\n");
            rodar(List.of("git", "add", "CONTRIBUTING.md"), cloneDir);
            rodar(List.of("git", "commit", "-m", "docs: adiciona guia de contribuição"), cloneDir);
            rodar(List.of("git", "push", "origin", "main"), cloneDir);

            rodar(List.of("git", "pull", "origin", "main"), repoDir);

            System.out.println("=".repeat(70));
            System.out.println("  Demonstração concluída -- repositórios temporários serão removidos.");
            System.out.println("=".repeat(70));
        } finally {
            removerDiretorio(baseDir);
        }
    }

    static void verificarBoasPraticasCommit() {
        System.out.println("--- BOAS PRÁTICAS: MENSAGENS DE COMMIT ---");
        String[][] boas = {
            {"✓ feat: adiciona endpoint de login", "Prefixo semântico + descrição clara"},
            {"✓ fix: corrige validação de email nulo", "Indica correção de bug"},
            {"✗ arrumei umas coisas", "Vago — não descreve o que foi feito"},
            {"✗ atualizacao", "Genérico — impossível rastrear mudanças"},
        };
        for (String[] item : boas) {
            System.out.printf("  %-50s (%s)%n", item[0], item[1]);
        }
    }

    public static void main(String[] args) throws IOException {
        demoRoteiroGit();
        verificarBoasPraticasCommit();
    }
}
