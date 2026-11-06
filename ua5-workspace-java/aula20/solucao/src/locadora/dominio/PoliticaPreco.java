package locadora.dominio;

/**
 * Políticas de preço — padrão Strategy (GoF).
 *
 * O desconto deixou de ser um "if (dias >= 7)" escondido em calcular():
 * agora é uma estratégia trocável, injetada no serviço (DIP).
 */
public interface PoliticaPreco {
    /** Recebe o preço-base e devolve o preço final (arredondado em centavos). */
    double aplicar(double valor, int dias);

    static double arredondar(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
