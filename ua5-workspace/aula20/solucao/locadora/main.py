"""
Demonstração: o MESMO roteiro rodando com dois adaptadores diferentes.

Executar (na pasta solucao/):  python -m locadora.main
"""

from .config import montar


def roteiro(servico):
    servico.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5)
    servico.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7)
    print("  Disponíveis:", [v.placa for v in servico.listar_disponiveis()])
    print(f"  Faturamento: R$ {servico.faturamento_total():.2f}")


def main():
    print("--- Adaptador: memória ---")
    roteiro(montar("memoria", silencioso=True))
    print("--- Adaptador: SQLite (:memory:) ---")
    roteiro(montar("sqlite", ":memory:", silencioso=True))
    print("LocadoraService não mudou nenhuma linha entre as duas execuções.")


if __name__ == "__main__":
    main()
