"""
Microsserviço de LOCAÇÕES — o contexto da Aula 21 (CQRS + Event Sourcing) exposto por HTTP.

Executar (na pasta solucao/):  python -m servicos.locacoes_servico
Variáveis:
  PORTA            (padrão 8002)
  FROTA_URL        (padrão http://localhost:8001)  -> chamada SÍNCRONA
  NOTIFICACAO_URL  (padrão http://localhost:8003)  -> entrega ASSÍNCRONA (outbox)
  LOCADORA_EVENTOS (padrão dados/eventos.jsonl)    -> event store deste serviço

  GET  /saude
  POST /v1/locacoes                   {"placa","cliente","dias"[, "data_inicio", "id"]}
  POST /v1/locacoes/{id}/devolucao    {["data"]}
  GET  /v1/locacoes/ativas
  GET  /v1/relatorios/faturamento?mes=AAAA-MM
  GET  /v1/relatorios/mais-alugados?mes=AAAA-MM
  GET  /v1/outbox                     (o que ainda não foi entregue)
"""

import json
import os
import urllib.request
import uuid
from datetime import date

from locadora.config import montar
from locadora.locacoes.adaptadores.frota_http import FrotaHttp
from locadora.locacoes.aplicacao.comandos import IniciarLocacao, RegistrarDevolucao
from locadora.locacoes.dominio.erros import (FrotaForaDoAr, LocacaoErro, VeiculoDesconhecido,
                                             VeiculoNaoDisponivel)
from locadora.locacoes.dominio.eventos import (LocacaoIniciada, MultaPorAtrasoAplicada,
                                               VeiculoDevolvido)

from .http_json import Rotas, criar_servidor
from .outbox import Outbox


def criar_entregador(frota, url_notificacao, timeout=2.0):
    def entregar(tarefa):
        if tarefa["tipo"] == "liberar":
            frota.liberar(tarefa["placa"])
        else:
            dados = json.dumps(tarefa).encode("utf-8")
            req = urllib.request.Request(url_notificacao.rstrip("/") + "/v1/notificacoes", data=dados,
                                         method="POST", headers={"Content-Type": "application/json"})
            urllib.request.urlopen(req, timeout=timeout).close()
    return entregar


def ligar_outbox(app, outbox):
    """Assina os eventos do barramento e transforma cada um em tarefa assíncrona."""
    def ao_iniciar(e):
        outbox.adicionar({"id": f"{e.id_agregado}:iniciada", "tipo": "notificar", "destino": e.cliente,
                          "mensagem": f"Locação {e.id_agregado} do {e.placa} confirmada: "
                                      f"R$ {e.valor_previsto_centavos / 100:.2f} por {e.dias_previstos} dia(s)."})

    def ao_devolver(e):
        outbox.adicionar({"id": f"{e.id_agregado}:liberar", "tipo": "liberar", "placa": e.placa})
        outbox.adicionar({"id": f"{e.id_agregado}:devolvido", "tipo": "notificar", "destino": f"cliente da {e.id_agregado}",
                          "mensagem": f"Veículo {e.placa} devolvido (locação {e.id_agregado})."})

    def ao_multar(e):
        outbox.adicionar({"id": f"{e.id_agregado}:multa", "tipo": "notificar", "destino": f"cliente da {e.id_agregado}",
                          "mensagem": f"Multa por {e.dias_atraso} dia(s) de atraso: "
                                      f"R$ {e.valor_centavos / 100:.2f}."})

    app.barramento.inscrever(LocacaoIniciada, ao_iniciar)
    app.barramento.inscrever(VeiculoDevolvido, ao_devolver)
    app.barramento.inscrever(MultaPorAtrasoAplicada, ao_multar)


def criar_rotas(app, outbox):
    rotas = Rotas()

    def executar(comando):
        try:
            return None, app.comandos.executar(comando)
        except VeiculoDesconhecido as e:
            return (404, {"erro": str(e)}), None
        except VeiculoNaoDisponivel as e:
            return (409, {"erro": str(e)}), None
        except FrotaForaDoAr as e:
            return (503, {"erro": str(e)}), None
        except KeyError as e:
            return (404, {"erro": str(e).strip("'\"")}), None
        except (LocacaoErro, ValueError) as e:
            return (400, {"erro": str(e)}), None

    def saude(**_):
        return 200, {"servico": "locacoes", "status": "ok", "outbox_pendentes": len(outbox.pendentes())}

    def iniciar(corpo, **_):
        try:
            comando = IniciarLocacao(
                corpo.get("id") or f"L-{uuid.uuid4().hex[:6]}", corpo["placa"], corpo["cliente"],
                date.fromisoformat(corpo.get("data_inicio") or date.today().isoformat()), int(corpo["dias"]))
        except (KeyError, ValueError) as e:
            return 400, {"erro": f"corpo inválido: {e}"}
        falha, loc = executar(comando)
        if falha:
            return falha
        return 201, {"id": loc.id, "placa": loc.placa, "valor_previsto": loc.valor_previsto.reais}

    def devolver(id_locacao, corpo, **_):
        try:
            data = date.fromisoformat(corpo.get("data") or date.today().isoformat())
        except ValueError as e:
            return 400, {"erro": str(e)}
        falha, loc = executar(RegistrarDevolucao(id_locacao, data))
        if falha:
            return falha
        return 200, {"id": loc.id, "multa": loc.multa.reais, "valor_total": loc.valor_total.reais}

    def ativas(**_):
        return 200, {"ativas": app.consultas.locacoes_ativas()}

    def faturamento(consulta, **_):
        mes = consulta.get("mes", date.today().isoformat()[:7])
        return 200, {"mes": mes, "faturamento": app.consultas.faturamento_do_mes(mes)}

    def mais_alugados(consulta, **_):
        mes = consulta.get("mes", date.today().isoformat()[:7])
        return 200, {"mes": mes, "ranking": app.consultas.mais_alugados(mes)}

    def ver_outbox(**_):
        return 200, {"pendentes": outbox.pendentes(), "ultimo_erro": outbox.ultimo_erro}

    rotas.adicionar("GET", "/saude", saude)
    rotas.adicionar("POST", "/v1/locacoes", iniciar)
    rotas.adicionar("POST", r"/v1/locacoes/(?P<id_locacao>[^/]+)/devolucao", devolver)
    rotas.adicionar("GET", "/v1/locacoes/ativas", ativas)
    rotas.adicionar("GET", "/v1/relatorios/faturamento", faturamento)
    rotas.adicionar("GET", "/v1/relatorios/mais-alugados", mais_alugados)
    rotas.adicionar("GET", "/v1/outbox", ver_outbox)
    return rotas


def montar_servico(url_frota, url_notificacao, caminho_eventos=None, intervalo=1.0):
    frota = FrotaHttp(url_frota)
    app = montar(caminho_eventos, servico_frota=frota, liberar_por_evento=False)
    outbox = Outbox(criar_entregador(frota, url_notificacao), intervalo)
    ligar_outbox(app, outbox)
    return app, outbox


def main():
    porta = int(os.environ.get("PORTA", "8002"))
    app, outbox = montar_servico(os.environ.get("FROTA_URL", "http://localhost:8001"),
                                 os.environ.get("NOTIFICACAO_URL", "http://localhost:8003"),
                                 os.environ.get("LOCADORA_EVENTOS", "dados/eventos.jsonl"))
    outbox.iniciar()
    servidor = criar_servidor(criar_rotas(app, outbox), porta, nome="locacoes")
    print(f"[locacoes] ouvindo em http://localhost:{porta}", flush=True)
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
