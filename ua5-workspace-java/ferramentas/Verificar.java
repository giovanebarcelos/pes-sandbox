import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Confere o ambiente (JDK 17+) e roda os testes das aulas.
 *
 * Uso (na pasta ua5-workspace-java/):
 *   java ferramentas/Verificar.java                  todas as aulas, pastas inicio e solucao
 *   java ferramentas/Verificar.java 20               só a Aula 20
 *   java ferramentas/Verificar.java 20 --pasta meu   a SUA versão da Aula 20
 */
public class Verificar {
    static final int[] AULAS = {18, 19, 20, 21, 22};

    public static void main(String[] args) throws Exception {
        Integer aula = null;
        String pasta = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--pasta") && i + 1 < args.length) {
                pasta = args[++i];
            } else {
                aula = Integer.parseInt(args[i]);
            }
        }
        int versao = Runtime.version().feature();
        if (versao < 17) {
            System.err.println("Java 17 ou superior é necessário (você tem " + Runtime.version() + ").");
            System.exit(1);
        }
        if (javax.tools.ToolProvider.getSystemJavaCompiler() == null) {
            System.err.println("Este Java não tem o javac. Instale um JDK 17+ (não só o JRE).");
            System.exit(1);
        }
        System.out.println("Java " + Runtime.version() + " OK  (" + System.getProperty("java.home") + ")\n");

        Path raiz = Path.of("").toAbsolutePath();
        if (!Files.isDirectory(raiz.resolve("ferramentas"))) {
            System.err.println("Rode na pasta ua5-workspace-java/:  java ferramentas/Verificar.java");
            System.exit(2);
        }
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String rodar = raiz.resolve("ferramentas/Rodar.java").toString();
        List<Integer> aulas = new ArrayList<>();
        for (int a : AULAS) {
            if (aula == null || aula == a) {
                aulas.add(a);
            }
        }
        List<String> pastas = pasta != null ? List.of(pasta) : List.of("inicio", "solucao");
        int falhas = 0;
        for (int a : aulas) {
            for (String p : pastas) {
                Path alvo = raiz.resolve("aula" + a).resolve(p);
                String rotulo = String.format("  aula%d/%-8s", a, p);
                if (!Files.isDirectory(alvo)) {
                    System.out.println(rotulo + " (não existe — rode java ferramentas/Preparar.java " + a + ")");
                    continue;
                }
                Process proc = new ProcessBuilder(java, rodar, "testes").directory(alvo.toFile())
                        .redirectErrorStream(true).start();
                List<String> linhas;
                try (BufferedReader r = new BufferedReader(
                        new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                    linhas = r.lines().collect(Collectors.toList());
                }
                int codigo = proc.waitFor();
                String resumo = linhas.stream()
                        .filter(l -> l.startsWith("Ran ") || l.startsWith("OK") || l.startsWith("FAILED"))
                        .collect(Collectors.joining(" | "));
                System.out.println(rotulo + " " + (codigo == 0 ? "OK   " : "FALHA") + " " + resumo);
                if (codigo != 0) {
                    falhas++;
                    for (String l : linhas.subList(Math.max(0, linhas.size() - 25), linhas.size())) {
                        System.out.println("    " + l);
                    }
                }
            }
        }
        System.exit(falhas == 0 ? 0 : 1);
    }
}
