package locadora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory Method (GoF): cria o veículo certo a partir de um texto ("carro", "moto"...).
 *
 * Quem lê dados de fora (arquivo, formulário, API) só conhece o nome do tipo;
 * a fábrica é o ÚNICO lugar que liga nome -> classe. Para um tipo novo,
 * basta registrá-lo (OCP) — nenhum if/else espalhado pelo sistema.
 */
public final class Fabrica {
    /** O "construtor" de um tipo: Carro::new, Moto::new... */
    @FunctionalInterface
    public interface Construtor {
        Veiculo criar(String placa, String modelo, double diaria);
    }

    private static final Map<String, Construtor> TIPOS = new HashMap<>(Map.of(
            "carro", Carro::new,
            "moto", Moto::new,
            "caminhao", Caminhao::new));

    private Fabrica() {
    }

    public static void registrarTipo(String nome, Construtor construtor) {
        TIPOS.put(nome, construtor);
    }

    public static List<String> tiposConhecidos() {
        List<String> nomes = new ArrayList<>(TIPOS.keySet());
        nomes.sort(null);
        return nomes;
    }

    public static Veiculo criarVeiculo(String tipo, String placa, String modelo, double diaria) {
        Construtor c = TIPOS.get(tipo);
        if (c == null) {
            throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        }
        return c.criar(placa, modelo, diaria);
    }
}
