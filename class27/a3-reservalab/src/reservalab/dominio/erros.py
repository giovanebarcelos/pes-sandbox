"""Erros de domínio: toda regra de negócio violada vira uma destas exceções."""


class ErroDeDominio(Exception):
    """Base de todos os erros de regra de negócio."""


class RegraViolada(ErroDeDominio):
    """Duração, antecedência ou perfil incompatíveis com o recurso."""


class ConflitoDeHorario(ErroDeDominio):
    """Já existe reserva ativa para o recurso no intervalo pedido."""


class UsuarioBloqueado(ErroDeDominio):
    """Usuário acumulou cancelamentos tardios e está temporariamente bloqueado."""


class NaoEncontrado(ErroDeDominio):
    """Recurso ou reserva inexistente."""


class TransicaoInvalida(ErroDeDominio):
    """Mudança de status que a máquina de estados da reserva não permite."""


class NaoAutorizado(ErroDeDominio):
    """O usuário não pode executar esta operação."""
