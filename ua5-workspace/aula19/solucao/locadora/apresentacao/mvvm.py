"""
MVVM — a View se LIGA (data binding) a propriedades observáveis do ViewModel.

  ViewModel -> estado da tela (placa, dias, cotacao, mensagem, frota) +
              comandos (alugar). Não conhece a View.
  View      -> se inscreve nas propriedades e se redesenha sozinha quando mudam.
  Model     -> ServicoLocacao

Sem um framework de binding (WPF, Jetpack, Angular...), o binding é feito à mão
com o padrão Observer (GoF, Aula 18) — classe Observavel abaixo.

Executar (na pasta solucao/):  python -m locadora.apresentacao.mvvm
"""

from ..erros import LocadoraErro
from .montagem import servico_exemplo


class Observavel:
    """Observer: guarda um valor e avisa os inscritos quando ele muda."""

    def __init__(self, valor=None):
        self._valor = valor
        self._inscritos = []

    def inscrever(self, funcao):
        self._inscritos.append(funcao)
        funcao(self._valor)

    @property
    def valor(self):
        return self._valor

    @valor.setter
    def valor(self, novo):
        if novo != self._valor:
            self._valor = novo
            for f in self._inscritos:
                f(novo)


class LocadoraViewModel:
    def __init__(self, modelo):
        self._modelo = modelo
        self.placa = Observavel("")
        self.dias = Observavel(1)
        self.cliente = Observavel("")
        self.email = Observavel("")
        self.cotacao = Observavel("")
        self.mensagem = Observavel("")
        self.frota = Observavel([])
        self.pode_alugar = Observavel(False)
        for campo in (self.placa, self.dias, self.cliente, self.email):
            campo.inscrever(lambda _v: self._recalcular())
        self._atualizar_frota()

    def _recalcular(self):
        """Estado derivado: cotação e habilitação do botão mudam a cada digitação."""
        try:
            self.cotacao.valor = f"R$ {self._modelo.cotar(self.placa.valor, int(self.dias.valor)):.2f}"
        except (LocadoraErro, ValueError, TypeError):
            self.cotacao.valor = "—"
        self.pode_alugar.valor = (
            self.cotacao.valor != "—" and bool(self.cliente.valor) and "@" in (self.email.valor or "")
        )

    def _atualizar_frota(self):
        self.frota.valor = [
            f"{v.placa} {v.modelo} ({'disponível' if v.disponivel else 'alugado'})"
            for v in self._modelo.listar()
        ]

    # ---- comando (o "botão" da tela se liga a este método) ----
    def comando_alugar(self):
        if not self.pode_alugar.valor:
            self.mensagem.valor = "Preencha placa válida, cliente e e-mail"
            return
        try:
            loc = self._modelo.alugar(self.placa.valor, self.cliente.valor,
                                      self.email.valor, int(self.dias.valor))
            self.mensagem.valor = f"OK: {loc.placa} alugado — R$ {loc.valor:.2f}"
        except LocadoraErro as e:
            self.mensagem.valor = f"ERRO: {str(e).strip(chr(39))}"
        self._atualizar_frota()
        self._recalcular()


def main():
    vm = LocadoraViewModel(servico_exemplo(silencioso=True))
    # "binding" da View: cada propriedade observada imprime quando muda
    vm.frota.inscrever(lambda linhas: print("Frota:\n  " + "\n  ".join(linhas)))
    vm.cotacao.inscrever(lambda c: print(f"  (cotação: {c})"))
    vm.mensagem.inscrever(lambda m: m and print(m))
    vm.placa.valor = input("Placa: ").strip().upper()
    vm.dias.valor = input("Dias: ").strip() or "1"
    vm.cliente.valor = input("Cliente: ").strip()
    vm.email.valor = input("E-mail: ").strip()
    vm.comando_alugar()


if __name__ == "__main__":
    main()
