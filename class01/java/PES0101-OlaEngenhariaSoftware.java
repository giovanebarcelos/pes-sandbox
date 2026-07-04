/*
 * PES0101 - Olá, Engenharia de Software
 * Aula 01: Boas-vindas e Introdução à Engenharia de Software
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Exemplo introdutório que exibe data, versão e uma mensagem de boas-vindas,
 * ilustrando versionamento e registro de informações básicas de um projeto.
 *
 * Compilar/executar:
 *   javac PES0101-OlaEngenhariaSoftware.java && java OlaEngenhariaSoftware
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class OlaEngenhariaSoftware {

    public static void exibirInfoProjeto(String nomeProjeto, String versao, String autor) {
        String dataAtual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        System.out.println("=".repeat(50));
        System.out.println("  " + nomeProjeto);
        System.out.println("  Versão: " + versao);
        System.out.println("  Autor: " + autor);
        System.out.println("  Data de execução: " + dataAtual);
        System.out.println("=".repeat(50));
        System.out.println();
        System.out.println("Bem-vindo(a) à disciplina de Projeto e Engenharia de Software!");
        System.out.println("Neste curso, você aprenderá a gerenciar, projetar e construir");
        System.out.println("software com qualidade, aplicando metodologias e boas práticas.");
        System.out.println();
    }

    public static void main(String[] args) {
        exibirInfoProjeto(
            "PES - Projeto e Engenharia de Software",
            "1.0.0",
            "Prof. Giovane Barcelos"
        );
    }
}
