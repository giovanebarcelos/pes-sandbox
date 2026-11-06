package locadora;

import static teste.Assertions.*;

import java.util.Map;
import locadora.adaptadores.entrada.ControladorConsole;
import locadora.adaptadores.entrada.ControladorWeb;
import locadora.adaptadores.entrada.VisaoConsole;
import teste.Test;

/** Adaptadores de entrada (terminal e web) continuam funcionando sobre o hexágono. */
class EntradaTest {
    @Test
    void cli_com_arquivo() {
        var pasta = Pastas.temporaria();
        try {
            ControladorConsole c = new ControladorConsole(Config.montar("arquivo", pasta.toString(), true, true),
                    new VisaoConsole());
            assertTrue(c.executar("alugar ABC-1234 5 Ana a@x").contains("R$ 660.00"));
            assertTrue(c.executar("listar").contains("alugado"));
        } finally {
            Pastas.apagar(pasta);
        }
    }

    @Test
    void web_com_memoria() {
        ControladorWeb c = new ControladorWeb(Config.montar("memoria", null, true, true));
        ControladorWeb.Resposta r = c.tratar("POST", "/alugar",
                Map.of("placa", "XYZ-9012", "dias", "3", "cliente", "Bia", "email", "b@x"));
        assertEquals(200, r.status());
        assertTrue(r.html().contains("R$ 240.00"));
    }
}
