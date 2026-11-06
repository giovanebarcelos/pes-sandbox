package locadora.locacoes.aplicacao;

import java.time.LocalDate;

public record IniciarLocacao(String idLocacao, String placa, String cliente, LocalDate dataInicio, int diasPrevistos)
        implements Comando {
}
