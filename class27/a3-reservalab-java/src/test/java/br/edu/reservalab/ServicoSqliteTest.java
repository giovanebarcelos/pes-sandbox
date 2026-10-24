package br.edu.reservalab;

import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.infra.RepositorioSqlite;

class ServicoSqliteTest extends ServicoReservasContrato {
    @Override RepositorioReservas novoRepositorio() { return new RepositorioSqlite(); }
}
