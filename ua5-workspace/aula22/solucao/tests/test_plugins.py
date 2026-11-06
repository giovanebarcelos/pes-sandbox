"""Microkernel: o núcleo descobre os plugins sozinho e aceita plugins novos sem mudar."""

import unittest

from locadora.pagamentos.nucleo import PluginPagamento, SistemaPagamentos


class TestMicrokernel(unittest.TestCase):
    def setUp(self):
        self.sistema = SistemaPagamentos().descobrir()

    def test_descobre_plugins_da_pasta(self):
        self.assertIn("pix", self.sistema.formas())
        self.assertIn("cartao", self.sistema.formas())

    def test_cada_plugin_aplica_sua_taxa(self):
        self.assertEqual(self.sistema.pagar("pix", 660.0, "L-1")[0], 660.0)
        self.assertEqual(self.sistema.pagar("cartao", 660.0, "L-1")[0], 679.8)

    def test_forma_nao_suportada(self):
        with self.assertRaises(ValueError):
            self.sistema.pagar("cheque", 10.0, "L-1")

    def test_plugin_registrado_em_tempo_de_execucao(self):
        class Cripto(PluginPagamento):
            nome = "cripto"

            def taxa(self, valor):
                return 1.0

            def pagar(self, valor, referencia):
                return "ok"

        self.sistema.registrar(Cripto())
        self.assertEqual(self.sistema.pagar("cripto", 10.0, "L-1"), (11.0, "ok"))

    def test_rejeita_objeto_fora_do_contrato(self):
        with self.assertRaises(TypeError):
            self.sistema.registrar(object())


if __name__ == "__main__":
    unittest.main()
