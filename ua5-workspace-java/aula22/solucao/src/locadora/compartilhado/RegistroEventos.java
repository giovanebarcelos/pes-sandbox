package locadora.compartilhado;

import java.lang.reflect.RecordComponent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Converte eventos (records) em mapas e de volta — o necessário para gravar
 * eventos em disco ou mandá-los pela rede. Cada tipo precisa ser REGISTRADO
 * para poder ser lido de volta (o @evento do Python).
 */
public final class RegistroEventos {
    private static final Map<String, Class<? extends EventoDominio>> TIPOS = new ConcurrentHashMap<>();

    private RegistroEventos() {
    }

    @SafeVarargs
    public static void registrar(Class<? extends EventoDominio>... classes) {
        for (Class<? extends EventoDominio> c : classes) {
            if (!c.isRecord()) {
                throw new IllegalArgumentException(c.getSimpleName() + " precisa ser um record");
            }
            TIPOS.put(c.getSimpleName(), c);
        }
    }

    /** {"tipo": "LocacaoIniciada", "idAgregado": "L-1", ...} */
    public static Map<String, Object> paraMapa(EventoDominio e) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("tipo", e.tipo());
        try {
            for (RecordComponent c : e.getClass().getRecordComponents()) {
                dados.put(c.getName(), c.getAccessor().invoke(e));
            }
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
        return dados;
    }

    public static EventoDominio deMapa(Map<String, Object> dados) {
        Class<? extends EventoDominio> classe = TIPOS.get((String) dados.get("tipo"));
        if (classe == null) {
            throw new IllegalArgumentException("Tipo de evento não registrado: " + dados.get("tipo"));
        }
        RecordComponent[] componentes = classe.getRecordComponents();
        Class<?>[] tipos = new Class<?>[componentes.length];
        Object[] valores = new Object[componentes.length];
        for (int i = 0; i < componentes.length; i++) {
            tipos[i] = componentes[i].getType();
            valores[i] = converter(dados.get(componentes[i].getName()), tipos[i]);
        }
        try {
            return classe.getDeclaredConstructor(tipos).newInstance(valores);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Object converter(Object valor, Class<?> tipo) {
        if (valor instanceof Number n) {
            if (tipo == int.class || tipo == Integer.class) {
                return n.intValue();
            }
            if (tipo == long.class || tipo == Long.class) {
                return n.longValue();
            }
            if (tipo == double.class || tipo == Double.class) {
                return n.doubleValue();
            }
        }
        return valor;
    }
}
