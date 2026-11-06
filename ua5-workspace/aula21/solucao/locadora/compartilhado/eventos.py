"""
Eventos de domínio + barramento (padrão Observer, Aula 18).

Um evento é um FATO no passado ("LocacaoIniciada"), imutável. O barramento
entrega cada evento publicado a quem se inscreveu para aquele tipo.
"""

from dataclasses import asdict, dataclass, fields


@dataclass(frozen=True)
class EventoDominio:
    id_agregado: str

    @property
    def tipo(self):
        return type(self).__name__

    def para_dict(self):
        return {"tipo": self.tipo, **asdict(self)}


_REGISTRO = {}


def evento(classe):
    """Decorador: registra a classe para que o evento possa ser lido de volta do disco."""
    _REGISTRO[classe.__name__] = classe
    return classe


def evento_de_dict(dados):
    dados = dict(dados)
    classe = _REGISTRO[dados.pop("tipo")]
    nomes = {f.name for f in fields(classe)}
    return classe(**{k: v for k, v in dados.items() if k in nomes})


class BarramentoEventos:
    def __init__(self):
        self._inscritos = {}

    def inscrever(self, tipo_evento, funcao):
        self._inscritos.setdefault(tipo_evento, []).append(funcao)

    def publicar(self, eventos):
        for e in eventos:
            for funcao in self._inscritos.get(type(e), []) + self._inscritos.get(EventoDominio, []):
                funcao(e)
