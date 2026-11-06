import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cria a sua cópia de trabalho de uma aula:  aulaXX/inicio  ->  aulaXX/meu
 *
 * Uso (na pasta ua5-workspace-java/):
 *   java ferramentas/Preparar.java 18                 começa do ponto de partida oficial
 *   java ferramentas/Preparar.java 19 --da-minha      começa da SUA aula18/meu
 *   java ferramentas/Preparar.java 18 --forcar        apaga aula18/meu e recria
 */
public class Preparar {
    static final Set<Integer> AULAS = Set.of(18, 19, 20, 21, 22);
    static final Set<String> IGNORAR = Set.of("out", "dados", "frota.csv", "eventos.jsonl");

    public static void main(String[] args) throws IOException {
        Integer aula = null;
        boolean daMinha = false;
        boolean forcar = false;
        for (String a : args) {
            switch (a) {
                case "--da-minha": daMinha = true; break;
                case "--forcar": forcar = true; break;
                default: aula = Integer.valueOf(a);
            }
        }
        if (aula == null || !AULAS.contains(aula)) {
            sair("Uso: java ferramentas/Preparar.java 18|19|20|21|22 [--da-minha] [--forcar]");
        }
        Path raiz = Path.of("").toAbsolutePath();
        Path destino = raiz.resolve("aula" + aula).resolve("meu");
        Path origem;
        if (daMinha) {
            if (aula == 18) {
                sair("A Aula 18 é a primeira: use sem --da-minha.");
            }
            origem = raiz.resolve("aula" + (aula - 1)).resolve("meu");
        } else {
            origem = raiz.resolve("aula" + aula).resolve("inicio");
        }
        if (!Files.isDirectory(origem)) {
            sair("Origem não encontrada: " + origem + "  (rode na pasta ua5-workspace-java/)");
        }
        if (Files.exists(destino)) {
            if (!forcar) {
                sair(destino + " já existe. Use --forcar para recriar (o conteúdo atual será apagado).");
            }
            try (Stream<Path> s = Files.walk(destino)) {
                for (Path f : s.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                    Files.delete(f);
                }
            }
        }
        List<Path> arquivos;
        try (Stream<Path> s = Files.walk(origem)) {
            arquivos = s.collect(Collectors.toList());
        }
        for (Path f : arquivos) {
            Path rel = origem.relativize(f);
            boolean ignorado = false;
            for (Path parte : rel) {
                ignorado |= IGNORAR.contains(parte.toString());
            }
            if (ignorado) {
                continue;
            }
            Path alvo = destino.resolve(rel.toString());
            if (Files.isDirectory(f)) {
                Files.createDirectories(alvo);
            } else {
                Files.copy(f, alvo);
            }
        }
        System.out.println("Pronto: " + raiz.relativize(origem) + "  ->  " + raiz.relativize(destino));
        System.out.println("Agora:  cd " + raiz.relativize(destino) + "   e   java ../../ferramentas/Rodar.java testes");
    }

    static void sair(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
