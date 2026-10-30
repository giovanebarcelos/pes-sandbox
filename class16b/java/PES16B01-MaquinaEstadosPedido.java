/*
 * PES16B01 - Máquina de Estados do Pedido
 * Aula 16B: Diagrama de Máquina de Estados (UML)
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa o diagrama PES16B-EstadosPedido com uma TABELA DE TRANSIÇÕES:
 * cada seta do diagrama vira uma entrada (origem, evento) -> (guarda, ação, destino).
 * Evento que não está na tabela = transição que o diagrama não desenhou = erro.
 *
 * Compilar/executar:
 *   javac PES16B01-MaquinaEstadosPedido.java && java MaquinaEstadosPedido
 */

import java.util.*;
import java.util.function.BiPredicate;

enum Estado { NOVO, PAGO, PREPARANDO, ENVIADO, ENTREGUE, CANCELADO }

class TransicaoInvalida extends RuntimeException {
    TransicaoInvalida(String msg) { super(msg); }
}

class Transicao {
    final BiPredicate<Pedido, Double> guarda;   // null = sem guarda
    final String acao;                          // null = sem ação
    final Estado destino;

    Transicao(BiPredicate<Pedido, Double> guarda, String acao, Estado destino) {
        this.guarda = guarda;
        this.acao = acao;
        this.destino = destino;
    }
}

class Pedido {
    // (origem, evento) -> transição — uma entrada por seta do diagrama
    private static final Map<Estado, Map<String, Transicao>> TRANSICOES = new EnumMap<>(Estado.class);
    // Ação de entrada (entry /) executada sempre que o objeto ENTRA no estado
    private static final Map<Estado, String> ENTRADA = new EnumMap<>(Estado.class);

    static {
        seta(Estado.NOVO, "confirmarPagamento",
             new Transicao((p, valor) -> valor >= p.getValor(), "reservarEstoque", Estado.PAGO));
        seta(Estado.NOVO, "cancelar", new Transicao(null, null, Estado.CANCELADO));
        seta(Estado.NOVO, "after(48h)", new Transicao(null, null, Estado.CANCELADO));
        seta(Estado.PAGO, "iniciarSeparacao", new Transicao(null, null, Estado.PREPARANDO));
        seta(Estado.PAGO, "cancelar", new Transicao(null, "estornarPagamento", Estado.CANCELADO));
        seta(Estado.PREPARANDO, "despachar", new Transicao(null, "gerarRastreio", Estado.ENVIADO));
        seta(Estado.ENVIADO, "confirmarEntrega", new Transicao(null, null, Estado.ENTREGUE));

        ENTRADA.put(Estado.PAGO, "enviarEmailConfirmacao");
        ENTRADA.put(Estado.ENVIADO, "avisarClienteRastreio");
        ENTRADA.put(Estado.CANCELADO, "liberarEstoque");
    }

    private static void seta(Estado origem, String evento, Transicao t) {
        TRANSICOES.computeIfAbsent(origem, e -> new LinkedHashMap<>()).put(evento, t);
    }

    private final int numero;
    private final double valor;
    private Estado estado = Estado.NOVO;        // [*] --> Novo : criarPedido
    private final List<Estado> historico = new ArrayList<>(List.of(Estado.NOVO));

    Pedido(int numero, double valor) {
        this.numero = numero;
        this.valor = valor;
    }

    double getValor() { return valor; }
    Estado getEstado() { return estado; }
    List<Estado> getHistorico() { return historico; }

    boolean disparar(String evento) { return disparar(evento, 0.0); }

    boolean disparar(String evento, double dado) {
        Transicao t = TRANSICOES.getOrDefault(estado, Map.of()).get(evento);
        if (t == null) {
            throw new TransicaoInvalida(String.format(
                "Pedido %d: '%s' não é permitido em %s", numero, evento, estado));
        }
        if (t.guarda != null && !t.guarda.test(this, dado)) {
            // Guarda falsa: a transição NÃO dispara e o objeto continua onde está
            System.out.printf("  [%s] %s: guarda falsa, evento ignorado%n", estado, evento);
            return false;
        }
        Estado origem = estado;
        if (t.acao != null) System.out.printf("  / %s%n", t.acao);
        estado = t.destino;
        historico.add(estado);
        System.out.printf("  %s --%s--> %s%n", origem, evento, estado);
        if (ENTRADA.containsKey(estado)) System.out.printf("  entry / %s%n", ENTRADA.get(estado));
        return true;
    }

    Set<String> eventosPossiveis() {
        return TRANSICOES.getOrDefault(estado, Map.of()).keySet();
    }

    boolean encerrado() {
        return estado == Estado.ENTREGUE || estado == Estado.CANCELADO;
    }
}

class MaquinaEstadosPedido {
    public static void main(String[] args) {
        System.out.println("=== Pedido 1: caminho feliz (com um pagamento a menor) ===");
        Pedido p1 = new Pedido(1, 150.00);
        p1.disparar("confirmarPagamento", 100.00);   // guarda [valor confere] falsa
        p1.disparar("confirmarPagamento", 150.00);
        p1.disparar("iniciarSeparacao");
        p1.disparar("despachar");
        p1.disparar("confirmarEntrega");
        System.out.println("  histórico: " + p1.getHistorico());

        System.out.println("\n=== Pedido 2: cancelado depois de pago ===");
        Pedido p2 = new Pedido(2, 80.00);
        p2.disparar("confirmarPagamento", 80.00);
        System.out.println("  eventos possíveis em PAGO: " + p2.eventosPossiveis());
        p2.disparar("cancelar");

        System.out.println("\n=== Pedido 3: expirou sem pagamento (evento de tempo) ===");
        Pedido p3 = new Pedido(3, 45.00);
        p3.disparar("after(48h)");

        System.out.println("\n=== Transições que o diagrama NÃO desenhou ===");
        Object[][] casos = { { new Pedido(4, 10.00), "despachar" },   // NOVO -> ENVIADO
                             { p1, "cancelar" } };                   // ENTREGUE é final
        for (Object[] caso : casos) {
            try {
                ((Pedido) caso[0]).disparar((String) caso[1]);
            } catch (TransicaoInvalida erro) {
                System.out.println("  ✗ " + erro.getMessage());
            }
        }
    }
}
