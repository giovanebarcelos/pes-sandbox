package locadora.locacoes.dominio;

/** Erro da Camada Anticorrupção (Aula 22): A Frota não conhece esta placa (visto com as palavras de Locações). */
public class VeiculoDesconhecido extends LocacaoErro {
    public VeiculoDesconhecido(String mensagem) {
        super(mensagem);
    }
}
