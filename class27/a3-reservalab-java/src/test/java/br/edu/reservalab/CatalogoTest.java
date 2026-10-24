package br.edu.reservalab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.edu.reservalab.aplicacao.CatalogoRecursos;
import br.edu.reservalab.dominio.Erros.NaoEncontrado;
import br.edu.reservalab.dominio.Laboratorio;
import br.edu.reservalab.dominio.Recurso;
import br.edu.reservalab.dominio.SalaEstudo;

/** RF-01: catálogo de recursos. */
class CatalogoTest {
    @Test void registrarEListarOrdenadoPorId() {
        CatalogoRecursos c = new CatalogoRecursos();
        c.registrar(new Laboratorio("LAB2", "Laboratório de Redes", 30));
        c.registrar(new SalaEstudo("S101", "Sala de estudo 101", 6));
        assertEquals(List.of("LAB2", "S101"), c.listar().stream().map(Recurso::id).toList());
    }

    @Test void registrarIdRepetidoERecusado() {
        CatalogoRecursos c = new CatalogoRecursos();
        c.registrar(new SalaEstudo("S101", "Sala 101", 6));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> c.registrar(new SalaEstudo("S101", "Outra sala", 4)));
        assertTrue(e.getMessage().contains("já registrado"));
    }

    @Test void capacidadeDeveSerPositiva() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new SalaEstudo("S102", "Sala 102", 0));
        assertTrue(e.getMessage().contains("capacidade"));
    }

    @Test void obterInexistente() {
        assertThrows(NaoEncontrado.class, () -> new CatalogoRecursos().obter("XXX"));
    }
}
