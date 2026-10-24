package br.edu.reservalab.dominio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import br.edu.reservalab.dominio.Erros.RegraViolada;

/**
 * Algo reservável. Cada tipo declara suas regras no construtor; a validação é única.
 *
 * <p>Polimorfismo por dados + Template Method: as subclasses só informam os valores;
 * {@link #validar} nunca precisa de um {@code if (tipo == ...)}.
 */
public abstract class Recurso {
    private final String id;
    private final String nome;
    private final int capacidade;
    private final Duration duracaoMaxima;
    private final Duration antecedenciaMinima;
    private final boolean exigeAprovacao;
    private final Set<Perfil> perfisPermitidos;

    protected Recurso(String id, String nome, int capacidade, Duration duracaoMaxima,
                      Duration antecedenciaMinima, boolean exigeAprovacao, Set<Perfil> perfisPermitidos) {
        if (capacidade < 1) {
            throw new IllegalArgumentException("capacidade deve ser >= 1");
        }
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.duracaoMaxima = duracaoMaxima;
        this.antecedenciaMinima = antecedenciaMinima;
        this.exigeAprovacao = exigeAprovacao;
        this.perfisPermitidos = perfisPermitidos;
    }

    /** Nome do tipo, usado nas mensagens de erro. */
    public abstract String tipo();

    public String id() { return id; }
    public String nome() { return nome; }
    public int capacidade() { return capacidade; }
    public boolean exigeAprovacao() { return exigeAprovacao; }

    /**
     * Valida o pedido contra as regras do tipo.
     *
     * @throws RegraViolada perfil, intervalo, duração ou antecedência inválidos
     */
    public void validar(Usuario usuario, LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora) {
        if (!perfisPermitidos.contains(usuario.perfil())) {
            throw new RegraViolada(tipo() + ": perfil " + usuario.perfil() + " não pode reservar");
        }
        if (!fim.isAfter(inicio)) {
            throw new RegraViolada("o fim da reserva deve ser depois do início");
        }
        if (Duration.between(inicio, fim).compareTo(duracaoMaxima) > 0) {
            throw new RegraViolada(tipo() + ": duração máxima de " + horas(duracaoMaxima) + "h");
        }
        if (Duration.between(agora, inicio).compareTo(antecedenciaMinima) < 0) {
            throw new RegraViolada(tipo() + ": reserve com pelo menos " + horas(antecedenciaMinima) + "h de antecedência");
        }
    }

    private static String horas(Duration d) {
        double h = d.toMinutes() / 60.0;
        return h == Math.floor(h) ? String.valueOf((long) h) : String.valueOf(h);
    }
}
