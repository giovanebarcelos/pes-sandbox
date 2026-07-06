/*
 * PES2001 - Arquitetura Hexagonal (Ports & Adapters)
 * Aula 20: Clean Architecture, Hexagonal e Onion
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa Ports & Adapters com o domínio "Locadora de Veículos":
 * uma porta (VeiculoRepository), dois adaptadores (MySQL simulado e
 * memória) e um serviço de domínio que depende apenas da porta,
 * recebendo o adaptador concreto via injeção de dependência (DIP).
 *
 * Compilar/executar:
 *   javac PES2001-Hexagonal.java && java Hexagonal
 */

import java.util.*;

// --- Domínio: entidade ---
class Veiculo {
    String placa;
    String modelo;
    boolean disponivel;

    Veiculo(String placa, String modelo) {
        this(placa, modelo, true);
    }

    Veiculo(String placa, String modelo, boolean disponivel) {
        this.placa = placa;
        this.modelo = modelo;
        this.disponivel = disponivel;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", placa, modelo, disponivel ? "disponível" : "locado");
    }
}

// --- PORTA (interface no domínio) ---
interface VeiculoRepository {
    void salvar(Veiculo veiculo);
    Veiculo buscarPorPlaca(String placa);
    List<Veiculo> listarDisponiveis();
}

// --- ADAPTADOR — implementação concreta (infraestrutura) ---
class VeiculoMySQLRepository implements VeiculoRepository {
    @Override
    public void salvar(Veiculo veiculo) {
        System.out.println("[MySQL] INSERT INTO veiculos ... " + veiculo.placa);
    }

    @Override
    public Veiculo buscarPorPlaca(String placa) {
        System.out.println("[MySQL] SELECT ... WHERE placa='" + placa + "'");
        return null;
    }

    @Override
    public List<Veiculo> listarDisponiveis() {
        System.out.println("[MySQL] SELECT ... WHERE disponivel=true");
        return new ArrayList<>();
    }
}

// --- ADAPTADOR — em memória, usado para testes (sem infraestrutura real) ---
class VeiculoMemoriaRepository implements VeiculoRepository {
    private final Map<String, Veiculo> dados = new HashMap<>();

    @Override
    public void salvar(Veiculo veiculo) {
        dados.put(veiculo.placa, veiculo);
    }

    @Override
    public Veiculo buscarPorPlaca(String placa) {
        return dados.get(placa);
    }

    @Override
    public List<Veiculo> listarDisponiveis() {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo v : dados.values()) {
            if (v.disponivel) resultado.add(v);
        }
        return resultado;
    }
}

// --- DOMÍNIO — não sabe nada sobre MySQL ou memória! ---
class LocadoraService {
    private final VeiculoRepository repo;

    LocadoraService(VeiculoRepository repo) {
        this.repo = repo; // DIP: depende da porta, não do adaptador
    }

    void cadastrar(Veiculo veiculo) {
        repo.salvar(veiculo);
    }

    List<Veiculo> veiculosDisponiveis() {
        return repo.listarDisponiveis();
    }
}

class Hexagonal {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  HEXAGONAL (PORTS & ADAPTERS) — LOCADORA DE VEÍCULOS");
        System.out.println("=".repeat(60));

        System.out.println("\n  --- Adaptador MySQL (simulado) ---");
        LocadoraService serviceMysql = new LocadoraService(new VeiculoMySQLRepository());
        serviceMysql.cadastrar(new Veiculo("ABC1234", "Fusca"));

        System.out.println("\n  --- Adaptador em Memória (testes, sem infraestrutura real) ---");
        LocadoraService serviceMemoria = new LocadoraService(new VeiculoMemoriaRepository());
        serviceMemoria.cadastrar(new Veiculo("XYZ9876", "Gol"));
        serviceMemoria.cadastrar(new Veiculo("DEF4567", "Onix", false));

        List<Veiculo> disponiveis = serviceMemoria.veiculosDisponiveis();
        System.out.println("  Veículos disponíveis: " + disponiveis.size());
        for (Veiculo v : disponiveis) System.out.println("    " + v);

        assert disponiveis.size() == 1;

        System.out.println("\n✓ Porta (VeiculoRepository): contrato definido pelo domínio.");
        System.out.println("✓ Adaptadores: MySQL e Memória são intercambiáveis via DIP.");
        System.out.println("✓ Trocar o adaptador não muda uma linha do domínio (LocadoraService).");
    }
}
