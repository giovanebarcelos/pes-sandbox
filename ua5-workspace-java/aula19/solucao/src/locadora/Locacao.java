package locadora;

/** Uma locação feita (record = imutável, com equals/hashCode/toString prontos). */
public record Locacao(String placa, String cliente, int dias, double valor) {
}
