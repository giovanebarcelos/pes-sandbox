package locadora.frota.dominio;

import java.util.NoSuchElementException;

/**
 * Erro de negócio com NOME (o legado lançava uma NoSuchElementException genérica).
 * Herda de NoSuchElementException para continuar compatível com o código legado:
 * os testes de caracterização esperam esse tipo.
 */
public class VeiculoNaoEncontrado extends NoSuchElementException {
    public VeiculoNaoEncontrado(String placa) {
        super("Veículo " + placa + " não encontrado");
    }
}
