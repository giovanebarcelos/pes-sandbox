package locadora.compartilhado;

import java.util.regex.Pattern;

/**
 * Objeto de VALOR: não tem identidade, é comparado pelos atributos e é imutável
 * (um record do Java já é tudo isso). Duas Placa("ABC-1234") são iguais.
 * Aceita o padrão antigo (ABC-1234) e o Mercosul (BRA2E19).
 */
public record Placa(String valor) {
    private static final Pattern FORMATO = Pattern.compile("^[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}$");

    public Placa {
        if (valor == null) {
            throw new IllegalArgumentException("Placa vazia");
        }
        String normalizada = valor.strip().toUpperCase();
        if (!FORMATO.matcher(normalizada).matches()) {
            throw new IllegalArgumentException("Placa inválida: '" + valor + "'");
        }
        valor = normalizada;
    }

    @Override
    public String toString() {
        return valor;
    }
}
