# -*- coding: utf-8 -*-
"""
PES2501 - Roteiro de Comandos Git Executável
Aula 25: Gerência de Configuração e Introdução ao Git
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Executa DE VERDADE a sequência de comandos Git mais usada no dia a dia,
usando repositórios temporários criados exclusivamente para esta
demonstração (nunca o repositório real da disciplina). Cada comando é
disparado via subprocess.run(["git", ...]) contra um repositório real em
/tmp e a saída exibida é a saída REAL do Git -- nada aqui é apenas texto
impresso. Ao final, todos os diretórios temporários são removidos.
"""

import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


def rodar(comando, cwd):
    """Executa um comando Git de verdade (subprocess) e mostra a saída real."""
    print(f"  $ {' '.join(comando)}")
    resultado = subprocess.run(comando, cwd=cwd, capture_output=True, text=True)
    saida = (resultado.stdout + resultado.stderr).strip()
    if saida:
        for linha in saida.splitlines():
            print(f"    {linha}")
    print()
    return resultado


def demo_roteiro_git():
    """Cria um repositório Git real e temporário e roda o roteiro da aula."""
    if shutil.which("git") is None:
        print("Git não encontrado no PATH -- instale o Git para rodar esta demo.")
        sys.exit(1)

    base_dir = Path(tempfile.mkdtemp(prefix="pes25-demo-"))
    repo_dir = base_dir / "meu-projeto"
    origin_dir = base_dir / "origin" / "meu-projeto.git"
    clone_dir = base_dir / "colega" / "meu-projeto"
    repo_dir.mkdir(parents=True)

    try:
        print("=" * 70)
        print("  ROTEIRO DE COMANDOS GIT (execução real em repositório temporário)")
        print(f"  Repositório de trabalho: {repo_dir}")
        print("=" * 70)
        print()

        print("--- INICIALIZAÇÃO ---")
        rodar(["git", "init", "-b", "main"], repo_dir)
        rodar(["git", "config", "user.name", "PES Aula 25"], repo_dir)
        rodar(["git", "config", "user.email", "pes25@exemplo.edu"], repo_dir)

        print("--- STAGING E COMMIT ---")
        (repo_dir / "README.md").write_text("# Meu Projeto\n")
        rodar(["git", "add", "README.md"], repo_dir)
        rodar(["git", "commit", "-m", "docs: adiciona README inicial"], repo_dir)

        (repo_dir / "main.py").write_text('print("Hello, Git!")\n')
        rodar(["git", "add", "."], repo_dir)
        rodar(["git", "commit", "-m", "feat: adiciona script principal"], repo_dir)

        print("--- CONSULTA ---")
        rodar(["git", "status"], repo_dir)
        rodar(["git", "log", "--oneline"], repo_dir)

        (repo_dir / "main.py").write_text('print("Hello, Git!")\nprint("Versão 2")\n')
        rodar(["git", "diff"], repo_dir)

        rodar(["git", "add", "main.py"], repo_dir)
        rodar(["git", "commit", "-m", "feat: adiciona mensagem de versão"], repo_dir)

        print("--- BRANCH ---")
        rodar(["git", "branch", "feature-login"], repo_dir)
        rodar(["git", "checkout", "feature-login"], repo_dir)
        (repo_dir / "login.py").write_text("def login():\n    pass\n")
        rodar(["git", "add", "login.py"], repo_dir)
        rodar(["git", "commit", "-m", "feat: adiciona esqueleto de login"], repo_dir)
        rodar(["git", "checkout", "main"], repo_dir)
        rodar(["git", "merge", "feature-login"], repo_dir)

        print("--- CONFIGURAÇÃO (.gitignore) ---")
        (repo_dir / ".gitignore").write_text("__pycache__/\n*.pyc\n")
        (repo_dir / "temp.pyc").write_text("lixo\n")
        rodar(["git", "add", ".gitignore"], repo_dir)
        rodar(["git", "commit", "-m", "chore: adiciona .gitignore"], repo_dir)
        rodar(["git", "status", "--ignored"], repo_dir)

        print("--- REMOTO ---")
        origin_dir.mkdir(parents=True)
        rodar(["git", "init", "--bare", "-b", "main"], origin_dir)
        rodar(["git", "remote", "add", "origin", str(origin_dir)], repo_dir)
        rodar(["git", "push", "-u", "origin", "main"], repo_dir)

        clone_dir.parent.mkdir(parents=True)
        rodar(["git", "clone", str(origin_dir), str(clone_dir)], clone_dir.parent)
        (clone_dir / "CONTRIBUTING.md").write_text("# Como contribuir\n")
        rodar(["git", "add", "CONTRIBUTING.md"], clone_dir)
        rodar(["git", "commit", "-m", "docs: adiciona guia de contribuição"], clone_dir)
        rodar(["git", "push", "origin", "main"], clone_dir)

        rodar(["git", "pull", "origin", "main"], repo_dir)

        print("=" * 70)
        print("  Demonstração concluída -- repositórios temporários serão removidos.")
        print("=" * 70)
    finally:
        shutil.rmtree(base_dir, ignore_errors=True)


def verificar_boas_praticas_commit():
    """Exibe boas práticas para mensagens de commit."""
    print("--- BOAS PRÁTICAS: MENSAGENS DE COMMIT ---")
    boas = [
        ("✓ feat: adiciona endpoint de login", "Prefixo semântico + descrição clara"),
        ("✓ fix: corrige validação de email nulo", "Indica correção de bug"),
        ("✗ arrumei umas coisas", "Vago — não descreve o que foi feito"),
        ("✗ atualizacao", "Genérico — impossível rastrear mudanças"),
    ]
    for msg, explicacao in boas:
        print(f"  {msg:<50} ({explicacao})")


if __name__ == "__main__":
    demo_roteiro_git()
    verificar_boas_praticas_commit()
