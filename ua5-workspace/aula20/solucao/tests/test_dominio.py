"""Domínio puro: nenhum banco, nenhum adaptador, nenhum serviço."""

import unittest

from locadora.dominio.fabrica import criar_veiculo
from locadora.dominio.precos import DescontoSemanal
from locadora.dominio.veiculos import Caminhao, Carro, Moto


class TestDominio(unittest.TestCase):
    def test_precos_base(self):
        self.assertAlmostEqual(Carro("A", "M", 120).preco_base(5), 660.0)
        self.assertAlmostEqual(Moto("B", "M", 80).preco_base(3), 240.0)
        self.assertAlmostEqual(Caminhao("C", "M", 300).preco_base(2), 710.0)

    def test_desconto_semanal(self):
        self.assertEqual(DescontoSemanal().aplicar(560.0, 7), 504.0)
        self.assertEqual(DescontoSemanal().aplicar(480.0, 6), 480.0)

    def test_fabrica(self):
        self.assertIsInstance(criar_veiculo("moto", "X", "CG", 80), Moto)


if __name__ == "__main__":
    unittest.main()
