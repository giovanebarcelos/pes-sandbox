/*
 * PES0601 - Entregas Incrementais Acumulando Valor
 * Aula 06: Modelos Incremental e Prototipação
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Simula entregas incrementais de um sistema, mostrando como
 * cada incremento adiciona funcionalidades e valor acumulado.
 *
 * Compilar/executar:
 *   javac PES0601-EntregasIncrementais.java && java EntregasIncrementais
 */

import java.util.*;

class EntregasIncrementais {

    static class Incremento {
        String versao;
        List<String> funcionalidades;
        int esforcoHoras;
        int valorNegocio;

        Incremento(String versao, List<String> funcionalidades, int esforcoHoras, int valorNegocio) {
            this.versao = versao;
            this.funcionalidades = funcionalidades;
            this.esforcoHoras = esforcoHoras;
            this.valorNegocio = valorNegocio;
        }
    }

    public static void simularIncrementos() {
        List<Incremento> incrementos = Arrays.asList(
            new Incremento("1.0", Arrays.asList("Cadastro de usuários", "Login"), 80, 30),
            new Incremento("2.0", Arrays.asList("Catálogo de produtos", "Busca"), 120, 40),
            new Incremento("3.0", Arrays.asList("Carrinho de compras", "Checkout"), 150, 50)
        );

        System.out.println("=".repeat(60));
        System.out.println("  SIMULAÇÃO DE ENTREGAS INCREMENTAIS");
        System.out.println("=".repeat(60));
        System.out.println();

        int esforcoAcumulado = 0;
        int valorAcumulado = 0;
        System.out.printf("%-8s %-45s %8s %6s%n", "Versão", "Funcionalidades", "Esforço", "Valor");
        System.out.println("-".repeat(72));
        for (Incremento inc : incrementos) {
            esforcoAcumulado += inc.esforcoHoras;
            valorAcumulado += inc.valorNegocio;
            String funcs = String.join(", ", inc.funcionalidades);
            System.out.printf("v%-7s %-45s %4dh %4dpts%n", inc.versao, funcs,
                    inc.esforcoHoras, inc.valorNegocio);
        }
        System.out.println("-".repeat(72));
        System.out.printf("%-8s %45s %4dh %4dpts%n", "TOTAL", " ", esforcoAcumulado, valorAcumulado);
        System.out.println();
        System.out.printf("ROI (Valor/Esforço): %.2f pts/h%n", (double) valorAcumulado / esforcoAcumulado);
        System.out.println();
        System.out.println("✓ Vantagem incremental: valor entregue a cada ciclo, feedback contínuo.");
    }

    public static void main(String[] args) {
        simularIncrementos();
    }
}
