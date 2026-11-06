package locadora;

import static teste.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import locadora.compartilhado.Dinheiro;
import locadora.compartilhado.Placa;
import locadora.compartilhado.RegistroEventos;
import locadora.infra.Json;
import locadora.locacoes.dominio.LocacaoIniciada;
import teste.Test;

/** Objetos de valor: imutáveis e comparados pelos atributos. */
class ValorTest {
    @Test
    void placa_normaliza_e_valida() {
        assertEquals(new Placa("ABC-1234"), new Placa(" abc-1234 "));
        assertEquals("BRA2E19", new Placa("bra2e19").toString());       // padrão Mercosul
        assertThrows(IllegalArgumentException.class, () -> new Placa("12-ABCD"));
    }

    @Test
    void dinheiro_sem_erro_de_ponto_flutuante() {
        assertNotEquals(0.3, 0.1 + 0.2);                                  // o problema do double
        assertEquals(Dinheiro.deReais(0.3), Dinheiro.deReais(0.1).mais(Dinheiro.deReais(0.2)));
        assertEquals("R$ 660.00", new Dinheiro(66000).toString());
    }

    @Test
    void imutavel() {
        for (Field f : Dinheiro.class.getDeclaredFields()) {
            assertTrue(Modifier.isFinal(f.getModifiers()), "campo " + f.getName() + " não é final");
        }
    }

    @Test
    void evento_vai_e_volta_em_json() {
        RegistroEventos.registrar(LocacaoIniciada.class);
        LocacaoIniciada e = new LocacaoIniciada("L-1", "ABC-1234", "Ana \"Bia\"", "2026-11-02", 5, 66000);
        String linha = Json.escrever(RegistroEventos.paraMapa(e));
        assertEquals(e, RegistroEventos.deMapa(Json.lerObjeto(linha)));
        assertEquals(Map.of("a", List.of(1L, 2.5, true)), Json.ler("{\"a\": [1, 2.5, true]}"));
    }
}
