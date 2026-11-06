package teste;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Executor dos testes: encontra as classes *Test na pasta compilada, roda cada
 * método @Test numa instância NOVA da classe e imprime um resumo no estilo do unittest.
 *
 * Normalmente é chamado por ferramentas/Rodar.java:
 *   java ../../ferramentas/Rodar.java testes            (todos os testes)
 *   java ../../ferramentas/Rodar.java testes Mvp        (só classes cujo nome contém "Mvp")
 */
public final class Executor {
    private int executados;
    private int pulados;
    private final List<String> falhas = new ArrayList<>();
    private final List<String> erros = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Path pasta = Path.of(args.length > 0 ? args[0] : "out");
        String filtro = args.length > 1 ? args[1] : "";
        Executor executor = new Executor();
        long inicio = System.nanoTime();
        for (Class<?> classe : classesDeTeste(pasta, filtro)) {
            executor.rodarClasse(classe);
        }
        executor.resumo((System.nanoTime() - inicio) / 1e9);
        System.exit(executor.falhas.isEmpty() && executor.erros.isEmpty() ? 0 : 1);
    }

    private static List<Class<?>> classesDeTeste(Path pasta, String filtro) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        try (Stream<Path> arquivos = Files.walk(pasta)) {
            for (Path p : arquivos.sorted().collect(Collectors.toList())) {
                String nome = pasta.relativize(p).toString().replace('\\', '/');
                if (!nome.endsWith("Test.class") || nome.contains("$") || nome.startsWith("teste/")) {
                    continue;
                }
                String classe = nome.substring(0, nome.length() - ".class".length()).replace('/', '.');
                if (!classe.contains(filtro)) {
                    continue;
                }
                try {
                    Class<?> c = Class.forName(classe);
                    if (!Modifier.isAbstract(c.getModifiers())) {
                        classes.add(c);
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return classes;
    }

    private void rodarClasse(Class<?> classe) {
        List<Method> testes = new ArrayList<>();
        List<Method> antes = new ArrayList<>();
        List<Method> depois = new ArrayList<>();
        for (Class<?> c = classe; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                m.setAccessible(true);
                if (m.isAnnotationPresent(Test.class)) {
                    testes.add(m);
                } else if (m.isAnnotationPresent(BeforeEach.class)) {
                    antes.add(0, m);
                } else if (m.isAnnotationPresent(AfterEach.class)) {
                    depois.add(m);
                }
            }
        }
        testes.sort(Comparator.comparing(Method::getName));
        for (Method teste : testes) {
            rodarTeste(classe, teste, antes, depois);
        }
    }

    private void rodarTeste(Class<?> classe, Method teste, List<Method> antes, List<Method> depois) {
        String nome = teste.getName() + " (" + classe.getSimpleName() + ")";
        executados++;
        Throwable problema = null;
        Object instancia = null;
        try {
            var construtor = classe.getDeclaredConstructor();
            construtor.setAccessible(true);
            instancia = construtor.newInstance();
            for (Method m : antes) {
                m.invoke(instancia);
            }
            teste.invoke(instancia);
        } catch (Throwable t) {
            problema = causa(t);
        }
        if (instancia != null) {
            for (Method m : depois) {
                try {
                    m.invoke(instancia);
                } catch (Throwable t) {
                    if (problema == null) {
                        problema = causa(t);
                    }
                }
            }
        }
        if (problema == null) {
            System.out.println(nome + " ... ok");
        } else if (problema instanceof Assumptions.TestePulado) {
            pulados++;
            System.out.println(nome + " ... PULADO: " + problema.getMessage());
        } else if (problema instanceof AssertionError) {
            falhas.add(nome + "\n" + rastro(problema));
            System.out.println(nome + " ... FALHOU");
        } else {
            erros.add(nome + "\n" + rastro(problema));
            System.out.println(nome + " ... ERRO");
        }
    }

    private static Throwable causa(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    /** Mensagem + as linhas do rastro que apontam para o código do aluno (sem o "miolo" da JVM). */
    private static String rastro(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getName() + ": " + t.getMessage());
        for (StackTraceElement e : t.getStackTrace()) {
            String c = e.getClassName();
            if (c.startsWith("java.") || c.startsWith("jdk.") || c.startsWith("sun.") || c.startsWith("teste.")) {
                continue;
            }
            sb.append("\n    at ").append(e);
        }
        return sb.toString();
    }

    private void resumo(double segundos) {
        for (String f : falhas) {
            System.out.println("\n======================================================================\nFAIL: " + f);
        }
        for (String e : erros) {
            System.out.println("\n======================================================================\nERROR: " + e);
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf(java.util.Locale.ROOT, "Ran %d tests in %.3fs%n%n", executados, segundos);
        if (falhas.isEmpty() && erros.isEmpty()) {
            System.out.println(pulados > 0 ? "OK (skipped=" + pulados + ")" : "OK");
        } else {
            System.out.println("FAILED (failures=" + falhas.size() + ", errors=" + erros.size()
                    + (pulados > 0 ? ", skipped=" + pulados : "") + ")");
        }
    }
}
