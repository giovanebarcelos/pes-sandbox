"""
PES2301 - Roteiro de Comandos Git Comentado
Aula 23: Gerência de Configuração e Introdução ao Git
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Exibe um roteiro comentado dos principais comandos Git,
funcionando como referência rápida para o laboratório prático.
"""

ROTEIRO_GIT = [
    # (comando, descrição, categoria)
    ("git init", "Inicializa um novo repositório Git no diretório atual", "inicialização"),
    ("git clone <url>", "Clona um repositório remoto para o diretório local", "inicialização"),
    ("git status", "Mostra o estado dos arquivos (modificados, staged, untracked)", "consulta"),
    ("git add <arquivo>", "Adiciona arquivo à staging area (prepara para commit)", "staging"),
    ("git add .", "Adiciona todos os arquivos modificados à staging area", "staging"),
    ("git commit -m 'mensagem'", "Registra as mudanças no repositório local", "commit"),
    ("git log --oneline", "Exibe o histórico de commits resumido", "consulta"),
    ("git diff", "Mostra as diferenças entre working directory e staging", "consulta"),
    ("git push origin main", "Envia commits locais para o repositório remoto", "remoto"),
    ("git pull origin main", "Baixa e mescla mudanças do repositório remoto", "remoto"),
    ("git branch <nome>", "Cria uma nova branch", "branch"),
    ("git checkout <branch>", "Muda para a branch especificada", "branch"),
    ("git merge <branch>", "Mescla a branch especificada na branch atual", "branch"),
    ("git status --ignored", "Mostra também arquivos ignorados pelo .gitignore", "consulta"),
    (".gitignore", "Arquivo que lista padrões de arquivos a serem ignorados", "config"),
]


def exibir_roteiro():
    print("=" * 70)
    print("  GUIA RÁPIDO DE COMANDOS GIT")
    print("=" * 70)
    print()

    categorias = {}
    for cmd, desc, cat in ROTEIRO_GIT:
        categorias.setdefault(cat, []).append((cmd, desc))

    for cat, comandos in categorias.items():
        print(f"--- {cat.upper()} ---")
        for cmd, desc in comandos:
            print(f"  $ {cmd:<32} # {desc}")
        print()


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
    exibir_roteiro()
    verificar_boas_praticas_commit()
