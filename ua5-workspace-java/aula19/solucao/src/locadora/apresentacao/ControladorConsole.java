package locadora.apresentacao;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import locadora.ServicoLocacao;

/**
 * MVC no terminal.
 *
 *   Model      -> ServicoLocacao (Aula 18) — regras e estado
 *   View       -> VisaoConsole — só transforma dados em texto (não decide nada)
 *   Controller -> ControladorConsole — interpreta o comando do usuário,
 *                 chama o Model e escolhe qual View mostrar
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.apresentacao.ControladorConsole
 * Comandos: listar | cotar PLACA DIAS | alugar PLACA DIAS NOME EMAIL | devolver PLACA | sair
 */
public class ControladorConsole {
    private final ServicoLocacao modelo;
    private final VisaoConsole visao;

    public ControladorConsole(ServicoLocacao modelo, VisaoConsole visao) {
        this.modelo = modelo;
        this.visao = visao;
    }

    /** Recebe uma linha digitada e devolve o texto a exibir. */
    public String executar(String linha) {
        String[] partes = linha.trim().split("\\s+");
        if (partes[0].isEmpty()) {
            return visao.ajuda();
        }
        String comando = partes[0].toLowerCase();
        String[] args = Arrays.copyOfRange(partes, 1, partes.length);
        try {
            if (comando.equals("listar")) {
                return visao.frota(modelo.listar());
            }
            if (comando.equals("cotar") && args.length == 2) {
                int dias = Integer.parseInt(args[1]);
                return visao.cotacao(args[0], dias, modelo.cotar(args[0], dias));
            }
            if (comando.equals("alugar") && args.length == 4) {
                return visao.locacao(modelo.alugar(args[0], args[2], args[3], Integer.parseInt(args[1])));
            }
            if (comando.equals("devolver") && args.length == 1) {
                modelo.devolver(args[0]);
                return visao.mensagem("OK: " + args[0] + " devolvido");
            }
        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException e) {
            return visao.erro(e.getMessage());
        }
        return visao.ajuda();
    }

    public static void main(String[] args) {
        ControladorConsole controlador = new ControladorConsole(Montagem.servicoExemplo(false), new VisaoConsole());
        System.out.println(controlador.visao.ajuda());
        Scanner entrada = new Scanner(System.in);
        while (true) {
            System.out.print("mvc> ");
            if (!entrada.hasNextLine()) {
                break;
            }
            String linha = entrada.nextLine();
            if (linha.trim().equalsIgnoreCase("sair")) {
                break;
            }
            System.out.println(controlador.executar(linha));
        }
    }
}
