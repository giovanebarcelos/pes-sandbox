package locadora.locacoes.dominio;

import locadora.compartilhado.EventoDominio;

public record MultaPorAtrasoAplicada(String idAgregado, int diasAtraso, long valorCentavos) implements EventoDominio {
}
