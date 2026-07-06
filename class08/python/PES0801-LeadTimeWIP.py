"""
PES0801 - Métrica de Lead Time e Limite de WIP (Kanban)
Aula 08: Kanban e Comparação de Abordagens
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Calcula métricas de fluxo Kanban: lead time, cycle time e
simula o efeito do limite de WIP (Work In Progress).
"""


def calcular_metricas_kanban(tarefas):
    """
    Calcula lead time, cycle time e throughput de um conjunto de tarefas.
    Cada tarefa: (nome, data_entrada, data_inicio, data_entrega)
    """
    print(f"{'Tarefa':<10} {'Lead Time (d)':>14} {'Cycle Time (d)':>15}")
    print("-" * 42)
    total_lead = 0
    total_cycle = 0
    for nome, entrada, inicio, entrega in tarefas:
        lead = (entrega - entrada).days
        cycle = (entrega - inicio).days
        total_lead += lead
        total_cycle += cycle
        print(f"{nome:<10} {lead:>14} {cycle:>15}")

    n = len(tarefas)
    print("-" * 42)
    print(f"{'MÉDIA':<10} {total_lead/n:>14.1f} {total_cycle/n:>15.1f}")
    print(f"\nThroughput: {n} tarefas em {(tarefas[-1][3] - tarefas[0][1]).days} dias"
          f" = {n/(tarefas[-1][3] - tarefas[0][1]).days:.2f} tarefas/dia")

    return total_lead / n, total_cycle / n


def simular_wip(limite_wip, total_tarefas, tempo_medio_tarefa_dias, capacidade_equipe):
    """
    Simula o efeito do limite de WIP no lead time.
    Lei de Little: Lead Time = WIP / Throughput.

    O throughput é limitado pela capacidade da equipe (quantas tarefas ela
    consegue processar em paralelo). Definir um limite de WIP acima dessa
    capacidade NÃO aumenta o throughput — apenas represa mais tarefas em
    andamento e aumenta o lead time.
    """
    wip_processavel = min(limite_wip, capacidade_equipe)
    throughput_max = wip_processavel / tempo_medio_tarefa_dias
    lead_time_estimado = limite_wip / throughput_max if throughput_max > 0 else float("inf")
    return throughput_max, lead_time_estimado


if __name__ == "__main__":
    from datetime import date, timedelta

    print("=" * 60)
    print("  MÉTRICAS KANBAN — LEAD TIME E WIP")
    print("=" * 60)

    # Dados simulados
    hoje = date(2025, 8, 1)
    tarefas = [
        ("US-01", hoje, hoje + timedelta(days=0), hoje + timedelta(days=5)),
        ("US-02", hoje + timedelta(days=1), hoje + timedelta(days=4), hoje + timedelta(days=9)),
        ("US-03", hoje + timedelta(days=2), hoje + timedelta(days=7), hoje + timedelta(days=12)),
        ("US-04", hoje + timedelta(days=3), hoje + timedelta(days=8), hoje + timedelta(days=13)),
        ("US-05", hoje + timedelta(days=4), hoje + timedelta(days=9), hoje + timedelta(days=16)),
    ]

    print("\n--- Métricas de Fluxo ---")
    avg_lead, avg_cycle = calcular_metricas_kanban(tarefas)

    capacidade_equipe = 3  # nº de tarefas que a equipe processa em paralelo
    print(f"\n--- Simulação de Limite de WIP (capacidade da equipe: {capacidade_equipe}) ---")
    for wip in [2, 3, 5, 10]:
        tp, lt = simular_wip(wip, 20, avg_cycle, capacidade_equipe)
        print(f"  WIP={wip:>2}: Throughput máx={tp:.2f} tar/dia, Lead Time est.={lt:.1f} dias")

    print("\n✓ Conclusão: WIP acima da capacidade da equipe não aumenta o throughput,"
          "\n  apenas represa tarefas e aumenta o lead time — por isso reduzir o WIP"
          "\n  (até o limite da capacidade) reduz o lead time e expõe gargalos.")
