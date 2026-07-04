/*
 * PES2001 - DDD: Entidade, Objeto de Valor e Agregado
 * Aula 20: DDD, Microserviços, Componentes e Interfaces
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra os blocos táticos do DDD: Entity, Value Object, Aggregate.
 * Domínio: Pedido (agregado) com itens (entidades) e preço (value object).
 *
 * Compilar/executar:
 *   javac PES2001-DDD-Dominio.java && java DDDDominio
 */

import java.util.*;

// ===== Value Object (imutável, identificado por seus atributos) =====
class Dinheiro {
    final double valor;

    Dinheiro(double valor) { this.valor = Math.round(valor * 100.0) / 100.0; }

    Dinheiro somar(Dinheiro outro) { return new Dinheiro(valor + outro.valor); }
    Dinheiro multiplicar(int qtd) { return new Dinheiro(valor * qtd); }

    @Override
    public String toString() { return String.format("R$ %.2f", valor); }

    @Override
    public boolean equals(Object o) {
        return o instanceof Dinheiro && ((Dinheiro) o).valor == valor;
    }

    @Override
    public int hashCode() { return Double.hashCode(valor); }
}

// ===== Entity =====
class ItemPedido {
    final String produtoId;
    String nome;
    Dinheiro precoUnitario;
    int quantidade;

    ItemPedido(String produtoId, String nome, Dinheiro precoUnitario, int quantidade) {
        this.produtoId = produtoId; this.nome = nome;
        this.precoUnitario = precoUnitario; this.quantidade = quantidade;
    }

    Dinheiro subtotal() { return precoUnitario.multiplicar(quantidade); }

    @Override
    public String toString() {
        return String.format("Item[%s] %s x%d = %s", produtoId, nome, quantidade, subtotal());
    }
}

// ===== Aggregate Root =====
class Pedido {
    final int pedidoId;
    String cliente;
    private List<ItemPedido> itens = new ArrayList<>();
    private String status = "ABERTO";

    Pedido(int pedidoId, String cliente) {
        this.pedidoId = pedidoId; this.cliente = cliente;
    }

    void adicionarItem(String produtoId, String nome, double preco, int quantidade) {
        if (!status.equals("ABERTO"))
            throw new IllegalStateException("Pedido não está aberto.");
        itens.add(new ItemPedido(produtoId, nome, new Dinheiro(preco), quantidade));
    }

    Dinheiro total() {
        Dinheiro t = new Dinheiro(0);
        for (ItemPedido i : itens) t = t.somar(i.subtotal());
        return t;
    }

    void fechar() {
        if (itens.isEmpty()) throw new IllegalStateException("Pedido sem itens.");
        status = "FECHADO";
    }

    String getStatus() { return status; }
    List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }

    @Override
    public String toString() {
        return String.format("Pedido[%d] %s — %s — %s", pedidoId, cliente, status, total());
    }
}

class DDDDominio {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  DDD — ENTIDADE, VALUE OBJECT E AGREGADO");
        System.out.println("=".repeat(60));

        Pedido pedido = new Pedido(1001, "Ana Silva");
        pedido.adicionarItem("P001", "Notebook", 4500.00, 1);
        pedido.adicionarItem("P002", "Mouse", 120.00, 2);
        pedido.adicionarItem("P003", "Teclado", 250.00, 1);

        System.out.printf("%n  %s%n", pedido);
        System.out.println("  Itens:");
        for (ItemPedido item : pedido.getItens())
            System.out.printf("    %s%n", item);
        System.out.printf("  Total: %s%n", pedido.total());

        pedido.fechar();
        System.out.printf("%n  Status após fechar: %s%n", pedido.getStatus());

        System.out.println("\n✓ Entity: tem identidade (produtoId, pedidoId).");
        System.out.println("✓ Value Object: imutável, comparado por valor (Dinheiro).");
        System.out.println("✓ Aggregate: Pedido garante consistência dos itens.");
    }
}
