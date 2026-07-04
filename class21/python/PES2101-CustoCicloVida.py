"""
PES2101 - Cálculo de Custo Acumulado por Fase do SDLC
Aula 21: Ciclo de Vida de Software
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula o custo acumulado ao longo das fases do ciclo de vida,
demonstrando que a manutenção é a fase mais longa e cara.
"""

FASES_SDLC = [
    ("Concepção", 0.05, "Viabilidade e escopo inicial"),
    ("Requisitos", 0.10, "Especificação e análise"),
    ("Projeto", 0.10, "Arquitetura e design"),
    ("Implementação", 0.20, "Codificação"),
    ("Testes", 0.15, "Verificação e validação"),
    ("Implantação", 0.05, "Entrega e deploy"),
    ("Manutenção", 0.35, "Correções, adaptações, evolução"),
]


def simular_custos(custo_total):
    print(f"\n{'Fase':<20} {'%':>6} {'Custo (R$)':>14} {'Acumulado (R$)':>16}")
    print("-" * 60)
    acumulado = 0
    for nome, proporcao, _ in FASES_SDLC:
        custo = custo_total * proporcao
        acumulado += custo
        print(f"{nome:<20} {proporcao:>5.0%} R$ {custo:>12,.0f} R$ {acumulado:>14,.0f}")
    print("-" * 60)
    print(f"{'TOTAL':<20} {'100%':>6} R$ {custo_total:>12,.0f}")


if __name__ == "__main__":
    print("=" * 60)
    print("  CUSTO ACUMULADO POR FASE DO CICLO DE VIDA")
    print("=" * 60)

    simular_custos(custo_total=500_000)

    print("\n✓ Manutenção consome ~35% do custo total do ciclo de vida.")
    print("✓ Custo de corrigir um defeito cresce exponencialmente a cada fase.")
