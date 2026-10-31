"""
PES1803 - Exemplos dos 18 padrões GoF da tabela do Slide 23
Aula 18: Princípios de Projeto e Padrões Arquiteturais
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Criacionais:   Singleton, Factory Method, Abstract Factory, Builder, Prototype
Estruturais:   Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
Comportamentais: Observer, Strategy, Command, Iterator, State, Template Method

Executar: python3 PES1803-PadroesGoF.py
"""

from __future__ import annotations
import copy
from abc import ABC, abstractmethod


def titulo(txt: str) -> None:
    print(f"\n=== {txt} ===")


# ───────────────────────── CRIACIONAIS ─────────────────────────

# 1. Singleton
class DatabaseConnection:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance


def demo_singleton():
    titulo("1. Singleton")
    a, b = DatabaseConnection(), DatabaseConnection()
    print("Mesma instância?", a is b)


# 2. Factory Method
class Transporte(ABC):
    @abstractmethod
    def entregar(self) -> str: ...


class Caminhao(Transporte):
    def entregar(self): return "Entrega por estrada (caminhão)"


class Navio(Transporte):
    def entregar(self): return "Entrega por mar (navio)"


class TransporteFactory(ABC):
    @abstractmethod
    def criar_transporte(self) -> Transporte: ...


class RodoviarioFactory(TransporteFactory):
    def criar_transporte(self): return Caminhao()


class MaritimoFactory(TransporteFactory):
    def criar_transporte(self): return Navio()


def demo_factory_method():
    titulo("2. Factory Method")
    for f in (RodoviarioFactory(), MaritimoFactory()):
        print(f.criar_transporte().entregar())


# 3. Abstract Factory
class BotaoDark:
    def desenhar(self): return "Botão escuro"


class CheckboxDark:
    def desenhar(self): return "Checkbox escuro"


class BotaoLight:
    def desenhar(self): return "Botão claro"


class CheckboxLight:
    def desenhar(self): return "Checkbox claro"


class GUIFactory(ABC):
    @abstractmethod
    def criar_botao(self): ...
    @abstractmethod
    def criar_checkbox(self): ...


class DarkFactory(GUIFactory):
    def criar_botao(self): return BotaoDark()
    def criar_checkbox(self): return CheckboxDark()


class LightFactory(GUIFactory):
    def criar_botao(self): return BotaoLight()
    def criar_checkbox(self): return CheckboxLight()


def demo_abstract_factory():
    titulo("3. Abstract Factory")
    for f in (DarkFactory(), LightFactory()):
        print(f.criar_botao().desenhar(), "+", f.criar_checkbox().desenhar())


# 4. Builder
class Pizza:
    def __init__(self):
        self.borda = "tradicional"
        self.coberturas: list[str] = []

    def __str__(self): return f"Pizza de borda {self.borda} com {', '.join(self.coberturas)}"


class PizzaBuilder:
    def __init__(self): self.pizza = Pizza()
    def borda(self, tipo): self.pizza.borda = tipo; return self
    def cobertura(self, c): self.pizza.coberturas.append(c); return self
    def build(self): return self.pizza


def demo_builder():
    titulo("4. Builder")
    print(PizzaBuilder().borda("fina").cobertura("queijo").cobertura("tomate").build())


# 5. Prototype
class Documento:
    def __init__(self, texto, tags): self.texto, self.tags = texto, tags
    def clonar(self): return copy.deepcopy(self)


def demo_prototype():
    titulo("5. Prototype")
    modelo = Documento("Contrato padrão", ["jurídico"])
    copia = modelo.clonar()
    copia.tags.append("cliente-X")
    print("Modelo:", modelo.tags, "| Cópia:", copia.tags)


# ───────────────────────── ESTRUTURAIS ─────────────────────────

# 6. Adapter
class PagamentoLegado:
    def efetuar_pgto(self, centavos: int): return f"Legado: pagos {centavos} centavos"


class PagamentoAdapter:
    def __init__(self, legado): self.legado = legado
    def pagar(self, reais: float): return self.legado.efetuar_pgto(int(reais * 100))


def demo_adapter():
    titulo("6. Adapter")
    print(PagamentoAdapter(PagamentoLegado()).pagar(12.50))


# 7. Bridge
class Dispositivo(ABC):
    @abstractmethod
    def ligar(self) -> str: ...


class TV(Dispositivo):
    def ligar(self): return "TV ligada"


class Radio(Dispositivo):
    def ligar(self): return "Rádio ligado"


class Controle:
    def __init__(self, disp: Dispositivo): self.disp = disp
    def botao_power(self): return self.disp.ligar()


def demo_bridge():
    titulo("7. Bridge")
    for d in (TV(), Radio()):
        print(Controle(d).botao_power())


# 8. Composite
class Arquivo:
    def __init__(self, kb): self.kb = kb
    def tamanho(self): return self.kb


class Pasta:
    def __init__(self): self.itens = []
    def add(self, i): self.itens.append(i); return self
    def tamanho(self): return sum(i.tamanho() for i in self.itens)


def demo_composite():
    titulo("8. Composite")
    raiz = Pasta().add(Arquivo(10)).add(Pasta().add(Arquivo(5)).add(Arquivo(20)))
    print("Tamanho total:", raiz.tamanho(), "KB")


# 9. Decorator
class Cafe:
    def custo(self): return 5.0


class ComLeite:
    def __init__(self, bebida): self.bebida = bebida
    def custo(self): return self.bebida.custo() + 2.0


class ComChocolate:
    def __init__(self, bebida): self.bebida = bebida
    def custo(self): return self.bebida.custo() + 3.0


def demo_decorator():
    titulo("9. Decorator")
    print("Café com leite e chocolate: R$", ComChocolate(ComLeite(Cafe())).custo())


# 10. Facade
class Luzes:
    def apagar(self): print("  luzes apagadas")


class Tela:
    def descer(self): print("  tela descendo")


class Projetor:
    def ligar(self): print("  projetor ligado")


class HomeTheaterFacade:
    def __init__(self): self.luzes, self.tela, self.projetor = Luzes(), Tela(), Projetor()

    def assistir_filme(self):
        self.luzes.apagar(); self.tela.descer(); self.projetor.ligar()


def demo_facade():
    titulo("10. Facade")
    HomeTheaterFacade().assistir_filme()


# 11. Flyweight
class Icone:
    def __init__(self, nome): self.nome = nome


class FabricaIcones:
    _cache: dict[str, Icone] = {}

    @classmethod
    def get(cls, nome):
        if nome not in cls._cache:
            cls._cache[nome] = Icone(nome)
        return cls._cache[nome]


def demo_flyweight():
    titulo("11. Flyweight")
    a, b = FabricaIcones.get("pasta"), FabricaIcones.get("pasta")
    print("Mesmo objeto compartilhado?", a is b, "| instâncias:", len(FabricaIcones._cache))


# 12. Proxy
class ImagemReal:
    def __init__(self, arq): print(f"  (carregando {arq} do disco...)"); self.arq = arq
    def exibir(self): print(f"  exibindo {self.arq}")


class ImagemProxy:
    def __init__(self, arq): self.arq, self._real = arq, None

    def exibir(self):
        if self._real is None:
            self._real = ImagemReal(self.arq)  # carregamento preguiçoso
        self._real.exibir()


def demo_proxy():
    titulo("12. Proxy")
    img = ImagemProxy("foto.png")
    img.exibir(); img.exibir()


# ───────────────────────── COMPORTAMENTAIS ─────────────────────────

# 13. Observer
class Subject:
    def __init__(self): self._observers = []
    def attach(self, obs): self._observers.append(obs)

    def notify(self, msg):
        for obs in self._observers:
            obs.update(msg)


class Assinante:
    def __init__(self, nome): self.nome = nome
    def update(self, msg): print(f"  {self.nome} recebeu: {msg}")


def demo_observer():
    titulo("13. Observer")
    s = Subject(); s.attach(Assinante("Ana")); s.attach(Assinante("Beto"))
    s.notify("Pedido enviado")


# 14. Strategy
class FreteStrategy(ABC):
    @abstractmethod
    def calcular(self, peso): ...


class FreteExpresso(FreteStrategy):
    def calcular(self, peso): return peso * 2.5


class FreteEconomico(FreteStrategy):
    def calcular(self, peso): return peso * 1.0


def demo_strategy():
    titulo("14. Strategy")
    for s in (FreteExpresso(), FreteEconomico()):
        print(f"{type(s).__name__}: R$ {s.calcular(4):.2f}")


# 15. Command
class Luz:
    def __init__(self): self.ligada = False
    def ligar(self): self.ligada = True
    def desligar(self): self.ligada = False


class LigarLuz:
    def __init__(self, luz): self.luz = luz
    def executar(self): self.luz.ligar()
    def desfazer(self): self.luz.desligar()


def demo_command():
    titulo("15. Command")
    luz = Luz(); cmd = LigarLuz(luz)
    cmd.executar(); print("Após executar:", luz.ligada)
    cmd.desfazer(); print("Após desfazer:", luz.ligada)


# 16. Iterator
class Playlist:
    def __init__(self, musicas): self._musicas = musicas

    def __iter__(self):
        for m in self._musicas:
            yield m


def demo_iterator():
    titulo("16. Iterator")
    for musica in Playlist(["Faixa A", "Faixa B", "Faixa C"]):
        print(" ", musica)


# 17. State
class Novo:
    nome = "Novo"
    def proximo(self): return Pago()


class Pago:
    nome = "Pago"
    def proximo(self): return Enviado()


class Enviado:
    nome = "Enviado"
    def proximo(self): return self


class Pedido:
    def __init__(self): self.estado = Novo()
    def avancar(self): self.estado = self.estado.proximo()


def demo_state():
    titulo("17. State")
    p = Pedido()
    for _ in range(3):
        print("Estado:", p.estado.nome); p.avancar()
    print("Estado final:", p.estado.nome)


# 18. Template Method
class Relatorio(ABC):
    def gerar(self):  # método-template
        return self.formatar(self.coletar())

    @abstractmethod
    def coletar(self): ...
    @abstractmethod
    def formatar(self, dados): ...


class RelatorioCSV(Relatorio):
    def coletar(self): return [("Ana", 8), ("Beto", 7)]
    def formatar(self, d): return "\n".join(f"{n},{v}" for n, v in d)


class RelatorioTexto(Relatorio):
    def coletar(self): return [("Ana", 8), ("Beto", 7)]
    def formatar(self, d): return "\n".join(f"{n} tirou {v}" for n, v in d)


def demo_template_method():
    titulo("18. Template Method")
    for r in (RelatorioCSV(), RelatorioTexto()):
        print(r.gerar())


if __name__ == "__main__":
    for demo in (demo_singleton, demo_factory_method, demo_abstract_factory, demo_builder,
                 demo_prototype, demo_adapter, demo_bridge, demo_composite, demo_decorator,
                 demo_facade, demo_flyweight, demo_proxy, demo_observer, demo_strategy,
                 demo_command, demo_iterator, demo_state, demo_template_method):
        demo()
