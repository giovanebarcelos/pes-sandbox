"""
Demonstração da Aula 21: comandos -> eventos -> projeções.

Executar (na pasta solucao/):  python -m locadora.main
Com event store em arquivo:    LOCADORA_EVENTOS=dados/eventos.jsonl python -m locadora.main
"""

from datetime import date

from .config import montar
from .locacoes.aplicacao.comandos import IniciarLocacao, RegistrarDevolucao


def main():
    app = montar()
    novembro = lambda dia: date(2026, 11, dia)
    app.comandos.executar(IniciarLocacao("L-001", "ABC-1234", "Ana Silva", novembro(2), 5))
    app.comandos.executar(IniciarLocacao("L-002", "XYZ-9012", "Carlos Santos", novembro(3), 7))
    app.comandos.executar(RegistrarDevolucao("L-001", novembro(7)))      # no prazo
    app.comandos.executar(IniciarLocacao("L-003", "ABC-1234", "Bia Souza", novembro(8), 2))
    app.comandos.executar(RegistrarDevolucao("L-003", novembro(12)))     # 2 dias de atraso

    print("=== EVENT STORE (fonte da verdade) ===")
    for e in app.store.todos():
        print(f"  {e.tipo:<24} {e.para_dict()}")
    print("\n=== CONSULTAS (lado de leitura) ===")
    print("  Ativas:", app.consultas.locacoes_ativas())
    print("  Mais alugados 2026-11:", app.consultas.mais_alugados("2026-11"))
    print(f"  Faturamento 2026-11: R$ {app.consultas.faturamento_do_mes('2026-11'):.2f}")
    print("  Frota:", [(v["placa"], v["disponivel"]) for v in app.frota.listar()])


if __name__ == "__main__":
    main()
