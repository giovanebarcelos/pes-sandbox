package locadora.apresentacao;

import java.util.Scanner;

/**
 * View do MVVM no terminal: só faz o "binding" (inscrição) e repassa a digitação.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.apresentacao.MvvmConsole
 */
public class MvvmConsole {
    public static void main(String[] args) {
        LocadoraViewModel vm = new LocadoraViewModel(Montagem.servicoExemplo(true));
        // "binding" da View: cada propriedade observada imprime quando muda
        vm.frota.inscrever(linhas -> System.out.println("Frota:\n  " + String.join("\n  ", linhas)));
        vm.cotacao.inscrever(c -> System.out.println("  (cotação: " + c + ")"));
        vm.mensagem.inscrever(m -> {
            if (!m.isEmpty()) {
                System.out.println(m);
            }
        });
        Scanner in = new Scanner(System.in);
        System.out.print("Placa: ");
        vm.placa.set(in.nextLine().trim().toUpperCase());
        System.out.print("Dias: ");
        String dias = in.nextLine().trim();
        vm.dias.set(dias.matches("\\d+") ? Integer.parseInt(dias) : 1);
        System.out.print("Cliente: ");
        vm.cliente.set(in.nextLine().trim());
        System.out.print("E-mail: ");
        vm.email.set(in.nextLine().trim());
        vm.comandoAlugar();
    }
}
