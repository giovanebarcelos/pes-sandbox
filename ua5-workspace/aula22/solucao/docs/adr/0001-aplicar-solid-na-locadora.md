# ADR-0001: Aplicar SOLID na Locadora antes de crescer o sistema

- **Status:** Aceito
- **Data:** (preencha com a data da aula)
- **Contexto:** a classe `Locadora` (legado em `aula18/inicio/locadora_legado.py`) acumula cadastro, cálculo de preço, persistência em JSON, envio de e-mail e relatório. Toda regra nova (desconto semanal, novo tipo de veículo) obriga a editar a mesma classe e os mesmos `if/elif`. As próximas aulas (19 a 22) vão acrescentar interface gráfica, banco de dados e serviços distribuídos.
- **Decisão:** separar responsabilidades (SRP) em `veiculos`, `precos`, `repositorio`, `notificacao`, `servico` e `relatorio`; usar polimorfismo + Factory Method para os tipos de veículo (OCP); usar Strategy para a política de desconto; injetar repositório, notificador e política no `ServicoLocacao` (DIP).
- **Alternativas consideradas:**
  - *Manter a classe única e só extrair funções* — rejeitada: continua com um único motivo de mudança para tudo e não permite testar sem arquivo.
  - *Aplicar todos os padrões GoF possíveis (Observer para tudo, Abstract Factory)* — rejeitada: complexidade sem problema real a resolver hoje.
- **Consequências:**
  - (+) Tipo novo (`Caminhao`) entrou sem alterar código existente; testes rodam sem arquivo e sem SMTP.
  - (+) Os testes de caracterização provam que o comportamento do legado foi preservado.
  - (−) Passamos de 1 para 7 módulos; quem chega ao projeto precisa entender a composição em `main.py`.
