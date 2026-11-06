package locadora.locacoes.dominio;

import locadora.compartilhado.EventoDominio;

public record VeiculoDevolvido(String idAgregado, String placa, String dataDevolucao, int diasUtilizados)
        implements EventoDominio {
}
