package locadora;

import static teste.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import locadora.frota.FrotaAPI;
import locadora.frota.dominio.VeiculoIndisponivel;
import locadora.locacoes.adaptadores.EventStoreMemoria;
import locadora.locacoes.aplicacao.IniciarLocacao;
import locadora.locacoes.aplicacao.RegistrarDevolucao;
import locadora.locacoes.dominio.ConflitoDeConcorrencia;
import locadora.locacoes.dominio.LocacaoIniciada;
import teste.BeforeEach;
import teste.Test;

/** Lado de escrita, lado de leitura, event store e integração entre contextos. */
class CqrsEventStoreTest {
    private Aplicacao app;

    static LocalDate d(int dia) {
        return LocalDate.of(2026, 11, dia);
    }

    @BeforeEach
    void preparar() {
        app = Config.montar((String) null);
        app.comandos.executar(new IniciarLocacao("L-1", "ABC-1234", "Ana", d(2), 5));
    }

    private boolean disponivel(String placa) {
        for (FrotaAPI.VeiculoDados v : app.frota.listar()) {
            if (v.placa().equals(placa)) {
                return v.disponivel();
            }
        }
        throw new AssertionError("placa não encontrada: " + placa);
    }

    @Test
    void iniciar_reserva_o_veiculo_na_frota() {
        assertFalse(disponivel("ABC-1234"));
        assertThrows(VeiculoIndisponivel.class,
                () -> app.comandos.executar(new IniciarLocacao("L-2", "ABC-1234", "Bia", d(3), 1)));
    }

    @Test
    void devolucao_libera_frota_por_evento() {
        app.comandos.executar(new RegistrarDevolucao("L-1", d(7)));
        assertTrue(disponivel("ABC-1234"));
    }

    @Test
    void projecoes_respondem_consultas() {
        app.comandos.executar(new IniciarLocacao("L-2", "XYZ-9012", "Bia", d(3), 7));
        assertEquals(Set.of("L-1", "L-2"), app.consultas.locacoesAtivas().keySet());
        assertEquals(1164.0, app.consultas.faturamentoDoMes("2026-11"));
    }

    @Test
    void reconstruir_projecoes_da_o_mesmo_resultado() {
        app.comandos.executar(new RegistrarDevolucao("L-1", d(9)));
        List<Object> antes = List.of(app.consultas.locacoesAtivas(), app.consultas.faturamentoDoMes("2026-11"),
                app.consultas.maisAlugados("2026-11"));
        app.reconstruirProjecoes();
        List<Object> depois = List.of(app.consultas.locacoesAtivas(), app.consultas.faturamentoDoMes("2026-11"),
                app.consultas.maisAlugados("2026-11"));
        assertEquals(antes, depois);
    }

    @Test
    void concorrencia_otimista() {
        EventStoreMemoria store = new EventStoreMemoria();
        LocacaoIniciada e = new LocacaoIniciada("L-1", "ABC-1234", "Ana", "2026-11-02", 5, 66000);
        store.anexar("L-1", List.of(e), 0);
        assertThrows(ConflitoDeConcorrencia.class, () -> store.anexar("L-1", List.of(e), 0));
    }

    @Test
    void arquivo_sobrevive_a_reinicio() throws Exception {
        Path pasta = Files.createTempDirectory("locadora");
        Path caminho = pasta.resolve("eventos.jsonl");
        try {
            Aplicacao primeira = Config.montar(caminho.toString());
            primeira.comandos.executar(new IniciarLocacao("L-1", "ABC-1234", "Ana", d(2), 5));
            Aplicacao reiniciada = Config.montar(caminho.toString());
            assertEquals(primeira.store.todos(), reiniciada.store.todos());
            assertTrue(reiniciada.consultas.locacoesAtivas().containsKey("L-1"));
            assertEquals(1, Files.readAllLines(caminho, StandardCharsets.UTF_8).size());
        } finally {
            Files.deleteIfExists(caminho);
            Files.deleteIfExists(pasta);
        }
    }
}
