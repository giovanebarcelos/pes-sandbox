"""
PES1702 - Locadora de Veículos (Esqueleto/Contingência)
Aula 17: Projeto Parcial — Modelagem UML + Implementação OO
Projeto e Engenharia de Software - Prof. Giovane Barcelos

CÓDIGO-BASE PARA DISTRIBUIÇÃO EM SALA (contingência de tempo):
Este esqueleto contém a classe abstrata Veiculo e a estrutura de Locacao/Locadora
prontas. Os alunos precisam apenas:
  (1) implementar calcular_locacao() em Carro e Moto (polimorfismo)
  (2) cadastrar veículos e simular locações no bloco __main__

Regras de negócio (do Slide 8 da Aula 17):
  - Carro: valor = diária × dias × 1.10 (acréscimo de 10% — taxa de seguro)
  - Moto:  valor = diária × dias (sem acréscimo)

Uso:
  python PES1702-LocadoraEsqueleto.py
"""

from abc import ABC, abstractmethod


class Veiculo(ABC):
    """Classe abstrata base para todos os veículos (NÃO MODIFICAR)."""

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
        """TODO (aluno): cada tipo de veículo implementa sua regra."""
        pass

    def __repr__(self):
        return f"{self.__class__.__name__}[{self.__placa}] {self.__modelo}"


# ============================================================================
# TODO (aluno): implementar calcular_locacao() em Carro e Moto
# ============================================================================

class Carro(Veiculo):
    """Carro: valor = diária × dias × 1.10 (+10% taxa de seguro)."""

    def __init__(self, placa, modelo, valor_diaria, num_portas=4):
        super().__init__(placa, modelo, valor_diaria)
        self.num_portas = num_portas

    def calcular_locacao(self, dias):
        # TODO: retornar diária × dias × 1.10
        raise NotImplementedError("Implemente calcular_locacao para Carro")


class Moto(Veiculo):
    """Moto: valor = diária × dias (sem acréscimo)."""

    def __init__(self, placa, modelo, valor_diaria, cilindradas=150):
        super().__init__(placa, modelo, valor_diaria)
        self.cilindradas = cilindradas

    def calcular_locacao(self, dias):
        # TODO: retornar diária × dias
        raise NotImplementedError("Implemente calcular_locacao para Moto")


# ============================================================================
# Classes de apoio (NÃO MODIFICAR — já prontas)
# ============================================================================

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


# ============================================================================
# TODO (aluno): cadastrar veículos e simular locações
# ============================================================================

if __name__ == "__main__":
    print("=" * 60)
    print("  LOCADORA DE VEÍCULOS — ESQUELETO (preencha os TODOs)")
    print("=" * 60)

    locadora = Locadora()

    # TODO: cadastrar 3 veículos (2 carros, 1 moto)
    # locadora.cadastrar(Carro("ABC-1234", "Fiat Uno", 120.00, 4))
    # locadora.cadastrar(Carro("DEF-5678", "Honda Civic", 200.00, 4))
    # locadora.cadastrar(Moto("XYZ-9012", "Honda CG 160", 80.00, 160))

    locadora.listar_disponiveis()

    # TODO: alugar 2 veículos
    # locadora.alugar("ABC-1234", "Ana Silva", 5)
    # locadora.alugar("XYZ-9012", "Carlos Santos", 3)

    # print(f"\n  ✓ Faturamento Total: R$ {locadora.faturamento_total():.2f}")
    print("\n  ⚠ Preencha os TODOs acima para executar o sistema completo.")
