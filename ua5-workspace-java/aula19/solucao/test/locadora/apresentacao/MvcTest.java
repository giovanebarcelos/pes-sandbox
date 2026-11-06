package locadora.apresentacao;

import static teste.Assertions.*;

import teste.BeforeEach;
import teste.Test;

/** MVC: o Controller é testado passando linhas de comando — sem teclado de verdade. */
class MvcTest {
    private ControladorConsole c;

    @BeforeEach
    void preparar() {
        c = new ControladorConsole(Montagem.servicoExemplo(true), new VisaoConsole());
    }

    @Test
    void listar() {
        assertTrue(c.executar("listar").contains("ABC-1234"));
    }

    @Test
    void cotar() {
        assertEquals("Cotação XYZ-9012 por 7 dia(s): R$ 504.00", c.executar("cotar XYZ-9012 7"));
    }

    @Test
    void alugar_e_erro_de_negocio() {
        assertTrue(c.executar("alugar ABC-1234 5 Ana ana@x.com").contains("R$ 660.00"));
        assertTrue(c.executar("alugar ABC-1234 5 Bia bia@x.com").startsWith("ERRO"));
    }

    @Test
    void comando_invalido_mostra_ajuda() {
        assertTrue(c.executar("voar").startsWith("Comandos"));
    }
}
