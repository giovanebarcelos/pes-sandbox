package locadora;

/** Regra do legado: 7 dias ou mais ganham 10% de desconto. */
public class DescontoSemanal implements PoliticaPreco {
    private final int diasMinimos;
    private final double percentual;

    public DescontoSemanal() {
        this(7, 0.10);
    }

    public DescontoSemanal(int diasMinimos, double percentual) {
        this.diasMinimos = diasMinimos;
        this.percentual = percentual;
    }

    @Override
    public double aplicar(double valor, int dias) {
        if (dias >= diasMinimos) {
            valor = valor * (1 - percentual);
        }
        return PoliticaPreco.arredondar(valor);
    }
}
