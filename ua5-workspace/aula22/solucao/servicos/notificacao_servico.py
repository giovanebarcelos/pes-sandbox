"""
Microsserviço de NOTIFICAÇÃO — recebe avisos e "envia" ao cliente (imprime).

Executar (na pasta solucao/):  python -m servicos.notificacao_servico
Variáveis: PORTA (padrão 8003)

  GET  /saude
  POST /v1/notificacoes   {"id": "...", "destino": "...", "mensagem": "..."}
  GET  /v1/notificacoes

IDEMPOTENTE: a entrega assíncrona é "pelo menos uma vez" — a mesma mensagem
pode chegar repetida depois de uma falha de rede. O id evita aviso duplicado.
"""

import os
import threading

from .http_json import Rotas, criar_servidor


def criar_rotas():
    recebidas, ids, trava = [], set(), threading.Lock()
    rotas = Rotas()

    def saude(**_):
        return 200, {"servico": "notificacao", "status": "ok"}

    def receber(corpo, **_):
        if not {"id", "mensagem"} <= set(corpo):
            return 400, {"erro": "campos obrigatórios: id, mensagem"}
        with trava:
            if corpo["id"] in ids:
                return 200, {"duplicada": True}
            ids.add(corpo["id"])
            recebidas.append(corpo)
        print(f"[notificacao] -> {corpo.get('destino', '?')}: {corpo['mensagem']}", flush=True)
        return 201, {"duplicada": False}

    def listar(**_):
        with trava:
            return 200, {"notificacoes": list(recebidas)}

    rotas.adicionar("GET", "/saude", saude)
    rotas.adicionar("POST", "/v1/notificacoes", receber)
    rotas.adicionar("GET", "/v1/notificacoes", listar)
    return rotas


def main():
    porta = int(os.environ.get("PORTA", "8003"))
    servidor = criar_servidor(criar_rotas(), porta, nome="notificacao")
    print(f"[notificacao] ouvindo em http://localhost:{porta}", flush=True)
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
