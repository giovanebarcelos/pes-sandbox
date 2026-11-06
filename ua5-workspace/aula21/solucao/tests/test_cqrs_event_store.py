"""Lado de escrita, lado de leitura, event store e integração entre contextos."""

import os
import tempfile
import unittest
from datetime import date

from locadora.config import montar
from locadora.frota.dominio.erros import VeiculoIndisponivel
from locadora.locacoes.adaptadores.event_store import EventStoreArquivo, EventStoreMemoria
from locadora.locacoes.aplicacao.comandos import IniciarLocacao, RegistrarDevolucao
from locadora.locacoes.dominio.erros import ConflitoDeConcorrencia
from locadora.locacoes.dominio.eventos import LocacaoIniciada

D = lambda dia: date(2026, 11, dia)


class TestFluxoCompleto(unittest.TestCase):
    def setUp(self):
        self.app = montar()
        self.app.comandos.executar(IniciarLocacao("L-1", "ABC-1234", "Ana", D(2), 5))

    def disponivel(self, placa):
        return {v["placa"]: v["disponivel"] for v in self.app.frota.listar()}[placa]

    def test_iniciar_reserva_o_veiculo_na_frota(self):
        self.assertFalse(self.disponivel("ABC-1234"))
        with self.assertRaises(VeiculoIndisponivel):
            self.app.comandos.executar(IniciarLocacao("L-2", "ABC-1234", "Bia", D(3), 1))

    def test_devolucao_libera_frota_por_evento(self):
        self.app.comandos.executar(RegistrarDevolucao("L-1", D(7)))
        self.assertTrue(self.disponivel("ABC-1234"))

    def test_projecoes_respondem_consultas(self):
        self.app.comandos.executar(IniciarLocacao("L-2", "XYZ-9012", "Bia", D(3), 7))
        self.assertEqual(set(self.app.consultas.locacoes_ativas()), {"L-1", "L-2"})
        self.assertEqual(self.app.consultas.faturamento_do_mes("2026-11"), 1164.0)

    def test_reconstruir_projecoes_da_o_mesmo_resultado(self):
        self.app.comandos.executar(RegistrarDevolucao("L-1", D(9)))
        antes = (self.app.consultas.locacoes_ativas(), self.app.consultas.faturamento_do_mes("2026-11"),
                 self.app.consultas.mais_alugados("2026-11"))
        self.app.reconstruir_projecoes()
        depois = (self.app.consultas.locacoes_ativas(), self.app.consultas.faturamento_do_mes("2026-11"),
                  self.app.consultas.mais_alugados("2026-11"))
        self.assertEqual(antes, depois)


class TestEventStore(unittest.TestCase):
    def test_concorrencia_otimista(self):
        store = EventStoreMemoria()
        e = LocacaoIniciada("L-1", "ABC-1234", "Ana", "2026-11-02", 5, 66000)
        store.anexar("L-1", [e], versao_esperada=0)
        with self.assertRaises(ConflitoDeConcorrencia):
            store.anexar("L-1", [e], versao_esperada=0)

    def test_arquivo_sobrevive_a_reinicio(self):
        with tempfile.TemporaryDirectory() as pasta:
            caminho = os.path.join(pasta, "eventos.jsonl")
            app = montar(caminho)
            app.comandos.executar(IniciarLocacao("L-1", "ABC-1234", "Ana", D(2), 5))
            reiniciado = montar(caminho)
            self.assertEqual(reiniciado.store.todos(), app.store.todos())
            self.assertIn("L-1", reiniciado.consultas.locacoes_ativas())
            with open(caminho, encoding="utf-8") as f:
                self.assertEqual(len(f.readlines()), 1)


if __name__ == "__main__":
    unittest.main()
