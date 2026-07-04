/*
 * PES1802 - Padrões de Projeto GoF (Singleton, Factory Method, Observer, Strategy)
 * Aula 18: Princípios de Projeto e Padrões Arquiteturais
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra 4 padrões GoF com exemplos práticos:
 * - Singleton: gerenciador de configuração (instância única)
 * - Factory Method: criador de documentos (PDF, CSV, HTML)
 * - Observer: sistema de notificação (eventos de pedido)
 * - Strategy: calculadora de frete (PAC, SEDEX, transportadora)
 *
 * Compilar/executar:
 *   javac PES1802-DesignPatterns.java && java DesignPatterns
 */

import java.util.*;

// ============================================================
// 1. SINGLETON — Gerenciador de Configuração (instância única)
// ============================================================

class ConfigManager {
    private static ConfigManager instance;
    private Map<String, String> settings = new HashMap<>();

    private ConfigManager() {
        settings.put("app_name", "PES-EAD");
        settings.put("version", "1.0.0");
        settings.put("language", "pt-BR");
        settings.put("theme", "dark");
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String get(String key) { return settings.getOrDefault(key, null); }

    public void set(String key, String value) { settings.put(key, value); }

    public Map<String, String> listAll() { return new HashMap<>(settings); }
}

// ============================================================
// 2. FACTORY METHOD — Criador de Documentos (PDF, CSV, HTML)
// ============================================================

interface Document {
    String render(List<Map<String, Object>> data);
}

class PdfDocument implements Document {
    public String render(List<Map<String, Object>> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(40)).append("\n")
          .append("  RELATÓRIO PDF\n").append("=".repeat(40)).append("\n\n");
        for (var d : data) {
            sb.append(String.format("  %s | %-20s | R$ %.2f%n",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
        }
        sb.append("\n[PDF gerado com sucesso]");
        return sb.toString();
    }
}

class CsvDocument implements Document {
    public String render(List<Map<String, Object>> data) {
        StringBuilder sb = new StringBuilder("id,nome,valor\n");
        for (var d : data) {
            sb.append(String.format("%s,%s,%.2f%n",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
        }
        return sb.toString();
    }
}

class HtmlDocument implements Document {
    public String render(List<Map<String, Object>> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>\n  <h1>Relatório HTML</h1>\n  <table border='1'>\n");
        for (var d : data) {
            sb.append(String.format("    <tr><td>%s</td><td>%s</td><td>R$ %.2f</td></tr>%n",
                d.get("id"), d.get("nome"), (Double) d.get("valor")));
        }
        sb.append("  </table>\n</body></html>");
        return sb.toString();
    }
}

abstract class DocumentFactory {
    abstract Document createDocument();

    String export(List<Map<String, Object>> data) {
        return createDocument().render(data);
    }
}

class PdfExporter extends DocumentFactory {
    Document createDocument() { return new PdfDocument(); }
}

class CsvExporter extends DocumentFactory {
    Document createDocument() { return new CsvDocument(); }
}

class HtmlExporter extends DocumentFactory {
    Document createDocument() { return new HtmlDocument(); }
}

// ============================================================
// 3. OBSERVER — Sistema de Notificação de Pedidos
// ============================================================

interface Observer {
    void update(String event, Map<String, Object> payload);
}

abstract class Subject {
    protected List<Observer> observers = new ArrayList<>();

    void attach(Observer o) { observers.add(o); }
    void detach(Observer o) { observers.remove(o); }

    void notify(String event, Map<String, Object> payload) {
        for (Observer o : observers) o.update(event, payload);
    }
}

class PedidoService extends Subject {
    Map<String, Object> criarPedido(String cliente, double valor) {
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("cliente", cliente);
        pedido.put("valor", valor);
        pedido.put("status", "CRIADO");
        notify("pedido.criado", pedido);
        return pedido;
    }

    void pagar(Map<String, Object> pedido) {
        pedido.put("status", "PAGO");
        notify("pedido.pago", pedido);
    }
}

class EmailNotifier implements Observer {
    public void update(String event, Map<String, Object> payload) {
        if ("pedido.criado".equals(event))
            System.out.printf("  📧 E-MAIL: Confirmação enviada para %s%n", payload.get("cliente"));
        else if ("pedido.pago".equals(event))
            System.out.printf("  📧 E-MAIL: Pagamento confirmado — R$ %.2f%n", payload.get("valor"));
    }
}

class LogNotifier implements Observer {
    public void update(String event, Map<String, Object> payload) {
        System.out.printf("  📋 LOG [%s] cliente=%s valor=R$ %.2f%n",
            event, payload.get("cliente"), payload.get("valor"));
    }
}

class SMSNotifier implements Observer {
    public void update(String event, Map<String, Object> payload) {
        if ("pedido.pago".equals(event))
            System.out.printf("  📱 SMS: Pagamento de R$ %.2f processado!%n", payload.get("valor"));
    }
}

// ============================================================
// 4. STRATEGY — Calculadora de Frete (PAC, SEDEX, Transportadora)
// ============================================================

interface FreteStrategy {
    double calcular(double pesoKg, double distanciaKm);
    String getNome();
}

class FretePAC implements FreteStrategy {
    public String getNome() { return "PAC (Correios)"; }
    public double calcular(double pesoKg, double distanciaKm) {
        return 12.0 + (pesoKg * 2.5) + (distanciaKm * 0.03);
    }
}

class FreteSEDEX implements FreteStrategy {
    public String getNome() { return "SEDEX (Correios)"; }
    public double calcular(double pesoKg, double distanciaKm) {
        return 18.0 + (pesoKg * 3.8) + (distanciaKm * 0.06);
    }
}

class FreteTransportadora implements FreteStrategy {
    public String getNome() { return "Transportadora Rápida"; }
    public double calcular(double pesoKg, double distanciaKm) {
        return 25.0 + (pesoKg * 1.8) + (distanciaKm * 0.04);
    }
}

class CalculadoraFrete {
    private FreteStrategy estrategia;

    CalculadoraFrete(FreteStrategy estrategia) { this.estrategia = estrategia; }
    void setEstrategia(FreteStrategy estrategia) { this.estrategia = estrategia; }

    String calcular(double pesoKg, double distanciaKm) {
        double valor = estrategia.calcular(pesoKg, distanciaKm);
        return String.format("Frete %s: R$ %.2f", estrategia.getNome(), valor);
    }
}

// ============================================================
// DEMONSTRAÇÃO
// ============================================================

class DesignPatterns {

    static void demoSingleton() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  1. SINGLETON — Gerenciador de Configuração");
        System.out.println("─".repeat(55));
        ConfigManager cfg1 = ConfigManager.getInstance();
        ConfigManager cfg2 = ConfigManager.getInstance();
        System.out.printf("  Mesma instância? %b%n", cfg1 == cfg2);
        System.out.printf("  Config atual: %s%n", cfg1.listAll());
        cfg1.set("theme", "light");
        System.out.printf("  Após cfg1.set('theme','light'): cfg2.get('theme') = %s%n", cfg2.get("theme"));
    }

    static void demoFactoryMethod() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  2. FACTORY METHOD — Exportação de Documentos");
        System.out.println("─".repeat(55));

        List<Map<String, Object>> dados = new ArrayList<>();
        dados.add(Map.of("id", 1, "nome", "Notebook", "valor", 4500.00));
        dados.add(Map.of("id", 2, "nome", "Mouse", "valor", 120.00));

        for (DocumentFactory f : new DocumentFactory[]{new PdfExporter(), new CsvExporter(), new HtmlExporter()}) {
            System.out.printf("%n  --- Exportando %s ---%n",
                f.getClass().getSimpleName().replace("Exporter", ""));
            System.out.println(f.export(dados));
        }
    }

    static void demoObserver() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  3. OBSERVER — Notificação de Pedidos");
        System.out.println("─".repeat(55));

        PedidoService service = new PedidoService();
        service.attach(new EmailNotifier());
        service.attach(new LogNotifier());
        service.attach(new SMSNotifier());

        Map<String, Object> pedido = service.criarPedido("Maria Souza", 250.00);
        service.pagar(pedido);
    }

    static void demoStrategy() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  4. STRATEGY — Cálculo de Frete");
        System.out.println("─".repeat(55));

        double peso = 5.0, distancia = 120.0;
        System.out.printf("  Pacote: %.1fkg, Distância: %.1fkm%n%n", peso, distancia);

        CalculadoraFrete calc = new CalculadoraFrete(new FretePAC());
        System.out.printf("  %s%n", calc.calcular(peso, distancia));

        calc.setEstrategia(new FreteSEDEX());
        System.out.printf("  %s%n", calc.calcular(peso, distancia));

        calc.setEstrategia(new FreteTransportadora());
        System.out.printf("  %s%n", calc.calcular(peso, distancia));
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(55));
        System.out.println("  GoF DESIGN PATTERNS — EXEMPLOS PRÁTICOS");
        System.out.println("=".repeat(55));

        demoSingleton();
        demoFactoryMethod();
        demoObserver();
        demoStrategy();

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  ✓ Singleton: instância única (ConfigManager)");
        System.out.println("  ✓ Factory Method: criação delegada (Document -> PDF/CSV/HTML)");
        System.out.println("  ✓ Observer: notificação em cascata (Pedido -> Email/Log/SMS)");
        System.out.println("  ✓ Strategy: algoritmo intercambiável (Frete PAC/SEDEX/Transportadora)");
        System.out.println("=".repeat(55));
    }
}
