"""MVC: o Controller é testado passando linhas de comando — sem input() real."""

import unittest

from locadora.apresentacao.montagem import servico_exemplo
from locadora.apresentacao.mvc_console import ControladorConsole, VisaoConsole


class TestControladorConsole(unittest.TestCase):
    def setUp(self):
        self.c = ControladorConsole(servico_exemplo(silencioso=True), VisaoConsole())

    def test_listar(self):
        self.assertIn("ABC-1234", self.c.executar("listar"))

    def test_cotar(self):
        self.assertEqual(self.c.executar("cotar XYZ-9012 7"), "Cotação XYZ-9012 por 7 dia(s): R$ 504.00")

    def test_alugar_e_erro_de_negocio(self):
        self.assertIn("R$ 660.00", self.c.executar("alugar ABC-1234 5 Ana ana@x.com"))
        self.assertTrue(self.c.executar("alugar ABC-1234 5 Bia bia@x.com").startswith("ERRO"))

    def test_comando_invalido_mostra_ajuda(self):
        self.assertTrue(self.c.executar("voar").startswith("Comandos"))


if __name__ == "__main__":
    unittest.main()
