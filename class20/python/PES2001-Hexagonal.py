"""
PES2001 - Arquitetura Hexagonal (Ports & Adapters)
Aula 20: Clean Architecture, Hexagonal e Onion
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa Ports & Adapters com o domínio "Locadora de Veículos":
uma porta (VeiculoRepository), dois adaptadores (MySQL simulado e
memória) e um serviço de domínio que depende apenas da porta,
recebendo o adaptador concreto via injeção de dependência (DIP).
"""

from abc import ABC, abstractmethod


# --- Domínio: entidade ---
class Veiculo:
    def __init__(self, placa, modelo, disponivel=True):
        self.placa = placa
        self.modelo = modelo
        self.disponivel = disponivel

    def __repr__(self):
        status = "disponível" if self.disponivel else "locado"
        return f"{self.placa} - {self.modelo} ({status})"


# --- PORTA (interface no domínio) — secundária/driven ---
class VeiculoRepository(ABC):
    @abstractmethod
    def salvar(self, veiculo) -> None:
        pass

    @abstractmethod
    def buscar_por_placa(self, placa: str):
        pass

    @abstractmethod
    def listar_disponiveis(self) -> list:
        pass


# --- ADAPTADOR — implementação concreta (infraestrutura) ---
class VeiculoMySQLRepository(VeiculoRepository):
    def salvar(self, veiculo):
        print(f"[MySQL] INSERT INTO veiculos ... {veiculo.placa}")

    def buscar_por_placa(self, placa):
        print(f"[MySQL] SELECT ... WHERE placa='{placa}'")
        return None

    def listar_disponiveis(self):
        print("[MySQL] SELECT ... WHERE disponivel=true")
        return []


# --- ADAPTADOR — em memória, usado para testes (sem infraestrutura real) ---
class VeiculoMemoriaRepository(VeiculoRepository):
    def __init__(self):
        self._dados = {}

    def salvar(self, veiculo):
        self._dados[veiculo.placa] = veiculo

    def buscar_por_placa(self, placa):
        return self._dados.get(placa)

    def listar_disponiveis(self):
        return [v for v in self._dados.values() if v.disponivel]


# --- DOMÍNIO — não sabe nada sobre MySQL ou memória! ---
class LocadoraService:
    def __init__(self, repo: VeiculoRepository):
        self.repo = repo  # DIP: depende da porta, não do adaptador

    def cadastrar(self, veiculo):
        self.repo.salvar(veiculo)

    def veiculos_disponiveis(self):
        return self.repo.listar_disponiveis()


if __name__ == "__main__":
    print("=" * 60)
    print("  HEXAGONAL (PORTS & ADAPTERS) — LOCADORA DE VEÍCULOS")
    print("=" * 60)

    print("\n  --- Adaptador MySQL (simulado) ---")
    service_mysql = LocadoraService(VeiculoMySQLRepository())
    service_mysql.cadastrar(Veiculo("ABC1234", "Fusca"))

    print("\n  --- Adaptador em Memória (testes, sem infraestrutura real) ---")
    service_memoria = LocadoraService(VeiculoMemoriaRepository())
    service_memoria.cadastrar(Veiculo("XYZ9876", "Gol"))
    service_memoria.cadastrar(Veiculo("DEF4567", "Onix", disponivel=False))

    disponiveis = service_memoria.veiculos_disponiveis()
    print(f"  Veículos disponíveis: {len(disponiveis)}")
    for v in disponiveis:
        print(f"    {v}")

    assert len(disponiveis) == 1

    print("\n✓ Porta (VeiculoRepository): contrato definido pelo domínio.")
    print("✓ Adaptadores: MySQL e Memória são intercambiáveis via DIP.")
    print("✓ Trocar o adaptador não muda uma linha do domínio (LocadoraService).")
