# ADR-0005: Monolito modular como arquitetura de deploy da Locadora

- **Status:** Aceito
- **Contexto:** a locadora tem dois bounded contexts (Frota e Locações, ADR-0004) e um módulo de pagamentos com plugins. O time tem 2 a 4 pessoas, uma única base de código e nenhuma equipe de operação. Precisamos de notificações ao cliente que não podem derrubar uma locação quando falharem.
- **Decisão:** publicar como **monolito modular** (um processo: `python -m locadora.main`), com fronteiras entre módulos verificadas por `tests/test_fronteiras.py` e comunicação entre contextos apenas por `FrotaAPI`/`ServicoFrota` e por eventos. Os microsserviços em `servicos/` ficam como **experimento** documentado, prontos para uso se a dor aparecer.
- **Alternativas consideradas:**
  - *Microsserviços (frota, locações, notificação)* — implementados em `servicos/` e testados; rejeitados como padrão porque trazem rede, timeouts, consistência eventual, 3 deploys e observabilidade distribuída para um time que não precisa escalar partes separadas.
  - *Monolito sem módulos (como na Aula 19)* — rejeitado: perderíamos as fronteiras que permitem extrair um serviço no futuro.
- **Gatilhos para revisar a decisão:** Locações precisar escalar sozinha em picos (feriados); um segundo time assumir a Frota; necessidade de deploy independente.
- **Consequências:**
  - (+) Um deploy, uma transação local, depuração simples.
  - (+) Extrair a Frota é trocar `FrotaLocal` por `FrotaHttp` — já demonstrado.
  - (−) Uma falha grave no processo derruba tudo; escala é do sistema inteiro.

> **Comunicação Locações → Notificação:** assíncrona (outbox com nova tentativa). Notificar não é crítico para concluir a locação; se fosse síncrona, Notificação fora do ar derrubaria as locações (falha em cascata). Ver `docs/comunicacao.md`.
