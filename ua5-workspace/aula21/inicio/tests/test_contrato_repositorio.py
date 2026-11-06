"""
Teste de CONTRATO da porta VeiculoRepository/LocacaoRepository.

A mesma bateria roda contra os dois adaptadores. Se um adaptador novo
(ex.: PostgreSQL) passar aqui, ele pode substituir os outros (LSP aplicado
à arquitetura).
"""

import unittest

from locadora.adaptadores.saida.memoria import LocacaoMemoriaRepository, VeiculoMemoriaRepository
from locadora.adaptadores.saida.sqlite import BancoSqlite, LocacaoSQLRepository, VeiculoSQLRepository
from locadora.dominio.fabrica import criar_veiculo
from locadora.dominio.locacao import Locacao


class ContratoRepositorio:
    """Mixin: as subclasses só dizem COMO criar os repositórios."""

    def criar(self):
        raise NotImplementedError

    def setUp(self):
        self.veiculos, self.locacoes = self.criar()

    def test_salvar_e_buscar(self):
        self.veiculos.salvar(criar_veiculo("carro", "ABC-1234", "Uno", 120))
        v = self.veiculos.buscar_por_placa("ABC-1234")
        self.assertEqual((v.placa, v.modelo, v.valor_diaria, v.tipo), ("ABC-1234", "Uno", 120.0, "carro"))

    def test_buscar_inexistente_devolve_none(self):
        self.assertIsNone(self.veiculos.buscar_por_placa("NAO-0000"))

    def test_salvar_de_novo_atualiza_disponibilidade(self):
        v = criar_veiculo("moto", "XYZ-9012", "CG", 80)
        self.veiculos.salvar(v)
        v.marcar_alugado()
        self.veiculos.salvar(v)
        self.assertFalse(self.veiculos.buscar_por_placa("XYZ-9012").disponivel)
        self.assertEqual(len(self.veiculos.listar()), 1)

    def test_locacoes(self):
        self.locacoes.adicionar(Locacao("ABC-1234", "Ana", 5, 660.0))
        self.assertEqual(self.locacoes.listar(), [Locacao("ABC-1234", "Ana", 5, 660.0)])


class TestMemoria(ContratoRepositorio, unittest.TestCase):
    def criar(self):
        return VeiculoMemoriaRepository(), LocacaoMemoriaRepository()


class TestSQLite(ContratoRepositorio, unittest.TestCase):
    def criar(self):
        banco = BancoSqlite(":memory:")
        return VeiculoSQLRepository(banco), LocacaoSQLRepository(banco)


if __name__ == "__main__":
    unittest.main()
