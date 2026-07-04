"""
PES1701 - Mini-Sistema Locadora de Veículos
Aula 17: Projeto Parcial — Modelagem UML + Implementação OO
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Sistema completo demonstrando herança, polimorfismo e encapsulamento
em um domínio de locadora de veículos (Carro, Moto).
"""

from abc import ABC, abstractmethod


class Veiculo(ABC):
    """Classe abstrata base para todos os veículos."""

    def __init__(self, placa, modelo, valor_diaria):
        self.__placa = placa
        self.__modelo = modelo
        self.__valor_diaria = float(valor_diaria)

    @property
    def placa(self):
        return self.__placa

    @property
    def modelo(self):
        return self.__modelo

    @property
    def valor_diaria(self):
        return self.__valor_diaria

    @abstractmethod
    def calcular_locacao(self, dias):
        """Polimorfismo: cada tipo de veículo tem sua regra de cálculo."""
        pass

    def __repr__(self):
        return f"{self.__class__.__name__}[{self.__placa}] {self.__modelo}"


class Carro(Veiculo):
    """Carro: valor = diária × dias + taxa de seguro (10%)."""

    def __init__(self, placa, modelo, valor_diaria, num_portas=4):
        super().__init__(placa, modelo, valor_diaria)
        self.num_portas = num_portas

    def calcular_locacao(self, dias):
        return self.valor_diaria * dias * 1.10  # +10% seguro


class Moto(Veiculo):
    """Moto: valor = diária × dias (sem seguro adicional)."""

    def __init__(self, placa, modelo, valor_diaria, cilindradas=150):
        super().__init__(placa, modelo, valor_diaria)
        self.cilindradas = cilindradas

    def calcular_locacao(self, dias):
        return self.valor_diaria * dias


class Locacao:
    """Registra uma locação de veículo."""

    def __init__(self, veiculo, cliente, dias):
        self.__veiculo = veiculo
        self.__cliente = cliente
        self.__dias = dias
        self.__valor_total = veiculo.calcular_locacao(dias)

    @property
    def valor_total(self):
        return self.__valor_total

    def resumo(self):
        print(f"  Veículo: {self.__veiculo}")
        print(f"  Cliente: {self.__cliente}")
        print(f"  Dias: {self.__dias}")
        print(f"  Valor Total: R$ {self.__valor_total:.2f}")


class Locadora:
    """Gerencia a frota e as locações."""

    def __init__(self):
        self.__frota = []
        self.__locacoes = []

    def cadastrar(self, veiculo):
        self.__frota.append(veiculo)
        print(f"  ✓ Cadastrado: {veiculo}")

    def listar_disponiveis(self):
        print(f"\n  --- Frota ({len(self.__frota)} veículos) ---")
        for v in self.__frota:
            print(f"  • {v} — R$ {v.valor_diaria:.2f}/dia")

    def alugar(self, placa, cliente, dias):
        for v in self.__frota:
            if v.placa == placa:
                loc = Locacao(v, cliente, dias)
                self.__locacoes.append(loc)
                print(f"\n  ✓ Locação registrada:")
                loc.resumo()
                return loc
        print(f"  ✗ Veículo {placa} não encontrado.")
        return None

    def faturamento_total(self):
        return sum(loc.valor_total for loc in self.__locacoes)


if __name__ == "__main__":
    print("=" * 60)
    print("  LOCADORA DE VEÍCULOS — SISTEMA OO")
    print("=" * 60)

    locadora = Locadora()

    # Cadastro
    print("\n--- Cadastro de Veículos ---")
    locadora.cadastrar(Carro("ABC-1234", "Fiat Uno", 120.00, 4))
    locadora.cadastrar(Carro("DEF-5678", "Honda Civic", 200.00, 4))
    locadora.cadastrar(Moto("XYZ-9012", "Honda CG 160", 80.00, 160))

    locadora.listar_disponiveis()

    # Locações
    print("\n--- Locações ---")
    locadora.alugar("ABC-1234", "Ana Silva", 5)
    locadora.alugar("XYZ-9012", "Carlos Santos", 3)

    print(f"\n  ✓ Faturamento Total: R$ {locadora.faturamento_total():.2f}")
    print("\n✓ Sistema completo com herança, polimorfismo e encapsulamento.")
