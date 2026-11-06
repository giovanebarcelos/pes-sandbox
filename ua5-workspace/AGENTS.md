# AGENTS.md
Workspace da UA5 (Aulas 18-22) — Locadora de Veículos evoluindo de um código legado até microsserviços. Python 3.10+, **somente biblioteca padrão** (nada de pip), testes com `unittest`.

## Onde está cada coisa
- Tutoriais passo a passo: tutoriais/Tutorial_Aula18..22_*.md
- Cada aula: aulaXX/inicio (ponto de partida) · aulaXX/solucao (referência do professor) · aulaXX/meu (cópia de trabalho do aluno)
- Decisões: aulaXX/solucao/docs/adr/ · Glossário: aula21/solucao/GLOSSARY.md

## Regras
- O aluno trabalha em aulaXX/meu. Não edite aulaXX/inicio nem aulaXX/solucao.
- Testes: `python -m unittest -v` dentro da pasta da aula — rode antes e depois de qualquer mudança.
- Não adicione dependências externas (pip/Flask/pytest): o curso precisa rodar em laboratório sem internet e sem Docker.
- Primeiro o aluno tenta à mão; o agente explica, revisa e propõe em passos pequenos, mostrando o diff.
- Nunca faça `git push`.

## Arquitetura (acrescentado na Aula 22)
<!-- Preencha na Aula 22, uma linha cada: padrão adotado, pasta docs/adr/, regra de dependência do domínio -->

## Agent skills
<!-- bloco escrito pela skill setup-matt-pocock-skills (Aula 01B, Slide 17) -->
