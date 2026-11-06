package locadora;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import locadora.compartilhado.EventoDominio;
import locadora.compartilhado.RegistroEventos;
import locadora.frota.FrotaAPI;
import locadora.locacoes.aplicacao.IniciarLocacao;
import locadora.locacoes.aplicacao.RegistrarDevolucao;

/**
 * Demonstração da Aula 21: comandos -> eventos -> projeções.
 *
 * Executar (na pasta solucao/):  java ../../ferramentas/Rodar.java locadora.Main
 * Com event store em arquivo:    LOCADORA_EVENTOS=dados/eventos.jsonl java ../../ferramentas/Rodar.java locadora.Main
 */
public class Main {
    static LocalDate novembro(int dia) {
        return LocalDate.of(2026, 11, dia);
    }

    public static void main(String[] args) {
        Aplicacao app = Config.montar();
        app.comandos.executar(new IniciarLocacao("L-001", "ABC-1234", "Ana Silva", novembro(2), 5));
        app.comandos.executar(new IniciarLocacao("L-002", "XYZ-9012", "Carlos Santos", novembro(3), 7));
        app.comandos.executar(new RegistrarDevolucao("L-001", novembro(7)));   // no prazo
        app.comandos.executar(new IniciarLocacao("L-003", "ABC-1234", "Bia Souza", novembro(8), 2));
        app.comandos.executar(new RegistrarDevolucao("L-003", novembro(12)));  // 2 dias de atraso

        System.out.println("=== EVENT STORE (fonte da verdade) ===");
        for (EventoDominio e : app.store.todos()) {
            System.out.printf("  %-24s %s%n", e.tipo(), RegistroEventos.paraMapa(e));
        }
        System.out.println("\n=== CONSULTAS (lado de leitura) ===");
        System.out.println("  Ativas: " + app.consultas.locacoesAtivas());
        System.out.println("  Mais alugados 2026-11: " + app.consultas.maisAlugados("2026-11"));
        System.out.printf(Locale.ROOT, "  Faturamento 2026-11: R$ %.2f%n", app.consultas.faturamentoDoMes("2026-11"));
        List<String> frota = new ArrayList<>();
        for (FrotaAPI.VeiculoDados v : app.frota.listar()) {
            frota.add(v.placa() + "=" + (v.disponivel() ? "disponível" : "alugado"));
        }
        System.out.println("  Frota: " + frota);
    }
}
