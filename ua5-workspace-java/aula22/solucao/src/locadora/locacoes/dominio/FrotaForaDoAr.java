package locadora.locacoes.dominio;

/** Erro da Camada Anticorrupção (Aula 22): A Frota não respondeu (rede, timeout, serviço parado). */
public class FrotaForaDoAr extends LocacaoErro {
    public FrotaForaDoAr(String mensagem) {
        super(mensagem);
    }
}
