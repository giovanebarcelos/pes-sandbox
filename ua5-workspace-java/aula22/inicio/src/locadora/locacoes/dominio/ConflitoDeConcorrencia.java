package locadora.locacoes.dominio;

/** Duas operações tentaram gravar eventos na mesma versão do agregado. */public class ConflitoDeConcorrencia extends LocacaoErro {
    public ConflitoDeConcorrencia(String mensagem) {
        super(mensagem);
    }
}
