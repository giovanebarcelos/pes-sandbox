/*
 * PES2202 - Microkernel (Plugin Architecture)
 * Aula 22: Microsserviços e Arquiteturas Distribuídas
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa um core mínimo (MicrokernelCore) que não conhece nenhum
 * plugin concreto, apenas o contrato IExportPlugin. Novos formatos de
 * exportação (PDF, CSV, ...) entram como plugins registrados em tempo
 * de execução, sem alterar uma linha do core.
 *
 * Compilar/executar:
 *   javac PES2202-Microkernel.java && java Microkernel
 */

import java.util.*;

// CONTRATO que todo plugin de exportação deve implementar
interface IExportPlugin {
    String exportar(Map<String, String> dados);
}

class PluginPDF implements IExportPlugin {
    @Override
    public String exportar(Map<String, String> dados) {
        return "[PDF] Exportando " + dados.size() + " registros";
    }
}

class PluginCSV implements IExportPlugin {
    @Override
    public String exportar(Map<String, String> dados) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : dados.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return "[CSV] " + sb;
    }
}

// CORE: não conhece PluginPDF nem PluginCSV, só o contrato IExportPlugin
class MicrokernelCore {
    private final Map<String, IExportPlugin> plugins = new HashMap<>();

    void registrarPlugin(String nome, IExportPlugin plugin) {
        plugins.put(nome, plugin);
        System.out.println("[Core] Plugin '" + nome + "' registrado.");
    }

    String exportarCom(String nome, Map<String, String> dados) {
        IExportPlugin plugin = plugins.get(nome);
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin '" + nome + "' não registrado");
        }
        return plugin.exportar(dados);
    }
}

class Microkernel {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  MICROKERNEL (PLUGIN) — EXPORTAÇÃO DE DADOS");
        System.out.println("=".repeat(60));

        MicrokernelCore core = new MicrokernelCore();
        core.registrarPlugin("pdf", new PluginPDF());
        core.registrarPlugin("csv", new PluginCSV());

        Map<String, String> dados = new LinkedHashMap<>();
        dados.put("nome", "Fusca");
        dados.put("placa", "ABC1234");

        System.out.println("\n" + core.exportarCom("pdf", dados));
        System.out.println(core.exportarCom("csv", dados));

        try {
            core.exportarCom("xml", dados);
        } catch (IllegalArgumentException e) {
            System.out.println("\n[Esperado] " + e.getMessage());
        }

        System.out.println("\n✓ Core nunca importa PluginPDF/PluginCSV diretamente, só IExportPlugin.");
        System.out.println("✓ Adicionar um novo formato (ex.: XML) não exige alterar o core.");
    }
}
