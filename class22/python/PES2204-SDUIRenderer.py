"""
PES2204 - Renderizador Server-Driven UI (SDUI)
Aula 22: Microsserviços e Arquiteturas Distribuídas
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra o núcleo de um cliente SDUI (Geração 2 - "JSON de componentes"):
o "servidor" envia uma árvore de componentes (aqui já desserializada em dict,
como se tivesse chegado por JSON) e o cliente a RENDERIZA. O cliente não conhece
nenhuma tela específica - só sabe renderizar os tipos de componente registrados.

Conceitos do curso aplicados:
  - Factory Method (GoF, Aula 18): mapeia component["type"] -> função de render
  - Fallback seguro: tipo desconhecido não quebra a tela (o "paradoxo do SDUI")
  - Ação declarativa (Command, Aula 21): o botão carrega uma ação como dado,
    não código - o cliente só a interpreta

Executar: python3 PES2204-SDUIRenderer.py
"""


class RenderizadorSDUI:
    """Cliente SDUI: registra tipos conhecidos e renderiza a árvore recebida."""

    def __init__(self):
        # Factory: nome do tipo -> função que renderiza aquele componente
        self._fabrica = {
            "column": self._render_column,
            "text": self._render_text,
            "button": self._render_button,
        }

    def registrar(self, tipo, funcao):
        """Novos componentes precisam ser registrados no cliente (limitação do SDUI)."""
        self._fabrica[tipo] = funcao

    def renderizar(self, node, nivel=0):
        tipo = node.get("type", "")
        render = self._fabrica.get(tipo, self._fallback)
        return render(node, nivel)

    # -- renderizadores por tipo (o "vocabulário" que o cliente conhece) --
    def _render_column(self, node, nivel):
        linhas = [self._ind(nivel) + "[coluna]"]
        for filho in node.get("children", []):
            linhas.append(self.renderizar(filho, nivel + 1))
        return "\n".join(linhas)

    def _render_text(self, node, nivel):
        valor = node.get("props", {}).get("value", "")
        return f"{self._ind(nivel)}texto: {valor}"

    def _render_button(self, node, nivel):
        props = node.get("props", {})
        rotulo = props.get("label", "OK")
        acao = props.get("action", {})
        return f"{self._ind(nivel)}[ {rotulo} ] -> acao declarativa: {self._descrever_acao(acao)}"

    def _fallback(self, node, nivel):
        # Tipo desconhecido: não quebra a renderização (fallback seguro)
        return f"{self._ind(nivel)}(componente '{node.get('type')}' desconhecido - ignorado)"

    @staticmethod
    def _descrever_acao(acao):
        tipo = acao.get("type", "nenhuma")
        if tipo == "navigate":
            return f"navegar para {acao.get('route')}"
        if tipo == "submit":
            return f"enviar para {acao.get('endpoint')}"
        return tipo

    @staticmethod
    def _ind(nivel):
        return "  " * nivel


def main():
    # Payload que "chegaria" do servidor como JSON (schema versionado)
    tela = {
        "screen": "home",
        "version": 3,
        "root": {
            "type": "column",
            "children": [
                {"type": "text", "props": {"value": "Leiloes ativos"}},
                {"type": "button", "props": {
                    "label": "Ver leilao",
                    "action": {"type": "navigate", "route": "/auction/123"},
                }},
                # tipo que este cliente (ainda) nao conhece -> fallback seguro
                {"type": "auction_card", "props": {"id": "abc-123"}},
            ],
        },
    }

    cliente = RenderizadorSDUI()
    print(f"Renderizando tela '{tela['screen']}' (schema v{tela['version']}):\n")
    print(cliente.renderizar(tela["root"]))

    print("\n--- Apos publicar update do app (novo tipo registrado) ---\n")
    cliente.registrar(
        "auction_card",
        lambda node, nivel: f"{'  ' * nivel}[card de leilao #{node['props']['id']}]",
    )
    print(cliente.renderizar(tela["root"]))


if __name__ == "__main__":
    main()
