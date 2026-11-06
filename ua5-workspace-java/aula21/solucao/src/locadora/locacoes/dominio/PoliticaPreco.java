package locadora.locacoes.dominio;

/**
 * Políticas de preço — Strategy (Aula 18). Agora pertencem a LOCAÇÕES:
 * desconto é regra do contrato, não do veículo.
 */
public interface PoliticaPreco {
    double aplicar(double valor, int dias);

    static double arredondar(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
