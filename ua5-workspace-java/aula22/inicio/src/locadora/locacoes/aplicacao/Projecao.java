package locadora.locacoes.aplicacao;

import java.util.List;
import locadora.compartilhado.EventoDominio;

/**
 * LADO DE LEITURA (Query) do CQRS — PROJEÇÕES.
 *
 * Cada projeção é um "modelo de leitura" montado a partir dos eventos,
 * no formato exato que a consulta precisa. Nenhuma consulta lê o agregado.
 * Se uma projeção estragar ou mudar de formato, basta reconstruí-la a
 * partir do event store (reconstruir).
 */
public interface Projecao {
    /** Recebe cada evento publicado (ignora os que não interessam). */
    void tratar(EventoDominio evento);

    /** Zera o estado da projeção. */
    void limpar();

    default void reconstruir(List<? extends EventoDominio> eventos) {
        limpar();
        for (EventoDominio e : eventos) {
            tratar(e);
        }
    }
}
