# SDR-001: Módulo de Reservas

**Requisitos vinculados:** RF-03, RF-04, RF-05, RF-06, RF-07, RF-08 · RNF-01, RNF-03, RNF-04

## 1. Modelo de dados

**Tabela `reservas` (SQLite)**

| Coluna | Tipo | Observação |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `recurso_id` | TEXT NOT NULL | referencia o catálogo (em memória) |
| `matricula` | TEXT NOT NULL | único dado pessoal guardado (RNF-05) |
| `inicio`, `fim` | TEXT NOT NULL | ISO-8601 em minutos (`2026-11-10T14:00`); comparação lexicográfica = cronológica |
| `status` | TEXT NOT NULL | `PENDENTE`, `CONFIRMADA`, `REJEITADA`, `CANCELADA` |
| `cancelada_em` | TEXT | nulo até o cancelamento |
| `cancelamento_tardio` | INTEGER NOT NULL | 0/1 |

Índices: `(recurso_id, inicio)` para a verificação de conflito; `(matricula)` para RF-07 e RF-09.

**Máquina de estados da `Reserva`**

| De | Evento | Para |
|---|---|---|
| (nova, recurso sem aprovação) | solicitar | CONFIRMADA |
| (nova, recurso com aprovação) | solicitar | PENDENTE |
| PENDENTE | confirmar / rejeitar | CONFIRMADA / REJEITADA |
| PENDENTE, CONFIRMADA | cancelar (antes do início) | CANCELADA |

Qualquer outra transição lança `TransicaoInvalida`. Só PENDENTE e CONFIRMADA ocupam o horário.

## 2. Contratos (`ServicoReservas`)

| Operação | Entrada | Saída | Erros |
|---|---|---|---|
| `solicitar` | usuário, recurso_id, início, fim | `Reserva` com `id` | `NaoEncontrado`, `UsuarioBloqueado`, `RegraViolada`, `ConflitoDeHorario` |
| `cancelar` | usuário, reserva_id | `Reserva` CANCELADA | `NaoEncontrado`, `NaoAutorizado`, `TransicaoInvalida`, `RegraViolada` |
| `decidir` | coordenador, reserva_id, aprovar | `Reserva` | `NaoAutorizado`, `NaoEncontrado`, `TransicaoInvalida` |
| `horarios_livres` | recurso_id, dia | lista de (início, fim) | `NaoEncontrado` |
| `minhas_reservas` | usuário | lista ordenada por início | — |

Todos os erros herdam de `ErroDeDominio`; a interface decide como mostrá-los.

## 3. Fluxo principal — `solicitar`

Diagrama: [`sequencia-solicitar.mmd`](../diagramas/sequencia-solicitar.mmd). Ordem das verificações (da mais barata e mais específica para a que toca o repositório com escrita):

1. recurso existe → 2. usuário não bloqueado (RF-07) → 3. regras do tipo (RF-05) → 4. gravação atômica sem conflito (RF-04, ADR-002).

## 4. Decisões de projeto e limites

- **Intervalos semiabertos** `[início, fim)`: `conflita_com` usa `<` estrito dos dois lados.
- **Relógio injetado** (`Relogio`): nenhum código de domínio chama `datetime.now()`.
- **Horários sem fuso** (naive): todo o campus está no mesmo fuso; mudar isso exige revisar `ISO` e `conflita_com`.
- **Janela de penalidade** de 30 dias conta a partir de `cancelada_em`, não da data da reserva.
- **Limite conhecido:** a consulta de penalidade carrega todas as reservas do usuário; aceitável para o volume atual.

## 5. Plano de testes

Cada critério de aceitação do documento de requisitos (seção 5) tem teste; a matriz completa está em `docs/requisitos.md`, seção 7. Valores-limite cobertos: 2h × 2h01 (duração), 30 min, 24 h e 72 h (antecedência), 12h00 × 12h01 (cancelamento tardio), 2 × 3 cancelamentos tardios, 30 dias, reservas encostadas.
