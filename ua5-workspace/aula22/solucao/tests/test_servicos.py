"""
Testes de integração dos MICROSSERVIÇOS — sobem os 3 servidores dentro do teste
(portas livres escolhidas pelo sistema), sem Docker.
"""

import json
import socket
import unittest
import urllib.error
import urllib.request

from servicos import frota_servico, notificacao_servico
from servicos.http_json import criar_servidor, rodar_em_thread
from servicos.locacoes_servico import criar_rotas, montar_servico


def porta_livre():
    with socket.socket() as s:
        s.bind(("127.0.0.1", 0))
        return s.getsockname()[1]


def chamar(metodo, url, corpo=None):
    dados = json.dumps(corpo).encode() if corpo is not None else None
    req = urllib.request.Request(url, data=dados, method=metodo)
    try:
        with urllib.request.urlopen(req, timeout=5) as r:
            return r.status, json.loads(r.read())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read() or b"{}")


class TestMicrosservicos(unittest.TestCase):
    def setUp(self):
        self.servidores = []
        self.p_frota, self.p_notif, self.p_loc = porta_livre(), porta_livre(), porta_livre()
        self.frota = self.subir(frota_servico.criar_rotas(frota_servico.criar_api()), self.p_frota)
        self.notif = self.subir(notificacao_servico.criar_rotas(), self.p_notif)
        self.app, self.outbox = montar_servico(f"http://127.0.0.1:{self.p_frota}",
                                               f"http://127.0.0.1:{self.p_notif}")
        self.subir(criar_rotas(self.app, self.outbox), self.p_loc)
        self.loc = f"http://127.0.0.1:{self.p_loc}"

    def subir(self, rotas, porta):
        srv = criar_servidor(rotas, porta, host="127.0.0.1", nome="teste")
        srv.RequestHandlerClass.log_message = lambda *a: None
        rodar_em_thread(srv)
        self.servidores.append(srv)
        return srv

    def derrubar(self, srv):
        srv.shutdown()
        srv.server_close()
        self.servidores.remove(srv)

    def tearDown(self):
        for srv in list(self.servidores):
            self.derrubar(srv)

    def alugar(self, placa="ABC-1234", id_locacao="L-1"):
        return chamar("POST", f"{self.loc}/v1/locacoes",
                      {"id": id_locacao, "placa": placa, "cliente": "Ana", "dias": 5, "data_inicio": "2026-11-02"})

    def notificacoes(self):
        return chamar("GET", f"http://127.0.0.1:{self.p_notif}/v1/notificacoes")[1]["notificacoes"]

    # ---------------------------------------------------------------- fluxo feliz
    def test_fluxo_completo_entre_servicos(self):
        self.assertEqual(self.alugar(), (201, {"id": "L-1", "placa": "ABC-1234", "valor_previsto": 660.0}))
        self.assertEqual(self.alugar(id_locacao="L-2")[0], 409)
        status, corpo = chamar("POST", f"{self.loc}/v1/locacoes/L-1/devolucao", {"data": "2026-11-09"})
        self.assertEqual((status, corpo["multa"]), (200, 396.0))
        self.assertEqual(self.outbox.entregar_pendentes(), 0)
        self.assertEqual(len(self.notificacoes()), 3)
        veiculos = chamar("GET", f"http://127.0.0.1:{self.p_frota}/v1/veiculos")[1]["veiculos"]
        self.assertTrue(all(v["disponivel"] for v in veiculos))

    def test_contrato_de_erros(self):
        self.assertEqual(self.alugar(placa="ZZZ-9999")[0], 404)
        self.assertEqual(chamar("POST", f"{self.loc}/v1/locacoes", {"placa": "ABC-1234"})[0], 400)
        self.assertEqual(chamar("POST", f"{self.loc}/v1/locacoes/NAO-EXISTE/devolucao", {})[0], 404)
        self.assertEqual(chamar("GET", f"{self.loc}/v2/locacoes")[0], 404)

    # ------------------------------------------------------- falhas (resiliência)
    def test_frota_fora_do_ar_responde_503(self):
        """Chamada SÍNCRONA: sem a Frota, Locações não consegue alugar (e diz isso claramente)."""
        self.derrubar(self.frota)
        status, corpo = self.alugar()
        self.assertEqual(status, 503)
        self.assertIn("Frota", corpo["erro"])

    def test_notificacao_fora_do_ar_nao_impede_locacao(self):
        """Chamada ASSÍNCRONA: a locação funciona; o aviso espera no outbox e é entregue depois."""
        self.derrubar(self.notif)
        self.assertEqual(self.alugar()[0], 201)
        self.assertEqual(self.outbox.entregar_pendentes(), 1)       # não entregou
        self.notif = self.subir(notificacao_servico.criar_rotas(), self.p_notif)   # volta
        self.assertEqual(self.outbox.entregar_pendentes(), 0)
        self.assertEqual(len(self.notificacoes()), 1)

    def test_notificacao_idempotente(self):
        url = f"http://127.0.0.1:{self.p_notif}/v1/notificacoes"
        msg = {"id": "X-1", "destino": "Ana", "mensagem": "oi"}
        self.assertEqual(chamar("POST", url, msg), (201, {"duplicada": False}))
        self.assertEqual(chamar("POST", url, msg), (200, {"duplicada": True}))
        self.assertEqual(len(self.notificacoes()), 1)

    def test_tela_sdui_vem_do_servidor(self):
        status, tela = chamar("GET", f"http://127.0.0.1:{self.p_frota}/v1/telas/frota")
        self.assertEqual((status, tela["type"]), (200, "column"))
        self.assertEqual(sum(1 for c in tela["children"] if c["type"] == "button"), 4)


if __name__ == "__main__":
    unittest.main()
