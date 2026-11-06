package locadora.dominio;

public class SemDesconto implements PoliticaPreco {
    @Override
    public double aplicar(double valor, int dias) {
        return PoliticaPreco.arredondar(valor);
    }
}
