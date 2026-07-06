"""
PES2202 - Microkernel (Plugin Architecture)
Aula 22: Microsserviços e Arquiteturas Distribuídas
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa um core mínimo (MicrokernelCore) que não conhece nenhum
plugin concreto, apenas o contrato IExportPlugin. Novos formatos de
exportação (PDF, CSV, ...) entram como plugins registrados em tempo
de execução, sem alterar uma linha do core.
"""

from abc import ABC, abstractmethod


# CONTRATO que todo plugin de exportação deve implementar
class IExportPlugin(ABC):
    @abstractmethod
    def exportar(self, dados: dict) -> str:
        pass


class PluginPDF(IExportPlugin):
    def exportar(self, dados: dict) -> str:
        return f"[PDF] Exportando {len(dados)} registros"


class PluginCSV(IExportPlugin):
    def exportar(self, dados: dict) -> str:
        linhas = ";".join(f"{k}={v}" for k, v in dados.items())
        return f"[CSV] {linhas}"


# CORE: não conhece PluginPDF nem PluginCSV, só o contrato IExportPlugin
class MicrokernelCore:
    def __init__(self):
        self._plugins: dict[str, IExportPlugin] = {}

    def registrar_plugin(self, nome: str, plugin: IExportPlugin) -> None:
        self._plugins[nome] = plugin
        print(f"[Core] Plugin '{nome}' registrado.")

    def exportar_com(self, nome: str, dados: dict) -> str:
        if nome not in self._plugins:
            raise ValueError(f"Plugin '{nome}' não registrado")
        return self._plugins[nome].exportar(dados)


if __name__ == "__main__":
    print("=" * 60)
    print("  MICROKERNEL (PLUGIN) — EXPORTAÇÃO DE DADOS")
    print("=" * 60)

    core = MicrokernelCore()
    core.registrar_plugin("pdf", PluginPDF())
    core.registrar_plugin("csv", PluginCSV())

    dados = {"nome": "Fusca", "placa": "ABC1234"}
    print("\n" + core.exportar_com("pdf", dados))
    print(core.exportar_com("csv", dados))

    try:
        core.exportar_com("xml", dados)
    except ValueError as e:
        print(f"\n[Esperado] {e}")

    print("\n✓ Core nunca importa PluginPDF/PluginCSV diretamente, só IExportPlugin.")
    print("✓ Adicionar um novo formato (ex.: XML) não exige alterar o core.")
