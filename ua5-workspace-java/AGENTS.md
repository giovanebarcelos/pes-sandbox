# AGENTS.md
Workspace da UA5 (Aulas 18-22), **versão Java** — Locadora de Veículos evoluindo de um código legado até microsserviços. **JDK 17+, somente o JDK** (nada de Maven, Gradle, Spring ou JUnit baixado), testes com o mini-JUnit do pacote `teste` (mesma API do JUnit 5).

## Onde está cada coisa
- Tutoriais passo a passo: Aula_18..22_Tutorial_*_Java.md (distribuídos com os slides; o aluno pode colá-los ou referenciá-los)
- Cada aula: aulaXX/inicio (ponto de partida) · aulaXX/solucao (referência do professor) · aulaXX/meu (cópia de trabalho do aluno)
- Em cada pasta de aula: src/ (código), test/ (testes; test/teste/ é o mini-JUnit — não altere), docs/
- Decisões: aulaXX/solucao/docs/adr/ · Glossário: aula21/solucao/GLOSSARY.md

## Regras
- O aluno trabalha em aulaXX/meu. Não edite aulaXX/inicio nem aulaXX/solucao.
- Testes: `java ../../ferramentas/Rodar.java testes` DENTRO da pasta da aula — rode antes e depois de qualquer mudança.
- Rodar uma classe: `java ../../ferramentas/Rodar.java pacote.Classe`.
- Não adicione dependências externas (Maven/Gradle/Spring/Lombok/JUnit): o curso precisa rodar em laboratório sem internet e sem Docker.
- Código em Java 17 (records e `instanceof` com padrão podem; `switch` com padrão e virtual threads não).
- Valores em reais formatados com `String.format(Locale.ROOT, "%.2f", ...)` (ponto decimal, como nos testes).
- Primeiro o aluno tenta à mão; o agente explica, revisa e propõe em passos pequenos, mostrando o diff.
- Nunca faça `git push`.

## Arquitetura (acrescentado na Aula 22)
<!-- Preencha na Aula 22, uma linha cada: padrão adotado, pasta docs/adr/, regra de dependência do domínio -->

## Agent skills
<!-- bloco escrito pela skill setup-matt-pocock-skills (Aula 01B, Slide 17) -->
