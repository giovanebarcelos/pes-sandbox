package br.edu.reservalab.dominio;

import java.time.Duration;
import java.util.EnumSet;

public class Laboratorio extends Recurso {
    public Laboratorio(String id, String nome, int capacidade) {
        super(id, nome, capacidade, Duration.ofHours(4), Duration.ofHours(24), false,
              EnumSet.of(Perfil.PROFESSOR, Perfil.COORDENADOR));
    }

    @Override public String tipo() { return "Laboratório"; }
}
