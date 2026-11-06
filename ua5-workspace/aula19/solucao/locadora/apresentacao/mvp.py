"""
MVP — a View é PASSIVA e conversa com o Presenter por uma interface.

  View      -> implementa LocadoraViewInterface (só lê campos e mostra coisas)
  Presenter -> LocadoraPresenter: TODA a lógica de apresentação
              (validação de campos, formatação, o que mostrar em caso de erro)
  Model     -> ServicoLocacao

Como a View é uma interface, o Presenter é testado com uma ViewFalsa,
sem abrir nenhuma tela (ver tests/test_mvp.py).

Executar (na pasta solucao/):  python -m locadora.apresentacao.mvp
"""

from abc import ABC, abstractmethod

from ..erros import LocadoraErro
from .montagem import servico_exemplo


class LocadoraViewInterface(ABC):
    @abstractmethod
    def obter_placa(self): ...

    @abstractmethod
    def obter_dias(self): ...

    @abstractmethod
    def obter_cliente(self): ...

    @abstractmethod
    def obter_email(self): ...

    @abstractmethod
    def mostrar_frota(self, linhas): ...

    @abstractmethod
    def mostrar_sucesso(self, mensagem): ...

    @abstractmethod
    def mostrar_erro(self, mensagem): ...


class LocadoraPresenter:
    def __init__(self, view, modelo):
        self.view = view
        self.modelo = modelo

    def carregar_frota(self):
        linhas = []
        for v in self.modelo.listar():
            marca = "[ ]" if v.disponivel else "[X]"
            linhas.append(f"{marca} {v.placa} {v.modelo} — R$ {v.valor_diaria:.2f}/dia")
        self.view.mostrar_frota(linhas)

    def ao_clicar_alugar(self):
        placa = self.view.obter_placa().strip().upper()
        cliente = self.view.obter_cliente().strip()
        email = self.view.obter_email().strip()
        dias_txt = self.view.obter_dias().strip()
        if not placa or not cliente:
            self.view.mostrar_erro("Informe placa e cliente")
            return
        if "@" not in email:
            self.view.mostrar_erro("E-mail inválido")
            return
        if not dias_txt.isdigit() or int(dias_txt) < 1:
            self.view.mostrar_erro("Dias deve ser um número inteiro maior que zero")
            return
        try:
            loc = self.modelo.alugar(placa, cliente, email, int(dias_txt))
        except LocadoraErro as e:
            self.view.mostrar_erro(str(e).strip("'\""))
            return
        self.view.mostrar_sucesso(f"{loc.placa} alugado para {loc.cliente}: R$ {loc.valor:.2f}")
        self.carregar_frota()


class ConsoleViewMVP(LocadoraViewInterface):
    """View real (terminal). Repare: nenhum if de regra aqui dentro."""

    def obter_placa(self):
        return input("Placa: ")

    def obter_dias(self):
        return input("Dias: ")

    def obter_cliente(self):
        return input("Cliente: ")

    def obter_email(self):
        return input("E-mail: ")

    def mostrar_frota(self, linhas):
        print("\n".join(linhas))

    def mostrar_sucesso(self, mensagem):
        print(f"OK: {mensagem}")

    def mostrar_erro(self, mensagem):
        print(f"ERRO: {mensagem}")


def main():
    presenter = LocadoraPresenter(ConsoleViewMVP(), servico_exemplo())
    presenter.carregar_frota()
    while input("\nAlugar um veículo? (s/n) ").strip().lower() == "s":
        presenter.ao_clicar_alugar()


if __name__ == "__main__":
    main()
