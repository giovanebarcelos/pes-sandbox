package locadora.pagamentos;

/** Contrato que todo plugin de pagamento cumpre. Precisa de construtor público sem argumentos. */
public interface PluginPagamento {
    /** Nome usado para escolher a forma: "pix", "cartao"... */
    String nome();

    /** Taxa cobrada pela forma de pagamento, em reais. */
    double taxa(double valor);

    /** Processa o pagamento e devolve um texto de comprovante. */
    String pagar(double valor, String referencia);
}
