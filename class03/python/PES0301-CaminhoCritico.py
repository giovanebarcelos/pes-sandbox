"""
PES0301 - Cálculo de Caminho Crítico e Estimativa de Custo
Aula 03: Planejamento — Escopo, Prazo, Custo e Qualidade
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa um algoritmo simplificado de caminho crítico (CPM) para um
conjunto de atividades com dependências e calcula o custo total estimado.
"""


class Atividade:
    def __init__(self, nome, duracao, custo_por_dia, dependencias=None):
        self.nome = nome
        self.duracao = duracao
        self.custo_por_dia = custo_por_dia
        self.dependencias = dependencias or []
        self.inicio_cedo = 0
        self.fim_cedo = 0

    @property
    def custo_total(self):
        return self.duracao * self.custo_por_dia

    def __repr__(self):
        return (f"{self.nome}[dur={self.duracao}d, ini={self.inicio_cedo}, "
                f"fim={self.fim_cedo}, custo=R${self.custo_total}]")


def calcular_caminho_critico(atividades):
    """Calcula o forward pass (início e fim mais cedo) e identifica o caminho crítico."""
    resolvidas = {}
    pendentes = list(atividades)

    while pendentes:
        for atv in list(pendentes):
            if all(dep in resolvidas for dep in atv.dependencias):
                inicio = 0
                for dep in atv.dependencias:
                    inicio = max(inicio, resolvidas[dep].fim_cedo)
                atv.inicio_cedo = inicio
                atv.fim_cedo = inicio + atv.duracao
                resolvidas[atv.nome] = atv
                pendentes.remove(atv)
                break
        else:
            raise ValueError("Dependência circular ou não resolvida.")

    duracao_total = max(a.fim_cedo for a in atividades)
    custo_total = sum(a.custo_total for a in atividades)

    # Identificar caminho crítico (folga zero)
    # Simplificação: atividades cujo fim == duração total ou são predecessoras de quem está
    criticas = [a for a in atividades if a.fim_cedo == duracao_total or
                any(dep.fim_cedo == duracao_total for dep in atividades
                    if a.nome in dep.dependencias)]

    return duracao_total, custo_total, criticas


if __name__ == "__main__":
    # Exemplo: projeto de sistema de biblioteca
    atvs = [
        Atividade("Plano de Projeto", 7, 200),
        Atividade("Entrevistas", 7, 150, ["Plano de Projeto"]),
        Atividade("DRS", 10, 180, ["Entrevistas"]),
        Atividade("Back-end", 21, 250, ["DRS"]),
        Atividade("Front-end", 21, 250, ["DRS"]),
        Atividade("Banco de Dados", 14, 220, ["DRS"]),
        Atividade("Testes Unitários", 7, 200, ["Back-end", "Front-end"]),
        Atividade("Testes Integrados", 7, 200, ["Testes Unitários", "Banco de Dados"]),
        Atividade("Implantação", 3, 300, ["Testes Integrados"]),
    ]

    duracao, custo, criticas = calcular_caminho_critico(atvs)

    print("=" * 60)
    print("  CAMINHO CRÍTICO E ESTIMATIVA DE CUSTO")
    print("=" * 60)
    print(f"\nDuração total do projeto: {duracao} dias")
    print(f"Custo total estimado: R$ {custo:,.2f}")
    print(f"\nAtividades no caminho crítico:")
    for a in criticas:
        print(f"  • {a.nome} ({a.duracao}d, R$ {a.custo_total:,.2f})")
