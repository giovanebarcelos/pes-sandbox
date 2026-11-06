package locadora.locacoes.aplicacao;

import java.time.LocalDate;

public record RegistrarDevolucao(String idLocacao, LocalDate dataDevolucao) implements Comando {
}
