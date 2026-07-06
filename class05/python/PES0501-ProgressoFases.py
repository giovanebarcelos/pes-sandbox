"""
PES0501 - Simulação de Progresso por Fases (Modelos Tradicionais)
Aula 05: Modelos Tradicionais — Cascata e Espiral
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula o progresso de um projeto seguindo as fases do modelo cascata.
Demonstra como as fases são sequenciais e como o esforço se distribui.
"""


FASES_CASCATA = [
    ("Requisitos", 0.15, "Documento de requisitos aprovado"),
    ("Projeto (Design)", 0.15, "Arquitetura e design detalhado"),
    ("Implementação", 0.30, "Código fonte completo"),
    ("Testes", 0.20, "Relatório de testes e bugs corrigidos"),
    ("Implantação", 0.10, "Sistema em produção"),
    ("Manutenção", 0.10, "Correções e evolução contínua"),
]


def simular_progresso(esforco_total_horas):
    """Simula o progresso de um projeto cascata por fase."""
    print("=" * 60)
    print(f"  SIMULAÇÃO DE PROJETO — MODELO CASCATA")
    print(f"  Esforço total estimado: {esforco_total_horas}h")
    print("=" * 60)
    print()

    acumulado = 0
    for nome, proporcao, entregavel in FASES_CASCATA:
        horas_fase = esforco_total_horas * proporcao
        acumulado += proporcao
        print(f"  📌 {nome}")
        print(f"     Horas estimadas: {horas_fase:.0f}h ({proporcao:.0%})")
        print(f"     Progresso acumulado: {acumulado:.0%}")
        print(f"     Entregável: {entregavel}")
        print()


if __name__ == "__main__":
    simular_progresso(esforco_total_horas=500)
    print("⚠️  Atenção: no modelo cascata, mudanças tardias são caras.")
    print("   Cada fase deve ser concluída antes de iniciar a próxima.")
