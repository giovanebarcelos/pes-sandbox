# A3 — Projeto Integrador de Projeto e Engenharia de Software  

**Unidade Curricular:** Projeto e Engenharia de Software  
**Cursos:** C&TIC, Engenharias e Administração  
**Período:** 2026/2  
**Valor:** 40 pontos  
**Apresentação:** Aula 28 (5-7 min por grupo, mais a pergunta do professor)  
**Grupos:** 3 a 5 integrantes (individual só com autorização do professor)  
**Exemplo de referência:** [A3 — Projeto Exemplo: ReservaLab](A3_Exemplo_ReservaLab.md) (código em Python **e** em Java)  

---  

## Visão Geral e Critérios de Avaliação  

A A3 é o trabalho integrador final da disciplina. Em grupo, vocês escolhem um problema, **especificam, modelam, projetam, implementam, versionam e documentam** um sistema de software, aplicando o que foi visto nas 8 unidades de aprendizagem. O trabalho é feito **com um agente de IA** (obrigatório, seção "Uso do Agente de IA") e é defendido oralmente: quem construiu sabe explicar.  

A nota total é de **40 pontos**:  

| Critério | Pontos | O que é avaliado |  
|---|---|---|  
| **Requisitos** | 10 pts | RF e RNF documentados (mín. 5 RF + 3 RNF), user stories, critérios de aceitação, rastreabilidade |  
| **Modelagem UML** | 10 pts | Casos de uso, classes e diagrama comportamental, coerentes com o código |  
| **Arquitetura** | 5 pts | Padrão definido e aplicado, justificado em ADR; design do módulo principal em SDR |  
| **Código** | 5 pts | OO funcional (herança, polimorfismo, encapsulamento) em Python ou Java, com testes e **empacotado com Docker Compose** |  
| **Documentação** | 5 pts | README, docstrings/Javadoc, C4 contexto e o **registro de Uso de IA** |  
| **Versionamento e colaboração** | 5 pts | Commits frequentes, Conventional Commits, branches, `.gitignore`, processo e quadro de tarefas |  
| **TOTAL** | **40 pts** | |  

> **Dica:** um sistema **pequeno, completo, testado e bem explicado** vale mais do que um sistema grande e incompleto. Qualidade das decisões e coerência entre os artefatos valem mais do que quantidade de funcionalidades.  

---  

## Tema do Projeto  

O tema é **aberto**. O grupo escolhe um domínio com **regras de negócio de verdade**, onde exista um problema que um software resolva.  

### Critérios para um tema válido  

| Critério | O que verificar |  
|---|---|  
| **Problema identificável** | Existe alguém com um problema real e descritível (não "um app de X" sem dono) |  
| **Acesso a stakeholders** | Dá para entrevistar ou observar pelo menos 2 pessoas que conhecem o problema |  
| **Regras de negócio** | Pelo menos **3 regras não triviais**, com valores-limite testáveis (prazos, limites, perfis, estados). Só cadastrar e listar (CRUD) **não** basta |  
| **Escopo viável** | Cabe em 5 a 9 requisitos funcionais, implementados **por inteiro**, até a Aula 28 |  
| **Rende OO e arquitetura** | Há pelo menos um "é-um" real (herança/polimorfismo), um objeto com estados e uma decisão arquitetural a justificar |  
| **Originalidade** | O grupo propõe a sua solução. Não vale o domínio dos exemplos das aulas (locadora de veículos, biblioteca, e-commerce, clínica) nem o do exemplo de referência (reserva de salas) |  

### Exemplos de temas  

- Gestão de empréstimo de equipamentos de um laboratório, com multa e reserva  
- Controle de estoque de uma cozinha comunitária, com validade e reposição  
- Sistema de pontuação de um campeonato universitário, com critérios de desempate  
- Agendamento de manutenção preventiva, com prioridade e prazo (SLA)  
- Fila de atendimento com prioridades legais (idosos, gestantes) e tempo de espera  
- Controle de doses e estoque de uma farmácia popular  
- Mensalidades de uma academia, com planos, descontos e inadimplência  
- Gestão de turmas e frequência de um curso livre  
- Simulador de rateio de despesas de uma república, com regras de divisão  

> **Teste rápido do tema:** consigam, em 2 minutos, listar 3 regras de negócio com um número (por exemplo "cancelamento com menos de 2 h é tardio"). Se não conseguirem, o tema é só um cadastro. Mostrem o tema ao professor **antes** de se comprometerem (veja o checkpoint 1).  

---  

## Metodologia e Roteiro ao Longo do Curso  

A A3 **não é feita na última semana**. Cada unidade produz um artefato que entra na entrega; as aulas de projeto parcial (8, 12 e 17) são os pontos de construção.  

| Quando | Artefato da A3 | Aulas | Checkpoint com o professor |  
|---|---|---|---|  
| Aulas 1-4 | Tema, partes interessadas, riscos iniciais | Gerenciamento de projetos | **1.** Aprovação do tema (regras de negócio) |  
| Aulas 5-8 | Processo escolhido e justificado, EAP, quadro de tarefas | Modelos e metodologias | Projeto parcial da Aula 8 |  
| Aulas 9-12 | Elicitação, requisitos RF/RNF, Gherkin, rastreabilidade | Engenharia de requisitos | **2.** `docs/requisitos.md` revisado (Aula 12) |  
| Aulas 13-17 | Casos de uso, classes, sequência/atividades, primeiras classes em código | UML e orientação a objetos | **3.** Diagrama de classes **antes** de codificar (Aula 17) |  
| Aulas 18-22 | Padrão arquitetural, ADRs, SDR, estrutura de diretórios | Projeto e arquitetura | **4.** ADR-001 (Aula 22) |  
| Aulas 23-24 | Plano de manutenção e dívida técnica conhecida | Ciclo de vida e manutenção | — |  
| Aulas 25-26 | Repositório, branches, Conventional Commits, PRs | Git | Repositório criado e com histórico |  
| Aula 27 | README, docstrings, C4, registro de IA, checklist | Documentação técnica | Revisão final com o checklist |  
| Aula 28 | Apresentação e defesa oral | Encerramento | — |  

---  

## O Projeto  

A A3 é entregue como um **projeto integrado**, composto pelos seis conjuntos de artefatos abaixo. Para cada um, a seção "No material" indica onde a técnica foi ensinada.  

### 1. Requisitos  

**10 pontos | Local: `docs/requisitos.md`**  

- **Contexto e problema:** quem tem o problema, por que importa, partes interessadas (poder × interesse)  
- **Elicitação:** pelo menos 2 técnicas (entrevista, questionário, observação, análise de documentos) e o que cada uma revelou  
- **Requisitos funcionais:** **mínimo 5 RF**, cada um com ID único, descrição, prioridade (MoSCoW) e origem  
- **Requisitos não funcionais:** **mínimo 3 RNF**, classificados (FURPS+) e **mensuráveis**. "Rápido" e "seguro" não são requisitos; "responde em até 200 ms" é  
- **User stories e critérios de aceitação** em Gherkin para as funcionalidades principais, com pelo menos um Esquema do Cenário com **valores-limite**  
- **Rastreabilidade:** matriz requisito → caso de uso → teste. Todo teste citado deve existir  
- **Fora de escopo:** o que o sistema deliberadamente não faz  

> **No material:** Aula 09 (Engenharia de Requisitos), Aula 10 (Elicitação, user stories e Gherkin), Aula 11 (RF, RNF, FURPS+, MoSCoW), Aula 12 (DRS, rastreabilidade e validação).  

### 2. Modelagem UML  

**10 pontos | Local: `docs/diagramas/*.mmd` (Mermaid ou PlantUML, versionados)**  

- **Diagrama de casos de uso** com atores, fronteira do sistema e `include`/`extend` onde fizer sentido, mais a **descrição textual** de pelo menos 1 caso de uso (fluxo principal e alternativos)  
- **Diagrama de classes** com atributos, métodos, multiplicidades e os tipos de relacionamento (herança, composição, agregação, associação)  
- **Diagrama de sequência ou de atividades** de pelo menos 1 fluxo importante, com fragmento `alt` ou decisão  
- **Coerência com o código:** o diagrama de classes tem de corresponder ao que foi implementado. Diagrama que não corresponde a nada no código é avaliado como **Insuficiente**  

> **No material:** Aula 13 (UML e OO), Aula 14 (casos de uso), Aula 15 (classes), Aula 16 (sequência e atividades), Aula 17 (do modelo ao código).  

### 3. Arquitetura  

**5 pontos | Local: `docs/adr/`, `docs/sdr/`, `docs/diagramas/`**  

- **Padrão arquitetural definido** (camadas, MVC, hexagonal, Clean...) e **aplicado de verdade** na estrutura do código  
- **Pelo menos 1 ADR** com contexto, decisão, **alternativas rejeitadas com o motivo** e consequências (positivas e negativas)  
- **SDR do módulo principal** com requisitos vinculados, modelo de dados, contratos, fluxo principal e **plano de testes** que fecha a cadeia requisito → SDR → teste  
- **Regra de dependência:** descreva qual camada depende de qual. Quem a verifica por teste automático mostra domínio do assunto  
- **Decisão de implantação:** monolito, monolito modular ou microsserviços, com justificativa (para um projeto acadêmico, microsserviços raramente se justificam)  

> **No material:** Aula 18 (SOLID, ADR), Aula 19 (MVC/MVP/MVVM), Aula 20 (Clean/Hexagonal/Onion), Aula 21 (DDD, CQRS), Aula 22 (microsserviços e implantação), Aula 27 (SDR).  

### 4. Código  

**5 pontos | Local: `src/` e `tests/`**  

- **Linguagem:** Python ou Java  
- **Orientação a objetos real:** herança com polimorfismo onde há "é-um", encapsulamento (o estado só muda por métodos do próprio objeto) e abstração por interfaces ou classes abstratas  
- **Sistema que executa, empacotado com Docker (obrigatório):** `Dockerfile` e `docker-compose.yml` **na raiz do repositório**. Quem avalia roda dois comandos, sem instalar nada além do Docker:  
  - `docker compose up --build` → sobe o sistema (demonstração em linha de comando, API ou web)  
  - `docker compose run --rm testes` → roda a suíte de testes (serviço `testes` no compose)  

  A **interface não é o que se avalia**; a lógica é  
- **Regras do compose:** versão da linguagem fixada na imagem (ex.: `python:3.12-slim`); nenhuma chave de API, senha ou `.env` dentro da imagem ou do repositório; se houver banco de dados, ele é **um serviço do compose** (com volume), não uma instalação na máquina  
- **Testes automatizados** (`pytest` ou JUnit) para as regras de negócio, **incluindo valores-limite** (o que acontece exatamente em 2 h, em 2 h 01?)  
- **Teste que protege de verdade:** quebrem o código de propósito em pelo menos 2 pontos e mostrem que algum teste fica vermelho  
- Princípios SOLID aplicados e justificados quando relevantes  

#### Rota Java: exemplo de referência  

Quem escolher Java tem um exemplo **executado e testado**: o mesmo ReservaLab, em `repository/class27/a3-reservalab-java/`, com **Java 21, Maven, JUnit 5 e SQLite (JDBC)**. Mesmas regras, mesmas camadas e os mesmos 74 casos de teste da versão Python. Use-o como guia de **estrutura e de Docker**, não como código para copiar.  

```bash  
docker compose up --build          # demonstração  
docker compose run --rm testes     # 74 testes (mvn test dentro do contêiner)  
```  

| Ponto | Python | Java |  
|---|---|---|  
| Regras do tipo de recurso | atributos de classe | parâmetros do construtor da superclasse (`super(...)`) |  
| Porta do repositório | `Protocol` | `interface` |  
| Erros de domínio | arquivo `erros.py` | classes aninhadas em `Erros` |  
| Testes sobre cada repositório | `pytest` com fixture parametrizada | classe-contrato abstrata + 2 subclasses (JUnit 5) |  
| Atomicidade no SQLite | `BEGIN IMMEDIATE` explícito | `SQLiteConfig.TransactionMode.IMMEDIATE` + `setAutoCommit(false)` |  
| Teste da regra de dependência | lê os `import` dos `.py` | lê os `import` dos `.java` |  

**O que o `Dockerfile` Java faz de diferente (e por quê):**  

- Imagem `maven:3.9-eclipse-temurin-21`; o `pom.xml` é copiado **antes** do código, e as dependências baixadas em uma camada própria (`dependency:go-offline`). Assim, mudar uma classe não baixa tudo de novo  
- Um teste rápido roda **durante o build** só para o Maven baixar o provider do JUnit; depois disso o serviço `testes` funciona com `mvn -o` (offline)  
- A **primeira** execução baixa as dependências e leva alguns minutos; as seguintes usam o cache. **Avisem quem for avaliar** e testem a cópia limpa com antecedência  

**Teste de mutação (executado):** 6 mutantes aplicados um a um (`<` → `<=` na sobreposição do domínio e no SQL, no limite de 2 h, na duração máxima e na antecedência; limite de bloqueio 3 → 4): **os 6 foram detectados**. Achado: a regra de sobreposição existe duas vezes (em `Reserva.conflitaCom` e no SQL do SQLite), e cada cópia só é pega pela suíte do seu próprio repositório. Por isso o contrato roda nos dois.  

**O que o exemplo Java não tem:** a pasta `docs/` (requisitos, ADRs, SDR, diagramas e gestão são os do exemplo Python). **Uma entrega em Java tem a sua própria pasta `docs/` completa.**  

> **No material:** Aula 13 e 15 (OO), Aula 17 (modelo → código), Aula 18 (SOLID), Aula 24 (manutenção e refatoração segura).  

### 5. Documentação  

**5 pontos | Local: `README.md`, docstrings/Javadoc, `docs/`**  

| Item | Pontos | O que entregar |  
|---|---|---|  
| **README** | 1 | Título, descrição, stack, como rodar **com Docker** (os dois comandos), estrutura de diretórios, como contribuir, licença |  
| **Docstrings / Javadoc** | 1 | Em todos os métodos públicos principais: o que faz, parâmetros, retorno, erros |  
| **Diagrama C4 nível 1** | 1 | Contexto do sistema: usuários e sistemas externos (níveis 2 a 4 são bônus) |  
| **Uso de IA** | 2 | Seção do README com o registro descrito na seção "Uso do Agente de IA" |  

Se o sistema expõe uma API HTTP, inclua também o `openapi.yaml`.  

> **No material:** Aula 27 (documentação técnica, README, C4, OpenAPI, ADR e SDR), Aula 09B (política de IA).  

### 6. Versionamento e colaboração  

**5 pontos | Local: repositório Git e `docs/gestao.md`**  

| Item | Pontos | O que entregar |  
|---|---|---|  
| **Git** | 3 | Commits frequentes de **todos** os integrantes ao longo das semanas, Conventional Commits, `.gitignore`, branches por tarefa e pull requests com revisão de outro integrante, tag da entrega |  
| **Gestão do projeto** | 2 | `docs/gestao.md` com o **processo escolhido e justificado** (Scrum, Kanban...), a **EAP**, **pelo menos 3 riscos** com probabilidade, impacto e resposta, e o **quadro de tarefas** (issues/board) |  

> **No material:** Aulas 2-4 (gestão, EAP, riscos), Aulas 5-8 (processos), Aula 25 (Git), Aula 26 (branches, PR, conflitos), Aula 28 (ferramentas colaborativas).  

---  

## Uso do Agente de IA (obrigatório)  

A A3 **exige** o uso do agente de IA da disciplina e a **declaração** do que foi feito com ele. A razão: um engenheiro hoje trabalha com agentes, e o que se ensina aqui é **usar, conferir e corrigir** — não aceitar.  

### O que usar  

O setup é o da **Aula 09B** (IA Assistida: Skills, OpenCode e OpenRouter): OpenCode aberto **na raiz do repositório do projeto**, modelo gratuito via OpenRouter, skills em `.agents/skills/`, `opencode.json` com `push` negado e `ask` para comandos (Slide 31) e `AGENTS.md` mantido atualizado (Slide 33). Quem usa o Claude Code segue o Slide 29. A política completa é o **Slide 32 da Aula 09B**.  

### Onde o agente entra: mapa por etapa  

Cada aula de 09 a 28 tem um slide **🤖 Com o Agente** que mostra como usá-lo naquela etapa. Para a A3, estes são os pontos de entrada:  

| Etapa da A3 | Use o agente para | Material |  
|---|---|---|  
| Requisitos | Entrevista com `grilling` e síntese com `to-spec`; treino de elicitação com stakeholder simulado; classificar RF/RNF e desafiar a priorização; DRS e matriz de rastreabilidade | Aula 09B (Slides 20-21, 25-27), Aula 10 (Slide 34), Aula 11 (Slide 32), Aula 12 (Slide 32) |  
| Modelagem | Gerar casos de uso, classes, sequência e atividades; **comparar diagrama com código** (agente `plan`) | Aulas 14, 15, 16 (Slide 32 de cada), Aula 17 (Slide 32) |  
| Implementação | Quebrar em tickets (`to-tickets`), desenvolver com `tdd`, retrospectiva (`retro`) | Aula 17 (Slide 32) |  
| Arquitetura | Questionar a decisão (`grill-with-docs`) e rascunhar ADR; verificar a regra de dependência; decidir a implantação | Aula 18 (Slide 39), Aula 20 (Slide 40), Aula 22 (Slide 43) |  
| Manutenção | Diagnosticar antes de editar (`investigate-first`), refatorar com testes (`safe-refactor`), listar dívida técnica | Aula 24 (Slide 31) |  
| Git | `.gitignore`, mensagens de commit (`caveman-commit`), revisão de PR (`caveman-review`), descrição de PR (`pr`) | Aula 25 (Slide 29), Aula 26 (Slide 30) |  
| Documentação | Docstrings, README, C4, OpenAPI, conferência do checklist da A3 | Aula 27 (🤖 Com o Agente) |  

### Regras do jogo  

1. **Primeiro à mão, depois o agente** nas atividades de aula (Slide 32 da Aula 09B). **Na A3 o agente é parte do método**, mas a decisão e o entendimento são seus  
2. **Revise tudo o que o agente produz** como se fosse um pull request. Documentação de IA não revisada é pior que nenhuma (Aula 27, Slide 20)  
3. **Dados:** nada de dados pessoais reais, credenciais ou dados de clientes nos prompts  
4. **Chave de API fora do Git.** Ela fica em `~/.local/share/opencode/auth.json`, nunca no repositório  
5. **O agente propõe, a equipe publica.** `git push` é de vocês (negado no `opencode.json`)  
6. **A responsabilidade é do grupo.** "Foi a IA" não é justificativa para erro, falha ou plágio  

### O registro de uso (seção "Uso de IA" do README)  

O README tem uma seção **Uso de IA** com:  

- **Ferramenta e modelo** (ex.: OpenCode + Nemotron 3 Ultra `:free` via OpenRouter) e **skills** utilizadas  
- Uma **tabela de registros**, no mínimo **5 linhas**, cobrindo **pelo menos 4 tipos de artefato diferentes** (por exemplo: requisitos, diagrama, teste, ADR, documentação):  

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |  
|---|---|---|---|---|  

- Em **pelo menos 2 linhas**, a coluna "o que o agente errou ou omitiu" deve ter um **erro real** encontrado e corrigido. Não encontrar nenhum erro ao longo do projeto inteiro é sinal de que a revisão não foi feita  
- Uma **reflexão curta**: onde a IA mais ajudou, onde mais errou, o que o grupo faria diferente  
- O `AGENTS.md` e o `opencode.json` **versionados** no repositório  

> **Pontuação do registro (2 pontos de Documentação):** 2 pts = tabela completa, erros reais documentados e `AGENTS.md` coerente com o repositório; 1 pt = tabela presente, mas genérica ("a IA ajudou muito"), sem erros reais; 0 = seção ausente. O registro mede **transparência e senso crítico**, não a quantidade de uso.  

### Defesa oral  

Na apresentação, o professor escolhe um trecho de qualquer artefato (código, diagrama, requisito, ADR) e pede ao grupo que o explique. **Artefato que o grupo não sabe explicar é avaliado como Insuficiente naquele critério — feito com ou sem IA** (Aula 09B, Slide 32; Aula 27, Slide 24). Usar IA e não declarar, sabendo explicar tudo, não reduz a nota do critério; não saber explicar, sim.  

---  

## Rubrica de Avaliação (40 pontos)  

**Níveis de desempenho aplicados ao peso de cada critério** (Aula 27, Slide 24):  

- **Excelente (90-100% do peso):** todos os itens presentes, completos e coerentes entre si  
- **Bom (60-89% do peso):** itens presentes, com lacunas pontuais  
- **Insuficiente (< 60% do peso):** item ausente, incompleto ou incoerente com o restante do projeto, ou que o grupo não sabe explicar  

### Requisitos — 10 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| RF e RNF | 4 | 5+ RF e 3+ RNF com ID, prioridade, origem; RNF mensuráveis | Menos de 5 RF ou 3 RNF, ou RNF vagos ("rápido") |  
| Critérios de aceitação | 3 | Gherkin com Esquema do Cenário e valores-limite nas funcionalidades principais | Ausentes ou sem valores-limite |  
| Rastreabilidade | 2 | Matriz requisito → caso de uso → teste, com testes que existem | Sem matriz, ou citando testes inexistentes |  
| Elicitação | 1 | 2+ técnicas, com o que cada uma revelou | Requisitos sem origem |  

### Modelagem UML — 10 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| Casos de uso | 3 | Atores, fronteira, `include`/`extend` corretos e descrição textual de 1 caso de uso | Caso de uso que é passo de tela, ator que é parte do sistema, sem descrição |  
| Classes | 3 | Multiplicidades e relacionamentos corretos; herança só onde há "é-um" | Só caixas e setas sem tipo; herança forçada |  
| Sequência ou atividades | 2 | Fluxo importante com `alt`/decisão | Ausente, ou só o fluxo feliz |  
| Coerência com o código | 2 | Diagramas refletem o código implementado | Diagrama diverge do código |  

### Arquitetura — 5 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| Padrão aplicado | 2 | Padrão claro na estrutura e na dependência entre camadas | Só nomeado, o código não o segue |  
| ADR | 1,5 | Alternativas rejeitadas com motivo; consequências positivas e negativas | Só a decisão, sem alternativas |  
| SDR | 1,5 | Contratos, fluxo e plano de testes ligados aos requisitos | Repete os requisitos |  

### Código — 5 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| Orientação a objetos | 2 | Herança + polimorfismo + encapsulamento, sem `if tipo ==` | Classes anêmicas, tudo em funções |  
| Funciona (Docker) | 1 | `docker compose up --build` sobe o sistema e `docker compose run --rm testes` passa, numa cópia limpa | Sem Dockerfile/compose, ou só roda "na minha máquina" |  
| Testes | 2 | Regras e valores-limite testados; prova de que os testes falham quando o código quebra | Só o "caminho feliz" |  

### Documentação — 5 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| README | 1 | Seções completas; outro aluno consegue rodar sem perguntar | Falta "como rodar" |  
| Docstrings / Javadoc | 1 | Métodos públicos principais documentados | Ausentes |  
| C4 contexto | 1 | Usuários e sistemas externos, coerente com os requisitos | Ausente |  
| Uso de IA | 2 | Tabela completa, erros reais, `AGENTS.md` coerente | Seção ausente ou genérica |  

### Versionamento e colaboração — 5 pontos  

| Item | Pontos | Excelente | Insuficiente |  
|---|---|---|---|  
| Git | 3 | Histórico distribuído no tempo e entre os integrantes, Conventional Commits, branches e PRs, `.gitignore`, tag | Poucos commits, todos de uma pessoa ou no último dia |  
| Gestão | 2 | Processo justificado, EAP, 3+ riscos, quadro em uso | Sem processo ou sem quadro |  

---  

## Apresentação (Aula 28)  

**5-7 minutos por grupo.** Todos falam. Não é leitura de slides: é a demonstração do sistema funcionando e a defesa das decisões.  

| Parte | Tempo | Conteúdo |  
|---|---|---|  
| **O problema** | 30 s | Quem tem o problema e qual é a dor |  
| **Demo** | 2 min | O sistema funcionando ao vivo, incluindo **um caso de erro** (regra de negócio recusando algo) |  
| **Diagramas** | 1 min | Casos de uso e classes, e onde aparecem no código |  
| **Decisão de arquitetura** | 1 min | Qual padrão e por quê (ADR), qual alternativa foi rejeitada |  
| **Lições aprendidas** | 1 min | O que faria diferente, incluindo o uso do agente |  
| **Pergunta do professor** | 1 min | Explicar um trecho escolhido de um artefato ou do código |  

> **Preparem a demo para falhar com graça:** relógio e dados fixos (como o `demo` do exemplo de referência), comandos já testados e uma gravação de reserva.  

---  

## Como Entregar no Anima Hub  

A entrega é **uma por grupo**, postada por **um integrante** na atividade "A3" do Anima Hub, dentro do prazo definido na própria atividade. Postem **todos** os itens abaixo:  

| # | O que postar | Formato |  
|---|---|---|  
| 1 | **Capa da entrega:** nome do projeto, integrantes (nome e RA), link do repositório, **tag da entrega** e o **hash do commit** | PDF de 1 página |  
| 2 | **Repositório completo**, incluindo a pasta `.git` (o histórico é avaliado) | `.zip` (sem `.venv` nem `node_modules`) |  
| 3 | **Link do repositório no GitHub** (público, ou com o professor convidado como colaborador), no campo de texto da atividade | Link |  

**Antes de postar, confiram:**  

- [ ] O `zip` abre e o `README` ensina a rodar com Docker  
- [ ] Numa **cópia limpa** (zip descompactado em outra pasta), `docker compose up --build` funciona e `docker compose run --rm testes` passa  
- [ ] A tag da entrega aponta para o commit que está no zip  
- [ ] Nenhuma chave de API, `.env` ou banco de dados no repositório  
- [ ] Os diagramas `.mmd` abrem (cole no [mermaid.live](https://mermaid.live))  
- [ ] Todos os integrantes têm commits no histórico  

> Mudanças **depois** da data de entrega não são consideradas: o commit da tag vale.  

---  

## Checklist Completo da A3  

Mesmo checklist da Aula 27 (Slide 30), com a gestão incluída:  

- [ ] `README.md` com título, descrição, stack, como rodar, estrutura de diretórios  
- [ ] Documento de requisitos (mín. 5 RF + 3 RNF, cada um com ID único, descrição, prioridade)  
- [ ] User stories e critérios de aceitação em Gherkin, com valores-limite  
- [ ] Matriz de rastreabilidade requisito → caso de uso → teste (testes existentes)  
- [ ] Diagrama de casos de uso (versionado) + descrição de 1 caso de uso  
- [ ] Diagrama de classes (versionado), coerente com o código  
- [ ] Diagrama de sequência ou de atividades de 1 fluxo importante  
- [ ] Diagrama C4 nível 1 — contexto  
- [ ] Pelo menos 1 ADR com alternativas rejeitadas  
- [ ] SDR do módulo principal, com plano de testes  
- [ ] Código funcional com OO (herança, polimorfismo, encapsulamento)  
- [ ] `Dockerfile` e `docker-compose.yml` na raiz: `docker compose up --build` e `docker compose run --rm testes` funcionam  
- [ ] Testes automatizados com valores-limite; teste que falha quando o código quebra  
- [ ] Docstrings (Python) ou Javadoc (Java) nos métodos principais  
- [ ] Repositório com commits frequentes de todos, Conventional Commits, branches e PRs  
- [ ] `.gitignore` configurado e `CHANGELOG.md` com versões  
- [ ] `docs/gestao.md`: processo justificado, EAP, 3+ riscos, quadro de tarefas  
- [ ] README com seção **Uso de IA** (ferramenta, modelo, skills, ≥ 5 registros, ≥ 2 erros reais, reflexão)  
- [ ] `AGENTS.md` e `opencode.json` versionados e coerentes com o repositório  
- [ ] Todos os integrantes sabem explicar qualquer artefato  

---  

## Estrutura de Diretórios Esperada  

```  
meu-projeto/  
├── README.md              # com a seção "Uso de IA"  
├── AGENTS.md              # memória do projeto para o agente  
├── opencode.json          # modelo e permissões do agente  
├── Dockerfile             # imagem do sistema  
├── docker-compose.yml     # serviços: sistema e testes (e banco, se houver)  
├── .dockerignore  
├── .gitignore  
├── CHANGELOG.md  
├── docs/  
│   ├── requisitos.md      # RF/RNF, Gherkin, rastreabilidade  
│   ├── gestao.md          # processo, EAP, riscos  
│   ├── adr/               # 001-titulo.md ...  
│   ├── sdr/               # 001-modulo.md ...  
│   └── diagramas/         # casos-uso.mmd, classes.mmd, c4-contexto.mmd ...  
├── src/                   # código-fonte  
└── tests/                 # testes automatizados  
```  

A estrutura é uma sugestão; adaptem à linguagem. Em Java, `src/` vira o layout do Maven (`src/main/java` e `src/test/java`) e a raiz ganha o `pom.xml`. O repositório de referência (`repository/class27/a3-reservalab/`) segue exatamente esta organização.  

---  

## Perguntas Frequentes  

**Posso usar um tema que já existe como sistema real?**  
Sim, desde que vocês projetem e implementem a sua própria solução, com regras de negócio explícitas. Não vale copiar código de tutorial.  

**Preciso de interface gráfica?**  
Não. A interface (linha de comando, API ou web) não é avaliada. Avalia-se a lógica, a modelagem, a arquitetura e a qualidade. Uma demo em linha de comando bem feita vale mais do que uma tela bonita sem regra por trás.  

**Preciso de banco de dados?**  
Não é obrigatório. Persistir em arquivo ou SQLite é bem-vindo se ajudar a mostrar a separação por camadas (o exemplo de referência troca memória por SQLite sem mexer no domínio). Se usarem PostgreSQL ou similar, ele entra como **serviço no `docker-compose.yml`**.  

**Python ou Java?**  
Os dois valem. Mantenham uma linguagem só no projeto. Em Java, use uma imagem Maven ou Temurin (ex.: `maven:3.9-eclipse-temurin-21`) e o mesmo contrato do compose: `up` roda o sistema e o serviço `testes` roda `mvn test`. Há um exemplo Java **testado** em `repository/class27/a3-reservalab-java/` (o mesmo ReservaLab, com Maven e JUnit 5).  

**Preciso saber Docker a fundo?**  
Não. Um `Dockerfile` de poucas linhas e um compose com 1 ou 2 serviços bastam; os repositórios de referência (`a3-reservalab` em Python e `a3-reservalab-java`) têm um `Dockerfile` e um compose que funcionam e podem servir de base. O que se avalia é que **outra pessoa rode o projeto sem configurar nada**.  

**Posso usar um modelo de IA diferente do da Aula 09B?**  
Pode, desde que declarado no README (ferramenta, modelo e versão) e respeitadas as regras de dados e de chave de API. O mapa de uso por etapa vale para qualquer agente.  

**A IA escreveu a maior parte do código. Isso é um problema?**  
Não por si só, **se vocês entendem e sabem defender**. O problema é código que o grupo não consegue explicar: a pergunta do professor vai a um trecho escolhido dele, e a resposta define o nível do critério.  

**E se o agente errar e eu não notar?**  
O erro é de vocês; é por isso que o registro de uso pede erros reais encontrados. Teste, mutação manual e conferência da rastreabilidade são as defesas.  

**Quantas linhas de código o sistema precisa ter?**  
Não há mínimo. O exemplo de referência tem cerca de 390 linhas de código-fonte e 280 de teste.  

---  

## Ferramentas Recomendadas  

| Categoria | Ferramenta | Acesso |  
|---|---|---|  
| Agente de IA | OpenCode + OpenRouter (modelo `:free`) | Gratuito (Aula 09B) |  
| Agente alternativo | Claude Code | Conforme sua conta (Aula 09B, Slide 29) |  
| Diagramas | Mermaid (mermaid.live), PlantUML | Gratuito |  
| Contêineres | Docker e Docker Compose | Gratuito (Docker Engine, ou Docker Desktop conforme a licença) |  
| Testes | pytest (Python), JUnit (Java) | Gratuito |  
| Versionamento | Git + GitHub | Gratuito |  
| Quadro de tarefas | GitHub Projects (issues), Trello | Gratuito |  
| API (se houver) | OpenAPI + Swagger UI | Gratuito |  
| Edição | VS Code, IntelliJ, PyCharm | Gratuito (edições Community) |  

---  

## Referências  

- SOMMERVILLE, Ian. *Engenharia de software*. 10. ed. Pearson, 2018.  
- PRESSMAN, Roger S.; MAXIM, Bruce R. *Engenharia de software: uma abordagem profissional*. 9. ed. AMGH, 2021.  
- MARTIN, Robert C. *Arquitetura limpa*. Alta Books, 2019.  
- BROWN, Simon. *The C4 model for visualising software architecture*. c4model.com.  
- NYGARD, Michael. *Documenting Architecture Decisions*. 2011.  
- Conventional Commits 1.0.0. conventionalcommits.org.  
- Material da disciplina: Aulas 1-28, Aula 09B (IA assistida) e o exemplo de referência *ReservaLab*.  
