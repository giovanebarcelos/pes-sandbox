/*
 * PES1903 - Clean, Onion e Hexagonal: Comparativo com o Mesmo Domínio
 * Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Exemplo comparativo: o mesmo domínio (Pedido) estruturado em três
 * estilos arquiteturais — Clean, Onion e Hexagonal — mostrando como
 * cada um organiza as dependências e camadas.
 *
 * Domínio comum: Gerenciamento de pedidos com ItemPedido e cálculo de total.
 *
 * Compilar/executar:
 *   javac PES1903-CleanOnionHexagonal.java && java CleanOnionHexagonal
 */

import java.util.*;

// ============================================================
// DOMÍNIO COMPARTILHADO (entidades, value objects, regras)
// ============================================================

class Dinheiro {
    final double valor;

    Dinheiro(double valor) { this.valor = valor; }

    Dinheiro somar(Dinheiro outro) { return new Dinheiro(this.valor + outro.valor); }

    @Override
    public String toString() { return String.format("R$ %,.2f", valor); }
}

class ItemPedido {
    final String produtoId, nome;
    final Dinheiro preco;
    final int quantidade;

    ItemPedido(String produtoId, String nome, Dinheiro preco, int quantidade) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    Dinheiro subtotal() { return new Dinheiro(preco.valor * quantidade); }

    @Override
    public String toString() {
        return String.format("%s x%d %s", nome, quantidade, subtotal());
    }
}

class Pedido {
    final int pedidoId;
    final String cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private String status = "ABERTO";

    Pedido(int pedidoId, String cliente) {
        this.pedidoId = pedidoId;
        this.cliente = cliente;
    }

    void adicionarItem(String produtoId, String nome, double preco, int qtd) {
        itens.add(new ItemPedido(produtoId, nome, new Dinheiro(preco), qtd));
    }

    Dinheiro total() {
        Dinheiro t = new Dinheiro(0);
        for (ItemPedido i : itens) t = t.somar(i.subtotal());
        return t;
    }

    void fechar() {
        if (itens.isEmpty()) throw new IllegalStateException("Pedido sem itens.");
        status = "FECHADO";
    }

    String getStatus() { return status; }
    List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }

    @Override
    public String toString() {
        return String.format("Pedido[%d] %s — %s — %s", pedidoId, cliente, status, total());
    }
}

// ============================================================
// INTERFACE DE REPOSITÓRIO (porta — usada por todos os estilos)
// ============================================================

interface PedidoRepository {
    void salvar(Pedido pedido);
    Pedido buscarPorId(int pedidoId);
}

// ============================================================
// 1. CLEAN ARCHITECTURE
// ============================================================

class PedidoRepositoryEmMemoria implements PedidoRepository {
    private final Map<Integer, Pedido> db = new HashMap<>();

    public void salvar(Pedido p) { db.put(p.pedidoId, p); }
    public Pedido buscarPorId(int id) { return db.get(id); }
}

// Caso de Uso (Application Layer)
class CriarPedidoUseCase {
    private final PedidoRepository repo;

    CriarPedidoUseCase(PedidoRepository repo) { this.repo = repo; }

    Pedido executar(int id, String cliente, Object[][] itens) {
        Pedido p = new Pedido(id, cliente);
        for (Object[] item : itens) {
            p.adicionarItem((String) item[0], (String) item[1],
                (Double) item[2], (Integer) item[3]);
        }
        p.fechar();
        repo.salvar(p);
        return p;
    }
}

// Controller (Interface Adapter)
class PedidoController {
    private final CriarPedidoUseCase usecase;

    PedidoController(CriarPedidoUseCase usecase) { this.usecase = usecase; }

    String criarPedido(int id, String cliente, Object[][] itens) {
        Pedido p = usecase.executar(id, cliente, itens);
        return String.format("[Clean] Pedido #%d | %s | %s | %s",
            p.pedidoId, p.cliente, p.total(), p.getStatus());
    }
}

// ============================================================
// 2. ONION ARCHITECTURE
// ============================================================

class OnionPedidoService {
    private final PedidoRepository repo;

    OnionPedidoService(PedidoRepository repo) { this.repo = repo; }

    Pedido criarEFechar(int id, String cliente, Object[][] itens) {
        Pedido p = new Pedido(id, cliente);
        for (Object[] item : itens) {
            p.adicionarItem((String) item[0], (String) item[1],
                (Double) item[2], (Integer) item[3]);
        }
        p.fechar();
        repo.salvar(p);
        return p;
    }

    String resumo(int id) {
        Pedido p = repo.buscarPorId(id);
        if (p == null) return null;
        return String.format("[Onion] Pedido #%d — %s — %s (%s)",
            p.pedidoId, p.cliente, p.total(), p.getStatus());
    }
}

// ============================================================
// 3. HEXAGONAL ARCHITECTURE (Ports & Adapters)
// ============================================================

class HexagonalPedidoHandler {
    private final PedidoRepository repo;

    HexagonalPedidoHandler(PedidoRepository repo) { this.repo = repo; }

    String handleCriarPedido(Map<String, Object> command) {
        int id = (Integer) command.get("pedido_id");
        String cliente = (String) command.get("cliente");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itens = (List<Map<String, Object>>) command.get("itens");

        Pedido p = new Pedido(id, cliente);
        for (Map<String, Object> item : itens) {
            p.adicionarItem((String) item.get("id"), (String) item.get("nome"),
                (Double) item.get("preco"), (Integer) item.get("qtd"));
        }
        p.fechar();
        repo.salvar(p);

        return String.format("[Hexagonal] Pedido #%d | %s | %s | %s",
            p.pedidoId, p.cliente, p.total(), p.getStatus());
    }

    String handleConsultar(int id) {
        Pedido p = repo.buscarPorId(id);
        if (p == null) return "Pedido não encontrado.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[Hexagonal] #%d — %s — %s (%s) | Itens: ",
            p.pedidoId, p.cliente, p.total(), p.getStatus()));
        for (ItemPedido i : p.getItens())
            sb.append(i.nome).append(" x").append(i.quantidade).append(", ");
        return sb.substring(0, sb.length() - 2);
    }
}

// ============================================================
// DEMONSTRAÇÃO COMPARATIVA
// ============================================================

class CleanOnionHexagonal {

    static void demoClean() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  1. CLEAN ARCHITECTURE");
        System.out.println("     Entities → Use Cases → Controllers → Repositories");
        System.out.println("─".repeat(55));

        PedidoRepository repo = new PedidoRepositoryEmMemoria();
        CriarPedidoUseCase usecase = new CriarPedidoUseCase(repo);
        PedidoController controller = new PedidoController(usecase);

        Object[][] itens = {
            {"P01", "Monitor 24\"", 1200.00, 1},
            {"P02", "Teclado Mecânico", 350.00, 2},
        };
        System.out.println("  " + controller.criarPedido(1001, "Carlos Oliveira", itens));
        System.out.println("  ✓ Domínio isolado → Controller → UseCase → Repository");
    }

    static void demoOnion() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  2. ONION ARCHITECTURE");
        System.out.println("     Domain ← Application ← Infrastructure ← Presentation");
        System.out.println("─".repeat(55));

        PedidoRepository repo = new PedidoRepositoryEmMemoria();
        OnionPedidoService service = new OnionPedidoService(repo);

        Object[][] itens = {
            {"P03", "Notebook", 4500.00, 1},
            {"P04", "Mouse", 120.00, 1},
        };
        service.criarEFechar(2001, "Ana Beatriz", itens);
        System.out.println("  " + service.resumo(2001));
        System.out.println("  ✓ Camadas concêntricas: dependência sempre para dentro");
    }

    static void demoHexagonal() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  3. HEXAGONAL (PORTS & ADAPTERS)");
        System.out.println("     Portas (interfaces) + Adaptadores (concretos)");
        System.out.println("─".repeat(55));

        PedidoRepository repo = new PedidoRepositoryEmMemoria();
        HexagonalPedidoHandler handler = new HexagonalPedidoHandler(repo);

        Map<String, Object> cmd = new HashMap<>();
        cmd.put("pedido_id", 3001);
        cmd.put("cliente", "Roberto Lima");
        List<Map<String, Object>> itens = new ArrayList<>();
        itens.add(Map.of("id", "P05", "nome", "SSD 1TB", "preco", 600.00, "qtd", 2));
        itens.add(Map.of("id", "P06", "nome", "Cabo HDMI", "preco", 45.00, "qtd", 3));
        cmd.put("itens", itens);

        System.out.println("  " + handler.handleCriarPedido(cmd));
        System.out.println("  " + handler.handleConsultar(3001));
        System.out.println("  ✓ Domínio 100% isolado por portas; adaptadores trocáveis");
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  CLEAN × ONION × HEXAGONAL — MESMO DOMÍNIO");
        System.out.println("=".repeat(60));
        System.out.println("\n  Domínio comum: Pedido + ItemPedido + Dinheiro (Value Object)");

        demoClean();
        demoOnion();
        demoHexagonal();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  COMPARATIVO:");
        System.out.println("  ┌──────────┬──────────────────────────────────┐");
        System.out.println("  │ Clean    │ Entities → UseCases → Adapters   │");
        System.out.println("  │ Onion    │ Domain ← App ← Infra ← Present.  │");
        System.out.println("  │ Hexagonal│ Ports (interfaces) + Adapters     │");
        System.out.println("  ├──────────┼──────────────────────────────────┤");
        System.out.println("  │ TODOS    │ Domínio isolado de frameworks     │");
        System.out.println("  │ TODOS    │ Dependência aponta para dentro    │");
        System.out.println("  │ TODOS    │ Testabilidade e troca de infra    │");
        System.out.println("  └──────────┴──────────────────────────────────┘");
        System.out.println("=".repeat(60));
    }
}
