"""
Cliente de demonstração: conversa com os 3 serviços pela rede.

Executar (com os serviços no ar):  python -m servicos.demo_cliente
Variáveis: FROTA_URL, LOCACOES_URL, NOTIFICACAO_URL (padrão: localhost 8001/8002/8003)
"""

import json
import os
import time
import urllib.error
import urllib.request
import uuid

FROTA = os.environ.get("FROTA_URL", "http://localhost:8001")
LOCACOES = os.environ.get("LOCACOES_URL", "http://localhost:8002")
NOTIFICACAO = os.environ.get("NOTIFICACAO_URL", "http://localhost:8003")


def chamar(metodo, url, corpo=None):
    dados = json.dumps(corpo).encode() if corpo is not None else None
    req = urllib.request.Request(url, data=dados, method=metodo, headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=5) as r:
            return r.status, json.loads(r.read())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read() or b"{}")


def renderizar_sdui(no, nivel=0):
    """Renderizador SDUI mínimo (mesma ideia do repository/class22/python/PES2204)."""
    pad = "  " * nivel
    if no["type"] == "column":
        return "\n".join(renderizar_sdui(f, nivel) for f in no.get("children", []))
    if no["type"] == "text":
        return f"{pad}{no['props']['value']}"
    if no["type"] == "button":
        return f"{pad}[ {no['props']['label']} ]"
    return f"{pad}(componente desconhecido: {no['type']})"


def main():
    sufixo = uuid.uuid4().hex[:4]
    print("1) Tela montada pelo SERVIDOR (SDUI):")
    print(renderizar_sdui(chamar("GET", f"{FROTA}/v1/telas/frota")[1]))

    print("\n2) Alugar ABC-1234 por 5 dias (Locações -> Frota, SÍNCRONO):")
    print("  ", chamar("POST", f"{LOCACOES}/v1/locacoes",
                       {"id": f"L-{sufixo}", "placa": "ABC-1234", "cliente": "Ana", "dias": 5,
                        "data_inicio": "2026-11-02"}))

    print("\n3) Tentar alugar o mesmo veículo de novo (erro 409 vindo da Frota):")
    print("  ", chamar("POST", f"{LOCACOES}/v1/locacoes",
                       {"placa": "ABC-1234", "cliente": "Bia", "dias": 1}))

    print("\n4) Devolver com 2 dias de atraso:")
    print("  ", chamar("POST", f"{LOCACOES}/v1/locacoes/L-{sufixo}/devolucao", {"data": "2026-11-09"}))

    time.sleep(1.5)  # dá tempo ao outbox (ASSÍNCRONO)
    print("\n5) Notificações recebidas pelo serviço de Notificação:")
    for n in chamar("GET", f"{NOTIFICACAO}/v1/notificacoes")[1]["notificacoes"]:
        print("   -", n["mensagem"])

    print("\n6) Frota depois da devolução (liberada pelo outbox):")
    for v in chamar("GET", f"{FROTA}/v1/veiculos")[1]["veiculos"]:
        print(f"   {v['placa']} disponível={v['disponivel']}")

    print("\n7) Relatório (lado de leitura do CQRS):",
          chamar("GET", f"{LOCACOES}/v1/relatorios/faturamento?mes=2026-11")[1])


if __name__ == "__main__":
    main()
