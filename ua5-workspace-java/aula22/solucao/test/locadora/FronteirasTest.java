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

/** Os bounded contexts não se enxergam por dentro (verificado no código-fonte). */
class FronteirasTest {
    static final Path RAIZ = Path.of("src", "locadora");
    private static final Pattern IMPORT = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.]+)", Pattern.MULTILINE);

    /** [arquivo, importado] de todos os .java da pasta. */
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

    static void proibir(String pasta, String... prefixos) throws IOException {
        List<String[]> imports = importsDe(pasta);
        assertFalse(imports.isEmpty(), "nenhum import encontrado em " + pasta);
        for (String[] imp : imports) {
            for (String proibido : prefixos) {
                assertFalse(imp[1].startsWith(proibido), imp[0] + " importa " + imp[1]);
            }
        }
    }

    @Test
    void locacoes_nao_importa_frota() throws IOException {
        proibir("locacoes", "locadora.frota");
    }

    @Test
    void frota_nao_importa_locacoes() throws IOException {
        proibir("frota", "locadora.locacoes");
    }

    @Test
    void pagamentos_e_um_modulo_independente() throws IOException {
        // Monolito modular (Aula 22): o microkernel de pagamentos não conhece os outros módulos.
        proibir("pagamentos", "locadora.frota", "locadora.locacoes");
    }

    @Test
    void dominio_de_locacoes_sem_infraestrutura() throws IOException {
        proibir("locacoes/dominio", "java.io", "java.nio", "java.net", "java.sql", "locadora.infra",
                "locadora.locacoes.adaptadores", "locadora.locacoes.aplicacao");
    }
}
