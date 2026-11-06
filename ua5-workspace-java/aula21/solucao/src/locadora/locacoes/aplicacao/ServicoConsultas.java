package locadora.locacoes.aplicacao;

import java.util.List;
import java.util.Map;

/** Fachada de leitura: só lê projeções, nunca o event store nem agregados. */
public class ServicoConsultas {
    private final LocacoesAtivas ativas;
    private final RankingMensal ranking;
    private final Faturamento faturamento;

    public ServicoConsultas(LocacoesAtivas ativas, RankingMensal ranking, Faturamento faturamento) {
        this.ativas = ativas;
        this.ranking = ranking;
        this.faturamento = faturamento;
    }

    public Map<String, LocacoesAtivas.Ativa> locacoesAtivas() {
        return ativas.ativas();
    }

    public List<RankingMensal.Contagem> maisAlugados(String mes) {
        return ranking.maisAlugados(mes, 3);
    }

    public double faturamentoDoMes(String mes) {
        return faturamento.doMes(mes);
    }
}
