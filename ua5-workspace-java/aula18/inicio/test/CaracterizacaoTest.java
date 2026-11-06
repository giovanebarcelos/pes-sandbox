import static teste.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import teste.AfterEach;
import teste.BeforeEach;
import teste.Test;

/**
 * Testes de CARACTERIZAÇÃO do código legado.
 *
 * Eles não dizem se o código é "bom" — apenas registram o que ele FAZ hoje.
 * Antes de refatorar, rode-os (devem passar). Depois de cada passo de
 * refatoração, rode-os de novo: se algum falhar, você mudou o comportamento.
 *
 * Executar (na pasta da aula):  java ../../ferramentas/Rodar.java testes
 */
class CaracterizacaoTest {
    private Path pasta;
    private LocadoraLegado loc;

    @BeforeEach
    void preparar() throws Exception {
        pasta = Files.createTempDirectory("locadora");
        loc = new LocadoraLegado(pasta.resolve("frota.csv").toString());
        loc.cadastrar("carro", "ABC-1234", "Fiat Uno", 120);
        loc.cadastrar("moto", "XYZ-9012", "Honda CG 160", 80);
    }

    @AfterEach
    void limpar() throws Exception {
        Files.deleteIfExists(pasta.resolve("frota.csv"));
        Files.deleteIfExists(pasta);
    }

    @Test
    void carro_5_dias_tem_10_por_cento_de_seguro() {
        assertEquals(660.00, loc.alugar("ABC-1234", "Ana", "a@x.com", 5));
    }

    @Test
    void moto_3_dias_sem_seguro() {
        assertEquals(240.00, loc.alugar("XYZ-9012", "Bia", "b@x.com", 3));
    }

    @Test
    void sete_dias_ou_mais_tem_10_por_cento_de_desconto() {
        assertEquals(504.00, loc.alugar("XYZ-9012", "Bia", "b@x.com", 7));
        assertEquals(1188.00, loc.alugar("ABC-1234", "Ana", "a@x.com", 10));
    }

    @Test
    void veiculo_alugado_fica_indisponivel() {
        loc.alugar("ABC-1234", "Ana", "a@x.com", 2);
        assertThrows(IllegalStateException.class, () -> loc.alugar("ABC-1234", "Outro", "o@x.com", 2));
    }

    @Test
    void devolver_libera_o_veiculo() {
        loc.alugar("ABC-1234", "Ana", "a@x.com", 2);
        loc.devolver("ABC-1234");
        assertEquals(132.00, loc.alugar("ABC-1234", "Outro", "o@x.com", 1));
    }

    @Test
    void placa_inexistente() {
        assertThrows(NoSuchElementException.class, () -> loc.alugar("ZZZ-0000", "Ana", "a@x.com", 1));
    }

    @Test
    void envia_email_de_confirmacao() {
        loc.alugar("ABC-1234", "Ana", "a@x.com", 5);
        String[] ultimo = loc.emailsEnviados.get(loc.emailsEnviados.size() - 1);
        assertEquals("a@x.com", ultimo[0]);
        assertTrue(ultimo[1].contains("R$ 660.00"), ultimo[1]);
    }

    @Test
    void relatorio_mostra_faturamento() {
        loc.alugar("ABC-1234", "Ana", "a@x.com", 5);
        loc.alugar("XYZ-9012", "Bia", "b@x.com", 3);
        assertTrue(loc.relatorio().contains("Faturamento: R$ 900.00"), loc.relatorio());
    }
}
