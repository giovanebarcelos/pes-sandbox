package br.edu.reservalab.dominio;

public enum Status {
    PENDENTE, CONFIRMADA, REJEITADA, CANCELADA;

    /** Só PENDENTE e CONFIRMADA ocupam o horário. */
    public boolean ativo() { return this == PENDENTE || this == CONFIRMADA; }
}
