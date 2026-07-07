# Repository — Projeto e Engenharia de Software

**Repositório de exemplos reais do curso** | **28 aulas** | **Prof. Giovane Barcelos**

Este repositório contém os artefatos práticos de todas as 28 aulas da disciplina Projeto e Engenharia de Software (PES). Cada `classXX/` contém diagramas Mermaid (`.mmd`), código Python e código Java, garantindo que **nenhuma aula fique sem exemplo real executável**.

---

## Estrutura

```
repository/
  class01/{python,java,diagrams}/  ... class28/
```

Cada diretório `classXX/` contém:

| Subdiretório | Conteúdo |
|---|---|
| `diagrams/` | Diagramas Mermaid (`.mmd`) — fluxos, UML, arquitetura |
| `python/` | Código Python 3.10+ executável |
| `java/` | Código Java 17+ executável |

## Tecnologias

- **Python 3.10+** — scripts e exemplos OO/arquiteturais
- **Java 17+** — mesmos exemplos em Java para comparação
- **Mermaid** — diagramas versionados como texto (`.mmd`), compatíveis com PlantUML (`@startmermaid`)

## Sumário por Aula

| Aula | UA | Tema | Diagramas | Python | Java |
|---|---|---|---|---|---|
| 01 | UA1 | Boas-vindas e Introdução à ES | PES01-MapaProcessoSoftware.mmd | PES0101-OlaEngenhariaSoftware.py | PES0101-OlaEngenhariaSoftware.java |
| 02 | UA1 | Fundamentos de Gerenciamento | PES02-GruposProcessos.mmd, PES02-CicloVidaProjeto.mmd | PES0201-RestricaoTripla.py | PES0201-RestricaoTripla.java |
| 03 | UA1 | Planejamento (Escopo/Prazo/Custo) | PES03-EAP.mmd, PES03-Gantt.mmd | PES0301-CaminhoCritico.py | PES0301-CaminhoCritico.java |
| 04 | UA1 | Riscos e Ferramentas | PES04-MatrizRiscos.mmd | PES0401-ExposicaoRisco.py | PES0401-ExposicaoRisco.java |
| 05 | UA2 | Modelos Tradicionais | PES05-Cascata.mmd, PES05-Espiral.mmd | PES0501-ProgressoFases.py | PES0501-ProgressoFases.java |
| 06 | UA2 | Incremental e Prototipação | PES06-Incremental.mmd | PES0601-EntregasIncrementais.py | PES0601-EntregasIncrementais.java |
| 07 | UA2 | Scrum e XP | PES07-FluxoScrum.mmd, PES07-Sprint.mmd | PES0701-BurndownSprint.py | PES0701-BurndownSprint.java |
| 08 | UA2 | Kanban e Escolha de Abordagem | PES08-QuadroKanban.mmd | PES0801-LeadTimeWIP.py | PES0801-LeadTimeWIP.java |
| 09 | UA3 | Engenharia de Requisitos | PES09-ProcessoRequisitos.mmd | PES0901-RegistroRequisitos.py | PES0901-RegistroRequisitos.java |
| 10 | UA3 | Elicitação de Requisitos | PES10-UserStoryMap.mmd | PES1001-UserStoryParser.py | PES1001-UserStoryParser.java |
| 11 | UA3 | RF e RNF (FURPS+) | PES11-ClassificacaoRequisitos.mmd | PES1101-ClassificadorRF-RNF.py | PES1101-ClassificadorRFRNF.java |
| 12 | UA3 | Documentação e Validação | PES12-MatrizRastreabilidade.mmd, PES12-DRS.md | PES1201-Rastreabilidade.py | PES1201-Rastreabilidade.java |
| 13 | UA4 | UML e OO | PES13-DiagramaClasseConta.mmd | PES1301-ClasseConta.py | PES1301-ClasseConta.java |
| 14 | UA4 | Casos de Uso | PES14-CasosUsoBiblioteca.mmd | PES1401-CasosUso.py | PES1401-CasosUso.java |
| 15 | UA4 | Diagrama de Classes e OO | PES15-HerancaFuncionario.mmd | PES1501-FuncionarioHeranca.py | PES1501-FuncionarioHeranca.java |
| 16 | UA4 | Sequência e Atividades | PES16-Sequencia.mmd, PES16-Atividades.mmd | PES1601-FluxoPedido.py | PES1601-FluxoPedido.java |
| 17 | UA4 | Projeto Parcial Locadora | PES17-CasosUsoLocadora.mmd, PES17-ClassesLocadora.mmd | PES1701-Locadora.py, PES1702-LocadoraEsqueleto.py | PES1701-Locadora.java, PES1702-LocadoraEsqueleto.java |
| 18 | UA5 | Princípios SOLID | PES18-CoesaoAcoplamento.mmd | PES1801-SOLID.py | PES1801-SOLID.java |
| 19 | UA5 | MVC, MVP, MVVM | PES19-Camadas.mmd, PES19-MVC.mmd, PES19-MVP.mmd, PES19-MVVM.mmd | PES1901-MVC.py, PES1902-MVP.py, PES1903-MVVM.py | PES1901-MVC.java, PES1902-MVP.java, PES1903-MVVM.java |
| 20 | UA5 | Clean Architecture, Hexagonal, Onion | PES20-CleanArch.mmd, PES20-Hexagonal.mmd, PES20-Onion.mmd, PES20-ComparativoArquiteturas.mmd | PES2001-Hexagonal.py, PES2002-Onion.py | PES2001-Hexagonal.java, PES2002-Onion.java |
| 21 | UA5 | DDD, CQRS, Event Sourcing | PES21-DDD-Contextos.mmd, PES21-CQRS.mmd, PES21-EventSourcing.mmd | PES2101-DDD-Dominio.py, PES2102-CQRS-EventSourcing.py | PES2101-DDD-Dominio.java, PES2102-CQRSEventSourcing.java |
| 22 | UA5 | Microsserviços e Arquiteturas Distribuídas | PES22-Microservicos.mmd, PES22-ModularMonolith.mmd, PES22-Microkernel.mmd, PES22-P2P.mmd | PES2201-Microservicos.py, PES2202-Microkernel.py, PES2203-ComunicacaoServicos.py | PES2201-Microservicos.java, PES2202-Microkernel.java, PES2203-ComunicacaoServicos.java |
| 23 | UA6 | Ciclo de Vida (SDLC) | PES23-SDLC.mmd | PES2301-CustoCicloVida.py | PES2301-CustoCicloVida.java |
| 24 | UA6 | Manutenção e Evolução | PES24-TiposManutencao.mmd | PES2401-Refatoracao.py | PES2401-Refatoracao.java |
| 25 | UA7 | Git — Fundamentos | PES25-EstadosGit.mmd, PES25-FluxoCommit.mmd | PES2501-ComandosGit.py | PES2501-ComandosGit.java |
| 26 | UA7 | Git — Branches e Colaboração | PES26-Branches.mmd, PES26-GitFlow.mmd | PES2601-ConflitoMerge.py | PES2601-ConflitoMerge.java |
| 27 | UA8 | Documentação Técnica | PES27-C4-Contexto.mmd, PES27-TemplateREADME.md | PES2701-Documentacao.py | PES2701-Documentacao.java |
| 28 | UA8 | Práticas Colaborativas e Ética | PES28-MapaMentalCurso.mmd | PES2801-CodeReviewChecklist.py | PES2801-CodeReviewChecklist.java |

## Como Executar

### Python
```bash
cd repository/classXX/python/
python3 PESXXYY-NomeDoArquivo.py
```

### Java
```bash
cd repository/classXX/java/
javac PESXXYY-NomeDoArquivo.java && java NomeDaClasse
```

## Pré-requisitos

- Python 3.10 ou superior
- Java 17 ou superior (JDK)
- Visualizador Mermaid (GitHub, MarkText, ou https://plantuml.com/ com `@startmermaid`)

## Nomenclatura

- **PES** = Projeto e Engenharia de Software (prefixo do curso)
- **XX** = Número da aula (01-28)
- **YY** = Sequencial do artefato (01-99)
- `.mmd` = Diagrama Mermaid (UML, fluxo, arquitetura)

## Licença

MIT — uso acadêmico livre. Prof. Giovane Barcelos.
