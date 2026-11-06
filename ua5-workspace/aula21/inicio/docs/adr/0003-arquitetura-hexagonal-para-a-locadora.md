# ADR-0003: Arquitetura Hexagonal para o domínio da Locadora

- **Status:** Aceito
- **Contexto:** o mesmo domínio precisa ser usado por terminal, web e testes automatizados, e a persistência vai sair da memória/JSON para um banco (SQLite agora, talvez outro depois). Na Aula 19, a regra de negócio ficou separada da tela, mas o serviço ainda importava diretamente o repositório concreto escolhido em `montagem.py`.
- **Decisão:** organizar o código em `dominio/` (regras puras), `aplicacao/` (`LocadoraService` + portas `VeiculoRepository`, `LocacaoRepository`, `Notificador`) e `adaptadores/` (`entrada/`: cli, web; `saida/`: memória, SQLite, console). Somente `config.py` conhece os adaptadores concretos. A Regra de Dependência é verificada por `tests/test_arquitetura.py`.
- **Alternativas consideradas:**
  - *Clean Architecture completa* (entities / use cases / interface adapters / frameworks, um caso de uso por classe com DTOs de entrada e saída) — rejeitada: mais camadas do que o tamanho do sistema pede hoje.
  - *Onion* — equivalente em espírito (ver `docs/onion.md`); escolhemos o vocabulário "portas e adaptadores" porque a troca de adaptador é o cenário mais frequente.
  - *Manter camadas simples (Aula 19)* — rejeitada: não impede que o serviço importe infraestrutura.
- **Consequências:**
  - (+) `tests/test_troca_adaptador.py` roda o mesmo roteiro em memória e em SQLite sem tocar no `LocadoraService`.
  - (+) Um teste de contrato garante que qualquer novo repositório se comporta como os atuais.
  - (−) Mais pastas e uma interface (porta) para cada dependência externa.
