import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Compila a pasta da aula (src/ + test/) e roda os testes ou uma classe com main.
 * Só usa o JDK: nada de Maven/Gradle, funciona igual em Windows, Linux e macOS.
 *
 * Uso (DENTRO da pasta da aula, ex.: aula18/meu):
 *   java ../../ferramentas/Rodar.java testes                 roda todos os testes
 *   java ../../ferramentas/Rodar.java testes Caracterizacao  só as classes *Test com esse trecho no nome
 *   java ../../ferramentas/Rodar.java locadora.Main          roda a classe locadora.Main
 *   java ../../ferramentas/Rodar.java compilar               só compila (mostra os erros do javac)
 *
 * O que ele faz:
 *   1. javac -encoding UTF-8 em todos os .java de src/ e test/, numa pasta temporária
 *      SÓ desta execução (assim dá para rodar 3 serviços ao mesmo tempo na mesma pasta)
 *   2. copia para lá os arquivos de src/ que não são .java (ex.: META-INF/services)
 *   3. executa:  java -cp <pasta temporária>[:lib/*] <classe> [argumentos]
 *   4. apaga a pasta temporária ao terminar (também com Ctrl+C)
 * "compilar" grava em out/ (para você olhar os .class, se quiser).
 * JARs opcionais (ex.: driver JDBC da Aula 20) vão em ua5-workspace-java/lib/.
 */
public class Rodar {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: java ../../ferramentas/Rodar.java testes | compilar | <classe.Main> [args...]");
            System.exit(2);
        }
        Path pasta = Path.of("").toAbsolutePath();
        if (!Files.isDirectory(pasta.resolve("src"))) {
            System.err.println("Não achei src/ em " + pasta + ". Rode este comando DENTRO da pasta da aula (ex.: aula18/meu).");
            System.exit(2);
        }
        if (args[0].equals("compilar")) {
            Path out = pasta.resolve("out");
            apagar(out);
            compilar(pasta, out);
            System.out.println("Compilado em out/");
            return;
        }
        Path destino = Files.createTempDirectory("rodar-");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                apagar(destino);
            } catch (IOException e) {
                // pasta temporária: o sistema limpa depois
            }
        }));
        String classpath = compilar(pasta, destino);
        List<String> comando = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp", classpath));
        if (args[0].equals("testes")) {
            comando.add("teste.Executor");
            comando.add(destino.toString());
            if (args.length > 1) {
                comando.add(args[1]);
            }
        } else {
            comando.addAll(List.of(args));
        }
        Process p = new ProcessBuilder(comando).directory(pasta.toFile()).inheritIO().start();
        Runtime.getRuntime().addShutdownHook(new Thread(p::destroy)); // encerrou o Rodar? encerra o programa também
        System.exit(p.waitFor());
    }

    static String compilar(Path pasta, Path out) throws IOException {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if (javac == null) {
            System.err.println("Este Java não tem o compilador (javac). Instale um JDK 17 ou superior, não só o JRE.");
            System.exit(2);
        }
        Files.createDirectories(out);
        List<String> fontes = new ArrayList<>();
        for (String raiz : List.of("src", "test")) {
            Path r = pasta.resolve(raiz);
            if (Files.isDirectory(r)) {
                try (Stream<Path> s = Files.walk(r)) {
                    fontes.addAll(s.filter(f -> f.toString().endsWith(".java")).map(Path::toString)
                            .sorted().collect(Collectors.toList()));
                }
            }
        }
        List<String> cp = new ArrayList<>(List.of(out.toString()));
        for (Path lib : List.of(pasta.resolve("lib"), pasta.resolve("../../lib").normalize())) {
            if (Files.isDirectory(lib)) {
                try (Stream<Path> s = Files.list(lib)) {
                    s.filter(f -> f.toString().endsWith(".jar")).sorted().forEach(f -> cp.add(f.toString()));
                }
            }
        }
        String classpath = String.join(File.pathSeparator, cp);
        List<String> opcoes = new ArrayList<>(List.of("-d", out.toString(), "-encoding", "UTF-8",
                "-cp", classpath, "-Xlint:-options"));
        opcoes.addAll(fontes);
        int r = javac.run(null, null, null, opcoes.toArray(new String[0]));
        if (r != 0) {
            System.err.println("\nCOMPILAÇÃO FALHOU — corrija os erros acima (arquivo:linha) e rode de novo.");
            System.exit(1);
        }
        Path src = pasta.resolve("src");
        try (Stream<Path> s = Files.walk(src)) {
            for (Path f : s.filter(Files::isRegularFile).filter(f -> !f.toString().endsWith(".java"))
                    .collect(Collectors.toList())) {
                Path destino = out.resolve(src.relativize(f).toString());
                Files.createDirectories(destino.getParent());
                Files.copy(f, destino);
            }
        }
        return classpath;
    }

    static void apagar(Path p) throws IOException {
        if (Files.exists(p)) {
            try (Stream<Path> s = Files.walk(p)) {
                for (Path f : s.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                    Files.delete(f);
                }
            }
        }
    }
}
