package locadora.compartilhado;

/**
 * Evento de domínio: um FATO que já aconteceu, com nome no passado
 * (LocacaoIniciada, VeiculoDevolvido). É imutável — cada evento é um record.
 */
public interface EventoDominio {
    /** A qual agregado (ex.: qual locação) o fato pertence. */
    String idAgregado();

    default String tipo() {
        return getClass().getSimpleName();
    }
}
