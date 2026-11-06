package locadora.locacoes.adaptadores;

import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;
import locadora.locacoes.dominio.ServicoFrota;

/**
 * Adaptador da porta ServicoFrota que chama a Frota no MESMO processo (monolito).
 * Na Aula 22 ele ganha um irmão, FrotaHttp, que faz a mesma coisa pela rede.
 *
 * Repare: ele NÃO importa nada de locadora.frota. As três operações chegam
 * prontas (injeção), ligadas pelo Config:  new FrotaLocal(api::precoBase, api::reservar, api::liberar)
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
        return precoBase.applyAsDouble(placa, dias);
    }

    @Override
    public void reservar(String placa) {
        reservar.accept(placa);
    }

    @Override
    public void liberar(String placa) {
        liberar.accept(placa);
    }
}
