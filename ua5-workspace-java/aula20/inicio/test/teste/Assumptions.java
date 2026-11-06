package teste;

/** Pula o teste quando uma condição do AMBIENTE não vale (igual ao JUnit 5 / skipTest do unittest). */
public final class Assumptions {
    private Assumptions() {
    }

    /** Lançada para indicar teste pulado; o Executor conta como "skipped", não como falha. */
    public static final class TestePulado extends RuntimeException {
        public TestePulado(String motivo) {
            super(motivo);
        }
    }

    public static void assumeTrue(boolean condicao, String motivo) {
        if (!condicao) {
            throw new TestePulado(motivo);
        }
    }
}
