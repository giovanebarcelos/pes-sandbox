"""
PES0601 - Entregas Incrementais Acumulando Valor
Aula 06: Modelos Incremental e Prototipação
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula entregas incrementais de um sistema, mostrando como
cada incremento adiciona funcionalidades e valor acumulado.
"""


class Incremento:
    def __init__(self, versao, funcionalidades, esforco_horas, valor_negocio):
        self.versao = versao
        self.funcionalidades = funcionalidades
        self.esforco_horas = esforco_horas
        self.valor_negocio = valor_negocio

    def __repr__(self):
        return (f"v{self.versao}: {', '.join(self.funcionalidades)} "
                f"({self.esforco_horas}h, valor={self.valor_negocio}pts)")


def simular_incrementos():
    """Simula entregas incrementais e calcula valor acumulado."""
    incrementos = [
        Incremento("1.0", ["Cadastro de usuários", "Login"], 80, 30),
        Incremento("2.0", ["Catálogo de produtos", "Busca"], 120, 40),
        Incremento("3.0", ["Carrinho de compras", "Checkout"], 150, 50),
    ]

    print("=" * 60)
    print("  SIMULAÇÃO DE ENTREGAS INCREMENTAIS")
    print("=" * 60)
    print()

    esforco_acumulado = 0
    valor_acumulado = 0
    print(f"{'Versão':<8} {'Funcionalidades':<45} {'Esforço':>8} {'Valor':>6}")
    print("-" * 72)
    for inc in incrementos:
        esforco_acumulado += inc.esforco_horas
        valor_acumulado += inc.valor_negocio
        funcs = ", ".join(inc.funcionalidades)
        print(f"v{inc.versao:<7} {funcs:<45} {inc.esforco_horas:>5}h {inc.valor_negocio:>4}pts")
    print("-" * 72)
    print(f"{'TOTAL':<7} {' ':>45} {esforco_acumulado:>5}h {valor_acumulado:>4}pts")
    print()
    print(f"ROI (Valor/Esforço): {valor_acumulado/esforco_acumulado:.2f} pts/h")
    print()
    print("✓ Vantagem incremental: valor entregue a cada ciclo, feedback contínuo.")


if __name__ == "__main__":
    simular_incrementos()
