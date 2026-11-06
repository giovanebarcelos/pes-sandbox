package locadora;

import static teste.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import teste.Test;

/** DIP na prática: o serviço é testado com fakes, sem arquivo e sem SMTP. */
class ServicoTest {
    @Test
    void politica_de_preco_e_trocavel() {
        Map<PoliticaPreco, Double> casos = Map.of(new SemDesconto(), 560.0, new DescontoSemanal(), 504.0);
        casos.forEach((politica, esperado) -> {
            ServicoLocacao s = new ServicoLocacao(new RepositorioMemoria(), new NotificadorMemoria(), politica);
            s.cadastrar(Fabrica.criarVeiculo("moto", "M-1", "CG", 80));
            assertEquals(esperado, s.cotar("M-1", 7), politica.getClass().getSimpleName());
        });
    }

    @Test
    void erros_de_negocio_tem_nome() {
        ServicoLocacao s = new ServicoLocacao(new RepositorioMemoria(), new NotificadorMemoria());
        s.cadastrar(Fabrica.criarVeiculo("moto", "M-1", "CG", 80));
        assertThrows(VeiculoNaoEncontrado.class, () -> s.alugar("NAO-EXISTE", "Ana", "a@x", 1));
        s.alugar("M-1", "Ana", "a@x", 1);
        assertThrows(VeiculoIndisponivel.class, () -> s.alugar("M-1", "Bia", "b@x", 1));
    }

    @Test
    void repositorio_em_arquivo_grava_e_rele() throws Exception {
        Path pasta = Files.createTempDirectory("locadora");
        String arq = pasta.resolve("frota.csv").toString();
        try {
            ServicoLocacao s = new ServicoLocacao(new RepositorioArquivo(arq), new NotificadorMemoria());
            s.cadastrar(Fabrica.criarVeiculo("carro", "ABC-1234", "Uno", 120));
            s.alugar("ABC-1234", "Ana", "a@x", 2);
            Veiculo relido = new RepositorioArquivo(arq).buscar("ABC-1234");
            assertEquals("Uno", relido.modelo());
            assertFalse(relido.disponivel());
        } finally {
            Files.deleteIfExists(Path.of(arq));
            Files.deleteIfExists(pasta);
        }
    }
}
