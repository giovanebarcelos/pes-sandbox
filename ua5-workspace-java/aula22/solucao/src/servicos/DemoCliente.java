package servicos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Cliente de demonstração: conversa com os 3 serviços pela rede.
 *
 * Executar (com os serviços no ar):  java ../../ferramentas/Rodar.java servicos.DemoCliente
 * Variáveis: FROTA_URL, LOCACOES_URL, NOTIFICACAO_URL (padrão: localhost 8001/8002/8003)
 */
public final class DemoCliente {
    private DemoCliente() {
    }

    /** Renderizador SDUI mínimo (mesma ideia do repository/class22/java/PES2204). */
    @SuppressWarnings("unchecked")
    static String renderizarSdui(Map<String, Object> no, int nivel) {
        String pad = "  ".repeat(nivel);
        Map<String, Object> props = (Map<String, Object>) no.getOrDefault("props", Map.of());
        switch ((String) no.get("type")) {
            case "column":
                StringBuilder sb = new StringBuilder();
                for (Object filho : (List<Object>) no.getOrDefault("children", List.of())) {
                    sb.append(renderizarSdui((Map<String, Object>) filho, nivel)).append('\n');
                }
                return sb.toString().stripTrailing();
            case "text":
                return pad + props.get("value");
            case "button":
                return pad + "[ " + props.get("label") + " ]";
            default:
                return pad + "(componente desconhecido: " + no.get("type") + ")";
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Map<String, String> env = System.getenv();
        String frota = env.getOrDefault("FROTA_URL", "http://localhost:8001");
        String locacoes = env.getOrDefault("LOCACOES_URL", "http://localhost:8002");
        String notificacao = env.getOrDefault("NOTIFICACAO_URL", "http://localhost:8003");
        String id = "L-" + UUID.randomUUID().toString().substring(0, 4);

        System.out.println("1) Tela montada pelo SERVIDOR (SDUI):");
        System.out.println(renderizarSdui((Map<String, Object>) HttpJson.chamar("GET", frota + "/v1/telas/frota", null).corpo(), 0));
        System.out.println("\n2) Alugar ABC-1234 por 5 dias (Locações -> Frota, SÍNCRONO):");
        System.out.println("   " + HttpJson.chamar("POST", locacoes + "/v1/locacoes", Map.of("id", id, "placa", "ABC-1234",
                "cliente", "Ana", "dias", 5, "data_inicio", "2026-11-02")));
        System.out.println("\n3) Tentar alugar o mesmo veículo de novo (erro 409 vindo da Frota):");
        System.out.println("   " + HttpJson.chamar("POST", locacoes + "/v1/locacoes",
                Map.of("placa", "ABC-1234", "cliente", "Bia", "dias", 1)));
        System.out.println("\n4) Devolver com 2 dias de atraso:");
        System.out.println("   " + HttpJson.chamar("POST", locacoes + "/v1/locacoes/" + id + "/devolucao",
                Map.of("data", "2026-11-09")));
        Thread.sleep(1500); // dá tempo ao outbox (ASSÍNCRONO)
        System.out.println("\n5) Notificações recebidas pelo serviço de Notificação:");
        Map<String, Object> n = (Map<String, Object>) HttpJson.chamar("GET", notificacao + "/v1/notificacoes", null).corpo();
        for (Object item : (List<Object>) n.get("notificacoes")) {
            System.out.println("   - " + ((Map<String, Object>) item).get("mensagem"));
        }
        System.out.println("\n6) Frota depois da devolução (liberada pelo outbox):");
        Map<String, Object> f = (Map<String, Object>) HttpJson.chamar("GET", frota + "/v1/veiculos", null).corpo();
        for (Object item : (List<Object>) f.get("veiculos")) {
            Map<String, Object> v = (Map<String, Object>) item;
            System.out.println("   " + v.get("placa") + " disponível=" + v.get("disponivel"));
        }
        System.out.println("\n7) Relatório (lado de leitura do CQRS): "
                + HttpJson.chamar("GET", locacoes + "/v1/relatorios/faturamento?mes=2026-11", null).corpo());
    }
}
