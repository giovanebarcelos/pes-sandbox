"""
PES1401 - Modelo de Atores e Casos de Uso
Aula 14: Diagrama de Casos de Uso
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa um modelo simples de atores e casos de uso com descrições,
demonstrando a relação entre atores e funcionalidades do sistema.
"""


class Ator:
    def __init__(self, nome, tipo="primário"):
        self.nome = nome
        self.tipo = tipo

    def __repr__(self):
        return f"{self.nome} ({self.tipo})"


class CasoDeUso:
    def __init__(self, id_uc, nome, ator_principal, pre_condicao, fluxo_principal,
                 fluxo_alternativo=None, pos_condicao=None):
        self.id = id_uc
        self.nome = nome
        self.ator_principal = ator_principal
        self.pre_condicao = pre_condicao
        self.fluxo_principal = fluxo_principal
        self.fluxo_alternativo = fluxo_alternativo or []
        self.pos_condicao = pos_condicao or "Caso de uso concluído com sucesso"

    def descrever(self):
        print(f"\n{'='*60}")
        print(f"  {self.id}: {self.nome}")
        print(f"{'='*60}")
        print(f"  Ator Principal: {self.ator_principal}")
        print(f"  Pré-condição: {self.pre_condicao}")
        print(f"  Fluxo Principal:")
        for i, passo in enumerate(self.fluxo_principal, 1):
            print(f"    {i}. {passo}")
        if self.fluxo_alternativo:
            print(f"  Fluxo Alternativo:")
            for i, passo in enumerate(self.fluxo_alternativo, 1):
                print(f"    {i}a. {passo}")
        print(f"  Pós-condição: {self.pos_condicao}")


if __name__ == "__main__":
    leitor = Ator("Leitor")
    bibliotecario = Ator("Bibliotecário")

    uc02 = CasoDeUso(
        id_uc="UC02",
        nome="Realizar empréstimo de livro",
        ator_principal=bibliotecario,
        pre_condicao="Leitor cadastrado e livro disponível",
        fluxo_principal=[
            "Bibliotecário identifica o leitor no sistema",
            "Bibliotecário pesquisa o livro desejado",
            "Sistema confirma disponibilidade do exemplar",
            "Bibliotecário confirma o empréstimo",
            "Sistema registra data de empréstimo e devolução prevista",
        ],
        fluxo_alternativo=[
            "Se livro indisponível: sistema exibe mensagem e sugere reserva",
        ],
        pos_condicao="Livro registrado como emprestado ao leitor"
    )

    uc02.descrever()
    print("\n✓ O diagrama de casos de uso captura a visão funcional do sistema.")
    print("  Cada UC descreve uma interação ator-sistema que entrega valor.")
