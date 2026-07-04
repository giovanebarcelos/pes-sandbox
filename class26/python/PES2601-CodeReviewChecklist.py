"""
PES2601 - Checklist de Code Review e Boas Práticas Colaborativas
Aula 26: Práticas Colaborativas, Ética e Encerramento
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa um checklist interativo de code review,
promovendo práticas colaborativas de qualidade.
"""

CHECKLIST_CODE_REVIEW = [
    # (categoria, itens)
    ("Funcionalidade", [
        "O código faz o que foi especificado?",
        "Todos os casos de borda foram tratados?",
        "Há testes automatizados cobrindo a mudança?",
    ]),
    ("Legibilidade", [
        "Nomes de variáveis/funções são descritivos?",
        "Funções têm tamanho adequado (< 30 linhas)?",
        "Código duplicado foi evitado (DRY)?",
    ]),
    ("Segurança", [
        "Dados sensíveis estão protegidos?",
        "Validação de entrada está presente?",
        "Não há credenciais hardcoded?",
    ]),
    ("Performance", [
        "Não há loops desnecessários?",
        "Consultas são eficientes?",
        "Recursos são liberados corretamente?",
    ]),
    ("Documentação", [
        "Docstrings/Javadoc estão presentes?",
        "README foi atualizado se necessário?",
        "Comentários explicam o 'porquê', não o 'como'?",
    ]),
    ("Colaboração", [
        "A mensagem de commit é clara e semântica?",
        "O PR tem descrição e link para a issue?",
        "Feedbacks anteriores foram endereçados?",
    ]),
]


def executar_checklist():
    """Executa o checklist interativo de code review."""
    print("=" * 60)
    print("  CHECKLIST DE CODE REVIEW")
    print("=" * 60)
    print()

    total = 0
    aprovados = 0

    for categoria, itens in CHECKLIST_CODE_REVIEW:
        print(f"--- {categoria} ---")
        for item in itens:
            total += 1
            resposta = input(f"  [{item}] (s/n)? ").strip().lower()
            if resposta in ("s", "sim", "y", "yes"):
                aprovados += 1
                print(f"    ✓ OK")
            else:
                print(f"    ⚠️  Pendente")
        print()

    pct = (aprovados / total * 100) if total > 0 else 0
    print(f"Resultado: {aprovados}/{total} itens aprovados ({pct:.0f}%)")
    if pct >= 90:
        print("✓ Code Review aprovado! Pronto para merge.")
    elif pct >= 70:
        print("⚠️  Code Review com ressalvas. Corrija os itens pendentes.")
    else:
        print("✗ Code Review reprovado. Resolva os itens antes do merge.")


def exibir_boas_praticas():
    """Exibe princípios de colaboração ética em equipe."""
    print("\n" + "=" * 60)
    print("  BOAS PRÁTICAS COLABORATIVAS E ÉTICA")
    print("=" * 60)
    praticas = [
        "Respeite opiniões divergentes — diversidade gera melhores soluções",
        "Seja construtivo(a) no feedback: critique o código, não a pessoa",
        "Reconheça contribuições alheias (atribuição justa)",
        "Mantenha confidencialidade de dados de usuários (LGPD/GDPR)",
        "Comunique-se de forma clara, objetiva e respeitosa",
        "Documente decisões importantes para a equipe",
        "Assuma responsabilidade por seus erros e aprenda com eles",
    ]
    for p in praticas:
        print(f"  • {p}")


if __name__ == "__main__":
    exibir_boas_praticas()
    print()
    # Descomente para modo interativo:
    # executar_checklist()
    print("(Para usar o checklist interativo, descomente 'executar_checklist()')")
