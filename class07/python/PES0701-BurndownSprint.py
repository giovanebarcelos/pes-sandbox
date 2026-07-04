"""
PES0701 - Cálculo de Velocidade e Burndown de Sprint
Aula 07: Metodologias Ágeis — Scrum e XP
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula uma sprint Scrum com cálculo de velocidade da equipe
e gráfico de burndown (textual) baseado em story points.
"""


def calcular_burndown(total_story_points, velocidade_diaria, dias_sprint):
    """Calcula o burndown diário de uma sprint."""
    restante = total_story_points
    print(f"\n{'Dia':<6} {'Planejado':>10} {'Realizado':>10} {'Restante':>10}")
    print("-" * 42)

    for dia in range(1, dias_sprint + 1):
        planejado_restante = total_story_points - (total_story_points / dias_sprint) * dia
        realizado = min(velocidade_diaria, restante)
        restante -= realizado
        print(f"{dia:<6} {max(planejado_restante, 0):>10.1f} {realizado:>10.1f} {max(restante, 0):>10.1f}")

    return total_story_points - restante


if __name__ == "__main__":
    total_sp = 34  # story points no sprint backlog
    velocidade = 5  # velocidade diária da equipe (story points/dia)
    dias = 10       # duração da sprint

    print("=" * 60)
    print("  BURNDOWN CHART — SPRINT SCRUM")
    print("=" * 60)
    print(f"  Story Points no Backlog: {total_sp}")
    print(f"  Velocidade diária: {velocidade} SP/dia")
    print(f"  Duração da Sprint: {dias} dias")
    print(f"  Capacidade teórica: {velocidade * dias} SP")

    entregue = calcular_burndown(total_sp, velocidade, dias)

    print(f"\n  ✓ Story Points entregues: {entregue}/{total_sp}")
    print(f"  ✓ Velocidade da Sprint: {entregue} SP")
    if entregue >= total_sp:
        print("  🎉 Meta da sprint atingida!")
    else:
        print(f"  ⚠️  {total_sp - entregue} SP migrarão para o próximo sprint backlog.")
