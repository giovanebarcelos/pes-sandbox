package locadora.locacoes.dominio;

import locadora.compartilhado.EventoDominio;

/**
 * Evento = FATO no passado, imutável. Só tipos simples (texto, números) para poder
 * ser gravado em arquivo e lido de volta anos depois.
 */
public record LocacaoIniciada(String idAgregado, String placa, String cliente, String dataInicio,
                              int diasPrevistos, long valorPrevistoCentavos) implements EventoDominio {
}
