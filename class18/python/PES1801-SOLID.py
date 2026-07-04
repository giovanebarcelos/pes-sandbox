"""
PES1801 - Princípios SOLID (Antes/Depois)
Aula 18: Princípios de Projeto e Padrões Arquiteturais
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra a aplicação dos princípios SOLID comparando código
antes (violando) e depois (aplicando) SRP, OCP e DIP.
"""

from abc import ABC, abstractmethod

# -----------------------------------------------------------
# ❌ ANTES: violando SRP, OCP e DIP
# -----------------------------------------------------------


class RelatorioAntes:
    """Faz tudo: calcula, formata PDF, envia email — viola SRP."""

    def gerar(self, dados, formato):
        total = sum(dados)
        if formato == "PDF":
            return f"Relatório PDF: Total = {total}"
        elif formato == "CSV":
            return f"Total,{total}"
        else:
            raise ValueError("Formato não suportado")


# -----------------------------------------------------------
# ✅ DEPOIS: aplicando SRP, OCP, DIP
# -----------------------------------------------------------


class Calculadora:
    """SRP: responsabilidade única — apenas calcular."""

    @staticmethod
    def total(dados):
        return sum(dados)


class Formatador(ABC):
    """OCP: aberto para extensão (novos formatos), fechado para modificação."""

    @abstractmethod
    def formatar(self, total):
        pass


class FormatadorPDF(Formatador):
    def formatar(self, total):
        return f"Relatório PDF: Total = {total}"


class FormatadorCSV(Formatador):
    def formatar(self, total):
        return f"Total,{total}"


class GeradorRelatorio:
    """DIP: depende de abstração (Formatador), não de implementação concreta."""

    def __init__(self, formatador: Formatador):
        self.formatador = formatador

    def gerar(self, dados):
        total = Calculadora.total(dados)
        return self.formatador.formatar(total)


if __name__ == "__main__":
    dados = [100, 200, 300]

    print("=" * 60)
    print("  PRINCÍPIOS SOLID — ANTES × DEPOIS")
    print("=" * 60)

    print("\n--- ❌ Antes (viola SRP/OCP/DIP) ---")
    antes = RelatorioAntes()
    print(f"  PDF: {antes.gerar(dados, 'PDF')}")
    print(f"  CSV: {antes.gerar(dados, 'CSV')}")

    print("\n--- ✅ Depois (SRP + OCP + DIP) ---")
    for fmt in [FormatadorPDF(), FormatadorCSV()]:
        gerador = GeradorRelatorio(fmt)
        print(f"  {gerador.gerar(dados)}")

    print("\n✓ SRP: cada classe tem uma única responsabilidade.")
    print("✓ OCP: novos formatos sem alterar código existente.")
    print("✓ DIP: dependência de abstrações, não de implementações.")
