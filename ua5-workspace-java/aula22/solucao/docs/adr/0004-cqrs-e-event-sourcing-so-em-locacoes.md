# ADR-0004: CQRS + Event Sourcing apenas no contexto de Locações

- **Status:** Aceito
- **Contexto:** a locadora cresceu e foi separada em dois bounded contexts (ver `docs/contextos.md`). A gerência pediu (1) histórico auditável de cada locação, inclusive multas por atraso, e (2) relatórios como "veículos mais alugados no mês" e "faturamento mensal", que não batem com o formato das tabelas de escrita.
- **Decisão:** **Locações** usa Event Sourcing (`Locacao` reconstruída a partir de `LocacaoIniciada`, `VeiculoDevolvido`, `MultaPorAtrasoAplicada`) e CQRS (comandos `IniciarLocacao`/`RegistrarDevolucao` + `ManipuladorComandos`; projeções `LocacoesAtivas`, `RankingMensal`, `Faturamento` em `locacoes/aplicacao/`). **Frota** continua com estado atual simples (CRUD).
- **Alternativas consideradas:**
  - *Event Sourcing nos dois contextos* — rejeitada: ninguém pediu histórico de mudanças de diária; seria custo sem benefício.
  - *CRUD + tabela de auditoria em Locações* — viável, mas a auditoria fica "ao lado" da verdade e pode divergir; e os relatórios exigiriam consultas complexas.
- **Consequências:**
  - (+) Auditoria completa; projeções novas podem ser criadas a qualquer momento reprocessando os eventos (`reconstruirProjecoes`).
  - (+) Integração entre contextos por evento (`VeiculoDevolvido` → frota libera o veículo).
  - (−) Consistência eventual nas leituras quando as projeções forem assíncronas (Aula 22); eventos não podem mais ser "editados" — correção = novo evento.
  - (−) Sem snapshots: com milhares de eventos por locação seria preciso adicioná-los (não é o caso).
