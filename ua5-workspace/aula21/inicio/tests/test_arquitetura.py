"""
Teste de ARQUITETURA — a Regra de Dependência vira teste automatizado.

Lê o código-fonte (sem executá-lo) e verifica os imports de cada camada:
  dominio    -> só biblioteca padrão "pura" e o próprio dominio
  aplicacao  -> dominio e aplicacao (nunca adaptadores)
Pasta não é camada: se alguém colocar `import sqlite3` no domínio, este teste falha.
"""

import ast
import pathlib
import unittest

RAIZ = pathlib.Path(__file__).resolve().parent.parent / "locadora"
PROIBIDOS_NO_NUCLEO = {"sqlite3", "http", "urllib", "json", "socket", "flask", "django", "requests"}


def imports_de(pasta):
    """Devolve [(arquivo, modulo_absoluto_importado)] de todos os .py da pasta."""
    resultado = []
    for arq in sorted((RAIZ / pasta).rglob("*.py")):
        pacote = ["locadora", *arq.relative_to(RAIZ).parent.parts]
        for no in ast.walk(ast.parse(arq.read_text(encoding="utf-8"))):
            if isinstance(no, ast.Import):
                resultado += [(arq.name, a.name) for a in no.names]
            elif isinstance(no, ast.ImportFrom):
                if no.level:  # import relativo -> converte para absoluto
                    base = pacote[: len(pacote) - no.level + 1]
                    nome = ".".join(base + ([no.module] if no.module else []))
                else:
                    nome = no.module
                resultado.append((arq.name, nome))
    return resultado


class TestRegraDeDependencia(unittest.TestCase):
    def test_dominio_nao_conhece_ninguem(self):
        for arq, mod in imports_de("dominio"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertFalse(mod.startswith(("locadora.aplicacao", "locadora.adaptadores",
                                                 "locadora.config")))
                self.assertNotIn(mod.split(".")[0], PROIBIDOS_NO_NUCLEO)

    def test_aplicacao_nao_conhece_adaptadores(self):
        for arq, mod in imports_de("aplicacao"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertFalse(mod.startswith(("locadora.adaptadores", "locadora.config")))
                self.assertNotIn(mod.split(".")[0], PROIBIDOS_NO_NUCLEO)

    def test_o_teste_enxerga_os_imports(self):
        """Sanidade: garante que o analisador realmente encontrou imports."""
        self.assertIn(("servico.py", "locadora.dominio.erros"), imports_de("aplicacao"))


if __name__ == "__main__":
    unittest.main()
