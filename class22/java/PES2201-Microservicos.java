/*
 * PES2201 - Arquitetura de Microserviços
 * Aula 22: Microsserviços e Arquiteturas Distribuídas
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula a comunicação entre dois microserviços independentes
 * (ServicoEstoque e ServicoVendas) através de suas interfaces públicas,
 * como se cada um rodasse em seu próprio processo/deploy. Cada serviço
 * só conhece o contrato do outro, nunca sua implementação interna.
 *
 * Compilar/executar:
 *   javac PES2201-Microservicos.java && java Microservicos
 */

import java.util.*;

// Simula um microserviço de estoque, com seu próprio dado isolado.
class ServicoEstoque {
    private final Map<String, Integer> estoque = new HashMap<>();

    ServicoEstoque() {
        estoque.put("ABC1234", 3);
        estoque.put("XYZ9876", 0);
    }

    boolean verificarDisponibilidade(String placa) {
        System.out.println("[Serviço Estoque] Verificando placa " + placa + "...");
        return estoque.getOrDefault(placa, 0) > 0;
    }

    void reservar(String placa) {
        int atual = estoque.getOrDefault(placa, 0);
        if (atual > 0) {
            estoque.put(placa, atual - 1);
            System.out.println("[Serviço Estoque] Reservado 1 unidade de " + placa);
        }
    }
}

// Simula um microserviço de vendas que depende do Serviço Estoque.
class ServicoVendas {
    private final ServicoEstoque estoque; // "chamada" a outro serviço via sua interface pública

    ServicoVendas(ServicoEstoque estoque) {
        this.estoque = estoque;
    }

    String criarPedido(String placa) {
        if (!estoque.verificarDisponibilidade(placa)) {
            System.out.println("[Serviço Vendas] Pedido recusado para " + placa + ": sem estoque");
            return "Indisponível";
        }
        estoque.reservar(placa);
        System.out.println("[Serviço Vendas] Pedido criado para " + placa);
        return "Criado";
    }
}

class Microservicos {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  MICROSSERVIÇOS — VENDAS x ESTOQUE");
        System.out.println("=".repeat(60));

        ServicoEstoque estoque = new ServicoEstoque();
        ServicoVendas vendas = new ServicoVendas(estoque);

        String resultado1 = vendas.criarPedido("ABC1234");
        String resultado2 = vendas.criarPedido("XYZ9876");

        System.out.println("\n  Pedido 1 (ABC1234): " + resultado1);
        System.out.println("  Pedido 2 (XYZ9876): " + resultado2);

        assert resultado1.equals("Criado");
        assert resultado2.equals("Indisponível");

        System.out.println("\n✓ Cada serviço tem seu próprio dado isolado (sem banco compartilhado).");
        System.out.println("✓ Vendas só conhece a interface pública de Estoque, nunca sua implementação.");
        System.out.println("✓ Em produção, essa chamada seria via rede (REST/gRPC), não uma referência Java direta.");
    }
}
