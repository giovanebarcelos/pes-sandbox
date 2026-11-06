package servicos;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Sobe os 3 microsserviços em PROCESSOS separados — SEM Docker.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java servicos.IniciarTodos
 * Parar: Ctrl+C (encerra os três).
 *
 * Dica para os experimentos de falha do tutorial: em vez deste programa, abra
 * 3 terminais e rode cada serviço separadamente — assim dá para derrubar um só.
 */
public final class IniciarTodos {
    private IniciarTodos() {
    }

    static boolean esperar(String url, int segundos) {
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(500)).build();
        long limite = System.currentTimeMillis() + segundos * 1000L;
        while (System.currentTimeMillis() < limite) {
            try {
                cliente.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.discarding());
                return true;
            } catch (IOException e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("java.class.path");
        Object[][] servicos = {
            {"frota", "servicos.FrotaServico", 8001},
            {"notificacao", "servicos.NotificacaoServico", 8003},
            {"locacoes", "servicos.LocacoesServico", 8002},
        };
        List<Process> processos = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> processos.forEach(Process::destroy)));
        for (Object[] s : servicos) {
            ProcessBuilder pb = new ProcessBuilder(java, "-cp", classpath, (String) s[1]).inheritIO();
            pb.environment().put("PORTA", String.valueOf(s[2]));
            processos.add(pb.start());
            boolean ok = esperar("http://localhost:" + s[2] + "/saude", 15);
            System.out.printf("  %-12s http://localhost:%d  %s%n", s[0], (int) s[2], ok ? "OK" : "NÃO RESPONDEU");
        }
        System.out.println("\nServiços no ar. Em outro terminal:  java ../../ferramentas/Rodar.java servicos.DemoCliente"
                + "   (Ctrl+C aqui para parar)");
        for (Process p : processos) {
            p.waitFor();
        }
    }
}
