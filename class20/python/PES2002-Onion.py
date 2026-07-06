"""
PES2002 - Onion Architecture (Camadas Concêntricas)
Aula 20: Clean Architecture, Hexagonal e Onion
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa as 4 camadas da Onion com o domínio "Locadora de Veículos":
Domínio (centro) define a interface de repositório; Aplicação orquestra
o caso de uso; Infraestrutura implementa a interface do Domínio (a
dependência de compilação aponta para dentro, mesmo estando "por fora"
no desenho); Apresentação monta tudo via injeção de dependência.
"""

from abc import ABC, abstractmethod


# --- DOMÍNIO (centro): entidade + interface de repositório (porta) ---
class Veiculo:
    def __init__(self, placa, modelo, disponivel=True):
        self.placa = placa
        self.modelo = modelo
        self.disponivel = disponivel

    def __repr__(self):
        status = "disponível" if self.disponivel else "locado"
        return f"{self.placa} - {self.modelo} ({status})"


class VeiculoRepository(ABC):
    @abstractmethod
    def salvar(self, veiculo) -> None:
        pass

    @abstractmethod
    def listar_disponiveis(self) -> list:
        pass


# --- APLICAÇÃO: caso de uso, depende só da interface do Domínio ---
class LocadoraService:
    def __init__(self, repo: VeiculoRepository):
        self.repo = repo

    def cadastrar(self, veiculo: Veiculo) -> None:
        self.repo.salvar(veiculo)

    def veiculos_disponiveis(self) -> list:
        return self.repo.listar_disponiveis()


# --- INFRAESTRUTURA: implementa a interface do Domínio ---
# Dependência de compilação aponta para dentro (Infra -> Domínio),
# mesmo que no desenho da Onion a Infraestrutura fique "por fora".
class VeiculoSQLRepository(VeiculoRepository):
    def __init__(self):
        self._tabela = {}

    def salvar(self, veiculo):
        print(f"[SQL] INSERT INTO veiculos ... {veiculo.placa}")
        self._tabela[veiculo.placa] = veiculo

    def listar_disponiveis(self):
        return [v for v in self._tabela.values() if v.disponivel]


# --- APRESENTAÇÃO: monta a aplicação via injeção de dependência ---
def montar_aplicacao() -> LocadoraService:
    repo = VeiculoSQLRepository()          # Infraestrutura
    service = LocadoraService(repo)        # Aplicação recebe a porta
    return service


if __name__ == "__main__":
    print("=" * 60)
    print("  ONION ARCHITECTURE — LOCADORA DE VEÍCULOS")
    print("=" * 60)

    service = montar_aplicacao()
    service.cadastrar(Veiculo("ABC1234", "Fusca"))
    service.cadastrar(Veiculo("XYZ9876", "Gol"))
    service.cadastrar(Veiculo("DEF4567", "Onix", disponivel=False))

    disponiveis = service.veiculos_disponiveis()
    print(f"\n  Veículos disponíveis: {len(disponiveis)}")
    for v in disponiveis:
        print(f"    {v}")

    assert len(disponiveis) == 2

    print("\n✓ Domínio (centro): define VeiculoRepository, não conhece SQL.")
    print("✓ Infraestrutura: implementa a interface do Domínio (dependência aponta para dentro).")
    print("✓ Apresentação: monta tudo via injeção de dependência (montar_aplicacao).")
