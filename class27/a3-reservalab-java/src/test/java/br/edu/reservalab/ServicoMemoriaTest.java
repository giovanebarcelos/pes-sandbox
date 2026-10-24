package br.edu.reservalab;

import br.edu.reservalab.aplicacao.RepositorioReservas;
import br.edu.reservalab.infra.RepositorioMemoria;

class ServicoMemoriaTest extends ServicoReservasContrato {
    @Override RepositorioReservas novoRepositorio() { return new RepositorioMemoria(); }
}
