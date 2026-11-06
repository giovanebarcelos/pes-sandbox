package locadora.apresentacao;

import java.util.List;
import java.util.Scanner;

/**
 * View real do MVP (terminal). Repare: nenhum if de regra aqui dentro.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.apresentacao.ConsoleViewMvp
 */
public class ConsoleViewMvp implements LocadoraView {
    private final Scanner entrada = new Scanner(System.in);

    private String perguntar(String rotulo) {
        System.out.print(rotulo);
        return entrada.hasNextLine() ? entrada.nextLine() : "";
    }

    @Override
    public String obterPlaca() {
        return perguntar("Placa: ");
    }

    @Override
    public String obterDias() {
        return perguntar("Dias: ");
    }

    @Override
    public String obterCliente() {
        return perguntar("Cliente: ");
    }

    @Override
    public String obterEmail() {
        return perguntar("E-mail: ");
    }

    @Override
    public void mostrarFrota(List<String> linhas) {
        System.out.println(String.join(System.lineSeparator(), linhas));
    }

    @Override
    public void mostrarSucesso(String mensagem) {
        System.out.println("OK: " + mensagem);
    }

    @Override
    public void mostrarErro(String mensagem) {
        System.out.println("ERRO: " + mensagem);
    }

    public static void main(String[] args) {
        ConsoleViewMvp view = new ConsoleViewMvp();
        LocadoraPresenter presenter = new LocadoraPresenter(view, Montagem.servicoExemplo(false));
        presenter.carregarFrota();
        while (view.perguntar("\nAlugar um veículo? (s/n) ").trim().equalsIgnoreCase("s")) {
            presenter.aoClicarAlugar();
        }
    }
}
