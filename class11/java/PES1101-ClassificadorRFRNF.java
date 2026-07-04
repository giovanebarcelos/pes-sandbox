/*
 * PES1101 - Classificador RF/RNF (FURPS+)
 * Aula 11: Requisitos Funcionais e Não Funcionais
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Classifica requisitos como Funcionais (RF) ou Não Funcionais (RNF)
 * usando palavras-chave do modelo FURPS+.
 *
 * Compilar/executar:
 *   javac PES1101-ClassificadorRF-RNF.java && java ClassificadorRFRNF
 */

import java.util.*;

class ClassificadorRFRNF {

    static final Map<String, List<String>> CATEGORIAS_RNF = new LinkedHashMap<>();

    static {
        CATEGORIAS_RNF.put("desempenho", Arrays.asList(
            "tempo", "segundo", "milissegundo", "rápido", "simultâneo",
            "concorrente", "vazão", "throughput", "latência"));
        CATEGORIAS_RNF.put("usabilidade", Arrays.asList(
            "fácil", "intuitivo", "acessível", "usabilidade", "UX",
            "interface", "acessibilidade", "WCAG"));
        CATEGORIAS_RNF.put("confiabilidade", Arrays.asList(
            "disponível", "tolerante", "falha", "recuperação",
            "backup", "redundante", "uptime", "MTBF"));
        CATEGORIAS_RNF.put("seguranca", Arrays.asList(
            "criptografado", "autenticado", "autorizado", "senha",
            "token", "permissão", "LGPD", "GDPR", "log", "auditoria"));
        CATEGORIAS_RNF.put("suportabilidade", Arrays.asList(
            "compatível", "portável", "multiplataforma", "navegador",
            "sistema operacional", "SO"));
    }

    static class Classificacao {
        String tipo, categoria;

        Classificacao(String tipo, String categoria) {
            this.tipo = tipo;
            this.categoria = categoria;
        }
    }

    public static Classificacao classificar(String descricao) {
        String texto = descricao.toLowerCase();
        for (var entry : CATEGORIAS_RNF.entrySet()) {
            for (String palavra : entry.getValue()) {
                if (texto.contains(palavra)) {
                    return new Classificacao("RNF", entry.getKey());
                }
            }
        }
        return new Classificacao("RF", "funcional");
    }

    public static void main(String[] args) {
        String[] requisitos = {
            "O sistema deve permitir cadastro de usuários com email e senha",
            "O tempo de resposta para qualquer operação deve ser inferior a 2 segundos",
            "A interface deve ser intuitiva e acessível conforme WCAG 2.1",
            "O sistema deve estar disponível 99.9% do tempo (uptime)",
            "Todos os dados devem ser criptografados em repouso e em trânsito",
            "O sistema deve gerar relatório mensal de vendas em PDF",
            "O sistema deve ser compatível com Chrome, Firefox e Safari",
            "O sistema deve suportar até 1000 usuários simultâneos",
        };

        System.out.println("=".repeat(60));
        System.out.println("  CLASSIFICADOR RF / RNF (FURPS+)");
        System.out.println("=".repeat(60));
        System.out.println();

        System.out.printf("%-6s %-20s %s%n", "Tipo", "Categoria FURPS+", "Requisito");
        System.out.println("-".repeat(80));
        for (String req : requisitos) {
            Classificacao c = classificar(req);
            String icone = c.tipo.equals("RF") ? "⚙️" : "🔧";
            System.out.printf("%s %-3s %-20s %s%n", icone, c.tipo, c.categoria, req);
        }
    }
}
