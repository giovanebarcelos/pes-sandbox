/*
 * PES2501 - Geração de Documentação (Javadoc)
 * Aula 25: Documentação Técnica e Projeto Integrador Final
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra documentação inline com Javadoc e boas práticas
 * de documentação de código.
 *
 * Compilar/executar:
 *   javac PES2501-Documentacao.java && java Documentacao
 */

/**
 * Classe que demonstra boas práticas de documentação com Javadoc.
 *
 * <p>Uma boa documentação de código inclui:
 * <ul>
 *   <li>Descrição do propósito da classe/método</li>
 *   <li>Parâmetros com @param</li>
 *   <li>Retorno com @return</li>
 *   <li>Exceções com @throws</li>
 * </ul>
 *
 * @author Prof. Giovane Barcelos
 * @version 1.0
 */
class Documentacao {

    /**
     * Calcula o valor do frete com base no peso e distância.
     *
     * @param pesoKg      peso do pacote em quilogramas (deve ser positivo)
     * @param distanciaKm distância da entrega em quilômetros (deve ser positiva)
     * @param urgente     se true, adiciona taxa de urgência de 50%
     * @return valor do frete calculado
     * @throws IllegalArgumentException se peso ou distância forem negativos
     */
    public static double calcularFrete(double pesoKg, double distanciaKm, boolean urgente) {
        if (pesoKg < 0 || distanciaKm < 0) {
            throw new IllegalArgumentException("Peso e distância devem ser positivos.");
        }
        double base = 10.0 + (pesoKg * 2.5) + (distanciaKm * 0.50);
        return urgente ? base * 1.5 : base;
    }

    /**
     * Valida se uma string tem formato de email.
     *
     * @param email string a ser validada
     * @return true se o formato for válido, false caso contrário
     */
    public static boolean validarEmail(String email) {
        if (email == null) return false;
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  DOCUMENTAÇÃO COM JAVADOC");
        System.out.println("=".repeat(60));

        System.out.printf("%n  Frete normal (5kg, 100km): R$ %.2f%n", calcularFrete(5, 100, false));
        System.out.printf("  Frete urgente (5kg, 100km): R$ %.2f%n", calcularFrete(5, 100, true));

        System.out.printf("%n  Email 'teste@exemplo.com': %s%n",
                validarEmail("teste@exemplo.com") ? "válido" : "inválido");
        System.out.printf("  Email 'invalido': %s%n",
                validarEmail("invalido") ? "válido" : "inválido");

        System.out.println("\n✓ Javadoc: documentação integrada ao código.");
        System.out.println("✓ Gere HTML com: javadoc -d docs *.java");
    }
}
