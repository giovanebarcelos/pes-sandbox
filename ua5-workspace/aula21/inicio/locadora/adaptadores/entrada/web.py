"""
Adaptador de ENTRADA: web (MVC da Aula 19, agora na borda do hexágono).

  Model      -> LocadoraService
  View       -> funções que devolvem HTML (pagina_frota, pagina_erro)
  Controller -> ControladorWeb.tratar(metodo, caminho, formulario)
               (testável sem subir servidor — ver tests/test_entrada.py)

O HTTPRequestHandler é só um adaptador fino entre a rede e o Controller.

Executar (na pasta solucao/):  python -m locadora.adaptadores.entrada.web
Abrir:  http://localhost:8020   (porta configurável pela variável PORT)
"""

import html
import os
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs

from ...dominio.erros import LocadoraErro
from ...config import montar


# ------------------------------- VIEW -------------------------------
def pagina_frota(veiculos, mensagem=""):
    linhas = "".join(
        f"<tr><td>{html.escape(v.placa)}</td><td>{html.escape(v.modelo)}</td>"
        f"<td>R$ {v.valor_diaria:.2f}</td>"
        f"<td>{'disponível' if v.disponivel else 'alugado'}</td>"
        f"<td>{_botao_devolver(v)}</td></tr>"
        for v in veiculos
    )
    aviso = f"<p class='msg'>{html.escape(mensagem)}</p>" if mensagem else ""
    return f"""<!doctype html><html lang="pt-br"><head><meta charset="utf-8">
<title>Locadora — MVC</title>
<style>body{{font-family:sans-serif;max-width:760px;margin:2rem auto;padding:0 1rem}}
table{{border-collapse:collapse;width:100%}}td,th{{border:1px solid #ccc;padding:.4rem}}
.msg{{background:#eef;padding:.5rem}}form.inline{{display:inline}}</style></head>
<body><h1>Locadora de Veículos</h1>{aviso}
<table><tr><th>Placa</th><th>Modelo</th><th>Diária</th><th>Status</th><th></th></tr>{linhas}</table>
<h2>Alugar</h2>
<form method="post" action="/alugar">
<input name="placa" placeholder="Placa" required> <input name="dias" type="number" min="1" value="1">
<input name="cliente" placeholder="Cliente" required> <input name="email" type="email" placeholder="E-mail" required>
<button>Alugar</button></form></body></html>"""


def _botao_devolver(v):
    if v.disponivel:
        return ""
    return (f"<form class='inline' method='post' action='/devolver'>"
            f"<input type='hidden' name='placa' value='{html.escape(v.placa)}'>"
            f"<button>Devolver</button></form>")


# ---------------------------- CONTROLLER ----------------------------
class ControladorWeb:
    def __init__(self, modelo):
        self.modelo = modelo

    def tratar(self, metodo, caminho, formulario):
        """Devolve (status_http, html)."""
        if metodo == "GET" and caminho == "/":
            return 200, pagina_frota(self.modelo.listar())
        try:
            if metodo == "POST" and caminho == "/alugar":
                loc = self.modelo.alugar(formulario["placa"].strip().upper(), formulario["cliente"],
                                         formulario["email"], int(formulario["dias"]))
                return 200, pagina_frota(self.modelo.listar(),
                                         f"{loc.placa} alugado para {loc.cliente}: R$ {loc.valor:.2f}")
            if metodo == "POST" and caminho == "/devolver":
                self.modelo.devolver(formulario["placa"])
                return 200, pagina_frota(self.modelo.listar(), f"{formulario['placa']} devolvido")
        except (LocadoraErro, KeyError, ValueError) as e:
            return 400, pagina_frota(self.modelo.listar(), f"Erro: {str(e).strip(chr(39))}")
        return 404, "<h1>404</h1>"


# --------------------- adaptador HTTP (infra fina) ---------------------
def criar_handler(controlador):
    class Handler(BaseHTTPRequestHandler):
        def _responder(self, metodo):
            tamanho = int(self.headers.get("Content-Length") or 0)
            corpo = self.rfile.read(tamanho).decode("utf-8") if tamanho else ""
            formulario = {k: v[0] for k, v in parse_qs(corpo).items()}
            status, pagina = controlador.tratar(metodo, self.path, formulario)
            dados = pagina.encode("utf-8")
            self.send_response(status)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(dados)))
            self.end_headers()
            self.wfile.write(dados)

        def do_GET(self):
            self._responder("GET")

        def do_POST(self):
            self._responder("POST")

    return Handler


def main():
    porta = int(os.environ.get("PORT", "8020"))
    servidor = ThreadingHTTPServer(("0.0.0.0", porta),
                                   criar_handler(ControladorWeb(montar())))
    print(f"Locadora MVC em http://localhost:{porta}  (Ctrl+C para parar)")
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
