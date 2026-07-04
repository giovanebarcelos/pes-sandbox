/*
 * PES1201 - Verificação de Rastreabilidade Requisito ↔ Caso de Uso
 * Aula 12: Documentação e Validação de Requisitos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Verifica a cobertura: todo requisito está ligado a pelo menos um caso de uso?
 * E todo caso de uso atende a pelo menos um requisito?
 *
 * Compilar/executar:
 *   javac PES1201-Rastreabilidade.java && java Rastreabilidade
 */

import java.util.*;

class Rastreabilidade {

    public static boolean verificarRastreabilidade(
            Map<String, String> requisitos,
            Map<String, String> casosUso,
            Map<String, List<String>> matriz) {

        System.out.println("=".repeat(60));
        System.out.println("  VERIFICAÇÃO DE RASTREABILIDADE");
        System.out.println("=".repeat(60));

        System.out.printf("%n--- Forward Trace (Requisito → Caso de Uso) ---%n");
        int reqsSemCobertura = 0;
        for (var entry : requisitos.entrySet()) {
            String reqId = entry.getKey();
            List<String> ucs = matriz.getOrDefault(reqId, Collections.emptyList());
            if (ucs.isEmpty()) {
                reqsSemCobertura++;
                System.out.printf("  ⚠️  %s: NÃO coberto por nenhum caso de uso!%n", reqId);
            } else {
                System.out.printf("  ✓ %s: coberto por %s%n", reqId, String.join(", ", ucs));
            }
        }

        System.out.printf("%n--- Backward Trace (Caso de Uso → Requisito) ---%n");
        Set<String> ucCobertos = new HashSet<>();
        for (List<String> ucs : matriz.values()) {
            ucCobertos.addAll(ucs);
        }
        int ucsSemReq = 0;
        for (String ucId : casosUso.keySet()) {
            if (ucCobertos.contains(ucId)) {
                System.out.printf("  ✓ %s: atende a pelo menos 1 requisito%n", ucId);
            } else {
                ucsSemReq++;
                System.out.printf("  ⚠️  %s: NÃO atende a nenhum requisito!%n", ucId);
            }
        }

        System.out.printf("%n--- Resumo ---%n");
        System.out.printf("  Requisitos sem caso de uso: %d/%d%n", reqsSemCobertura, requisitos.size());
        System.out.printf("  Casos de uso sem requisito: %d/%d%n", ucsSemReq, casosUso.size());

        return reqsSemCobertura == 0 && ucsSemReq == 0;
    }

    public static void main(String[] args) {
        Map<String, String> requisitos = new LinkedHashMap<>();
        requisitos.put("RF01", "Login com email/senha");
        requisitos.put("RF02", "Buscar produtos por nome");
        requisitos.put("RF03", "Realizar pedido");
        requisitos.put("RNF01", "Tempo de resposta < 2s");

        Map<String, String> casosUso = new LinkedHashMap<>();
        casosUso.put("UC01", "Autenticar usuário");
        casosUso.put("UC02", "Buscar produtos");
        casosUso.put("UC03", "Criar pedido");
        casosUso.put("UC04", "Processar pagamento");

        Map<String, List<String>> matriz = new LinkedHashMap<>();
        matriz.put("RF01", Arrays.asList("UC01"));
        matriz.put("RF02", Arrays.asList("UC02"));
        matriz.put("RF03", Arrays.asList("UC03", "UC04"));
        matriz.put("RNF01", Arrays.asList("UC01", "UC02", "UC03"));

        boolean completo = verificarRastreabilidade(requisitos, casosUso, matriz);
        System.out.printf("%n%s%n", completo ? "✓ Rastreabilidade completa!" : "⚠️  Há lacunas na rastreabilidade.");
    }
}
