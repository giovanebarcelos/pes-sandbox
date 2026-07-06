"""
PES2203 - Comunicação Síncrona x Assíncrona entre Serviços
Aula 22: Microsserviços e Arquiteturas Distribuídas
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Contrasta os dois estilos de comunicação entre microsserviços:
- Síncrona (request/response): quem chama espera a resposta antes de continuar.
- Assíncrona (mensageria/eventos): quem chama publica um evento e segue
  em frente, sem esperar quem vai consumi-lo nem quando.
"""

import queue


# --- COMUNICAÇÃO SÍNCRONA: Pedidos chama Estoque e espera a resposta ---
class ServicoEstoqueSincrono:
    def consultar_estoque_sincrono(self, placa: str) -> int:
        print(f"[Estoque] Consultando quantidade de {placa}...")
        return 3  # resposta imediata, quem chamou fica bloqueado até aqui


class ServicoPedidosSincrono:
    def __init__(self, estoque: ServicoEstoqueSincrono):
        self.estoque = estoque

    def criar_pedido(self, placa: str) -> str:
        quantidade = self.estoque.consultar_estoque_sincrono(placa)  # bloqueia
        print(f"[Pedidos] Resposta recebida: {quantidade} unidades")
        return "Criado" if quantidade > 0 else "Recusado"


# --- COMUNICAÇÃO ASSÍNCRONA: Pedidos publica um evento e não espera ---
class BarramentoEventos:
    """Simula um message broker (ex.: RabbitMQ/Kafka) com uma fila em memória."""

    def __init__(self):
        self._fila = queue.Queue()
        self._assinantes = []

    def assinar(self, callback) -> None:
        self._assinantes.append(callback)

    def publicar(self, evento: str, dados: dict) -> None:
        print(f"[Barramento] Evento publicado: {evento} {dados}")
        self._fila.put((evento, dados))

    def processar_fila(self) -> None:
        while not self._fila.empty():
            evento, dados = self._fila.get()
            for callback in self._assinantes:
                callback(evento, dados)


class ServicoPedidosAssincrono:
    def __init__(self, barramento: BarramentoEventos):
        self.barramento = barramento

    def notificar_pedido_criado(self, placa: str) -> None:
        print(f"[Pedidos] Publicando evento e seguindo em frente (não bloqueia)...")
        self.barramento.publicar("PedidoCriado", {"placa": placa})


class ServicoEmailAssincrono:
    def ao_receber_evento(self, evento: str, dados: dict) -> None:
        if evento == "PedidoCriado":
            print(f"[E-mail] Recebeu o evento com atraso e envia confirmação para {dados['placa']}")


if __name__ == "__main__":
    print("=" * 60)
    print("  COMUNICAÇÃO SÍNCRONA x ASSÍNCRONA")
    print("=" * 60)

    print("\n--- Síncrona (request/response) ---")
    pedidos_sinc = ServicoPedidosSincrono(ServicoEstoqueSincrono())
    resultado = pedidos_sinc.criar_pedido("ABC1234")
    print(f"  Resultado: {resultado}")
    assert resultado == "Criado"

    print("\n--- Assíncrona (mensageria/eventos) ---")
    barramento = BarramentoEventos()
    email = ServicoEmailAssincrono()
    barramento.assinar(email.ao_receber_evento)

    pedidos_assinc = ServicoPedidosAssincrono(barramento)
    pedidos_assinc.notificar_pedido_criado("XYZ9876")
    print("  [Pedidos] Já retornou para o cliente, sem esperar o e-mail ser enviado.")

    barramento.processar_fila()  # o consumo do evento acontece depois, de forma desacoplada

    print("\n✓ Síncrona: quem chama fica bloqueado esperando a resposta.")
    print("✓ Assíncrona: quem chama publica e segue, o consumidor processa depois.")
