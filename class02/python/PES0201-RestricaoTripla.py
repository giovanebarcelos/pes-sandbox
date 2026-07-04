"""
PES0201 - Calculadora de Restrição Tripla
Aula 02: Fundamentos de Gerenciamento de Projetos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra a relação entre escopo, prazo e custo (restrição tripla).
Dado um escopo base, calcula o esforço estimado e permite simular
variações de prazo e equipe.

Fórmula simplificada: esforço = escopo (pontos) / (prazo_semanas * tamanho_equipe)
"""


def calcular_esforco(escopo_pontos, prazo_semanas, tamanho_equipe):
    """Calcula o esforço necessário dado escopo, prazo e equipe."""
    if prazo_semanas <= 0 or tamanho_equipe <= 0:
        raise ValueError("Prazo e tamanho da equipe devem ser positivos.")
    capacidade = prazo_semanas * tamanho_equipe
    esforco_por_pessoa = escopo_pontos / capacidade
    return capacidade, esforco_por_pessoa


def simular_cenario(escopo, prazo, equipe, nome=""):
    """Simula um cenário da restrição tripla."""
    cap, esf = calcular_esforco(escopo, prazo, equipe)
    print(f"  Cenário{f' ({nome})' if nome else ''}:")
    print(f"    Escopo: {escopo} pts | Prazo: {prazo} sem | Equipe: {equipe} pessoas")
    print(f"    Capacidade: {cap} pessoa-semanas | Esforço/pessoa: {esf:.2f} pts/sem")
    if esf > 10:
        print(f"    ⚠️  Alta carga (>{10} pts/sem) — risco de atraso!")
    print()


if __name__ == "__main__":
    print("=" * 60)
    print("  CALCULADORA DE RESTRIÇÃO TRIPLA")
    print("  Escopo × Prazo × Custo (Equipe)")
    print("=" * 60)
    print()

    # Cenário base
    simular_cenario(escopo=100, prazo=10, equipe=5, nome="Base")

    # Reduzindo prazo (mantendo escopo) — aumenta pressão
    simular_cenario(escopo=100, prazo=5, equipe=5, nome="Prazo reduzido")

    # Aumentando equipe (mantendo escopo e prazo)
    simular_cenario(escopo=100, prazo=10, equipe=8, nome="Equipe maior")

    print("Conclusão: alterar um vértice do triângulo afeta os demais.")
