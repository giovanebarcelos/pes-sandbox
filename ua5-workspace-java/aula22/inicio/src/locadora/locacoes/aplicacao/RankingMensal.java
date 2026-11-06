package locadora.locacoes.aplicacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import locadora.compartilhado.EventoDominio;
import locadora.locacoes.dominio.LocacaoIniciada;

/** Bônus do Slide 30: "veículos mais alugados no mês". */
public class RankingMensal implements Projecao {
    public record Contagem(String placa, int locacoes) {
    }

    /** mês ("2026-11") -> placa -> quantidade (em ordem de primeira aparição). */
    private final Map<String, LinkedHashMap<String, Integer>> porMes = new HashMap<>();

    @Override
    public synchronized void tratar(EventoDominio evento) {
        if (evento instanceof LocacaoIniciada e) {
            porMes.computeIfAbsent(e.dataInicio().substring(0, 7), m -> new LinkedHashMap<>())
                  .merge(e.placa(), 1, Integer::sum);
        }
    }

    @Override
    public synchronized void limpar() {
        porMes.clear();
    }

    public synchronized List<Contagem> maisAlugados(String mes, int limite) {
        List<Contagem> lista = new ArrayList<>();
        porMes.getOrDefault(mes, new LinkedHashMap<>()).forEach((placa, n) -> lista.add(new Contagem(placa, n)));
        lista.sort(Comparator.comparingInt(Contagem::locacoes).reversed()); // sort estável: empate mantém a ordem
        return lista.subList(0, Math.min(limite, lista.size()));
    }
}
