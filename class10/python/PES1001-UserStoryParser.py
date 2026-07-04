"""
PES1001 - Parser/Validador de User Stories
Aula 10: Levantamento e Elicitação de Requisitos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Valida user stories no formato "Como [ator], quero [ação] para [benefício]"
e extrai os três componentes.
"""

import re

USER_STORY_PATTERN = re.compile(
    r"^Como\s+(.+?),\s*quero\s+(.+?),\s*para\s+(.+?)$",
    re.IGNORECASE
)


def validar_user_story(texto):
    """Valida e extrai os componentes de uma user story."""
    texto = texto.strip()
    match = USER_STORY_PATTERN.match(texto)
    if not match:
        return None
    ator = match.group(1).strip()
    acao = match.group(2).strip()
    beneficio = match.group(3).strip().rstrip(".")
    return {"ator": ator, "acao": acao, "beneficio": beneficio}


def analisar_user_story(texto):
    """Analisa uma user story fornecendo feedback."""
    resultado = validar_user_story(texto)
    if resultado is None:
        return {"valida": False, "erro": "Formato inválido. Use: Como [ator], quero [ação] para [benefício]"}
    erros = []
    if len(resultado["acao"].split()) < 2:
        erros.append("Ação muito curta — seja mais específico(a)")
    if len(resultado["beneficio"].split()) < 2:
        erros.append("Benefício muito vago — explique o valor entregue")
    return {
        "valida": True,
        "componentes": resultado,
        "erros": erros,
        "qualidade": "Boa" if not erros else "Revisar"
    }


if __name__ == "__main__":
    stories = [
        "Como cliente, quero buscar produtos por nome, para encontrar rapidamente o que desejo comprar",
        "Como admin, quero listar usuários, para gerenciar acessos",
        "Como usuário, quero login para segurança",  # benefício curto (alerta)
        "Cliente quer buscar produtos",              # formato inválido
    ]

    print("=" * 60)
    print("  VALIDADOR DE USER STORIES")
    print("=" * 60)

    for i, story in enumerate(stories, 1):
        print(f"\n--- Story {i} ---")
        print(f"  Texto: \"{story}\"")
        analise = analisar_user_story(story)
        if analise["valida"]:
            c = analise["componentes"]
            print(f"  ✓ Válida ({analise['qualidade']})")
            print(f"    Ator: {c['ator']}")
            print(f"    Ação: {c['acao']}")
            print(f"    Benefício: {c['beneficio']}")
            for e in analise["erros"]:
                print(f"    ⚠️  {e}")
        else:
            print(f"  ✗ Inválida: {analise['erro']}")
