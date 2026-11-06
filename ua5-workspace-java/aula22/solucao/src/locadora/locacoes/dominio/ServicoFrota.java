package locadora.locacoes.dominio;

/**
 * Porta — Camada Anticorrupção (ACL): Locações fala com a Frota só por estas
 * 3 operações, na SUA linguagem. Implementações: FrotaLocal (Aula 21) e FrotaHttp (Aula 22).
 */
public interface ServicoFrota {
    double precoBase(String placa, int dias);

    void reservar(String placa);

    void liberar(String placa);
}
