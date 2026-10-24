package br.edu.reservalab.aplicacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.edu.reservalab.dominio.Erros.NaoEncontrado;
import br.edu.reservalab.dominio.Recurso;

/** Recursos reserváveis do campus (RF-01). Em memória: ver SDR-001, fora de escopo. */
public class CatalogoRecursos {
    private final Map<String, Recurso> recursos = new LinkedHashMap<>();

    public void registrar(Recurso recurso) {
        if (recursos.containsKey(recurso.id())) {
            throw new IllegalArgumentException("recurso " + recurso.id() + " já registrado");
        }
        recursos.put(recurso.id(), recurso);
    }

    public Recurso obter(String recursoId) {
        Recurso r = recursos.get(recursoId);
        if (r == null) {
            throw new NaoEncontrado("recurso " + recursoId + " não existe");
        }
        return r;
    }

    public List<Recurso> listar() {
        List<Recurso> lista = new ArrayList<>(recursos.values());
        lista.sort(Comparator.comparing(Recurso::id));
        return lista;
    }
}
