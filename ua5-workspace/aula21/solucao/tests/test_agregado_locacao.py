"""
Testes no formato DADOS estes eventos / QUANDO este comando / ENTÃO estes eventos.
O agregado é testado sem banco, sem frota e sem barramento.
"""

import unittest
from datetime import date

from locadora.compartilhado.valor import Dinheiro
from locadora.locacoes.dominio.erros import DataInvalida, LocacaoJaEncerrada
from locadora.locacoes.dominio.eventos import (LocacaoIniciada, MultaPorAtrasoAplicada,
                                               VeiculoDevolvido)
from locadora.locacoes.dominio.locacao import Locacao

INICIADA = LocacaoIniciada("L-1", "ABC-1234", "Ana", "2026-11-02", 5, 66000)


class TestLocacao(unittest.TestCase):
    def test_iniciar_gera_evento(self):
        loc = Locacao.iniciar("L-1", "abc-1234", "Ana", date(2026, 11, 2), 5, Dinheiro(66000))
        self.assertEqual(loc.pendentes, [INICIADA])

    def test_devolucao_no_prazo_nao_gera_multa(self):
        dados = Locacao.reconstituir("L-1", [INICIADA])
        dados.registrar_devolucao(date(2026, 11, 7))
        self.assertEqual(dados.pendentes, [VeiculoDevolvido("L-1", "ABC-1234", "2026-11-07", 5)])

    def test_devolucao_atrasada_gera_multa(self):
        loc = Locacao.reconstituir("L-1", [INICIADA])
        loc.registrar_devolucao(date(2026, 11, 9))           # 7 dias usados, 2 de atraso
        # diária média 132,00 × 1,5 × 2 = 396,00
        self.assertEqual(loc.pendentes[-1], MultaPorAtrasoAplicada("L-1", 2, 39600))
        self.assertEqual(loc.valor_total, Dinheiro(105600))

    def test_nao_devolve_duas_vezes(self):
        loc = Locacao.reconstituir("L-1", [INICIADA, VeiculoDevolvido("L-1", "ABC-1234", "2026-11-07", 5)])
        with self.assertRaises(LocacaoJaEncerrada):
            loc.registrar_devolucao(date(2026, 11, 8))

    def test_invariantes_de_data(self):
        with self.assertRaises(DataInvalida):
            Locacao.iniciar("L-2", "ABC-1234", "Ana", date(2026, 11, 2), 0, Dinheiro(1))
        loc = Locacao.reconstituir("L-1", [INICIADA])
        with self.assertRaises(DataInvalida):
            loc.registrar_devolucao(date(2026, 11, 1))

    def test_reconstituir_chega_ao_mesmo_estado(self):
        original = Locacao.iniciar("L-1", "ABC-1234", "Ana", date(2026, 11, 2), 5, Dinheiro(66000))
        original.registrar_devolucao(date(2026, 11, 9))
        copia = Locacao.reconstituir("L-1", original.pendentes)
        self.assertEqual((copia.encerrada, copia.valor_total, copia.versao),
                         (original.encerrada, original.valor_total, original.versao))


if __name__ == "__main__":
    unittest.main()
