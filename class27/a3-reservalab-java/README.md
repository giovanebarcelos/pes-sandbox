# ReservaLab (versão Java)

**Versão:** 1.0.0 · **Licença:** MIT · **Status:** Referência da A3 (rota Java)

Mesmo sistema do `a3-reservalab` (Python), portado para **Java 21 + Maven + JUnit 5 + SQLite (JDBC)**: mesmas regras, mesmas camadas, mesmos 74 casos de teste. Serve de referência para grupos que escolherem Java.

> **Documentação compartilhada:** requisitos, ADRs, SDR, diagramas e gestão são os mesmos e ficam em `../a3-reservalab/docs/`. Uma entrega real em Java tem a sua própria pasta `docs/`; aqui ela não foi duplicada.

## Como rodar (forma oficial de entrega)

```bash
docker compose up --build              # demonstração
docker compose run --rm testes         # 74 testes
```

A primeira execução baixa as dependências do Maven (alguns minutos); as seguintes usam o cache das camadas do Docker.

## Estrutura

```
reservalab-java/
├── pom.xml · Dockerfile · docker-compose.yml · .dockerignore · .gitignore
└── src/
    ├── main/java/br/edu/reservalab/
    │   ├── dominio/       # Recurso (abstrata) + SalaEstudo, Laboratorio, Auditorio; Reserva; Usuario; Erros
    │   ├── aplicacao/     # ServicoReservas, CatalogoRecursos, RepositorioReservas (porta)
    │   ├── infra/         # RepositorioMemoria, RepositorioSqlite
    │   └── Cli.java
    └── test/java/br/edu/reservalab/
        ├── ServicoReservasContrato.java   # mesma suíte, executada sobre cada repositório
        ├── ServicoMemoriaTest.java · ServicoSqliteTest.java
        └── CatalogoTest · ArquiteturaTest · ConcorrenciaTest
```

## Diferenças em relação à versão Python

| Ponto | Python | Java |
|---|---|---|
| Regras do tipo de recurso | atributos de classe | parâmetros do construtor da superclasse (`super(...)`) |
| Porta do repositório | `Protocol` | `interface` |
| Erros de domínio | arquivo `erros.py` | classes aninhadas em `Erros` |
| Testes por repositório | `pytest` com fixture parametrizada | classe-contrato abstrata + 2 subclasses (JUnit 5) |
| `BEGIN IMMEDIATE` | comando SQL explícito | `SQLiteConfig.TransactionMode.IMMEDIATE` + `setAutoCommit(false)` |

## Teste de mutação

Quebrando o código de propósito, um mutante por vez, e rodando `docker compose run --rm testes`: **6 de 6 mutantes detectados**.

| Mutante | Onde | Quem detecta |
|---|---|---|
| `<` → `<=` na sobreposição | `Reserva.conflitaCom` | suíte do repositório em memória (3 erros) |
| `<` → `<=` na sobreposição | SQL de `RepositorioSqlite.adicionarSemConflito` | suíte do SQLite (3 erros) |
| `<` → `<=` no limite de 2 h | `Reserva.cancelar` | 1 falha por repositório |
| limite de bloqueio 3 → 4 | `ServicoReservas` | 1 falha por repositório |
| `>` → `>=` na duração máxima | `Recurso.validar` | 15 erros por repositório |
| `<` → `<=` na antecedência | `Recurso.validar` | 2 erros por repositório |

A regra de sobreposição existe em dois lugares (domínio e SQL), e cada cópia só é pega pela suíte do seu próprio repositório. Por isso a classe-contrato roda sobre os dois.

## Como contribuir

Branch por tarefa, Conventional Commits, PR com revisão; o `docker compose run --rm testes` deve passar antes do merge.
