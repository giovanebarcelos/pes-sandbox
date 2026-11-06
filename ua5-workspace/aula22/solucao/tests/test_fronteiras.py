"""Os bounded contexts não se enxergam por dentro (verificado no código-fonte)."""

import ast
import pathlib
import unittest

RAIZ = pathlib.Path(__file__).resolve().parent.parent / "locadora"


def imports_de(pasta):
    resultado = []
    for arq in sorted((RAIZ / pasta).rglob("*.py")):
        pacote = ["locadora", *arq.relative_to(RAIZ).parent.parts]
        for no in ast.walk(ast.parse(arq.read_text(encoding="utf-8"))):
            if isinstance(no, ast.Import):
                resultado += [(str(arq.relative_to(RAIZ)), a.name) for a in no.names]
            elif isinstance(no, ast.ImportFrom):
                if no.level:
                    base = pacote[: len(pacote) - no.level + 1]
                    nome = ".".join(base + ([no.module] if no.module else []))
                else:
                    nome = no.module
                resultado.append((str(arq.relative_to(RAIZ)), nome))
    return resultado


class TestFronteiras(unittest.TestCase):
    def test_locacoes_nao_importa_frota(self):
        for arq, mod in imports_de("locacoes"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertFalse(mod.startswith("locadora.frota"))

    def test_frota_nao_importa_locacoes(self):
        for arq, mod in imports_de("frota"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertFalse(mod.startswith("locadora.locacoes"))

    def test_pagamentos_e_um_modulo_independente(self):
        """Monolito modular (Aula 22): o microkernel de pagamentos não conhece os outros módulos."""
        for arq, mod in imports_de("pagamentos"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertFalse(mod.startswith(("locadora.frota", "locadora.locacoes")))

    def test_dominio_de_locacoes_sem_infraestrutura(self):
        for arq, mod in imports_de("locacoes/dominio"):
            with self.subTest(arquivo=arq, importa=mod):
                self.assertNotIn(mod.split(".")[0], {"json", "sqlite3", "http", "urllib", "os"})
                self.assertFalse(mod.startswith(("locadora.locacoes.adaptadores",
                                                 "locadora.locacoes.aplicacao")))


if __name__ == "__main__":
    unittest.main()
