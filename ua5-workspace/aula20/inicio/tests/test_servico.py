"""DIP na prática: o serviço é testado com fakes, sem arquivo e sem SMTP."""

import os
import tempfile
import unittest

from locadora.erros import VeiculoIndisponivel, VeiculoNaoEncontrado
from locadora.fabrica import criar_veiculo
from locadora.notificacao import NotificadorMemoria
from locadora.precos import DescontoSemanal, SemDesconto
from locadora.repositorio import RepositorioJson, RepositorioMemoria
from locadora.servico import ServicoLocacao


class TestServico(unittest.TestCase):
    def test_politica_de_preco_e_trocavel(self):
        for politica, esperado in [(SemDesconto(), 560.0), (DescontoSemanal(), 504.0)]:
            with self.subTest(politica=type(politica).__name__):
                s = ServicoLocacao(RepositorioMemoria(), NotificadorMemoria(), politica)
                s.cadastrar(criar_veiculo("moto", "M-1", "CG", 80))
                self.assertEqual(s.cotar("M-1", 7), esperado)

    def test_erros_de_negocio_tem_nome(self):
        s = ServicoLocacao(RepositorioMemoria(), NotificadorMemoria())
        s.cadastrar(criar_veiculo("moto", "M-1", "CG", 80))
        with self.assertRaises(VeiculoNaoEncontrado):
            s.alugar("NAO-EXISTE", "Ana", "a@x", 1)
        s.alugar("M-1", "Ana", "a@x", 1)
        with self.assertRaises(VeiculoIndisponivel):
            s.alugar("M-1", "Bia", "b@x", 1)


class TestRepositorioJson(unittest.TestCase):
    def test_grava_e_rele_o_arquivo(self):
        with tempfile.TemporaryDirectory() as pasta:
            arq = os.path.join(pasta, "frota.json")
            s = ServicoLocacao(RepositorioJson(arq), NotificadorMemoria())
            s.cadastrar(criar_veiculo("carro", "ABC-1234", "Uno", 120))
            s.alugar("ABC-1234", "Ana", "a@x", 2)
            relido = RepositorioJson(arq).buscar("ABC-1234")
            self.assertEqual(relido.modelo, "Uno")
            self.assertFalse(relido.disponivel)


if __name__ == "__main__":
    unittest.main()
