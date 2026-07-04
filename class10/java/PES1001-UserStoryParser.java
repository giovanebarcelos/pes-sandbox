/*
 * PES1001 - Parser/Validador de User Stories
 * Aula 10: Levantamento e Elicitação de Requisitos
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Valida user stories no formato "Como [ator], quero [ação] para [benefício]"
 * e extrai os três componentes.
 *
 * Compilar/executar:
 *   javac PES1001-UserStoryParser.java && java UserStoryParser
 */

import java.util.*;
import java.util.regex.*;

class UserStoryParser {

    static final Pattern USER_STORY_PATTERN = Pattern.compile(
        "^Como\\s+(.+?),\\s*quero\\s+(.+?),\\s*para\\s+(.+?)$",
        Pattern.CASE_INSENSITIVE
    );

    static class Componentes {
        String ator, acao, beneficio;

        Componentes(String ator, String acao, String beneficio) {
            this.ator = ator;
            this.acao = acao;
            this.beneficio = beneficio;
        }
    }

    static class Analise {
        boolean valida;
        Componentes componentes;
        List<String> erros = new ArrayList<>();
        String erroGeral;

        static Analise invalida(String erro) {
            Analise a = new Analise();
            a.valida = false;
            a.erroGeral = erro;
            return a;
        }

        static Analise valida(Componentes c) {
            Analise a = new Analise();
            a.valida = true;
            a.componentes = c;
            return a;
        }
    }

    public static Componentes validarUserStory(String texto) {
        Matcher m = USER_STORY_PATTERN.matcher(texto.trim());
        if (!m.matches()) return null;
        String beneficio = m.group(3).trim();
        if (beneficio.endsWith(".")) beneficio = beneficio.substring(0, beneficio.length() - 1);
        return new Componentes(m.group(1).trim(), m.group(2).trim(), beneficio);
    }

    public static Analise analisarUserStory(String texto) {
        Componentes c = validarUserStory(texto);
        if (c == null) {
            return Analise.invalida("Formato inválido. Use: Como [ator], quero [ação] para [benefício]");
        }
        Analise a = Analise.valida(c);
        if (c.acao.split("\\s+").length < 2) {
            a.erros.add("Ação muito curta — seja mais específico(a)");
        }
        if (c.beneficio.split("\\s+").length < 2) {
            a.erros.add("Benefício muito vago — explique o valor entregue");
        }
        return a;
    }

    public static void main(String[] args) {
        String[] stories = {
            "Como cliente, quero buscar produtos por nome, para encontrar rapidamente o que desejo comprar",
            "Como admin, quero listar usuários, para gerenciar acessos",
            "Como usuário, quero login para segurança",
            "Cliente quer buscar produtos",
        };

        System.out.println("=".repeat(60));
        System.out.println("  VALIDADOR DE USER STORIES");
        System.out.println("=".repeat(60));

        for (int i = 0; i < stories.length; i++) {
            System.out.printf("%n--- Story %d ---%n", i + 1);
            System.out.printf("  Texto: \"%s\"%n", stories[i]);
            Analise analise = analisarUserStory(stories[i]);
            if (analise.valida) {
                Componentes c = analise.componentes;
                String qualidade = analise.erros.isEmpty() ? "Boa" : "Revisar";
                System.out.printf("  ✓ Válida (%s)%n", qualidade);
                System.out.printf("    Ator: %s%n", c.ator);
                System.out.printf("    Ação: %s%n", c.acao);
                System.out.printf("    Benefício: %s%n", c.beneficio);
                for (String e : analise.erros) {
                    System.out.printf("    ⚠️  %s%n", e);
                }
            } else {
                System.out.printf("  ✗ Inválida: %s%n", analise.erroGeral);
            }
        }
    }
}
