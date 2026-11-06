"""
Microsserviço de FROTA — expõe a FrotaAPI (Aula 21) por HTTP.

Executar (na pasta solucao/):  python -m servicos.frota_servico
Variáveis: PORTA (padrão 8001)

  GET    /saude
  GET    /v1/veiculos
  GET    /v1/veiculos/{placa}/preco?dias=N
  POST   /v1/veiculos/{placa}/reserva     (409 se já alugado)
  DELETE /v1/veiculos/{placa}/reserva
  GET    /v1/telas/frota                  (bônus SDUI: a TELA vem do servidor)
"""

import os

from locadora.compartilhado.eventos import BarramentoEventos
from locadora.config import FROTA_EXEMPLO
from locadora.frota.adaptadores.memoria import VeiculoMemoriaRepository
from locadora.frota.api import FrotaAPI

from .http_json import Rotas, criar_servidor

PLACA = r"(?P<placa>[^/?]+)"


def criar_rotas(api):
    rotas = Rotas()

    def erro(e):
        if isinstance(e, KeyError):
            return 404, {"erro": str(e).strip("'\"")}
        if isinstance(e, RuntimeError):
            return 409, {"erro": str(e)}
        return 400, {"erro": str(e)}

    def saude(**_):
        return 200, {"servico": "frota", "status": "ok"}

    def listar(**_):
        return 200, {"veiculos": api.listar()}

    def preco(placa, consulta, **_):
        try:
            return 200, {"placa": placa, "preco_base": api.preco_base(placa, int(consulta.get("dias", 1)))}
        except (KeyError, RuntimeError, ValueError) as e:
            return erro(e)

    def reservar(placa, **_):
        try:
            api.reservar(placa)
            return 200, {"placa": placa, "disponivel": False}
        except (KeyError, RuntimeError, ValueError) as e:
            return erro(e)

    def liberar(placa, **_):
        try:
            api.liberar(placa)
            return 200, {"placa": placa, "disponivel": True}
        except (KeyError, RuntimeError, ValueError) as e:
            return erro(e)

    def tela_frota(**_):
        """SDUI (Geração 2 — JSON de componentes), mesmo vocabulário do PES2204."""
        filhos = [{"type": "text", "props": {"value": "Frota disponível"}}]
        for v in api.listar():
            if v["disponivel"]:
                filhos.append({"type": "button", "props": {
                    "label": f"Alugar {v['modelo']} ({v['placa']}) — R$ {v['diaria']:.2f}/dia",
                    "action": {"type": "post", "url": "/v1/locacoes", "body": {"placa": v["placa"]}}}})
        return 200, {"type": "column", "children": filhos}

    rotas.adicionar("GET", "/saude", saude)
    rotas.adicionar("GET", "/v1/veiculos", listar)
    rotas.adicionar("GET", f"/v1/veiculos/{PLACA}/preco", preco)
    rotas.adicionar("POST", f"/v1/veiculos/{PLACA}/reserva", reservar)
    rotas.adicionar("DELETE", f"/v1/veiculos/{PLACA}/reserva", liberar)
    rotas.adicionar("GET", "/v1/telas/frota", tela_frota)
    return rotas


def criar_api():
    api = FrotaAPI(VeiculoMemoriaRepository(), BarramentoEventos())
    for dados in FROTA_EXEMPLO:
        api.cadastrar(*dados)
    return api


def main():
    porta = int(os.environ.get("PORTA", "8001"))
    servidor = criar_servidor(criar_rotas(criar_api()), porta, nome="frota")
    print(f"[frota] ouvindo em http://localhost:{porta}", flush=True)
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
