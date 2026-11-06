package locadora.frota.dominio;

import locadora.compartilhado.EventoDominio;

/** Evento publicado pelo contexto de Frota. */
public record VeiculoCadastrado(String idAgregado, String tipoVeiculo, String modelo, double diaria)
        implements EventoDominio {
}
