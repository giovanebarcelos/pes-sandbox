package locadora.locacoes.dominio;

/** Base dos erros do contexto de Locações. */
public class LocacaoErro extends RuntimeException {
    public LocacaoErro(String mensagem) {
        super(mensagem);
    }
}
