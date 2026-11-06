"""Atividade do Slide 33: trocar o adaptador SEM alterar LocadoraService."""

import os
import tempfile
import unittest

from locadora.config import montar


class TestTrocaAdaptador(unittest.TestCase):
    def roteiro(self, servico):
        servico.alugar("ABC-1234", "Ana", "a@x", 5)
        servico.alugar("XYZ-9012", "Bia", "b@x", 7)
        return ([v.placa for v in servico.listar_disponiveis()], servico.faturamento_total())

    def test_mesmo_resultado_em_memoria_e_sqlite(self):
        r_memoria = self.roteiro(montar("memoria", silencioso=True))
        r_sqlite = self.roteiro(montar("sqlite", ":memory:", silencioso=True))
        self.assertEqual(r_memoria, r_sqlite)
        self.assertEqual(r_memoria, (["DEF-5678", "CAM-0001"], 1164.0))

    def test_sqlite_em_arquivo_persiste_entre_execucoes(self):
        with tempfile.TemporaryDirectory() as pasta:
            caminho = os.path.join(pasta, "locadora.db")
            montar("sqlite", caminho, silencioso=True).alugar("ABC-1234", "Ana", "a@x", 5)
            reaberto = montar("sqlite", caminho, silencioso=True)
            self.assertFalse(reaberto.listar()[0].disponivel)
            self.assertEqual(reaberto.faturamento_total(), 660.0)
            reaberto._veiculos._db.conexao.close()

    def test_adaptador_desconhecido(self):
        with self.assertRaises(ValueError):
            montar("mongodb")


if __name__ == "__main__":
    unittest.main()
