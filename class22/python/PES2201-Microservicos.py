"""
PES2201 - Arquitetura de Microserviços
Aula 22: Microsserviços e Arquiteturas Distribuídas
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Simula a comunicação entre dois microserviços independentes
(ServicoEstoque e ServicoVendas) através de suas interfaces públicas,
como se cada um rodasse em seu próprio processo/deploy. Cada serviço
só conhece o contrato do outro, nunca sua implementação interna.
"""


class ServicoEstoque:
    """Simula um microserviço de estoque, com seu próprio dado isolado."""

    def __init__(self):
        self._estoque = {"ABC1234": 3, "XYZ9876": 0}

    def verificar_disponibilidade(self, placa: str) -> bool:
        print(f"[Serviço Estoque] Verificando placa {placa}...")
        return self._estoque.get(placa, 0) > 0

    def reservar(self, placa: str) -> None:
        if self._estoque.get(placa, 0) > 0:
            self._estoque[placa] -= 1
            print(f"[Serviço Estoque] Reservado 1 unidade de {placa}")


class ServicoVendas:
    """Simula um microserviço de vendas que depende do Serviço Estoque."""

    def __init__(self, estoque: ServicoEstoque):
        self.estoque = estoque  # "chamada" a outro serviço via sua interface pública

    def criar_pedido(self, placa: str) -> str:
        if not self.estoque.verificar_disponibilidade(placa):
            print(f"[Serviço Vendas] Pedido recusado para {placa}: sem estoque")
            return "Indisponível"
        self.estoque.reservar(placa)
        print(f"[Serviço Vendas] Pedido criado para {placa}")
        return "Criado"


if __name__ == "__main__":
    print("=" * 60)
    print("  MICROSSERVIÇOS — VENDAS x ESTOQUE")
    print("=" * 60)

    estoque = ServicoEstoque()
    vendas = ServicoVendas(estoque)

    resultado1 = vendas.criar_pedido("ABC1234")
    resultado2 = vendas.criar_pedido("XYZ9876")

    print(f"\n  Pedido 1 (ABC1234): {resultado1}")
    print(f"  Pedido 2 (XYZ9876): {resultado2}")

    assert resultado1 == "Criado"
    assert resultado2 == "Indisponível"

    print("\n✓ Cada serviço tem seu próprio dado isolado (sem banco compartilhado).")
    print("✓ Vendas só conhece a interface pública de Estoque, nunca sua implementação.")
    print("✓ Em produção, essa chamada seria via rede (REST/gRPC), não uma referência Python direta.")
