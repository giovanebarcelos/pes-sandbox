"""
Adaptador de ENTRADA: terminal (MVC da Aula 19, agora na borda do hexágono).

  Model      -> LocadoraService (porta de entrada) — regras e estado
  View       -> VisaoConsole — só transforma dados em texto (não decide nada)
  Controller -> ControladorConsole — interpreta o comando do usuário,
                chama o Model e escolhe qual View mostrar

Executar (na pasta solucao/):  python -m locadora.adaptadores.entrada.cli
Comandos: listar | cotar PLACA DIAS | alugar PLACA DIAS NOME EMAIL | devolver PLACA | sair
"""

from ...dominio.erros import LocadoraErro
from ...config import montar


class VisaoConsole:
    def frota(self, veiculos):
        linhas = ["PLACA     MODELO            DIÁRIA     STATUS"]
        for v in veiculos:
            status = "disponível" if v.disponivel else "alugado"
            linhas.append(f"{v.placa:<9} {v.modelo:<17} R$ {v.valor_diaria:>7.2f} {status}")
        return "\n".join(linhas)

    def cotacao(self, placa, dias, valor):
        return f"Cotação {placa} por {dias} dia(s): R$ {valor:.2f}"

    def locacao(self, locacao):
        return f"OK: {locacao.placa} alugado para {locacao.cliente} — R$ {locacao.valor:.2f}"

    def mensagem(self, texto):
        return texto

    def erro(self, texto):
        return f"ERRO: {texto}"

    def ajuda(self):
        return ("Comandos: listar | cotar PLACA DIAS | alugar PLACA DIAS NOME EMAIL | "
                "devolver PLACA | sair")


class ControladorConsole:
    def __init__(self, modelo, visao):
        self.modelo = modelo
        self.visao = visao

    def executar(self, linha):
        """Recebe uma linha digitada e devolve o texto a exibir."""
        partes = linha.split()
        if not partes:
            return self.visao.ajuda()
        comando, args = partes[0].lower(), partes[1:]
        try:
            if comando == "listar":
                return self.visao.frota(self.modelo.listar())
            if comando == "cotar" and len(args) == 2:
                placa, dias = args[0], int(args[1])
                return self.visao.cotacao(placa, dias, self.modelo.cotar(placa, dias))
            if comando == "alugar" and len(args) == 4:
                placa, dias, nome, email = args[0], int(args[1]), args[2], args[3]
                return self.visao.locacao(self.modelo.alugar(placa, nome, email, dias))
            if comando == "devolver" and len(args) == 1:
                self.modelo.devolver(args[0])
                return self.visao.mensagem(f"OK: {args[0]} devolvido")
        except (LocadoraErro, ValueError) as e:
            return self.visao.erro(str(e).strip("'\""))
        return self.visao.ajuda()


def main():
    controlador = ControladorConsole(montar(), VisaoConsole())
    print(controlador.visao.ajuda())
    while True:
        try:
            linha = input("mvc> ")
        except EOFError:
            break
        if linha.strip().lower() == "sair":
            break
        print(controlador.executar(linha))


if __name__ == "__main__":
    main()
