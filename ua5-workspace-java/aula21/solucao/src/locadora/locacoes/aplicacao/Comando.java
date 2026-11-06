package locadora.locacoes.aplicacao;

/**
 * LADO DE ESCRITA (Command) do CQRS.
 * Comando = intenção ("IniciarLocacao"), pode ser recusado.
 * Evento  = fato consumado ("LocacaoIniciada"), não se discute.
 */
public interface Comando {
}
