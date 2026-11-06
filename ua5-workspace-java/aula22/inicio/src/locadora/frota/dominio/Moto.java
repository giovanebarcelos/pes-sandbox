package locadora.frota.dominio;

public class Moto extends Veiculo {
    public Moto(String placa, String modelo, double valorDiaria) {
        super(placa, modelo, valorDiaria);
    }

    @Override
    public String tipo() {
        return "moto";
    }

    @Override
    public double precoBase(int dias) {
        return valorDiaria() * dias;
    }
}
