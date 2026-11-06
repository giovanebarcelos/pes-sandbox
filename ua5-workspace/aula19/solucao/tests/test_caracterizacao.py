"""
Os MESMOS cenários de aula18/inicio/tests/test_caracterizacao.py, agora contra o
código refatorado. Se estes passam, a refatoração preservou o comportamento.
"""

import unittest

from locadora.fabrica import criar_veiculo
from locadora.notificacao import NotificadorMemoria
from locadora.precos import DescontoSemanal
from locadora.relatorio import relatorio_frota
from locadora.repositorio import RepositorioMemoria
from locadora.servico import ServicoLocacao


class TestComportamentoPreservado(unittest.TestCase):
    def setUp(self):
        self.notificador = NotificadorMemoria()
        self.servico = ServicoLocacao(RepositorioMemoria(), self.notificador, DescontoSemanal())
        self.servico.cadastrar(criar_veiculo("carro", "ABC-1234", "Fiat Uno", 120))
        self.servico.cadastrar(criar_veiculo("moto", "XYZ-9012", "Honda CG 160", 80))

    def valor(self, placa, dias):
        return self.servico.alugar(placa, "Cliente", "c@x.com", dias).valor

    def test_carro_5_dias_tem_10_por_cento_de_seguro(self):
        self.assertEqual(self.valor("ABC-1234", 5), 660.00)

    def test_moto_3_dias_sem_seguro(self):
        self.assertEqual(self.valor("XYZ-9012", 3), 240.00)

    def test_7_dias_ou_mais_tem_10_por_cento_de_desconto(self):
        self.assertEqual(self.valor("XYZ-9012", 7), 504.00)
        self.assertEqual(self.valor("ABC-1234", 10), 1188.00)

    def test_veiculo_alugado_fica_indisponivel(self):
        self.valor("ABC-1234", 2)
        with self.assertRaises(RuntimeError):
            self.valor("ABC-1234", 2)

    def test_devolver_libera_o_veiculo(self):
        self.valor("ABC-1234", 2)
        self.servico.devolver("ABC-1234")
        self.assertEqual(self.valor("ABC-1234", 1), 132.00)

    def test_placa_inexistente(self):
        with self.assertRaises(KeyError):
            self.valor("ZZZ-0000", 1)

    def test_envia_notificacao_de_confirmacao(self):
        self.servico.alugar("ABC-1234", "Ana", "a@x.com", 5)
        destino, mensagem = self.notificador.enviadas[-1]
        self.assertEqual(destino, "a@x.com")
        self.assertIn("R$ 660.00", mensagem)

    def test_relatorio_mostra_faturamento(self):
        self.valor("ABC-1234", 5)
        self.valor("XYZ-9012", 3)
        self.assertIn("Faturamento: R$ 900.00", relatorio_frota(self.servico))


if __name__ == "__main__":
    unittest.main()
