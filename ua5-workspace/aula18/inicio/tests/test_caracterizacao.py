"""
Testes de CARACTERIZAÇÃO do código legado.

Eles não dizem se o código é "bom" — apenas registram o que ele FAZ hoje.
Antes de refatorar, rode-os (devem passar). Depois de cada passo de
refatoração, rode-os de novo: se algum falhar, você mudou o comportamento.

Executar (na pasta da aula):  python -m unittest -v
"""

import os
import tempfile
import unittest

from locadora_legado import Locadora


class TestCaracterizacaoLegado(unittest.TestCase):
    def setUp(self):
        self.pasta = tempfile.TemporaryDirectory()
        arquivo = os.path.join(self.pasta.name, "frota.json")
        self.loc = Locadora(arquivo)
        self.loc.cadastrar("carro", "ABC-1234", "Fiat Uno", 120)
        self.loc.cadastrar("moto", "XYZ-9012", "Honda CG 160", 80)

    def tearDown(self):
        self.pasta.cleanup()

    def test_carro_5_dias_tem_10_por_cento_de_seguro(self):
        self.assertEqual(self.loc.alugar("ABC-1234", "Ana", "a@x.com", 5), 660.00)

    def test_moto_3_dias_sem_seguro(self):
        self.assertEqual(self.loc.alugar("XYZ-9012", "Bia", "b@x.com", 3), 240.00)

    def test_7_dias_ou_mais_tem_10_por_cento_de_desconto(self):
        self.assertEqual(self.loc.alugar("XYZ-9012", "Bia", "b@x.com", 7), 504.00)
        self.assertEqual(self.loc.alugar("ABC-1234", "Ana", "a@x.com", 10), 1188.00)

    def test_veiculo_alugado_fica_indisponivel(self):
        self.loc.alugar("ABC-1234", "Ana", "a@x.com", 2)
        with self.assertRaises(RuntimeError):
            self.loc.alugar("ABC-1234", "Outro", "o@x.com", 2)

    def test_devolver_libera_o_veiculo(self):
        self.loc.alugar("ABC-1234", "Ana", "a@x.com", 2)
        self.loc.devolver("ABC-1234")
        self.assertEqual(self.loc.alugar("ABC-1234", "Outro", "o@x.com", 1), 132.00)

    def test_placa_inexistente(self):
        with self.assertRaises(KeyError):
            self.loc.alugar("ZZZ-0000", "Ana", "a@x.com", 1)

    def test_envia_email_de_confirmacao(self):
        self.loc.alugar("ABC-1234", "Ana", "a@x.com", 5)
        destino, mensagem = self.loc.emails_enviados[-1]
        self.assertEqual(destino, "a@x.com")
        self.assertIn("R$ 660.00", mensagem)

    def test_relatorio_mostra_faturamento(self):
        self.loc.alugar("ABC-1234", "Ana", "a@x.com", 5)
        self.loc.alugar("XYZ-9012", "Bia", "b@x.com", 3)
        self.assertIn("Faturamento: R$ 900.00", self.loc.relatorio())


if __name__ == "__main__":
    unittest.main()
