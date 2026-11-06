"""
Cria a sua cópia de trabalho de uma aula:  aulaXX/inicio  ->  aulaXX/meu

Uso (na pasta ua5-workspace/):
  python ferramentas/preparar.py 18                 # começa do ponto de partida oficial
  python ferramentas/preparar.py 19 --da-minha      # começa da SUA aula18/meu
  python ferramentas/preparar.py 18 --forcar        # apaga aula18/meu e recria

Funciona igual em Windows, Linux e macOS (só biblioteca padrão).
"""

import argparse
import pathlib
import shutil
import sys

RAIZ = pathlib.Path(__file__).resolve().parent.parent
AULAS = (18, 19, 20, 21, 22)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("aula", type=int, choices=AULAS)
    p.add_argument("--da-minha", action="store_true",
                   help="copia a sua pasta 'meu' da aula anterior em vez de 'inicio'")
    p.add_argument("--forcar", action="store_true", help="apaga a pasta 'meu' existente")
    a = p.parse_args()

    destino = RAIZ / f"aula{a.aula}" / "meu"
    if a.da_minha:
        if a.aula == AULAS[0]:
            sys.exit("A Aula 18 é a primeira: use sem --da-minha.")
        origem = RAIZ / f"aula{a.aula - 1}" / "meu"
    else:
        origem = RAIZ / f"aula{a.aula}" / "inicio"
    if not origem.is_dir():
        sys.exit(f"Origem não encontrada: {origem}")
    if destino.exists():
        if not a.forcar:
            sys.exit(f"{destino} já existe. Use --forcar para recriar (o conteúdo atual será apagado).")
        shutil.rmtree(destino)
    shutil.copytree(origem, destino, ignore=shutil.ignore_patterns("__pycache__", "dados", "*.db",
                                                                    "*.jsonl", "frota.json"))
    print(f"Pronto: {origem.relative_to(RAIZ)}  ->  {destino.relative_to(RAIZ)}")
    print(f"Agora:  cd {destino.relative_to(RAIZ)}  e  python -m unittest -v")


if __name__ == "__main__":
    main()
