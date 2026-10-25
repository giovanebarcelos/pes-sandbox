# ReservaLab

**Versão:** 1.0.0 · **Licença:** MIT · **Status:** Entrega A3

Sistema de reserva de salas de estudo, laboratórios e do auditório do campus. Evita conflito de horário e aplica regras por tipo de recurso (perfil, duração, antecedência, aprovação), com bloqueio de quem cancela em cima da hora.

> **Repositório de referência** da disciplina Projeto e Engenharia de Software (A3). O grupo e os registros de IA são ilustrativos; o código, os testes e os números são reais e foram executados.

## Stack

Python 3.10+ · pytest · SQLite (biblioteca padrão) · Docker e Docker Compose · Mermaid (diagramas)

## Como rodar

**Com Docker (forma oficial de entrega):**

```bash
docker compose up --build              # demonstração
docker compose run --rm testes         # 74 testes
```

**Sem Docker:**

```bash
python -m venv .venv && source .venv/bin/activate
pip install pytest
pytest                              # 74 testes
PYTHONPATH=src python -m reservalab.cli demo
```

## Estrutura

```
reservalab/
├── README.md · AGENTS.md · opencode.json · CHANGELOG.md · .gitignore
├── Dockerfile · docker-compose.yml · .dockerignore
├── docs/
│   ├── gestao.md              # processo (Kanban), EAP e riscos
│   ├── requisitos.md          # RF, RNF, Gherkin, rastreabilidade
│   ├── adr/                   # 001 camadas com portas · 002 atomicidade do conflito
│   ├── sdr/001-modulo-reservas.md
│   └── diagramas/             # casos de uso, classes, sequência, atividades, C4
├── src/reservalab/
│   ├── dominio/               # Recurso (SalaEstudo, Laboratorio, Auditorio), Reserva, Usuario, erros
│   ├── aplicacao/             # ServicoReservas, CatalogoRecursos, portas
│   ├── infra/                 # RepositorioMemoria, RepositorioSqlite
│   └── cli.py
└── tests/
```

## Como contribuir

Branch por tarefa (`feat/...`, `fix/...`), Conventional Commits, pull request com revisão de outro integrante; `pytest` verde antes do merge. O agente propõe, a equipe publica (nunca `git push` pelo agente).

## Uso de IA

> Seção de **exemplo** — a de vocês deve relatar o que realmente aconteceu. Política: Aula 01B, Slide 32.

**Ferramenta e modelo:** OpenCode + Nemotron 3 Ultra (`:free`) via OpenRouter. **Skills:** `grilling`, `to-spec`, `tdd`, `grill-with-docs`, `caveman-commit`, `retro`. Configuração em `opencode.json`; memória do agente em `AGENTS.md`.

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |
|---|---|---|---|---|
| 1 | `docs/requisitos.md` | `grilling` sobre regras de reserva, depois `to-spec` | Não perguntou o que acontece com o horário de uma reserva rejeitada; sugeriu "sistema rápido" sem número | Acrescentou o cenário "rejeitada libera o horário"; trocou "rápido" por RNF-01 (≤ 200 ms) |
| 2 | `docs/diagramas/classes.mmd` | Classes a partir do `docs/requisitos.md` | Criou `Sala`/`Lab`/`Auditorio` com `if tipo ==` em vez de herança com atributos | Refez com `Recurso` abstrata e subclasses só com dados (polimorfismo) |
| 3 | `tests/` | Skill `tdd` para RF-04 e RF-06 | Escreveu teste de "2h antes" sem os valores 12h00/12h01; afirmou "cobertura completa" | Acrescentou os valores-limite; quebrou o código de propósito (mutação) para provar que os testes falham |
| 4 | `docs/adr/002-...` | `grill-with-docs` sobre a corrida | Propôs `UNIQUE` no banco, que não cobre sobreposição | Rejeitou, registrou a alternativa no ADR e escolheu `BEGIN IMMEDIATE` |
| 5 | `docs/sdr/001-...` | Rascunho do SDR a partir do código | Listou teste de RNF-05 que não existe | Removeu e marcou a **lacuna conhecida** na matriz |

**Defesa oral:** todos os integrantes sabem explicar qualquer linha de `dominio/` e `aplicacao/`.
