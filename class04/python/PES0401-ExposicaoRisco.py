"""
PES0401 - Cálculo de Exposição a Risco e Indicadores
Aula 04: Acompanhamento, Riscos e Ferramentas de Gestão
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Calcula a exposição a risco (probabilidade × impacto financeiro)
e exibe indicadores de progresso do projeto (EV, PV, AC simplificados).
"""


def calcular_exposicao(probabilidade, impacto_financeiro):
    """Calcula a exposição a risco = P × I."""
    return probabilidade * impacto_financeiro


def classificar_risco(exposicao):
    """Classifica o risco com base na exposição financeira."""
    if exposicao > 50_000:
        return "ALTO", "🔴"
    elif exposicao > 10_000:
        return "MÉDIO", "🟡"
    else:
        return "BAIXO", "🟢"


def calcular_evm(valor_planejado, valor_agregado, custo_real):
    """Calcula indicadores simplificados de EVM (Earned Value Management)."""
    if valor_planejado == 0:
        return {}
    spi = valor_agregado / valor_planejado  # Schedule Performance Index
    cpi = valor_agregado / custo_real if custo_real > 0 else 1.0  # Cost Performance Index
    return {
        "SPI": round(spi, 2),
        "CPI": round(cpi, 2),
        "SV": round(valor_agregado - valor_planejado, 2),  # Schedule Variance
        "CV": round(valor_agregado - custo_real, 2),       # Cost Variance
        "status_prazo": "No prazo" if spi >= 1.0 else "Atrasado",
        "status_custo": "Dentro do orçamento" if cpi >= 1.0 else "Acima do orçamento",
    }


if __name__ == "__main__":
    print("=" * 60)
    print("  GESTÃO DE RISCOS E INDICADORES (EVM)")
    print("=" * 60)

    # Matriz de riscos
    riscos = [
        ("Atraso em dependência externa", 0.7, 80_000),
        ("Saída de desenvolvedor chave", 0.5, 120_000),
        ("Mudança de requisitos tardia", 0.4, 25_000),
        ("Falha de integração", 0.3, 60_000),
        ("Indisponibilidade de servidor", 0.1, 5_000),
    ]

    print("\n--- Matriz de Riscos ---")
    print(f"{'Risco':<35} {'Prob':>6} {'Impacto':>12} {'Exposição':>12} {'Clas.'}")
    print("-" * 75)
    for nome, prob, impacto in riscos:
        exp = calcular_exposicao(prob, impacto)
        classe, icone = classificar_risco(exp)
        print(f"{nome:<35} {prob:>6.0%} R${impacto:>11,.0f} R${exp:>11,.0f} {icone} {classe}")

    # Indicadores EVM
    print("\n--- Indicadores de Progresso (EVM Simplificado) ---")
    evm = calcular_evm(valor_planejado=50000, valor_agregado=42000, custo_real=48000)
    print(f"SPI: {evm['SPI']} ({evm['status_prazo']})")
    print(f"CPI: {evm['CPI']} ({evm['status_custo']})")
    print(f"SV (Schedule Variance): R$ {evm['SV']:,.2f}")
    print(f"CV (Cost Variance): R$ {evm['CV']:,.2f}")
