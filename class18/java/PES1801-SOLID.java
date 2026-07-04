/*
 * PES1801 - Princípios SOLID (Antes/Depois)
 * Aula 18: Princípios de Projeto e Padrões Arquiteturais
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra a aplicação dos princípios SOLID comparando código
 * antes (violando) e depois (aplicando) SRP, OCP e DIP.
 *
 * Compilar/executar:
 *   javac PES1801-SOLID.java && java SOLID
 */

import java.util.*;

// -----------------------------------------------------------
// ❌ ANTES: violando SRP, OCP e DIP
// -----------------------------------------------------------

class RelatorioAntes {
    String gerar(List<Integer> dados, String formato) {
        int total = dados.stream().mapToInt(Integer::intValue).sum();
        switch (formato) {
            case "PDF": return "Relatório PDF: Total = " + total;
            case "CSV": return "Total," + total;
            default: throw new IllegalArgumentException("Formato não suportado");
        }
    }
}

// -----------------------------------------------------------
// ✅ DEPOIS: aplicando SRP, OCP, DIP
// -----------------------------------------------------------

class Calculadora {
    static int total(List<Integer> dados) {
        return dados.stream().mapToInt(Integer::intValue).sum();
    }
}

interface Formatador {
    String formatar(int total);
}

class FormatadorPDF implements Formatador {
    public String formatar(int total) {
        return "Relatório PDF: Total = " + total;
    }
}

class FormatadorCSV implements Formatador {
    public String formatar(int total) {
        return "Total," + total;
    }
}

class GeradorRelatorio {
    private Formatador formatador;

    GeradorRelatorio(Formatador formatador) {
        this.formatador = formatador;
    }

    String gerar(List<Integer> dados) {
        int total = Calculadora.total(dados);
        return formatador.formatar(total);
    }
}

class SOLID {
    public static void main(String[] args) {
        List<Integer> dados = Arrays.asList(100, 200, 300);

        System.out.println("=".repeat(60));
        System.out.println("  PRINCÍPIOS SOLID — ANTES × DEPOIS");
        System.out.println("=".repeat(60));

        System.out.println("\n--- ❌ Antes (viola SRP/OCP/DIP) ---");
        RelatorioAntes antes = new RelatorioAntes();
        System.out.println("  PDF: " + antes.gerar(dados, "PDF"));
        System.out.println("  CSV: " + antes.gerar(dados, "CSV"));

        System.out.println("\n--- ✅ Depois (SRP + OCP + DIP) ---");
        for (Formatador fmt : Arrays.asList(new FormatadorPDF(), new FormatadorCSV())) {
            GeradorRelatorio gerador = new GeradorRelatorio(fmt);
            System.out.println("  " + gerador.gerar(dados));
        }

        System.out.println("\n✓ SRP: cada classe tem uma única responsabilidade.");
        System.out.println("✓ OCP: novos formatos sem alterar código existente.");
        System.out.println("✓ DIP: dependência de abstrações, não de implementações.");
    }
}
