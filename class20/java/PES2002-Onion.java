/*
 * PES2002 - Onion Architecture (Camadas Concêntricas)
 * Aula 20: Clean Architecture, Hexagonal e Onion
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Implementa as 4 camadas da Onion com o domínio "Locadora de Veículos":
 * Domínio (centro) define a interface de repositório; Aplicação orquestra
 * o caso de uso; Infraestrutura implementa a interface do Domínio (a
 * dependência de compilação aponta para dentro, mesmo estando "por fora"
 * no desenho); Apresentação monta tudo via injeção de dependência.
 *
 * Compilar/executar:
 *   javac PES2002-Onion.java && java Onion
 */

import java.util.*;

// --- DOMÍNIO (centro): entidade ---
class VeiculoOnion {
    String placa;
    String modelo;
    boolean disponivel;

    VeiculoOnion(String placa, String modelo) {
        this(placa, modelo, true);
    }

    VeiculoOnion(String placa, String modelo, boolean disponivel) {
        this.placa = placa;
        this.modelo = modelo;
        this.disponivel = disponivel;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", placa, modelo, disponivel ? "disponível" : "locado");
    }
}

// --- DOMÍNIO (centro): interface de repositório (porta) ---
interface VeiculoRepositoryOnion {
    void salvar(VeiculoOnion veiculo);
    List<VeiculoOnion> listarDisponiveis();
}

// --- APLICAÇÃO: caso de uso, depende só da interface do Domínio ---
class LocadoraServiceOnion {
    private final VeiculoRepositoryOnion repo;

    LocadoraServiceOnion(VeiculoRepositoryOnion repo) {
        this.repo = repo;
    }

    void cadastrar(VeiculoOnion veiculo) {
        repo.salvar(veiculo);
    }

    List<VeiculoOnion> veiculosDisponiveis() {
        return repo.listarDisponiveis();
    }
}

// --- INFRAESTRUTURA: implementa a interface do Domínio ---
// Dependência de compilação aponta para dentro (Infra -> Domínio),
// mesmo que no desenho da Onion a Infraestrutura fique "por fora".
class VeiculoSQLRepositoryOnion implements VeiculoRepositoryOnion {
    private final Map<String, VeiculoOnion> tabela = new HashMap<>();

    @Override
    public void salvar(VeiculoOnion veiculo) {
        System.out.println("[SQL] INSERT INTO veiculos ... " + veiculo.placa);
        tabela.put(veiculo.placa, veiculo);
    }

    @Override
    public List<VeiculoOnion> listarDisponiveis() {
        List<VeiculoOnion> resultado = new ArrayList<>();
        for (VeiculoOnion v : tabela.values()) {
            if (v.disponivel) resultado.add(v);
        }
        return resultado;
    }
}

class Onion {
    // --- APRESENTAÇÃO: monta a aplicação via injeção de dependência ---
    static LocadoraServiceOnion montarAplicacao() {
        VeiculoRepositoryOnion repo = new VeiculoSQLRepositoryOnion(); // Infraestrutura
        return new LocadoraServiceOnion(repo);                        // Aplicação recebe a porta
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  ONION ARCHITECTURE — LOCADORA DE VEÍCULOS");
        System.out.println("=".repeat(60));

        LocadoraServiceOnion service = montarAplicacao();
        service.cadastrar(new VeiculoOnion("ABC1234", "Fusca"));
        service.cadastrar(new VeiculoOnion("XYZ9876", "Gol"));
        service.cadastrar(new VeiculoOnion("DEF4567", "Onix", false));

        List<VeiculoOnion> disponiveis = service.veiculosDisponiveis();
        System.out.println("\n  Veículos disponíveis: " + disponiveis.size());
        for (VeiculoOnion v : disponiveis) System.out.println("    " + v);

        assert disponiveis.size() == 2;

        System.out.println("\n✓ Domínio (centro): define VeiculoRepositoryOnion, não conhece SQL.");
        System.out.println("✓ Infraestrutura: implementa a interface do Domínio (dependência aponta para dentro).");
        System.out.println("✓ Apresentação: monta tudo via injeção de dependência (montarAplicacao).");
    }
}
