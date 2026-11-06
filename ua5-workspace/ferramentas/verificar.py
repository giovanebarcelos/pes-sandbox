"""
Confere o ambiente e roda os testes das aulas.

Uso (na pasta ua5-workspace/):
  python ferramentas/verificar.py                  # todas as aulas, pastas inicio e solucao
  python ferramentas/verificar.py 20               # só a Aula 20
  python ferramentas/verificar.py 20 --pasta meu   # a SUA versão da Aula 20
"""

import argparse
import os
import pathlib
import subprocess
import sys

RAIZ = pathlib.Path(__file__).resolve().parent.parent
AULAS = (18, 19, 20, 21, 22)


def main():
    p = argparse.ArgumentParser()
    p.add_argument("aula", type=int, nargs="?", choices=AULAS)
    p.add_argument("--pasta", choices=("inicio", "solucao", "meu"))
    a = p.parse_args()

    if sys.version_info < (3, 10):
        sys.exit(f"Python 3.10 ou superior é necessário (você tem {sys.version.split()[0]}).")
    print(f"Python {sys.version.split()[0]} OK  ({sys.executable})\n")

    aulas = [a.aula] if a.aula else AULAS
    pastas = [a.pasta] if a.pasta else ("inicio", "solucao")
    falhas = 0
    for aula in aulas:
        for pasta in pastas:
            alvo = RAIZ / f"aula{aula}" / pasta
            if not alvo.is_dir():
                print(f"  aula{aula}/{pasta:<8} (não existe — rode ferramentas/preparar.py {aula})")
                continue
            env = dict(os.environ, PYTHONDONTWRITEBYTECODE="1")
            r = subprocess.run([sys.executable, "-m", "unittest", "-q"], cwd=alvo, env=env,
                               capture_output=True, text=True)
            resumo = [l for l in r.stderr.splitlines() if l.startswith(("Ran ", "OK", "FAILED"))]
            print(f"  aula{aula}/{pasta:<8} {'OK   ' if r.returncode == 0 else 'FALHA'} {' | '.join(resumo)}")
            if r.returncode:
                falhas += 1
                print("    " + "\n    ".join(r.stderr.splitlines()[-15:]))
    sys.exit(1 if falhas else 0)


if __name__ == "__main__":
    main()
