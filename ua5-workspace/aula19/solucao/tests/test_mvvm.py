"""MVVM: testamos o ViewModel mudando propriedades e observando o estado — sem View."""

import unittest

from locadora.apresentacao.montagem import servico_exemplo
from locadora.apresentacao.mvvm import LocadoraViewModel, Observavel


class TestObservavel(unittest.TestCase):
    def test_avisa_inscritos_so_quando_muda(self):
        o, recebidos = Observavel(1), []
        o.inscrever(recebidos.append)
        o.valor = 2
        o.valor = 2
        self.assertEqual(recebidos, [1, 2])


class TestViewModel(unittest.TestCase):
    def setUp(self):
        self.vm = LocadoraViewModel(servico_exemplo(silencioso=True))

    def test_cotacao_reage_a_digitacao(self):
        self.vm.placa.valor = "XYZ-9012"
        self.vm.dias.valor = 7
        self.assertEqual(self.vm.cotacao.valor, "R$ 504.00")
        self.vm.placa.valor = "NAO-EXISTE"
        self.assertEqual(self.vm.cotacao.valor, "—")

    def test_botao_so_habilita_com_campos_validos(self):
        self.vm.placa.valor = "ABC-1234"
        self.assertFalse(self.vm.pode_alugar.valor)
        self.vm.cliente.valor = "Ana"
        self.vm.email.valor = "a@x.com"
        self.assertTrue(self.vm.pode_alugar.valor)

    def test_comando_alugar_atualiza_frota_e_mensagem(self):
        self.vm.placa.valor, self.vm.dias.valor = "ABC-1234", 5
        self.vm.cliente.valor, self.vm.email.valor = "Ana", "a@x.com"
        self.vm.comando_alugar()
        self.assertEqual(self.vm.mensagem.valor, "OK: ABC-1234 alugado — R$ 660.00")
        self.assertIn("ABC-1234 Fiat Uno (alugado)", self.vm.frota.valor)


if __name__ == "__main__":
    unittest.main()
