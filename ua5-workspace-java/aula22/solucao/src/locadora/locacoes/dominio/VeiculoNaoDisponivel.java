package locadora.locacoes.dominio;

/** Erro da Camada Anticorrupção (Aula 22): O veículo já está reservado por outra locação. */
public class VeiculoNaoDisponivel extends LocacaoErro {
    public VeiculoNaoDisponivel(String mensagem) {
        super(mensagem);
    }
}
