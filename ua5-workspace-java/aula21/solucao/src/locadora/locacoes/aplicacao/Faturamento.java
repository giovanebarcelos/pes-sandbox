package locadora.locacoes.aplicacao;

import java.util.HashMap;
import java.util.Map;
import locadora.compartilhado.EventoDominio;
import locadora.locacoes.dominio.LocacaoIniciada;
import locadora.locacoes.dominio.MultaPorAtrasoAplicada;

public class Faturamento implements Projecao {
    private final Map<String, String> mesDaLocacao = new HashMap<>();
    private final Map<String, Long> centavosPorMes = new HashMap<>();

    @Override
    public synchronized void tratar(EventoDominio evento) {
        if (evento instanceof LocacaoIniciada e) {
            String mes = e.dataInicio().substring(0, 7);
            mesDaLocacao.put(e.idAgregado(), mes);
            centavosPorMes.merge(mes, e.valorPrevistoCentavos(), Long::sum);
        } else if (evento instanceof MultaPorAtrasoAplicada e) {
            centavosPorMes.merge(mesDaLocacao.get(e.idAgregado()), e.valorCentavos(), Long::sum);
        }
    }

    @Override
    public synchronized void limpar() {
        mesDaLocacao.clear();
        centavosPorMes.clear();
    }

    public synchronized double doMes(String mes) {
        return centavosPorMes.getOrDefault(mes, 0L) / 100.0;
    }
}
