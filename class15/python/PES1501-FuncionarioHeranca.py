"""
PES1501 - Funcionário com Herança e Polimorfismo
Aula 15: Diagrama de Classes e Orientação a Objetos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra herança, polimorfismo e encapsulamento com uma hierarquia
Funcionario → Desenvolvedor, Gerente.
"""

from abc import ABC, abstractmethod


class Funcionario(ABC):
    """Classe abstrata base — encapsula nome e salário base."""

    def __init__(self, nome, salario_base):
        self.__nome = nome
        self.__salario_base = float(salario_base)

    @property
    def nome(self):
        return self.__nome

    @property
    def salario_base(self):
        return self.__salario_base

    @abstractmethod
    def calcular_salario(self):
        """Polimorfismo: cada subclasse implementa sua regra."""
        pass

    def __repr__(self):
        return f"{self.__class__.__name__}[nome={self.__nome}, salário=R$ {self.calcular_salario():.2f}]"


class Desenvolvedor(Funcionario):
    """Desenvolvedor: salário base + 15% bônus técnico."""

    def __init__(self, nome, salario_base, linguagem):
        super().__init__(nome, salario_base)
        self.linguagem = linguagem

    def calcular_salario(self):
        return self.salario_base * 1.15


class Gerente(Funcionario):
    """Gerente: salário base + bônus fixo de R$ 2000."""

    def __init__(self, nome, salario_base, bonus=2000.0):
        super().__init__(nome, salario_base)
        self.bonus = bonus

    def calcular_salario(self):
        return self.salario_base + self.bonus


def processar_folha(funcionarios):
    """Polimorfismo: mesmo método chamado em objetos diferentes."""
    print(f"\n{'Nome':<20} {'Cargo':<18} {'Salário Final':>15}")
    print("-" * 55)
    total = 0
    for f in funcionarios:
        sal = f.calcular_salario()
        total += sal
        print(f"{f.nome:<20} {f.__class__.__name__:<18} R$ {sal:>12.2f}")
    print("-" * 55)
    print(f"{'TOTAL':<20} {' ':>18} R$ {total:>12.2f}")


if __name__ == "__main__":
    equipe = [
        Desenvolvedor("Ana Silva", 8000, "Python"),
        Desenvolvedor("Carlos Santos", 7500, "Java"),
        Gerente("Mariana Costa", 12000),
        Gerente("Roberto Lima", 11000, bonus=2500),
    ]

    print("=" * 60)
    print("  FOLHA DE PAGAMENTO — HERANÇA E POLIMORFISMO")
    print("=" * 60)

    processar_folha(equipe)
    print("\n✓ Polimorfismo: cada subclasse implementa calcular_salario() de forma diferente.")
