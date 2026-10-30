"""
PES16B01 - Máquina de Estados do Pedido
Aula 16B: Diagrama de Máquina de Estados (UML)
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa o diagrama PES16B-EstadosPedido com uma TABELA DE TRANSIÇÕES:
cada seta do diagrama vira uma linha (origem, evento) -> (guarda, ação, destino).
Evento que não está na tabela = transição que o diagrama não desenhou = erro.

Executar: python3 PES16B01-MaquinaEstadosPedido.py
"""
from enum import Enum


class Estado(Enum):
    NOVO = "Novo"
    PAGO = "Pago"
    PREPARANDO = "Preparando"
    ENVIADO = "Enviado"
    ENTREGUE = "Entregue"
    CANCELADO = "Cancelado"


class TransicaoInvalida(Exception):
    """Evento que o diagrama não prevê para o estado atual."""


class Pedido:
    # (origem, evento) -> (guarda, ação, destino) — uma linha por seta do diagrama
    TRANSICOES = {
        (Estado.NOVO, "confirmarPagamento"):
            (lambda p, valor: valor >= p.valor, "reservarEstoque", Estado.PAGO),
        (Estado.NOVO, "cancelar"): (None, None, Estado.CANCELADO),
        (Estado.NOVO, "after(48h)"): (None, None, Estado.CANCELADO),
        (Estado.PAGO, "iniciarSeparacao"): (None, None, Estado.PREPARANDO),
        (Estado.PAGO, "cancelar"): (None, "estornarPagamento", Estado.CANCELADO),
        (Estado.PREPARANDO, "despachar"): (None, "gerarRastreio", Estado.ENVIADO),
        (Estado.ENVIADO, "confirmarEntrega"): (None, None, Estado.ENTREGUE),
    }
    # Ação de entrada (entry /) executada sempre que o objeto ENTRA no estado
    ENTRADA = {
        Estado.PAGO: "enviarEmailConfirmacao",
        Estado.ENVIADO: "avisarClienteRastreio",
        Estado.CANCELADO: "liberarEstoque",
    }
    FINAIS = {Estado.ENTREGUE, Estado.CANCELADO}

    def __init__(self, numero, valor):
        self.numero = numero
        self.valor = valor
        self.estado = Estado.NOVO          # [*] --> Novo : criarPedido
        self.historico = [Estado.NOVO]

    def disparar(self, evento, dado=None):
        chave = (self.estado, evento)
        if chave not in self.TRANSICOES:
            raise TransicaoInvalida(
                f"Pedido {self.numero}: '{evento}' não é permitido em {self.estado.value}")
        guarda, acao, destino = self.TRANSICOES[chave]
        if guarda and not guarda(self, dado):
            # Guarda falsa: a transição NÃO dispara e o objeto continua onde está
            print(f"  [{self.estado.value}] {evento}: guarda falsa, evento ignorado")
            return False
        origem = self.estado
        if acao:
            print(f"  / {acao}")
        self.estado = destino
        self.historico.append(destino)
        print(f"  {origem.value} --{evento}--> {destino.value}")
        if destino in self.ENTRADA:
            print(f"  entry / {self.ENTRADA[destino]}")
        return True

    def eventos_possiveis(self):
        return [ev for (origem, ev) in self.TRANSICOES if origem == self.estado]

    def encerrado(self):
        return self.estado in self.FINAIS


if __name__ == "__main__":
    print("=== Pedido 1: caminho feliz (com um pagamento a menor) ===")
    p1 = Pedido(1, 150.00)
    p1.disparar("confirmarPagamento", 100.00)   # guarda [valor confere] falsa
    p1.disparar("confirmarPagamento", 150.00)
    p1.disparar("iniciarSeparacao")
    p1.disparar("despachar")
    p1.disparar("confirmarEntrega")
    print("  histórico:", " -> ".join(e.value for e in p1.historico))

    print("\n=== Pedido 2: cancelado depois de pago ===")
    p2 = Pedido(2, 80.00)
    p2.disparar("confirmarPagamento", 80.00)
    print("  eventos possíveis em Pago:", p2.eventos_possiveis())
    p2.disparar("cancelar")

    print("\n=== Pedido 3: expirou sem pagamento (evento de tempo) ===")
    p3 = Pedido(3, 45.00)
    p3.disparar("after(48h)")

    print("\n=== Transições que o diagrama NÃO desenhou ===")
    for pedido, evento in [(Pedido(4, 10.00), "despachar"),   # Novo -> Enviado
                           (p1, "cancelar")]:                 # Entregue é final
        try:
            pedido.disparar(evento)
        except TransicaoInvalida as erro:
            print(f"  ✗ {erro}")
