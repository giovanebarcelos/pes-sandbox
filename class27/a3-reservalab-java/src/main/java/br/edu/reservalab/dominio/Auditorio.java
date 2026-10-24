package br.edu.reservalab.dominio;

import java.time.Duration;
import java.util.EnumSet;

public class Auditorio extends Recurso {
    public Auditorio(String id, String nome, int capacidade) {
        super(id, nome, capacidade, Duration.ofHours(6), Duration.ofHours(72), true,
              EnumSet.of(Perfil.PROFESSOR, Perfil.COORDENADOR));
    }

    @Override public String tipo() { return "Auditório"; }
}
