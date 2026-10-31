/*
 * PES1803 - Exemplos dos 18 padrões GoF da tabela do Slide 23
 * Aula 18: Princípios de Projeto e Padrões Arquiteturais
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Criacionais:     Singleton, Factory Method, Abstract Factory, Builder, Prototype
 * Estruturais:     Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
 * Comportamentais: Observer, Strategy, Command, Iterator, State, Template Method
 *
 * Compilar/executar:
 *   javac PES1803-PadroesGoF.java && java GoFPatterns
 */

import java.util.*;
import java.util.function.Consumer;

class GoFPatterns {

    static void titulo(String t) { System.out.println("\n=== " + t + " ==="); }

    // ───────────────────────── CRIACIONAIS ─────────────────────────

    // 1. Singleton
    static class DatabaseConnection {
        private static DatabaseConnection instance;
        private DatabaseConnection() {}
        static synchronized DatabaseConnection getInstance() {
            if (instance == null) instance = new DatabaseConnection();
            return instance;
        }
    }

    static void demoSingleton() {
        titulo("1. Singleton");
        System.out.println("Mesma instância? " +
            (DatabaseConnection.getInstance() == DatabaseConnection.getInstance()));
    }

    // 2. Factory Method
    interface Transporte { String entregar(); }
    static class Caminhao implements Transporte { public String entregar() { return "Entrega por estrada (caminhão)"; } }
    static class Navio implements Transporte { public String entregar() { return "Entrega por mar (navio)"; } }
    static abstract class TransporteFactory { abstract Transporte criarTransporte(); }
    static class RodoviarioFactory extends TransporteFactory { Transporte criarTransporte() { return new Caminhao(); } }
    static class MaritimoFactory extends TransporteFactory { Transporte criarTransporte() { return new Navio(); } }

    static void demoFactoryMethod() {
        titulo("2. Factory Method");
        for (TransporteFactory f : List.of(new RodoviarioFactory(), new MaritimoFactory()))
            System.out.println(f.criarTransporte().entregar());
    }

    // 3. Abstract Factory
    interface Botao { String desenhar(); }
    interface Checkbox { String desenhar(); }
    interface GUIFactory { Botao criarBotao(); Checkbox criarCheckbox(); }
    static class DarkFactory implements GUIFactory {
        public Botao criarBotao() { return () -> "Botão escuro"; }
        public Checkbox criarCheckbox() { return () -> "Checkbox escuro"; }
    }
    static class LightFactory implements GUIFactory {
        public Botao criarBotao() { return () -> "Botão claro"; }
        public Checkbox criarCheckbox() { return () -> "Checkbox claro"; }
    }

    static void demoAbstractFactory() {
        titulo("3. Abstract Factory");
        for (GUIFactory f : List.of(new DarkFactory(), new LightFactory()))
            System.out.println(f.criarBotao().desenhar() + " + " + f.criarCheckbox().desenhar());
    }

    // 4. Builder
    static class Pizza {
        String borda = "tradicional";
        List<String> coberturas = new ArrayList<>();
        public String toString() { return "Pizza de borda " + borda + " com " + String.join(", ", coberturas); }
    }
    static class PizzaBuilder {
        private final Pizza pizza = new Pizza();
        PizzaBuilder borda(String b) { pizza.borda = b; return this; }
        PizzaBuilder cobertura(String c) { pizza.coberturas.add(c); return this; }
        Pizza build() { return pizza; }
    }

    static void demoBuilder() {
        titulo("4. Builder");
        System.out.println(new PizzaBuilder().borda("fina").cobertura("queijo").cobertura("tomate").build());
    }

    // 5. Prototype
    static class Documento {
        String texto; List<String> tags;
        Documento(String texto, List<String> tags) { this.texto = texto; this.tags = tags; }
        Documento clonar() { return new Documento(texto, new ArrayList<>(tags)); } // cópia profunda das tags
    }

    static void demoPrototype() {
        titulo("5. Prototype");
        Documento modelo = new Documento("Contrato padrão", new ArrayList<>(List.of("jurídico")));
        Documento copia = modelo.clonar();
        copia.tags.add("cliente-X");
        System.out.println("Modelo: " + modelo.tags + " | Cópia: " + copia.tags);
    }

    // ───────────────────────── ESTRUTURAIS ─────────────────────────

    // 6. Adapter
    static class PagamentoLegado { String efetuarPgto(int centavos) { return "Legado: pagos " + centavos + " centavos"; } }
    static class PagamentoAdapter {
        private final PagamentoLegado legado;
        PagamentoAdapter(PagamentoLegado l) { legado = l; }
        String pagar(double reais) { return legado.efetuarPgto((int) Math.round(reais * 100)); }
    }

    static void demoAdapter() {
        titulo("6. Adapter");
        System.out.println(new PagamentoAdapter(new PagamentoLegado()).pagar(12.50));
    }

    // 7. Bridge
    interface Dispositivo { String ligar(); }
    static class TV implements Dispositivo { public String ligar() { return "TV ligada"; } }
    static class Radio implements Dispositivo { public String ligar() { return "Rádio ligado"; } }
    static class Controle {
        private final Dispositivo disp;
        Controle(Dispositivo d) { disp = d; }
        String botaoPower() { return disp.ligar(); }
    }

    static void demoBridge() {
        titulo("7. Bridge");
        for (Dispositivo d : List.of(new TV(), new Radio()))
            System.out.println(new Controle(d).botaoPower());
    }

    // 8. Composite
    interface Item { int tamanho(); }
    static class Arquivo implements Item {
        final int kb;
        Arquivo(int kb) { this.kb = kb; }
        public int tamanho() { return kb; }
    }
    static class Pasta implements Item {
        final List<Item> itens = new ArrayList<>();
        Pasta add(Item i) { itens.add(i); return this; }
        public int tamanho() { return itens.stream().mapToInt(Item::tamanho).sum(); }
    }

    static void demoComposite() {
        titulo("8. Composite");
        Pasta raiz = new Pasta().add(new Arquivo(10)).add(new Pasta().add(new Arquivo(5)).add(new Arquivo(20)));
        System.out.println("Tamanho total: " + raiz.tamanho() + " KB");
    }

    // 9. Decorator
    interface Bebida { double custo(); }
    static class Cafe implements Bebida { public double custo() { return 5.0; } }
    static class ComLeite implements Bebida {
        private final Bebida b;
        ComLeite(Bebida b) { this.b = b; }
        public double custo() { return b.custo() + 2.0; }
    }
    static class ComChocolate implements Bebida {
        private final Bebida b;
        ComChocolate(Bebida b) { this.b = b; }
        public double custo() { return b.custo() + 3.0; }
    }

    static void demoDecorator() {
        titulo("9. Decorator");
        System.out.println("Café com leite e chocolate: R$ " + new ComChocolate(new ComLeite(new Cafe())).custo());
    }

    // 10. Facade
    static class HomeTheaterFacade {
        void assistirFilme() {
            System.out.println("  luzes apagadas");
            System.out.println("  tela descendo");
            System.out.println("  projetor ligado");
        }
    }

    static void demoFacade() {
        titulo("10. Facade");
        new HomeTheaterFacade().assistirFilme();
    }

    // 11. Flyweight
    static class Icone { final String nome; Icone(String n) { nome = n; } }
    static class FabricaIcones {
        private static final Map<String, Icone> cache = new HashMap<>();
        static Icone get(String nome) { return cache.computeIfAbsent(nome, Icone::new); }
        static int total() { return cache.size(); }
    }

    static void demoFlyweight() {
        titulo("11. Flyweight");
        Icone a = FabricaIcones.get("pasta"), b = FabricaIcones.get("pasta");
        System.out.println("Mesmo objeto compartilhado? " + (a == b) + " | instâncias: " + FabricaIcones.total());
    }

    // 12. Proxy
    interface Imagem { void exibir(); }
    static class ImagemReal implements Imagem {
        private final String arq;
        ImagemReal(String arq) { this.arq = arq; System.out.println("  (carregando " + arq + " do disco...)"); }
        public void exibir() { System.out.println("  exibindo " + arq); }
    }
    static class ImagemProxy implements Imagem {
        private final String arq; private ImagemReal real;
        ImagemProxy(String arq) { this.arq = arq; }
        public void exibir() {
            if (real == null) real = new ImagemReal(arq); // carregamento preguiçoso
            real.exibir();
        }
    }

    static void demoProxy() {
        titulo("12. Proxy");
        Imagem img = new ImagemProxy("foto.png");
        img.exibir(); img.exibir();
    }

    // ───────────────────────── COMPORTAMENTAIS ─────────────────────────

    // 13. Observer
    static class Subject {
        private final List<Consumer<String>> observers = new ArrayList<>();
        void attach(Consumer<String> o) { observers.add(o); }
        void notifyAll(String msg) { observers.forEach(o -> o.accept(msg)); }
    }

    static void demoObserver() {
        titulo("13. Observer");
        Subject s = new Subject();
        s.attach(m -> System.out.println("  Ana recebeu: " + m));
        s.attach(m -> System.out.println("  Beto recebeu: " + m));
        s.notifyAll("Pedido enviado");
    }

    // 14. Strategy
    interface FreteStrategy { double calcular(double peso); }
    static class FreteExpresso implements FreteStrategy { public double calcular(double p) { return p * 2.5; } }
    static class FreteEconomico implements FreteStrategy { public double calcular(double p) { return p * 1.0; } }

    static void demoStrategy() {
        titulo("14. Strategy");
        for (FreteStrategy s : List.of(new FreteExpresso(), new FreteEconomico()))
            System.out.printf("%s: R$ %.2f%n", s.getClass().getSimpleName(), s.calcular(4));
    }

    // 15. Command
    static class Luz { boolean ligada; }
    interface Comando { void executar(); void desfazer(); }
    static class LigarLuz implements Comando {
        private final Luz luz;
        LigarLuz(Luz l) { luz = l; }
        public void executar() { luz.ligada = true; }
        public void desfazer() { luz.ligada = false; }
    }

    static void demoCommand() {
        titulo("15. Command");
        Luz luz = new Luz(); Comando cmd = new LigarLuz(luz);
        cmd.executar(); System.out.println("Após executar: " + luz.ligada);
        cmd.desfazer(); System.out.println("Após desfazer: " + luz.ligada);
    }

    // 16. Iterator
    static class Playlist implements Iterable<String> {
        private final List<String> musicas;
        Playlist(List<String> m) { musicas = m; }
        public Iterator<String> iterator() {
            return new Iterator<>() {
                int i = 0;
                public boolean hasNext() { return i < musicas.size(); }
                public String next() { return musicas.get(i++); }
            };
        }
    }

    static void demoIterator() {
        titulo("16. Iterator");
        for (String m : new Playlist(List.of("Faixa A", "Faixa B", "Faixa C"))) System.out.println("  " + m);
    }

    // 17. State
    interface Estado { String nome(); Estado proximo(); }
    static class Novo implements Estado { public String nome() { return "Novo"; } public Estado proximo() { return new Pago(); } }
    static class Pago implements Estado { public String nome() { return "Pago"; } public Estado proximo() { return new Enviado(); } }
    static class Enviado implements Estado { public String nome() { return "Enviado"; } public Estado proximo() { return this; } }
    static class Pedido {
        Estado estado = new Novo();
        void avancar() { estado = estado.proximo(); }
    }

    static void demoState() {
        titulo("17. State");
        Pedido p = new Pedido();
        for (int i = 0; i < 3; i++) { System.out.println("Estado: " + p.estado.nome()); p.avancar(); }
        System.out.println("Estado final: " + p.estado.nome());
    }

    // 18. Template Method
    static abstract class Relatorio {
        final String gerar() { return formatar(coletar()); } // método-template
        List<String[]> coletar() { return List.of(new String[]{"Ana", "8"}, new String[]{"Beto", "7"}); }
        abstract String formatar(List<String[]> dados);
    }
    static class RelatorioCSV extends Relatorio {
        String formatar(List<String[]> d) {
            StringBuilder sb = new StringBuilder();
            d.forEach(l -> sb.append(l[0]).append(',').append(l[1]).append('\n'));
            return sb.toString().stripTrailing();
        }
    }
    static class RelatorioTexto extends Relatorio {
        String formatar(List<String[]> d) {
            StringBuilder sb = new StringBuilder();
            d.forEach(l -> sb.append(l[0]).append(" tirou ").append(l[1]).append('\n'));
            return sb.toString().stripTrailing();
        }
    }

    static void demoTemplateMethod() {
        titulo("18. Template Method");
        for (Relatorio r : List.of(new RelatorioCSV(), new RelatorioTexto())) System.out.println(r.gerar());
    }

    public static void main(String[] args) {
        demoSingleton(); demoFactoryMethod(); demoAbstractFactory(); demoBuilder(); demoPrototype();
        demoAdapter(); demoBridge(); demoComposite(); demoDecorator(); demoFacade(); demoFlyweight(); demoProxy();
        demoObserver(); demoStrategy(); demoCommand(); demoIterator(); demoState(); demoTemplateMethod();
    }
}
