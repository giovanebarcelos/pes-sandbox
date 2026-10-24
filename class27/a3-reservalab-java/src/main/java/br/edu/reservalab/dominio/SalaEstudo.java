package br.edu.reservalab.dominio;

import java.time.Duration;
import java.util.EnumSet;

public class SalaEstudo extends Recurso {
    public SalaEstudo(String id, String nome, int capacidade) {
        super(id, nome, capacidade, Duration.ofHours(2), Duration.ofMinutes(30), false, EnumSet.allOf(Perfil.class));
    }

    @Override public String tipo() { return "Sala de estudo"; }
}
