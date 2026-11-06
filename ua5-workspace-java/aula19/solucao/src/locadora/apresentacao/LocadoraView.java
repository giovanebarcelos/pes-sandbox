package locadora.apresentacao;

import java.util.List;

/**
 * MVP — o contrato da View PASSIVA: ela só lê campos e mostra coisas.
 * Como é uma interface, o Presenter é testado com uma ViewFalsa (test/.../MvpTest.java).
 */
public interface LocadoraView {
    String obterPlaca();

    String obterDias();

    String obterCliente();

    String obterEmail();

    void mostrarFrota(List<String> linhas);

    void mostrarSucesso(String mensagem);

    void mostrarErro(String mensagem);
}
