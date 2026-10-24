# A3 — Texto de Postagem no Anima Hub  

> Material para o **professor**: o que colar e anexar ao criar a atividade A3 no Anima Hub. Não é distribuído aos alunos.  

---  

## Configuração sugerida da atividade  

| Campo | Sugestão |  
|---|---|  
| **Título** | A3 — Projeto Integrador: sistema de software com agente de IA |  
| **Valor** | 40 pontos |  
| **Modalidade** | Em grupo (3 a 5 integrantes); uma entrega por grupo |  
| **Tipo de envio** | Arquivo (`.pdf` da capa e `.zip` do repositório) e link (GitHub) |  
| **Tentativas** | 1 envio por grupo (reenvio só antes do prazo) |  
| **Prazo** | Antes da Aula 28 (definir a data no Hub) |  
| **Anexos do professor** | `A3_Projeto_PES` (enunciado), `A3_Exemplo_ReservaLab` (exemplo de referência) e **dois zips** de repositório de referência: `repository/class27/a3-reservalab/` (**Python**) e `repository/class27/a3-reservalab-java/` (**Java**) |  

> Ajuste ao que o Anima Hub oferecer para a sua turma (nomes de campos, tentativas, grupos). Este roteiro foi escrito sem acesso à configuração da atividade no portal.  

---  

## Descrição (para colar no campo de texto da atividade)  

**A3 — Projeto Integrador (40 pontos)**  

Em grupo, vocês vão escolher um problema com regras de negócio de verdade e construir, do início ao fim, um sistema de software: requisitos, modelagem UML, arquitetura, código com testes, documentação e versionamento no GitHub. O trabalho é feito **com o agente de IA da disciplina** (Aula 09B), e o uso é **declarado** e **defendido oralmente** na Aula 28.  

**O tema é livre**, desde que tenha pelo menos 3 regras de negócio com valores-limite testáveis (só cadastrar e listar não basta). Mostrem o tema ao professor antes de se comprometerem.  

**Critérios (40 pts):** Requisitos 10 · Modelagem UML 10 · Arquitetura 5 · Código 5 · Documentação 5 (inclui o registro de uso de IA) · Versionamento e colaboração 5.  

**Uso do agente de IA (obrigatório):** configurem o OpenCode conforme a Aula 09B, usem as skills nas etapas indicadas no enunciado e registrem no README, em uma tabela, o que pediram, **o que o agente errou** e o que o grupo corrigiu (mínimo 5 registros e 2 erros reais). Artefato que o grupo não sabe explicar é avaliado como Insuficiente, com ou sem IA.  

**Linguagem:** **Python ou Java** (uma só por projeto). Há um exemplo de referência em cada uma, com as mesmas regras e os mesmos 74 testes.  

**Entrega com Docker Compose (obrigatório, nas duas linguagens):** o repositório traz `Dockerfile` e `docker-compose.yml` na raiz, de modo que `docker compose up --build` suba o sistema e `docker compose run --rm testes` rode os testes (`pytest` em Python, `mvn test` em Java), numa máquina que só tenha o Docker. No exemplo Java, a primeira execução baixa as dependências do Maven e leva alguns minutos.  

**Apresentação (Aula 28):** 5 a 7 minutos, com demonstração ao vivo e a pergunta do professor.  

**Material de apoio (em anexo):**  
- Enunciado completo, com rubrica detalhada e checklist  
- Exemplo completo de referência: ReservaLab (use como guia, não copie)  
- Repositório de referência em **Python** (`a3-reservalab`): código, testes, documentação e `docker-compose.yml`  
- Repositório de referência em **Java** (`a3-reservalab-java`): Maven, JUnit 5 e `docker-compose.yml`  

---  

## O que o grupo deve postar  

1. **Capa da entrega (PDF, 1 página):** nome do projeto, integrantes (nome e RA), link do repositório, tag da entrega e hash do commit  
2. **Repositório completo (`.zip`)**, incluindo a pasta `.git` e os arquivos `Dockerfile` e `docker-compose.yml`, sem `.venv` nem `node_modules`  
3. **Link do repositório no GitHub** (público, ou com o professor como colaborador), no campo de texto  

**Teste antes de postar:** descompacte o zip em outra pasta e rode `docker compose up --build` e `docker compose run --rm testes`.  

Mudanças depois do prazo não são consideradas: vale o commit da tag.  

---  

## Mensagem de aviso à turma (opcional)  

> Pessoal, a A3 está publicada no Anima Hub. O tema é livre, mas precisa ter regras de negócio de verdade: tragam o tema para eu aprovar até a Aula 4. Há um exemplo completo (ReservaLab) com código, testes e documentação, em Python e em Java, para vocês usarem como guia. A entrega precisa rodar com `docker compose up` (Dockerfile e docker-compose na raiz), então testem cedo, numa cópia limpa. e o enunciado mostra em que aula cada parte do projeto é construída. O uso do agente de IA é obrigatório e vale pontos, mas o que vale mesmo é saber explicar o que entregaram. Dúvidas no fórum da atividade.  
