/*
 * PES1902 - Arquitetura Hexagonal (Ports & Adapters)
 * Aula 19: Camadas, MVC, Clean Architecture e Hexagonal
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Demonstra Ports & Adapters: o domínio define portas (interfaces),
 * os adaptadores implementam detalhes técnicos (DB, API).
 *
 * Compilar/executar:
 *   javac PES1902-HexagonalPorts.java && java HexagonalPorts
 */

import java.util.*;

// ===== Porta (interface no domínio) =====
interface RepositorioProduto {
    Produto buscarPorId(int id);
    void salvar(Produto produto);
}

// ===== Domínio (Core) =====
class Produto {
    int id;
    String nome;
    double preco;

    Produto(int id, String nome, double preco) {
        this.id = id; this.nome = nome; this.preco = preco;
    }

    @Override
    public String toString() {
        return String.format("Produto[%d] %s — R$ %.2f", id, nome, preco);
    }
}

class CatalogoService {
    private RepositorioProduto repositorio;

    CatalogoService(RepositorioProduto repositorio) {
        this.repositorio = repositorio;
    }

    Produto obterProduto(int id) {
        return repositorio.buscarPorId(id);
    }

    Produto cadastrarProduto(int id, String nome, double preco) {
        Produto p = new Produto(id, nome, preco);
        repositorio.salvar(p);
        return p;
    }
}

// ===== Adaptador de Memória =====
class RepositorioMemoria implements RepositorioProduto {
    private Map<Integer, Produto> dados = new HashMap<>();

    public Produto buscarPorId(int id) { return dados.get(id); }
    public void salvar(Produto produto) { dados.put(produto.id, produto); }
}

class HexagonalPorts {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  ARQUITETURA HEXAGONAL — PORTS & ADAPTERS");
        System.out.println("=".repeat(60));

        RepositorioProduto repo = new RepositorioMemoria();
        CatalogoService catalogo = new CatalogoService(repo);

        catalogo.cadastrarProduto(1, "Notebook", 4500.00);
        catalogo.cadastrarProduto(2, "Mouse", 120.00);

        Produto p = catalogo.obterProduto(1);
        System.out.printf("%n  Produto encontrado: %s%n", p);

        System.out.println("\n✓ Domínio isolado: troque o adaptador sem alterar a lógica.");
        System.out.println("✓ Porta = interface; Adaptador = implementação concreta.");
    }
}
