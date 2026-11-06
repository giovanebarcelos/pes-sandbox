package locadora.locacoes.aplicacao;

import java.util.LinkedHashMap;
import java.util.Map;
import locadora.compartilhado.EventoDominio;
import locadora.locacoes.dominio.LocacaoIniciada;
import locadora.locacoes.dominio.VeiculoDevolvido;

public class LocacoesAtivas implements Projecao {
    public record Ativa(String placa, String cliente, String inicio, int dias) {
    }

    private final Map<String, Ativa> ativas = new LinkedHashMap<>();

    @Override
    public synchronized void tratar(EventoDominio evento) {
        if (evento instanceof LocacaoIniciada e) {
            ativas.put(e.idAgregado(), new Ativa(e.placa(), e.cliente(), e.dataInicio(), e.diasPrevistos()));
        } else if (evento instanceof VeiculoDevolvido e) {
            ativas.remove(e.idAgregado());
        }
    }

    @Override
    public synchronized void limpar() {
        ativas.clear();
    }

    public synchronized Map<String, Ativa> ativas() {
        return new LinkedHashMap<>(ativas);
    }
}
