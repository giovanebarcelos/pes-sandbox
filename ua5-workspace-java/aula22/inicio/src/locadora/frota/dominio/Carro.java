package locadora.frota.dominio;

public class Carro extends Veiculo {
    public Carro(String placa, String modelo, double valorDiaria) {
        super(placa, modelo, valorDiaria);
    }

    @Override
    public String tipo() {
        return "carro";
    }

    @Override
    public double precoBase(int dias) {
        return valorDiaria() * dias * 1.10; // +10% de seguro
    }
}
