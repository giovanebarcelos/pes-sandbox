"""
PES1601 - Implementação do Fluxo Modelado (Pedido)
Aula 16: Diagramas de Sequência e de Atividades
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Implementa o fluxo de pedido modelado nos diagramas de sequência
e atividades, demonstrando a tradução de UML para código.
"""


class EstoqueService:
    def __init__(self):
        self._estoque = {"Produto A": 10, "Produto B": 5, "Produto C": 0}

    def verificar_disponibilidade(self, itens):
        indisponiveis = [item for item in itens
                         if self._estoque.get(item, 0) <= 0]
        return len(indisponiveis) == 0, indisponiveis

    def reservar(self, itens):
        for item in itens:
            self._estoque[item] -= 1

    def liberar(self, itens):
        for item in itens:
            self._estoque[item] += 1


class PagamentoService:
    @staticmethod
    def processar(valor):
        # Simulação: pagamentos acima de R$ 10.000 falham (regra de negócio)
        return valor <= 10000


class PedidoService:
    def __init__(self):
        self.estoque = EstoqueService()

    def criar_pedido(self, cliente, itens, valor):
        print(f"\n{'='*50}")
        print(f"  PEDIDO — {cliente}")
        print(f"{'='*50}")

        # Passo 1: verificar disponibilidade
        disponivel, indisponiveis = self.estoque.verificar_disponibilidade(itens)
        if not disponivel:
            print(f"  ✗ Itens indisponíveis: {', '.join(indisponiveis)}")
            return False

        # Passo 2: reservar
        self.estoque.reservar(itens)
        print(f"  ✓ Itens reservados: {', '.join(itens)}")

        # Passo 3: processar pagamento
        if not PagamentoService.processar(valor):
            self.estoque.liberar(itens)
            print(f"  ✗ Pagamento recusado (valor: R$ {valor:.2f})")
            return False
        print(f"  ✓ Pagamento aprovado: R$ {valor:.2f}")

        # Passo 4: confirmar
        print(f"  ✓ Pedido confirmado!")
        print(f"  ✓ Email de confirmação enviado para {cliente}")
        return True


if __name__ == "__main__":
    ps = PedidoService()

    # Cenário 1: sucesso
    ps.criar_pedido("ana@email.com", ["Produto A", "Produto B"], 150.00)

    # Cenário 2: item indisponível
    ps.criar_pedido("carlos@email.com", ["Produto C"], 50.00)

    # Cenário 3: pagamento recusado
    ps.criar_pedido("maria@email.com", ["Produto A"], 15000.00)

    print(f"\n✓ O código reflete fielmente o fluxo dos diagramas UML.")
