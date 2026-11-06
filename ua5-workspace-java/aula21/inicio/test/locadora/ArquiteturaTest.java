package locadora;

import static teste.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import teste.Test;

/**
 * Teste de ARQUITETURA — a Regra de Dependência vira teste automatizado.
 *
 * Lê o código-fonte (sem executá-lo) e verifica os imports de cada camada:
 *   dominio    -> só java.util / java.time... e o próprio dominio
 *   aplicacao  -> dominio e aplicacao (nunca adaptadores)
 * Pasta não é camada: se alguém colocar "import java.sql.Connection" no domínio, este teste falha.
 * (Em projetos maiores, a biblioteca ArchUnit faz o mesmo com mais recursos.)
 */
class ArquiteturaTest {
    static final Path RAIZ = Path.of("src", "locadora");
    static final List<String> PROIBIDOS_NO_NUCLEO = List.of(
            "java.sql", "javax.sql", "java.net", "java.io", "java.nio", "com.sun.net", "org.springframework");
    private static final Pattern IMPORT = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.]+)", Pattern.MULTILINE);

    /** [arquivo, pacote/classe importado] de todos os .java da pasta. */
    static List<String[]> importsDe(String pasta) throws IOException {
        List<String[]> resultado = new ArrayList<>();
        List<Path> arquivos;
        try (Stream<Path> s = Files.walk(RAIZ.resolve(pasta))) {
            arquivos = s.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
        }
        for (Path arq : arquivos) {
            Matcher m = IMPORT.matcher(Files.readString(arq, StandardCharsets.UTF_8));
            while (m.find()) {
                resultado.add(new String[] {RAIZ.relativize(arq).toString(), m.group(1)});
            }
        }
        return resultado;
    }

    private static void proibir(String pasta, List<String> prefixos) throws IOException {
        for (String[] imp : importsDe(pasta)) {
            for (String proibido : prefixos) {
                assertFalse(imp[1].startsWith(proibido), imp[0] + " importa " + imp[1]);
            }
        }
    }

    @Test
    void dominio_nao_conhece_ninguem() throws IOException {
        List<String> proibidos = new ArrayList<>(List.of("locadora.aplicacao", "locadora.adaptadores", "locadora.Config"));
        proibidos.addAll(PROIBIDOS_NO_NUCLEO);
        proibir("dominio", proibidos);
    }

    @Test
    void aplicacao_nao_conhece_adaptadores() throws IOException {
        List<String> proibidos = new ArrayList<>(List.of("locadora.adaptadores", "locadora.Config"));
        proibidos.addAll(PROIBIDOS_NO_NUCLEO);
        proibir("aplicacao", proibidos);
    }

    @Test
    void o_teste_enxerga_os_imports() throws IOException {
        // Sanidade: garante que o analisador realmente encontrou imports.
        assertTrue(importsDe("aplicacao").stream().anyMatch(
                i -> i[0].endsWith("LocadoraService.java") && i[1].equals("locadora.dominio.Locacao")));
    }
}
