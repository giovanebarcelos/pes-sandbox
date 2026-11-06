"""
Adaptadores SQL — SQLite REAL (vem com o Python, não precisa instalar nada).

Este é o ÚNICO lugar do sistema que conhece SQL. Use ":memory:" para um banco
temporário ou um caminho de arquivo (ex.: dados/locadora.db) para persistir.
"""

import os
import sqlite3
import threading

from ...aplicacao.portas import LocacaoRepository, VeiculoRepository
from ...dominio.fabrica import criar_veiculo
from ...dominio.locacao import Locacao

_ESQUEMA = """
CREATE TABLE IF NOT EXISTS veiculos (
    placa TEXT PRIMARY KEY, tipo TEXT NOT NULL, modelo TEXT NOT NULL,
    diaria REAL NOT NULL, disponivel INTEGER NOT NULL);
CREATE TABLE IF NOT EXISTS locacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT, placa TEXT NOT NULL,
    cliente TEXT NOT NULL, dias INTEGER NOT NULL, valor REAL NOT NULL);
"""


class BancoSqlite:
    """Uma conexão compartilhada pelos dois repositórios (com trava para a web)."""

    def __init__(self, caminho=":memory:"):
        if caminho != ":memory:":
            os.makedirs(os.path.dirname(caminho) or ".", exist_ok=True)
        self.conexao = sqlite3.connect(caminho, check_same_thread=False)
        self.trava = threading.Lock()
        with self.trava:
            self.conexao.executescript(_ESQUEMA)

    def executar(self, sql, parametros=()):
        with self.trava, self.conexao:
            return self.conexao.execute(sql, parametros).fetchall()


class VeiculoSQLRepository(VeiculoRepository):
    def __init__(self, banco):
        self._db = banco

    def salvar(self, veiculo):
        self._db.executar(
            "INSERT INTO veiculos (placa, tipo, modelo, diaria, disponivel) VALUES (?,?,?,?,?) "
            "ON CONFLICT(placa) DO UPDATE SET disponivel = excluded.disponivel",
            (veiculo.placa, veiculo.tipo, veiculo.modelo, veiculo.valor_diaria, int(veiculo.disponivel)))

    def buscar_por_placa(self, placa):
        linhas = self._db.executar(
            "SELECT tipo, placa, modelo, diaria, disponivel FROM veiculos WHERE placa = ?", (placa,))
        return self._para_veiculo(linhas[0]) if linhas else None

    def listar(self):
        return [self._para_veiculo(l) for l in self._db.executar(
            "SELECT tipo, placa, modelo, diaria, disponivel FROM veiculos ORDER BY rowid")]

    @staticmethod
    def _para_veiculo(linha):
        tipo, placa, modelo, diaria, disponivel = linha
        v = criar_veiculo(tipo, placa, modelo, diaria)
        if not disponivel:
            v.marcar_alugado()
        return v


class LocacaoSQLRepository(LocacaoRepository):
    def __init__(self, banco):
        self._db = banco

    def adicionar(self, locacao):
        self._db.executar("INSERT INTO locacoes (placa, cliente, dias, valor) VALUES (?,?,?,?)",
                          (locacao.placa, locacao.cliente, locacao.dias, locacao.valor))

    def listar(self):
        return [Locacao(*l) for l in self._db.executar(
            "SELECT placa, cliente, dias, valor FROM locacoes ORDER BY id")]
