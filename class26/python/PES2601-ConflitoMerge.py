"""
PES2601 - Simulação de Conflito de Merge e Resolução
Aula 26: Fluxos de Trabalho com Git e Colaboração
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula um cenário de conflito de merge quando duas branches
modificam a mesma linha e demonstra a resolução.
"""

import difflib


def simular_conflito():
    """Simula conflito de merge entre duas branches."""

    # Versão base (main antes das branches)
    base = [
        "def calcular_total(itens):",
        "    total = 0",
        "    for item in itens:",
        "        total += item['preco']",
        "    return total",
    ]

    # Branch feature/desconto
    branch_desconto = [
        "def calcular_total(itens):",
        "    total = 0",
        "    for item in itens:",
        "        preco = item['preco'] * 0.9  # 10% desconto",
        "        total += preco",
        "    return total",
    ]

    # Branch feature/imposto
    branch_imposto = [
        "def calcular_total(itens):",
        "    total = 0",
        "    for item in itens:",
        "        preco = item['preco'] * 1.15  # 15% imposto",
        "        total += preco",
        "    return total",
    ]

    print("=" * 60)
    print("  SIMULAÇÃO DE CONFLITO DE MERGE")
    print("=" * 60)
    print()

    print("--- Base (main) ---")
    for line in base:
        print(f"  {line}")

    print("\n--- Branch feature/desconto ---")
    for line in branch_desconto:
        print(f"  {line}")

    print("\n--- Branch feature/imposto ---")
    for line in branch_imposto:
        print(f"  {line}")

    print("\n--- Diff: Base → Desconto ---")
    for line in difflib.unified_diff(base, branch_desconto, lineterm=""):
        print(f"  {line}")

    print("\n--- Diff: Base → Imposto ---")
    for line in difflib.unified_diff(base, branch_imposto, lineterm=""):
        print(f"  {line}")

    print("\n--- Resolução (merge manual) ---")
    resolvido = [
        "def calcular_total(itens):",
        "    total = 0",
        "    for item in itens:",
        "        preco = item['preco'] * 0.9   # 10% desconto (feature/desconto)",
        "        preco = preco * 1.15           # 15% imposto (feature/imposto)",
        "        total += preco",
        "    return total",
    ]
    for line in resolvido:
        print(f"  {line}")

    print("\n✓ Conflito resolvido mesclando ambas as alterações.")
    print("✓ Estratégias: aceitar uma, combinar ambas ou reescrever.")


if __name__ == "__main__":
    simular_conflito()
