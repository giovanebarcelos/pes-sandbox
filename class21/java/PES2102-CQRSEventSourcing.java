/*
 * PES2102 - CQRS + Event Sourcing: Conta Bancária
 * Aula 21: DDD, CQRS e Event Sourcing
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra CQRS (Command Query Responsibility Segregation) combinado
 * com Event Sourcing usando o domínio de conta bancária.
 *
 * - Commands: CriarConta, Depositar, Sacar (operações de escrita)
 * - Queries: ConsultarSaldo, Extrato (operações de leitura)
 * - Event Sourcing: cada operação gera um evento imutável;
 *   o estado atual é reconstruído reprocessando o log de eventos.
 *
 * Compilar/executar:
 *   javac PES2102-CQRSEventSourcing.java && java CQRSEventSourcing
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ============================================================
// DOMAIN EVENTS (imutáveis — fonte da verdade)
// ============================================================

interface DomainEvent {
    String timestamp();
    ContaState aplicar(ContaState estado);
}

record ContaCriada(String contaId, String titular) implements DomainEvent {
    public String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ContaState aplicar(ContaState estado) {
        return new ContaState(contaId, titular, 0.0, true);
    }

    @Override
    public String toString() {
        return String.format("ContaCriada[conta=%s, titular=%s, ts=%s]", contaId, titular, timestamp());
    }
}

record DepositoRealizado(double valor) implements DomainEvent {
    public String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ContaState aplicar(ContaState estado) {
        return new ContaState(estado.contaId(), estado.titular(), estado.saldo() + valor, estado.ativa());
    }

    @Override
    public String toString() {
        return String.format("DepositoRealizado[valor=%.2f, ts=%s]", valor, timestamp());
    }
}

record SaqueRealizado(double valor) implements DomainEvent {
    public String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ContaState aplicar(ContaState estado) {
        return new ContaState(estado.contaId(), estado.titular(), estado.saldo() - valor, estado.ativa());
    }

    @Override
    public String toString() {
        return String.format("SaqueRealizado[valor=%.2f, ts=%s]", valor, timestamp());
    }
}

record ContaState(String contaId, String titular, double saldo, boolean ativa) {
    static final ContaState VAZIO = new ContaState("", "", 0.0, false);
}

// ============================================================
// EVENT STORE (log imutável de eventos)
// ============================================================

class EventStore {
    private final Map<String, List<DomainEvent>> streams = new HashMap<>();

    void append(String contaId, List<DomainEvent> events) {
        streams.computeIfAbsent(contaId, k -> new ArrayList<>()).addAll(events);
    }

    List<DomainEvent> load(String contaId) {
        return Collections.unmodifiableList(streams.getOrDefault(contaId, List.of()));
    }
}

// ============================================================
// COMMAND SIDE (escrita)
// ============================================================

class ContaCommandHandler {
    private final EventStore store;

    ContaCommandHandler(EventStore store) { this.store = store; }

    List<DomainEvent> criarConta(String contaId, String titular) {
        if (!store.load(contaId).isEmpty())
            throw new IllegalArgumentException("Conta '" + contaId + "' já existe.");
        List<DomainEvent> eventos = List.of(new ContaCriada(contaId, titular));
        store.append(contaId, eventos);
        return eventos;
    }

    List<DomainEvent> depositar(String contaId, double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor de depósito deve ser positivo.");
        ContaState estado = reconstruirEstado(contaId);
        if (!estado.ativa()) throw new IllegalArgumentException("Conta '" + contaId + "' não encontrada.");
        List<DomainEvent> eventos = List.of(new DepositoRealizado(valor));
        store.append(contaId, eventos);
        return eventos;
    }

    List<DomainEvent> sacar(String contaId, double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        ContaState estado = reconstruirEstado(contaId);
        if (!estado.ativa()) throw new IllegalArgumentException("Conta '" + contaId + "' não encontrada.");
        if (estado.saldo() < valor)
            throw new IllegalArgumentException(String.format("Saldo insuficiente: %.2f < %.2f", estado.saldo(), valor));
        List<DomainEvent> eventos = List.of(new SaqueRealizado(valor));
        store.append(contaId, eventos);
        return eventos;
    }

    private ContaState reconstruirEstado(String contaId) {
        List<DomainEvent> eventos = store.load(contaId);
        ContaState estado = ContaState.VAZIO;
        for (DomainEvent evt : eventos) estado = evt.aplicar(estado);
        return estado;
    }
}

// ============================================================
// QUERY SIDE (leitura)
// ============================================================

class ContaQueryHandler {
    private final EventStore store;

    ContaQueryHandler(EventStore store) { this.store = store; }

    ContaState consultarSaldo(String contaId) {
        ContaState estado = ContaState.VAZIO;
        for (DomainEvent evt : store.load(contaId)) estado = evt.aplicar(estado);
        return estado;
    }

    void imprimirExtrato(String contaId) {
        List<DomainEvent> eventos = store.load(contaId);
        double saldo = 0.0;
        System.out.printf("    %-12s %-35s %s%n", "Tipo", "Detalhe", "Valor");
        System.out.printf("    %-12s %-35s %s%n", "─".repeat(12), "─".repeat(35), "─".repeat(10));

        for (DomainEvent evt : eventos) {
            if (evt instanceof ContaCriada cc) {
                System.out.printf("    %-12s %-35s%n", "ABERTURA",
                    "Conta criada — titular: " + cc.titular());
            } else if (evt instanceof DepositoRealizado dr) {
                saldo += dr.valor();
                System.out.printf("    %-12s %-35s → saldo: R$ %,.2f%n",
                    "DEPÓSITO", String.format("+ R$ %,.2f", dr.valor()), saldo);
            } else if (evt instanceof SaqueRealizado sr) {
                saldo -= sr.valor();
                System.out.printf("    %-12s %-35s → saldo: R$ %,.2f%n",
                    "SAQUE", String.format("- R$ %,.2f", sr.valor()), saldo);
            }
        }
    }
}

// ============================================================
// DEMONSTRAÇÃO
// ============================================================

class CQRSEventSourcing {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  CQRS + EVENT SOURCING — CONTA BANCÁRIA");
        System.out.println("=".repeat(60));

        EventStore store = new EventStore();
        ContaCommandHandler cmd = new ContaCommandHandler(store);
        ContaQueryHandler qry = new ContaQueryHandler(store);

        // --- COMMANDS (escrita) ---
        System.out.println("\n  📤 COMMANDS (escrita):");
        cmd.criarConta("C001", "João da Silva");
        System.out.println("    ✓ Conta 'C001' criada para João da Silva");

        cmd.depositar("C001", 500.00);
        System.out.println("    ✓ Depósito de R$ 500,00");

        cmd.depositar("C001", 200.00);
        System.out.println("    ✓ Depósito de R$ 200,00");

        cmd.sacar("C001", 150.00);
        System.out.println("    ✓ Saque de R$ 150,00");

        cmd.depositar("C001", 100.00);
        System.out.println("    ✓ Depósito de R$ 100,00");

        // --- QUERIES (leitura) ---
        System.out.println("\n  📥 QUERIES (leitura):");
        ContaState saldo = qry.consultarSaldo("C001");
        System.out.printf("    Saldo atual: R$ %,.2f%n", saldo.saldo());

        System.out.println("\n  📋 EXTRATO (projeção do event log):");
        qry.imprimirExtrato("C001");

        // --- EVENT LOG bruto ---
        System.out.println("\n  📜 EVENT LOG (fonte da verdade — imutável):");
        for (DomainEvent evt : store.load("C001")) {
            System.out.println("    " + evt);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ✓ CQRS: Commands (escrita) separados de Queries (leitura)");
        System.out.println("  ✓ Event Sourcing: estado reconstruído do log de eventos");
        System.out.println("  ✓ Event Store: append-only, imutável, auditável");
        System.out.println("=".repeat(60));
    }
}
