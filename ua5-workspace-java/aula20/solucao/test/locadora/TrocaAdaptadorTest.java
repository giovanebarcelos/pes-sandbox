package locadora;

import static teste.Assertions.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import locadora.aplicacao.LocadoraService;
import locadora.dominio.Veiculo;
import teste.Assumptions;
import teste.Test;

/** Atividade do Slide 33: trocar o adaptador SEM alterar LocadoraService. */
class TrocaAdaptadorTest {
    private static List<Object> roteiro(LocadoraService servico) {
        servico.alugar("ABC-1234", "Ana", "a@x", 5);
        servico.alugar("XYZ-9012", "Bia", "b@x", 7);
        List<String> livres = new ArrayList<>();
        for (Veiculo v : servico.listarDisponiveis()) {
            livres.add(v.placa());
        }
        return List.of(livres, servico.faturamentoTotal());
    }

    @Test
    void mesmo_resultado_em_memoria_e_arquivo() {
        Path pasta = Pastas.temporaria();
        try {
            List<Object> memoria = roteiro(Config.montar("memoria", null, true, true));
            List<Object> arquivo = roteiro(Config.montar("arquivo", pasta.toString(), true, true));
            assertEquals(memoria, arquivo);
            assertEquals(List.of(List.of("DEF-5678", "CAM-0001"), 1164.0), memoria);
        } finally {
            Pastas.apagar(pasta);
        }
    }

    @Test
    void mesmo_resultado_com_jdbc() {
        String url = JdbcDisponivel.emMemoria();
        Assumptions.assumeTrue(url != null, JdbcDisponivel.SEM_DRIVER);
        assertEquals(List.of(List.of("DEF-5678", "CAM-0001"), 1164.0), roteiro(Config.montar("jdbc", url, true, true)));
    }

    @Test
    void arquivo_persiste_entre_execucoes() {
        Path pasta = Pastas.temporaria();
        try {
            Config.montar("arquivo", pasta.toString(), true, true).alugar("ABC-1234", "Ana", "a@x", 5);
            LocadoraService reaberto = Config.montar("arquivo", pasta.toString(), true, true);
            assertFalse(reaberto.listar().get(0).disponivel());
            assertEquals(660.0, reaberto.faturamentoTotal());
        } finally {
            Pastas.apagar(pasta);
        }
    }

    @Test
    void jdbc_em_arquivo_persiste_entre_execucoes() {
        Path pasta = Pastas.temporaria();
        try {
            String url = JdbcDisponivel.emArquivo(pasta);
            Assumptions.assumeTrue(url != null, JdbcDisponivel.SEM_DRIVER);
            Config.montar("jdbc", url, true, true).alugar("ABC-1234", "Ana", "a@x", 5);
            LocadoraService reaberto = Config.montar("jdbc", url, true, true);
            assertFalse(reaberto.listar().get(0).disponivel());
            assertEquals(660.0, reaberto.faturamentoTotal());
        } finally {
            Pastas.apagar(pasta);
        }
    }

    @Test
    void adaptador_desconhecido() {
        assertThrows(IllegalArgumentException.class, () -> Config.montar("mongodb", null, true, true));
    }
}
