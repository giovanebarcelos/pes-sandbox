#!/usr/bin/env python3
"""
PES25B01 - Simulação de Pipeline de CI/CD
Aula 25B (complementar): Integração Contínua e Entrega Contínua (CI/CD)
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Executa em sequência as etapas típicas de um pipeline (Slide 7 da Aula 25B)
aplicando as três regras centrais da aula:

  1. FAIL FAST     -- se uma etapa falha, as seguintes NÃO são executadas;
  2. QUALITY GATE  -- a cobertura de testes abaixo do mínimo reprova o pipeline,
                      mesmo com todos os testes passando;
  3. CD DELIVERY x CD DEPLOY -- na Entrega Contínua a produção espera uma
                      aprovação humana; no Deploy Contínuo ela é automática.

Nada aqui depende de GitHub Actions, Gitea Actions, GitLab CI ou Jenkins: os
resultados de cada etapa são fixos (determinísticos), para que a saída seja
sempre a mesma em sala. Duração = tempo SIMULADO de um pipeline real.

Uso: python3 PES25B01-SimulacaoPipeline.py
"""

COBERTURA_MINIMA = 80.0


class Etapa:
    """Uma etapa (stage/job) do pipeline."""

    def __init__(self, nome: str, duracao_s: int, passa: bool = True):
        self.nome = nome
        self.duracao_s = duracao_s
        self.passa = passa

    def executar(self) -> tuple[bool, str]:
        return self.passa, "OK" if self.passa else "FALHOU"


class QualityGate(Etapa):
    """Etapa que reprova o pipeline se a cobertura ficar abaixo do mínimo."""

    def __init__(self, cobertura: float, duracao_s: int = 20):
        super().__init__(f"Quality Gate (cobertura >= {COBERTURA_MINIMA:.0f}%)", duracao_s)
        self.cobertura = cobertura

    def executar(self) -> tuple[bool, str]:
        ok = self.cobertura >= COBERTURA_MINIMA
        return ok, f"{'OK' if ok else 'FALHOU'} - cobertura {self.cobertura:.1f}%"


class DeployProducao(Etapa):
    """Última etapa: em 'delivery' espera aprovação humana; em 'deploy' é automática."""

    def __init__(self, modo: str, aprovado: bool, duracao_s: int = 40):
        super().__init__("Deploy em produção", duracao_s)
        self.modo = modo
        self.aprovado = aprovado

    def executar(self) -> tuple[bool, str]:
        if self.modo == "deploy":
            return True, "OK - automático (CD Deploy)"
        if self.aprovado:
            return True, "OK - aprovado manualmente (CD Delivery)"
        return False, "PENDENTE - aguardando aprovação (CD Delivery)"


class Pipeline:
    """Executa as etapas em sequência, com fail fast."""

    def __init__(self, nome: str, etapas: list[Etapa]):
        self.nome = nome
        self.etapas = etapas

    def executar(self) -> bool:
        print("=" * 64)
        print(f"  PIPELINE: {self.nome}")
        print("=" * 64)
        tempo_total = 0
        for indice, etapa in enumerate(self.etapas, start=1):
            ok, mensagem = etapa.executar()
            tempo_total += etapa.duracao_s
            print(f"[{indice}/{len(self.etapas)}] {etapa.nome:<40} [{mensagem}] ({etapa.duracao_s}s)")
            if not ok:
                restantes = len(self.etapas) - indice
                if isinstance(etapa, DeployProducao):
                    print(f"\n>> Artefato pronto e homologado; produção espera um clique humano.")
                else:
                    print(f"\n>> FAIL FAST: '{etapa.nome}' falhou; {restantes} etapa(s) NÃO executada(s).")
                print(f"   Feedback em {tempo_total}s (tempo simulado)")
                print("   RESULTADO: PIPELINE INTERROMPIDO")
                return False
        print(f"\n   Commit em produção em {tempo_total}s (tempo simulado)")
        print("   RESULTADO: PIPELINE APROVADO")
        return True


def etapas_padrao(testes_passam: bool, cobertura: float, modo: str, aprovado: bool) -> list[Etapa]:
    return [
        Etapa("Checkout", 5),
        Etapa("Build (compilar / instalar deps)", 40),
        Etapa("Testes unitários", 60, passa=testes_passam),
        Etapa("Testes de integração", 90),
        QualityGate(cobertura),
        Etapa("Package (imagem Docker)", 45),
        Etapa("Deploy em staging", 30),
        Etapa("Testes de API/E2E em staging", 120),
        DeployProducao(modo, aprovado),
    ]


def main() -> None:
    cenarios = [
        ("reservalab-api | main | CD Delivery, aprovado",
         etapas_padrao(True, 86.4, "delivery", aprovado=True)),
        ("reservalab-api | main | CD Delivery, sem aprovação",
         etapas_padrao(True, 86.4, "delivery", aprovado=False)),
        ("reservalab-api | main | CD Deploy (automático)",
         etapas_padrao(True, 86.4, "deploy", aprovado=False)),
        ("reservalab-api | feature/multa | teste quebrado",
         etapas_padrao(False, 86.4, "delivery", aprovado=True)),
        ("reservalab-api | feature/relatorio | testes verdes, cobertura baixa",
         etapas_padrao(True, 72.5, "delivery", aprovado=True)),
    ]
    for nome, etapas in cenarios:
        Pipeline(nome, etapas).executar()
        print()


if __name__ == "__main__":
    main()
