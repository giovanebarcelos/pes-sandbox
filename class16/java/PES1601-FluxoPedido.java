/*
 * PES1601 - Implementação do Fluxo Modelado (Pedido)
 * Aula 16: Diagramas de Sequência e de Atividades
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa o fluxo de pedido modelado nos diagramas de sequência
 * e atividades, demonstrando a tradução de UML para código.
 *
 * Compilar/executar:
 *   javac PES1601-FluxoPedido.java && java FluxoPedido
 */

import java.util.*;

class EstoqueService {
    private Map<String, Integer> estoque = new HashMap<>();

    EstoqueService() {
        estoque.put("Produto A", 10);
        estoque.put("Produto B", 5);
        estoque.put("Produto C", 0);
    }

    boolean verificarDisponibilidade(List<String> itens, List<String> indisponiveis) {
        boolean tudoDisponivel = true;
        for (String item : itens) {
            if (estoque.getOrDefault(item, 0) <= 0) {
                indisponiveis.add(item);
                tudoDisponivel = false;
            }
        }
        return tudoDisponivel;
    }

    void reservar(List<String> itens) {
        for (String item : itens) estoque.merge(item, -1, Integer::sum);
    }

    void liberar(List<String> itens) {
        for (String item : itens) estoque.merge(item, 1, Integer::sum);
    }
}

class PagamentoService {
    static boolean processar(double valor) {
        return valor <= 10000;
    }
}

class PedidoService {
    private EstoqueService estoque = new EstoqueService();

    boolean criarPedido(String cliente, List<String> itens, double valor) {
        System.out.printf("%n%s%n", "=".repeat(50));
        System.out.printf("  PEDIDO — %s%n", cliente);
        System.out.printf("%s%n", "=".repeat(50));

        List<String> indisponiveis = new ArrayList<>();
        if (!estoque.verificarDisponibilidade(itens, indisponiveis)) {
            System.out.printf("  ✗ Itens indisponíveis: %s%n", String.join(", ", indisponiveis));
            return false;
        }

        estoque.reservar(itens);
        System.out.printf("  ✓ Itens reservados: %s%n", String.join(", ", itens));

        if (!PagamentoService.processar(valor)) {
            estoque.liberar(itens);
            System.out.printf("  ✗ Pagamento recusado (valor: R$ %.2f)%n", valor);
            return false;
        }
        System.out.printf("  ✓ Pagamento aprovado: R$ %.2f%n", valor);

        System.out.printf("  ✓ Pedido confirmado!%n");
        System.out.printf("  ✓ Email de confirmação enviado para %s%n", cliente);
        return true;
    }
}

class FluxoPedido {
    public static void main(String[] args) {
        PedidoService ps = new PedidoService();

        ps.criarPedido("ana@email.com", Arrays.asList("Produto A", "Produto B"), 150.00);
        ps.criarPedido("carlos@email.com", Arrays.asList("Produto C"), 50.00);
        ps.criarPedido("maria@email.com", Arrays.asList("Produto A"), 15000.00);

        System.out.printf("%n✓ O código reflete fielmente o fluxo dos diagramas UML.%n");
    }
}
