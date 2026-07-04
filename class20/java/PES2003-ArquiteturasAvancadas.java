/*
 * PES2003 - Arquiteturas Avançadas: Modular Monolith, Microkernel, P2P
 * Aula 20: DDD, Microserviços, Componentes e Interfaces
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Referência comparativa demonstrando três arquiteturas avançadas:
 * - Modular Monolith: módulos isolados, único deploy, chamadas in-process
 * - Microkernel/Plugin: core mínimo + plugins extensíveis
 * - Peer-to-Peer: rede descentralizada de nós autônomos
 *
 * Compilar/executar:
 *   javac PES2003-ArquiteturasAvancadas.java && java ArquiteturasAvancadas
 */

import java.util.*;

// ============================================================
// 1. MODULAR MONOLITH
// ============================================================

abstract class ModuloBase {
    final String nome;
    final List<String> eventos = new ArrayList<>();

    ModuloBase(String nome) { this.nome = nome; }
    abstract Map<String, Object> processar(Map<String, Object> dados);
    void registrarEvento(String evento) { eventos.add(evento); }
}

class ModuloVendas extends ModuloBase {
    ModuloVendas() { super("Vendas"); }

    Map<String, Object> processar(Map<String, Object> dados) {
        int pedidoId = (Integer) dados.getOrDefault("pedido_id", 0);
        registrarEvento("Pedido #" + pedidoId + " criado");
        Map<String, Object> result = new HashMap<>();
        result.put("modulo", "Vendas");
        result.put("status", "OK");
        result.put("pedido_id", pedidoId);
        return result;
    }
}

class ModuloEstoque extends ModuloBase {
    ModuloEstoque() { super("Estoque"); }

    Map<String, Object> processar(Map<String, Object> dados) {
        String produtoId = (String) dados.getOrDefault("produto_id", "");
        int qtd = (Integer) dados.getOrDefault("quantidade", 0);
        registrarEvento("Reserva de " + qtd + " un. do produto " + produtoId);
        Map<String, Object> result = new HashMap<>();
        result.put("modulo", "Estoque");
        result.put("status", "RESERVADO");
        result.put("produto_id", produtoId);
        return result;
    }
}

class ModuloPagamento extends ModuloBase {
    ModuloPagamento() { super("Pagamento"); }

    Map<String, Object> processar(Map<String, Object> dados) {
        double valor = (Double) dados.getOrDefault("valor", 0.0);
        registrarEvento(String.format("Pagamento de R$ %.2f processado", valor));
        Map<String, Object> result = new HashMap<>();
        result.put("modulo", "Pagamento");
        result.put("status", "APROVADO");
        result.put("valor", valor);
        return result;
    }
}

class ModularMonolith {
    final ModuloVendas vendas = new ModuloVendas();
    final ModuloEstoque estoque = new ModuloEstoque();
    final ModuloPagamento pagamento = new ModuloPagamento();

    Map<String, Object> fluxoPedido(int pedidoId, String produtoId, int qtd, double valor) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("fluxo", "pedido_completo");
        resultado.put("vendas", vendas.processar(Map.of("pedido_id", pedidoId)));
        resultado.put("estoque", estoque.processar(Map.of("produto_id", produtoId, "quantidade", qtd)));
        resultado.put("pagamento", pagamento.processar(Map.of("valor", valor)));
        return resultado;
    }
}

// ============================================================
// 2. MICROKERNEL / PLUGIN ARCHITECTURE
// ============================================================

interface IExportPlugin {
    String exportar(List<Map<String, Object>> dados);
    String getExtensao();
}

class PluginPDF implements IExportPlugin {
    public String getExtensao() { return ".pdf"; }

    public String exportar(List<Map<String, Object>> dados) {
        StringBuilder sb = new StringBuilder("[PDF]\n" + "=".repeat(30) + "\n");
        for (var d : dados) {
            sb.append(String.format("  %s | %-15s | R$ %.2f%n",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
        }
        sb.append("=".repeat(30));
        return sb.toString();
    }
}

class PluginCSV implements IExportPlugin {
    public String getExtensao() { return ".csv"; }

    public String exportar(List<Map<String, Object>> dados) {
        StringBuilder sb = new StringBuilder("id,nome,valor\n");
        for (var d : dados) {
            sb.append(String.format("%s,%s,%.2f%n",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
        }
        return sb.toString();
    }
}

class PluginJSON implements IExportPlugin {
    public String getExtensao() { return ".json"; }

    public String exportar(List<Map<String, Object>> dados) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < dados.size(); i++) {
            var d = dados.get(i);
            sb.append(String.format("  {\"id\": %s, \"nome\": \"%s\", \"valor\": %.2f}",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
            if (i < dados.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}

class PluginRegistry {
    private final Map<String, IExportPlugin> plugins = new HashMap<>();

    void registrar(IExportPlugin plugin) { plugins.put(plugin.getExtensao(), plugin); }

    String exportar(String formato, List<Map<String, Object>> dados) {
        IExportPlugin p = plugins.get(formato);
        if (p == null) return "Formato '" + formato + "' não suportado.";
        return p.exportar(dados);
    }

    List<String> getFormatos() { return new ArrayList<>(plugins.keySet()); }
}

// ============================================================
// 3. PEER-TO-PEER (P2P)
// ============================================================

class NoP2P {
    final String nodeId;
    final String recurso;
    private final List<NoP2P> vizinhos = new ArrayList<>();
    final List<String> mensagens = new ArrayList<>();

    NoP2P(String nodeId, String recurso) {
        this.nodeId = nodeId;
        this.recurso = recurso;
    }

    void conectar(NoP2P outro) {
        if (!vizinhos.contains(outro) && outro != this) {
            vizinhos.add(outro);
            outro.vizinhos.add(this);
        }
    }

    List<String> getVizinhos() {
        return vizinhos.stream().map(v -> v.nodeId).toList();
    }

    void compartilhar(String mensagem) {
        mensagens.add("[local] " + mensagem);
        for (NoP2P v : vizinhos) v.receber(nodeId, mensagem);
    }

    void receber(String origem, String mensagem) {
        mensagens.add("[de " + origem + "] " + mensagem);
    }

    Map<String, Object> resumo() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("no", nodeId);
        r.put("recurso", recurso);
        r.put("vizinhos", getVizinhos());
        r.put("mensagens", mensagens);
        return r;
    }
}

// ============================================================
// DEMONSTRAÇÃO
// ============================================================

class ArquiteturasAvancadas {

    static void demoModularMonolith() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  1. MODULAR MONOLITH");
        System.out.println("     Módulos isolados — comunicação in-process — deploy único");
        System.out.println("─".repeat(55));

        ModularMonolith app = new ModularMonolith();
        Map<String, Object> resultado = app.fluxoPedido(1001, "P-ABC", 2, 250.00);
        resultado.forEach((k, v) -> System.out.println("    " + k + ": " + v));

        System.out.println("  ✓ Módulos: Vendas, Estoque, Pagamento");
        System.out.println("  ✓ Vantagem: simplicidade de deploy com isolamento lógico");
    }

    static void demoMicrokernel() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  2. MICROKERNEL / PLUGIN ARCHITECTURE");
        System.out.println("     Core mínimo + plugins dinâmicos");
        System.out.println("─".repeat(55));

        PluginRegistry registry = new PluginRegistry();
        registry.registrar(new PluginPDF());
        registry.registrar(new PluginCSV());
        registry.registrar(new PluginJSON());

        List<Map<String, Object>> dados = new ArrayList<>();
        dados.add(Map.of("id", 1, "nome", "Notebook", "valor", 4500.00));
        dados.add(Map.of("id", 2, "nome", "Monitor", "valor", 1200.00));

        System.out.println("    Formatos disponíveis: " + registry.getFormatos());
        for (String fmt : List.of(".pdf", ".csv", ".json")) {
            System.out.println("\n    --- Exportando " + fmt + " ---");
            System.out.println(registry.exportar(fmt, dados));
        }

        System.out.println("  ✓ Core (Registry) é estável, plugins são extensíveis");
    }

    static void demoP2P() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  3. PEER-TO-PEER (P2P)");
        System.out.println("     Rede descentralizada — nós autônomos");
        System.out.println("─".repeat(55));

        NoP2P a = new NoP2P("Nó-A", "Arquivo: projeto.pdf");
        NoP2P b = new NoP2P("Nó-B", "Arquivo: especificacao.docx");
        NoP2P c = new NoP2P("Nó-C", "Música: jazz.mp3");
        NoP2P d = new NoP2P("Nó-D", "Vídeo: tutorial.mp4");

        a.conectar(b);
        b.conectar(c);
        c.conectar(d);
        a.conectar(c);

        System.out.println("    Topologia: A—B—C—D e A—C");
        System.out.println("    Vizinhos de A: " + a.getVizinhos());
        System.out.println("    Vizinhos de B: " + b.getVizinhos());
        System.out.println("    Vizinhos de C: " + c.getVizinhos());

        a.compartilhar("Novo arquivo disponível: projeto.pdf");
        System.out.println("\n    Mensagens em A: " + a.mensagens);
        System.out.println("    Mensagens em B: " + b.mensagens);

        System.out.println("  ✓ Cada nó é autônomo (cliente + servidor)");
        System.out.println("  ✓ Sem ponto central de falha");
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  ARQUITETURAS AVANÇADAS");
        System.out.println("  Modular Monolith | Microkernel | Peer-to-Peer");
        System.out.println("=".repeat(60));

        demoModularMonolith();
        demoMicrokernel();
        demoP2P();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  COMPARATIVO:");
        System.out.println("  ┌──────────────────┬─────────────────────────────────┐");
        System.out.println("  │ Modular Monolith │ Módulos isolados, deploy único  │");
        System.out.println("  │ Microkernel      │ Core estável + plugins flexíveis│");
        System.out.println("  │ P2P              │ Nós autônomos descentralizados  │");
        System.out.println("  ├──────────────────┼─────────────────────────────────┤");
        System.out.println("  │ Quando usar cada │ Modular: transição p/ µserviços │");
        System.out.println("  │                  │ Microkernel: IDEs, navegadores  │");
        System.out.println("  │                  │ P2P: blockchain, torrent, IoT   │");
        System.out.println("  └──────────────────┴─────────────────────────────────┘");
        System.out.println("=".repeat(60));
    }
}
