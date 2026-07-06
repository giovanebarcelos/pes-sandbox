"""
PES0602 - Ciclo de Prototipação: Descartável vs Evolutivo
Aula 06: Modelos Incremental e Prototipação
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula o ciclo de prototipação (construir -> avaliar com usuário -> refinar)
por N iterações, coletando feedback simulado do usuário, e ilustra a
diferença de destino entre um protótipo Descartável (jogado fora após
validar os requisitos) e um Evolutivo (refinado até virar o sistema real).
"""

from dataclasses import dataclass

DESCARTAVEL = "descartavel"
EVOLUTIVO = "evolutivo"

FEEDBACKS_SIMULADOS = [
    "Falta um campo para filtrar por categoria.",
    "O fluxo de cadastro está confuso, simplifique.",
    "Adicione um gráfico para visualizar o resultado.",
    "Está bom, só falta um botão de exportar.",
]


@dataclass
class Prototipo:
    nome: str
    tipo: str  # DESCARTAVEL ou EVOLUTIVO
    custo_construcao: float
    custo_avaliacao: float = 500.0


def ciclo_prototipacao(prototipo: Prototipo, iteracoes: int) -> float:
    """Simula N iterações do ciclo construir -> avaliar -> refinar.

    A cada iteração o custo de refino cai para 70% do anterior (o
    protótipo já validado nas rodadas anteriores exige menos retrabalho).
    Ao final, aplica o destino do protótipo: descartar e reconstruir do
    zero, ou evoluir direto para o sistema real. Retorna o custo total.
    """
    custo_acumulado = 0.0
    print(f"\n--- Ciclo de Prototipação: {prototipo.nome} ({prototipo.tipo}) ---")
    for i in range(iteracoes):
        fator_refino = 0.7 ** i
        custo_iteracao = prototipo.custo_construcao * fator_refino
        custo_acumulado += custo_iteracao + prototipo.custo_avaliacao
        feedback = FEEDBACKS_SIMULADOS[i % len(FEEDBACKS_SIMULADOS)]
        print(f"  Iteração {i + 1}: construir R$ {custo_iteracao:.2f} + "
              f"avaliar R$ {prototipo.custo_avaliacao:.2f} -> "
              f"feedback do usuário: \"{feedback}\"")

    if prototipo.tipo == DESCARTAVEL:
        custo_sistema_real = prototipo.custo_construcao * 3
        print(f"  Decisão: DESCARTAR o protótipo e construir o sistema real "
              f"do zero (R$ {custo_sistema_real:.2f}).")
        custo_acumulado += custo_sistema_real
    else:
        print("  Decisão: EVOLUIR o protótipo até virar o sistema real "
              "(sem custo extra de reconstrução).")

    print(f"  Custo total do ciclo: R$ {custo_acumulado:.2f}")
    return custo_acumulado


def demonstrar_prototipacao():
    """Executa o ciclo para um protótipo descartável e um evolutivo,
    usando o mesmo escopo, para comparar o destino de cada um."""
    print("=" * 60)
    print("  CICLO DE PROTOTIPAÇÃO: DESCARTÁVEL x EVOLUTIVO")
    print("=" * 60)

    prototipo_descartavel = Prototipo("Mockup de Navegação", DESCARTAVEL, 1000.0)
    custo_descartavel = ciclo_prototipacao(prototipo_descartavel, 3)

    prototipo_evolutivo = Prototipo("MVP de Cadastro", EVOLUTIVO, 1600.0)
    custo_evolutivo = ciclo_prototipacao(prototipo_evolutivo, 3)

    print("\n" + "=" * 60)
    print(f"Descartável (3 iterações + sistema real do zero): R$ {custo_descartavel:.2f}")
    print(f"Evolutivo   (3 iterações, vira o sistema real):    R$ {custo_evolutivo:.2f}")
    print("=" * 60)


if __name__ == "__main__":
    demonstrar_prototipacao()
