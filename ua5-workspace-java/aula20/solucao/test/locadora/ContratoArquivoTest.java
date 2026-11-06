package locadora;

import java.nio.file.Path;
import locadora.adaptadores.saida.LocacaoArquivoRepository;
import locadora.adaptadores.saida.VeiculoArquivoRepository;
import teste.AfterEach;

class ContratoArquivoTest extends ContratoRepositorioTest {
    private Path pasta;

    @Override
    protected void criar() {
        pasta = Pastas.temporaria();
        veiculos = new VeiculoArquivoRepository(pasta);
        locacoes = new LocacaoArquivoRepository(pasta);
    }

    @AfterEach
    void limpar() {
        Pastas.apagar(pasta);
    }
}
