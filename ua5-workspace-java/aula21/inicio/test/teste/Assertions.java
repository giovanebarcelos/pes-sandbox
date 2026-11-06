package teste;

import java.util.Objects;

/**
 * Asserções com os MESMOS nomes do JUnit 5 (subconjunto).
 * Uso:  import static teste.Assertions.*;
 */
public final class Assertions {
    private Assertions() {
    }

    /** Bloco de código que pode lançar exceção (igual a org.junit.jupiter.api.function.Executable). */
    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    public static void fail(String mensagem) {
        throw new AssertionError(mensagem);
    }

    public static void assertTrue(boolean condicao) {
        assertTrue(condicao, "esperava true");
    }

    public static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            fail(mensagem);
        }
    }

    public static void assertFalse(boolean condicao) {
        assertFalse(condicao, "esperava false");
    }

    public static void assertFalse(boolean condicao, String mensagem) {
        if (condicao) {
            fail(mensagem);
        }
    }

    public static void assertNull(Object valor) {
        if (valor != null) {
            fail("esperava null, veio <" + valor + ">");
        }
    }

    public static void assertNotNull(Object valor) {
        if (valor == null) {
            fail("esperava um valor, veio null");
        }
    }

    public static void assertEquals(Object esperado, Object atual) {
        assertEquals(esperado, atual, null);
    }

    public static void assertEquals(Object esperado, Object atual, String mensagem) {
        if (!Objects.equals(esperado, atual)) {
            fail((mensagem == null ? "" : mensagem + " ==> ")
                    + "esperado: <" + esperado + "> mas foi: <" + atual + ">");
        }
    }

    /** Comparação EXATA de double (como no JUnit): use com valores já arredondados. */
    public static void assertEquals(double esperado, double atual) {
        if (Double.compare(esperado, atual) != 0) {
            fail("esperado: <" + esperado + "> mas foi: <" + atual + ">");
        }
    }

    /** Comparação com tolerância — o assertAlmostEqual do unittest. */
    public static void assertEquals(double esperado, double atual, double delta) {
        if (Math.abs(esperado - atual) > delta) {
            fail("esperado: <" + esperado + "> mas foi: <" + atual + "> (tolerância " + delta + ")");
        }
    }

    public static void assertEquals(long esperado, long atual) {
        if (esperado != atual) {
            fail("esperado: <" + esperado + "> mas foi: <" + atual + ">");
        }
    }

    public static void assertNotEquals(Object naoEsperado, Object atual) {
        if (Objects.equals(naoEsperado, atual)) {
            fail("esperava valor diferente de <" + atual + ">");
        }
    }

    /** Executa o bloco e exige que ele lance a exceção do tipo indicado (ou de uma subclasse). */
    public static <T extends Throwable> T assertThrows(Class<T> tipo, Executable bloco) {
        try {
            bloco.execute();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                return tipo.cast(t);
            }
            throw new AssertionError("esperava " + tipo.getSimpleName() + ", veio "
                    + t.getClass().getSimpleName() + ": " + t.getMessage(), t);
        }
        throw new AssertionError("esperava " + tipo.getSimpleName() + ", mas nada foi lançado");
    }
}
