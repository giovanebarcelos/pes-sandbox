package locadora.locacoes.adaptadores;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import locadora.locacoes.dominio.ServicoFrota;
import locadora.locacoes.dominio.VeiculoDesconhecido;
import locadora.locacoes.dominio.VeiculoNaoDisponivel;

/**
 * Adaptador da porta ServicoFrota que chama a Frota no MESMO processo (monolito modular).
 *
 * Camada Anticorrupção: traduz os erros da Frota (NoSuchElementException,
 * IllegalStateException) para os erros de Locações, sem importar nada de
 * locadora.frota (as operações chegam por injeção, ligadas pelo Config).
 */
public class FrotaLocal implements ServicoFrota {
    private final ToDoubleBiFunction<String, Integer> precoBase;
    private final Consumer<String> reservar;
    private final Consumer<String> liberar;

    public FrotaLocal(ToDoubleBiFunction<String, Integer> precoBase, Consumer<String> reservar, Consumer<String> liberar) {
        this.precoBase = precoBase;
        this.reservar = reservar;
        this.liberar = liberar;
    }

    @Override
    public double precoBase(String placa, int dias) {
        return traduzir(() -> precoBase.applyAsDouble(placa, dias));
    }

    @Override
    public void reservar(String placa) {
        traduzir(() -> {
            reservar.accept(placa);
            return null;
        });
    }

    @Override
    public void liberar(String placa) {
        traduzir(() -> {
            liberar.accept(placa);
            return null;
        });
    }

    private static <T> T traduzir(Supplier<T> chamada) {
        try {
            return chamada.get();
        } catch (NoSuchElementException e) {
            throw new VeiculoDesconhecido(e.getMessage());
        } catch (IllegalStateException e) {
            throw new VeiculoNaoDisponivel(e.getMessage());
        }
    }
}
