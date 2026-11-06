"""Objetos de valor: imutáveis e comparados pelos atributos."""

import dataclasses
import unittest

from locadora.compartilhado.valor import Dinheiro, Placa


class TestObjetosDeValor(unittest.TestCase):
    def test_placa_normaliza_e_valida(self):
        self.assertEqual(Placa(" abc-1234 "), Placa("ABC-1234"))
        self.assertEqual(str(Placa("bra2e19")), "BRA2E19")       # padrão Mercosul
        with self.assertRaises(ValueError):
            Placa("12-ABCD")

    def test_dinheiro_sem_erro_de_ponto_flutuante(self):
        self.assertEqual(Dinheiro.de_reais(0.1) + Dinheiro.de_reais(0.2), Dinheiro.de_reais(0.3))
        self.assertEqual(str(Dinheiro(66000)), "R$ 660.00")

    def test_imutavel(self):
        with self.assertRaises(dataclasses.FrozenInstanceError):
            Dinheiro(1).centavos = 2


if __name__ == "__main__":
    unittest.main()
