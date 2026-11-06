package servicos;

import static servicos.HttpJson.resposta;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Microsserviço de NOTIFICAÇÃO — recebe avisos e "envia" ao cliente (imprime).
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java servicos.NotificacaoServico
 * Variáveis: PORTA (padrão 8003)
 *   GET  /saude
 *   POST /v1/notificacoes   {"id": "...", "destino": "...", "mensagem": "..."}
 *   GET  /v1/notificacoes
 *
 * IDEMPOTENTE: a entrega assíncrona é "pelo menos uma vez" — a mesma mensagem
 * pode chegar repetida depois de uma falha de rede. O id evita aviso duplicado.
 */
public final class NotificacaoServico {
    private NotificacaoServico() {
    }

    public static HttpJson.Rotas criarRotas() {
        List<Map<String, Object>> recebidas = new ArrayList<>();
        Set<Object> ids = new HashSet<>();
        HttpJson.Rotas rotas = new HttpJson.Rotas();
        rotas.adicionar("GET", "/saude", r -> resposta(200, "servico", "notificacao", "status", "ok"));
        rotas.adicionar("POST", "/v1/notificacoes", r -> {
            Map<String, Object> corpo = r.corpo();
            if (!corpo.containsKey("id") || !corpo.containsKey("mensagem")) {
                return resposta(400, "erro", "campos obrigatórios: id, mensagem");
            }
            synchronized (recebidas) {
                if (!ids.add(corpo.get("id"))) {
                    return resposta(200, "duplicada", true);
                }
                recebidas.add(corpo);
            }
            System.out.println("[notificacao] -> " + corpo.getOrDefault("destino", "?") + ": " + corpo.get("mensagem"));
            return resposta(201, "duplicada", false);
        });
        rotas.adicionar("GET", "/v1/notificacoes", r -> {
            synchronized (recebidas) {
                return resposta(200, "notificacoes", new ArrayList<>(recebidas));
            }
        });
        return rotas;
    }

    public static void main(String[] args) throws IOException {
        int porta = HttpJson.porta("8003");
        HttpServer servidor = HttpJson.criarServidor(criarRotas(), "0.0.0.0", porta, "notificacao", true);
        servidor.start();
        System.out.println("[notificacao] ouvindo em http://localhost:" + porta);
    }
}
