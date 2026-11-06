package locadora;

/** Herda de IllegalStateException para continuar compatível com o código legado. */
public class VeiculoIndisponivel extends IllegalStateException {
    public VeiculoIndisponivel(String placa) {
        super("Veículo " + placa + " já está alugado");
    }
}
