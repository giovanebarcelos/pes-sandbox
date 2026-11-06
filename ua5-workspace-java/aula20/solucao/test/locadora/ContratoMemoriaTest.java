package locadora;

import locadora.adaptadores.saida.LocacaoMemoriaRepository;
import locadora.adaptadores.saida.VeiculoMemoriaRepository;

class ContratoMemoriaTest extends ContratoRepositorioTest {
    @Override
    protected void criar() {
        veiculos = new VeiculoMemoriaRepository();
        locacoes = new LocacaoMemoriaRepository();
    }
}
