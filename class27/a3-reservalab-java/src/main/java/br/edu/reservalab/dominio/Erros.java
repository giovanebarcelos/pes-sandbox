package br.edu.reservalab.dominio;

/** Erros de domínio: toda regra de negócio violada vira uma destas exceções. */
public final class Erros {
    private Erros() { }

    /** Base de todos os erros de regra de negócio. */
    public static class ErroDeDominio extends RuntimeException {
        public ErroDeDominio(String mensagem) { super(mensagem); }
    }

    /** Duração, antecedência ou perfil incompatíveis com o recurso. */
    public static class RegraViolada extends ErroDeDominio {
        public RegraViolada(String m) { super(m); }
    }

    /** Já existe reserva ativa para o recurso no intervalo pedido. */
    public static class ConflitoDeHorario extends ErroDeDominio {
        public ConflitoDeHorario(String m) { super(m); }
    }

    /** Usuário acumulou cancelamentos tardios e está temporariamente bloqueado. */
    public static class UsuarioBloqueado extends ErroDeDominio {
        public UsuarioBloqueado(String m) { super(m); }
    }

    /** Recurso ou reserva inexistente. */
    public static class NaoEncontrado extends ErroDeDominio {
        public NaoEncontrado(String m) { super(m); }
    }

    /** Mudança de status que a máquina de estados da reserva não permite. */
    public static class TransicaoInvalida extends ErroDeDominio {
        public TransicaoInvalida(String m) { super(m); }
    }

    /** O usuário não pode executar esta operação. */
    public static class NaoAutorizado extends ErroDeDominio {
        public NaoAutorizado(String m) { super(m); }
    }
}
