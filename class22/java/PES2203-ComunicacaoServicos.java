/*
 * PES2203 - Comunicação Síncrona x Assíncrona entre Serviços
 * Aula 22: Microsserviços e Arquiteturas Distribuídas
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Contrasta os dois estilos de comunicação entre microsserviços:
 * - Síncrona (request/response): quem chama espera a resposta antes de continuar.
 * - Assíncrona (mensageria/eventos): quem chama publica um evento e segue
 *   em frente, sem esperar quem vai consumi-lo nem quando.
 *
 * Compilar/executar:
 *   javac PES2203-ComunicacaoServicos.java && java ComunicacaoServicos
 */

import java.util.*;

// --- COMUNICAÇÃO SÍNCRONA: Pedidos chama Estoque e espera a resposta ---
class ServicoEstoqueSincrono {
    int consultarEstoqueSincrono(String placa) {
        System.out.println("[Estoque] Consultando quantidade de " + placa + "...");
        return 3; // resposta imediata, quem chamou fica bloqueado até aqui
    }
}

class ServicoPedidosSincrono {
    private final ServicoEstoqueSincrono estoque;

    ServicoPedidosSincrono(ServicoEstoqueSincrono estoque) {
        this.estoque = estoque;
    }

    String criarPedido(String placa) {
        int quantidade = estoque.consultarEstoqueSincrono(placa); // bloqueia
        System.out.println("[Pedidos] Resposta recebida: " + quantidade + " unidades");
        return quantidade > 0 ? "Criado" : "Recusado";
    }
}

// --- COMUNICAÇÃO ASSÍNCRONA: Pedidos publica um evento e não espera ---
interface OuvinteEvento {
    void aoReceberEvento(String evento, Map<String, String> dados);
}

// Simula um message broker (ex.: RabbitMQ/Kafka) com uma fila em memória.
class BarramentoEventos {
    private final Queue<Map.Entry<String, Map<String, String>>> fila = new LinkedList<>();
    private final List<OuvinteEvento> assinantes = new ArrayList<>();

    void assinar(OuvinteEvento ouvinte) {
        assinantes.add(ouvinte);
    }

    void publicar(String evento, Map<String, String> dados) {
        System.out.println("[Barramento] Evento publicado: " + evento + " " + dados);
        fila.add(new AbstractMap.SimpleEntry<>(evento, dados));
    }

    void processarFila() {
        while (!fila.isEmpty()) {
            Map.Entry<String, Map<String, String>> item = fila.poll();
            for (OuvinteEvento ouvinte : assinantes) {
                ouvinte.aoReceberEvento(item.getKey(), item.getValue());
            }
        }
    }
}

class ServicoPedidosAssincrono {
    private final BarramentoEventos barramento;

    ServicoPedidosAssincrono(BarramentoEventos barramento) {
        this.barramento = barramento;
    }

    void notificarPedidoCriado(String placa) {
        System.out.println("[Pedidos] Publicando evento e seguindo em frente (não bloqueia)...");
        Map<String, String> dados = new HashMap<>();
        dados.put("placa", placa);
        barramento.publicar("PedidoCriado", dados);
    }
}

class ServicoEmailAssincrono implements OuvinteEvento {
    @Override
    public void aoReceberEvento(String evento, Map<String, String> dados) {
        if (evento.equals("PedidoCriado")) {
            System.out.println("[E-mail] Recebeu o evento com atraso e envia confirmação para " + dados.get("placa"));
        }
    }
}

class ComunicacaoServicos {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  COMUNICAÇÃO SÍNCRONA x ASSÍNCRONA");
        System.out.println("=".repeat(60));

        System.out.println("\n--- Síncrona (request/response) ---");
        ServicoPedidosSincrono pedidosSinc = new ServicoPedidosSincrono(new ServicoEstoqueSincrono());
        String resultado = pedidosSinc.criarPedido("ABC1234");
        System.out.println("  Resultado: " + resultado);
        assert resultado.equals("Criado");

        System.out.println("\n--- Assíncrona (mensageria/eventos) ---");
        BarramentoEventos barramento = new BarramentoEventos();
        barramento.assinar(new ServicoEmailAssincrono());

        ServicoPedidosAssincrono pedidosAssinc = new ServicoPedidosAssincrono(barramento);
        pedidosAssinc.notificarPedidoCriado("XYZ9876");
        System.out.println("  [Pedidos] Já retornou para o cliente, sem esperar o e-mail ser enviado.");

        barramento.processarFila(); // o consumo do evento acontece depois, de forma desacoplada

        System.out.println("\n✓ Síncrona: quem chama fica bloqueado esperando a resposta.");
        System.out.println("✓ Assíncrona: quem chama publica e segue, o consumidor processa depois.");
    }
}
