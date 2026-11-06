"""MVC web: Controller testado sem servidor + um teste de fumaça com servidor real."""

import threading
import unittest
import urllib.parse
import urllib.request
from http.server import ThreadingHTTPServer

from locadora.apresentacao.montagem import servico_exemplo
from locadora.apresentacao.web_mvc import ControladorWeb, criar_handler


class TestControladorWeb(unittest.TestCase):
    def setUp(self):
        self.c = ControladorWeb(servico_exemplo(silencioso=True))

    def test_get_lista_frota(self):
        status, pagina = self.c.tratar("GET", "/", {})
        self.assertEqual(status, 200)
        self.assertIn("Fiat Uno", pagina)

    def test_post_alugar(self):
        status, pagina = self.c.tratar("POST", "/alugar", dict(placa="abc-1234", dias="5",
                                                               cliente="Ana", email="a@x"))
        self.assertEqual(status, 200)
        self.assertIn("R$ 660.00", pagina)

    def test_post_alugar_invalido(self):
        status, _ = self.c.tratar("POST", "/alugar", dict(placa="ZZZ", dias="1", cliente="A", email="a@x"))
        self.assertEqual(status, 400)

    def test_rota_inexistente(self):
        self.assertEqual(self.c.tratar("GET", "/nada", {})[0], 404)


class TestServidorReal(unittest.TestCase):
    def test_fumaca(self):
        srv = ThreadingHTTPServer(("127.0.0.1", 0),
                                  criar_handler(ControladorWeb(servico_exemplo(silencioso=True))))
        threading.Thread(target=srv.serve_forever, daemon=True).start()
        try:
            url = f"http://127.0.0.1:{srv.server_address[1]}"
            dados = urllib.parse.urlencode(dict(placa="XYZ-9012", dias="3", cliente="Bia",
                                                email="b@x")).encode()
            with urllib.request.urlopen(url + "/alugar", data=dados, timeout=5) as r:
                self.assertIn("R$ 240.00", r.read().decode("utf-8"))
        finally:
            srv.shutdown()
            srv.server_close()


if __name__ == "__main__":
    unittest.main()
