"""
PES2401 - Antes/Depois de Refatoração
Aula 24: Tipos de Manutenção e Evolução de Software
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra refatoração: código com débito técnico (antes)
vs. código limpo e modular (depois), mantendo mesmo comportamento.
"""


# ============================================================
# ❌ ANTES: código com débito técnico
# ============================================================
def calcular_preco_antes(produtos):
    """Código com cheiro: função longa, muitos ifs, difícil de estender."""
    total = 0
    for p in produtos:
        preco = p["preco"]
        if p["tipo"] == "eletronico":
            preco = preco * 1.20  # imposto
        elif p["tipo"] == "alimento":
            preco = preco * 1.05
        else:
            preco = preco * 1.15
        if p.get("promocao"):
            preco = preco * 0.90
        total += preco
    return total


# ============================================================
# ✅ DEPOIS: código refatorado (Clean Code)
# ============================================================
IMPOSTOS = {"eletronico": 1.20, "alimento": 1.05}
IMPOSTO_PADRAO = 1.15
DESCONTO_PROMOCAO = 0.90


def aplicar_imposto(produto):
    tipo = produto.get("tipo", "")
    taxa = IMPOSTOS.get(tipo, IMPOSTO_PADRAO)
    return produto["preco"] * taxa


def aplicar_desconto(preco, produto):
    return preco * DESCONTO_PROMOCAO if produto.get("promocao") else preco


def calcular_preco_depois(produtos):
    """Código refatorado: funções pequenas, fácil de estender."""
    return sum(aplicar_desconto(aplicar_imposto(p), p) for p in produtos)


if __name__ == "__main__":
    produtos = [
        {"tipo": "eletronico", "preco": 100, "promocao": True},
        {"tipo": "alimento", "preco": 50, "promocao": False},
        {"tipo": "livro", "preco": 80, "promocao": True},
    ]

    print("=" * 60)
    print("  REFATORAÇÃO — ANTES × DEPOIS")
    print("=" * 60)

    print(f"\n  Antes:  R$ {calcular_preco_antes(produtos):.2f}")
    print(f"  Depois: R$ {calcular_preco_depois(produtos):.2f}")
    print(f"  (resultados idênticos ✓)")

    print("\n✓ Refatoração: mesmo comportamento, código mais limpo.")
    print("✓ Manutenção perfectiva: melhora estrutura interna sem alterar externo.")
