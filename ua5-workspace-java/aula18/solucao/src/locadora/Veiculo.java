package locadora;

/**
 * Hierarquia de veículos.
 *
 * SRP: cada classe só sabe o próprio preço-base e o próprio estado.
 * OCP: um novo tipo (ex.: Caminhao) é uma NOVA classe — nenhum if/else é alterado.
 * LSP: toda subclasse cumpre o contrato de Veiculo (preço >= 0 para dias >= 1,
 *      sem lançar exceção nova) — ver test/locadora/VeiculosTest.java.
 */
public abstract class Veiculo {
    private final String placa;
    private final String modelo;
    private final double valorDiaria;
    private boolean disponivel = true;

    protected Veiculo(String placa, String modelo, double valorDiaria) {
        if (valorDiaria <= 0) {
            throw new IllegalArgumentException("A diária deve ser positiva");
        }
        this.placa = placa;
        this.modelo = modelo;
        this.valorDiaria = valorDiaria;
    }

    /** Nome do tipo usado em arquivos e formulários ("carro", "moto"...). */
    public abstract String tipo();

    /** Preço sem descontos — cada tipo tem a sua regra (polimorfismo, Aula 17). */
    public abstract double precoBase(int dias);

    public String placa() {
        return placa;
    }

    public String modelo() {
        return modelo;
    }

    public double valorDiaria() {
        return valorDiaria;
    }

    public boolean disponivel() {
        return disponivel;
    }

    public void marcarAlugado() {
        disponivel = false;
    }

    public void marcarDisponivel() {
        disponivel = true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + placa + "] " + modelo;
    }
}
