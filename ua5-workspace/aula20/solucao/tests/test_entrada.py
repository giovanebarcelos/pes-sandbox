"""Adaptadores de entrada (terminal e web) continuam funcionando sobre o hexágono."""

import unittest

from locadora.adaptadores.entrada.cli import ControladorConsole, VisaoConsole
from locadora.adaptadores.entrada.web import ControladorWeb
from locadora.config import montar


class TestEntradas(unittest.TestCase):
    def test_cli_com_sqlite(self):
        c = ControladorConsole(montar("sqlite", ":memory:", silencioso=True), VisaoConsole())
        self.assertIn("R$ 660.00", c.executar("alugar ABC-1234 5 Ana a@x"))
        self.assertIn("alugado", c.executar("listar"))

    def test_web_com_memoria(self):
        c = ControladorWeb(montar("memoria", silencioso=True))
        status, pagina = c.tratar("POST", "/alugar", dict(placa="XYZ-9012", dias="3",
                                                          cliente="Bia", email="b@x"))
        self.assertEqual((status, "R$ 240.00" in pagina), (200, True))


if __name__ == "__main__":
    unittest.main()
