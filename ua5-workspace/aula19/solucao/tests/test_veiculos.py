"""Testes de OCP, LSP e Factory Method."""

import unittest

from locadora.fabrica import criar_veiculo, tipos_conhecidos
from locadora.veiculos import Caminhao, Carro, Moto, Veiculo


class TestContratoLSP(unittest.TestCase):
    """LSP: QUALQUER subclasse de Veiculo deve passar neste mesmo teste."""

    SUBCLASSES = [Carro, Moto, Caminhao]

    def test_toda_subclasse_cumpre_o_contrato(self):
        for classe in self.SUBCLASSES:
            with self.subTest(classe=classe.__name__):
                v = classe("AAA-0001", "Modelo", 100)
                self.assertTrue(v.disponivel)
                for dias in (1, 2, 7, 30):
                    preco = v.preco_base(dias)
                    self.assertGreaterEqual(preco, 0)
                # preço cresce com os dias (ninguém "inverte" a regra)
                self.assertLess(v.preco_base(1), v.preco_base(2))
                v.marcar_alugado()
                self.assertFalse(v.disponivel)

    def test_todas_as_subclasses_concretas_estao_na_lista(self):
        concretas = {c for c in Veiculo.__subclasses__()}
        self.assertEqual(concretas, set(self.SUBCLASSES))


class TestFabrica(unittest.TestCase):
    def test_cria_cada_tipo(self):
        self.assertIsInstance(criar_veiculo("carro", "A", "M", 10), Carro)
        self.assertIsInstance(criar_veiculo("moto", "B", "M", 10), Moto)
        self.assertIsInstance(criar_veiculo("caminhao", "C", "M", 10), Caminhao)

    def test_tipo_desconhecido(self):
        with self.assertRaises(ValueError):
            criar_veiculo("navio", "D", "M", 10)

    def test_tipos_conhecidos(self):
        self.assertEqual(tipos_conhecidos(), ["caminhao", "carro", "moto"])


class TestCaminhaoOCP(unittest.TestCase):
    def test_caminhao_tem_seguro_e_taxa_fixa(self):
        self.assertAlmostEqual(Caminhao("C", "M", 300).preco_base(2), 710.0)


if __name__ == "__main__":
    unittest.main()
