"""Mini-framework HTTP+JSON (biblioteca padrão) compartilhado pelos três serviços."""

import json
import re
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse


class Rotas:
    def __init__(self):
        self._rotas = []

    def adicionar(self, metodo, padrao, funcao):
        """padrao: regex com grupos nomeados, ex.: r"/v1/veiculos/(?P<placa>[^/]+)/reserva"."""
        self._rotas.append((metodo, re.compile(f"^{padrao}$"), funcao))

    def despachar(self, metodo, caminho, corpo):
        url = urlparse(caminho)
        consulta = {k: v[0] for k, v in parse_qs(url.query).items()}
        for m, regex, funcao in self._rotas:
            achou = regex.match(url.path)
            if m == metodo and achou:
                return funcao(corpo=corpo, consulta=consulta, **achou.groupdict())
        return 404, {"erro": f"rota não encontrada: {metodo} {url.path}"}


def criar_servidor(rotas, porta, host="0.0.0.0", nome="servico"):
    class Handler(BaseHTTPRequestHandler):
        def _tratar(self, metodo):
            tamanho = int(self.headers.get("Content-Length") or 0)
            bruto = self.rfile.read(tamanho) if tamanho else b""
            try:
                corpo = json.loads(bruto) if bruto.strip() else {}
                status, resposta = rotas.despachar(metodo, self.path, corpo)
            except json.JSONDecodeError:
                status, resposta = 400, {"erro": "JSON inválido"}
            except Exception as e:  # noqa: BLE001 — devolve 500 em vez de derrubar o serviço
                status, resposta = 500, {"erro": f"{type(e).__name__}: {e}"}
            dados = json.dumps(resposta, ensure_ascii=False).encode("utf-8")
            self.send_response(status)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(dados)))
            self.end_headers()
            self.wfile.write(dados)

        def do_GET(self):
            self._tratar("GET")

        def do_POST(self):
            self._tratar("POST")

        def do_DELETE(self):
            self._tratar("DELETE")

        def log_message(self, formato, *args):
            print(f"[{nome}] {self.command} {self.path} -> {args[1] if len(args) > 1 else ''}", flush=True)

    servidor = ThreadingHTTPServer((host, porta), Handler)
    servidor.daemon_threads = True
    return servidor


def rodar_em_thread(servidor):
    t = threading.Thread(target=servidor.serve_forever, daemon=True)
    t.start()
    return t
