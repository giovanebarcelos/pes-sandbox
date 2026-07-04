"""
PES0901 - Registro e Priorização de Requisitos (MoSCoW)
Aula 09: Introdução à Engenharia de Requisitos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa um registro de requisitos com priorização MoSCoW
(Must have, Should have, Could have, Won't have).
"""

from enum import Enum


class Prioridade(Enum):
    MUST = ("Must have", 4)
    SHOULD = ("Should have", 3)
    COULD = ("Could have", 2)
    WONT = ("Won't have", 1)

    def __init__(self, label, peso):
        self.label = label
        self.peso = peso


class Requisito:
    def __init__(self, id_req, descricao, prioridade, stakeholder, tipo="RF"):
        self.id = id_req
        self.descricao = descricao
        self.prioridade = prioridade
        self.stakeholder = stakeholder
        self.tipo = tipo  # RF ou RNF

    def __repr__(self):
        return (f"[{self.id}] {self.prioridade.label} ({self.tipo}) "
                f"{self.descricao} — @{self.stakeholder}")


class RegistroRequisitos:
    def __init__(self):
        self.requisitos = []

    def adicionar(self, requisito):
        self.requisitos.append(requisito)

    def listar_por_prioridade(self):
        return sorted(self.requisitos, key=lambda r: r.prioridade.peso, reverse=True)

    def filtrar_must_should(self):
        return [r for r in self.requisitos
                if r.prioridade in (Prioridade.MUST, Prioridade.SHOULD)]

    def resumo(self):
        print(f"Total: {len(self.requisitos)} requisitos")
        for p in Prioridade:
            count = sum(1 for r in self.requisitos if r.prioridade == p)
            print(f"  {p.label}: {count}")


if __name__ == "__main__":
    registro = RegistroRequisitos()

    registro.adicionar(Requisito("RF01", "O sistema deve permitir login com email e senha",
                                 Prioridade.MUST, "Usuário Final"))
    registro.adicionar(Requisito("RF02", "O sistema deve enviar email de confirmação de cadastro",
                                 Prioridade.SHOULD, "Product Owner"))
    registro.adicionar(Requisito("RNF01", "O sistema deve responder em até 2 segundos",
                                 Prioridade.MUST, "Arquiteto", "RNF"))
    registro.adicionar(Requisito("RF03", "O usuário pode exportar relatório em PDF",
                                 Prioridade.COULD, "Usuário Final"))
    registro.adicionar(Requisito("RF04", "Integração com redes sociais para login",
                                 Prioridade.WONT, "Marketing"))

    print("=" * 60)
    print("  REGISTRO DE REQUISITOS — PRIORIZAÇÃO MOSCOW")
    print("=" * 60)
    print()

    print("--- Lista por Prioridade ---")
    for r in registro.listar_por_prioridade():
        print(f"  {r}")

    print(f"\n--- MVP (Must + Should) ---")
    for r in registro.filtrar_must_should():
        print(f"  {r}")

    print(f"\n--- Resumo ---")
    registro.resumo()
