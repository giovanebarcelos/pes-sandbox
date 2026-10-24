# AGENTS.md
Reserva de salas, laboratórios e auditório do campus — Python 3.12, pytest, SQLite.

## Onde está cada coisa
- Requisitos e rastreabilidade: docs/requisitos.md · Decisões: docs/adr/ · Design do módulo: docs/sdr/
- Diagramas: docs/diagramas/*.mmd

## Regras
- Testes: `pytest` — rode antes e depois de qualquer mudança; o `docker compose run --rm testes` deve passar numa cópia limpa
- src/reservalab/dominio não importa aplicacao/, infra/ nem sqlite3 (teste em tests/test_qualidade.py)
- Nenhum `datetime.now()` fora de cli.py: use o relógio injetado
- Commits em Conventional Commits; nunca faça push
- Nada de dados pessoais além de matrícula, nome e perfil

## Agent skills
(bloco escrito pela skill setup-matt-pocock-skills)
