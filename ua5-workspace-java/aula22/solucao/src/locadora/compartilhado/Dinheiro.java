package locadora.compartilhado;

import java.util.Locale;

/**
 * Objeto de VALOR para dinheiro, guardado em CENTAVOS (long) para não sofrer
 * com erro de ponto flutuante (0.1 + 0.2 != 0.3 em double).
 */
public record Dinheiro(long centavos) {
    public static Dinheiro deReais(double reais) {
        return new Dinheiro(Math.round(reais * 100));
    }

    public Dinheiro mais(Dinheiro outro) {
        return new Dinheiro(centavos + outro.centavos);
    }

    public Dinheiro vezes(double fator) {
        return new Dinheiro(Math.round(centavos * fator));
    }

    public double reais() {
        return centavos / 100.0;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "R$ %.2f", reais());
    }
}
