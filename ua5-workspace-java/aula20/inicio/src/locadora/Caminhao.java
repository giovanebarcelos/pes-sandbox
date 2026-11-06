package locadora;

/** Extensão feita no Passo 4 (OCP): +10% de seguro e taxa fixa de R$ 50,00. */
public class Caminhao extends Veiculo {
    public Caminhao(String placa, String modelo, double valorDiaria) {
        super(placa, modelo, valorDiaria);
    }

    @Override
    public String tipo() {
        return "caminhao";
    }

    @Override
    public double precoBase(int dias) {
        return valorDiaria() * dias * 1.10 + 50.0;
    }
}
