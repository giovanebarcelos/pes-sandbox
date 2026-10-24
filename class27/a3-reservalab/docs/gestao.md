# Gestão do Projeto — ReservaLab

## 1. Processo escolhido: Kanban com limite de WIP

| Critério (Aula 8) | Situação do grupo | Efeito na escolha |
|---|---|---|
| Tamanho da equipe | 4 pessoas | Scrum completo (papéis, cerimônias) seria mais cerimônia que trabalho |
| Disponibilidade | Variável: cada um tem trabalho e outras disciplinas | Sprint de tamanho fixo seria quebrada toda semana; fluxo contínuo absorve a variação |
| Requisitos | Razoavelmente estáveis depois da validação com a coordenação (Aula 12) | Pouca necessidade de replanejar a cada ciclo |
| Prazo | Fixo (entrega A3) | Exige visibilidade de gargalos — o quadro dá isso |

**Decisão:** Kanban em quadro de *issues* do GitHub, colunas `Backlog → Fazendo → Revisão → Pronto`, **WIP = 2 em "Fazendo" por pessoa** e **WIP = 3 em "Revisão" no total** (revisão parada é o gargalo clássico de times pequenos). Reunião de 15 min por semana, na terça, para olhar o quadro.

**Definição de pronto:** código com teste que passa, revisado por outro integrante, requisito/ADR/SDR atualizado se mudou, merge na `main` por *pull request*.

## 2. Estrutura analítica (EAP)

```
1 ReservaLab
├── 1.1 Requisitos         1.1.1 Entrevistas · 1.1.2 Documento de requisitos · 1.1.3 Validação
├── 1.2 Modelagem          1.2.1 Casos de uso · 1.2.2 Classes · 1.2.3 Sequência e atividades
├── 1.3 Arquitetura        1.3.1 ADRs · 1.3.2 SDR · 1.3.3 C4
├── 1.4 Implementação      1.4.1 Domínio · 1.4.2 Aplicação · 1.4.3 Repositórios · 1.4.4 CLI
├── 1.5 Qualidade          1.5.1 Testes · 1.5.2 Valores-limite e mutação · 1.5.3 Concorrência
└── 1.6 Entrega            1.6.1 README e Uso de IA · 1.6.2 Pitch · 1.6.3 Ensaio e defesa oral
```

## 3. Riscos

Probabilidade (P) e impacto (I) de 1 a 5; exposição = P × I.

| ID | Risco | P | I | Exp. | Resposta | Gatilho |
|---|---|---|---|---|---|---|
| R1 | Regras de negócio mudarem depois de implementadas (coordenação muda o limite de horas) | 3 | 4 | 12 | **Mitigar:** regras em atributos de classe (`duracao_maxima`...), cobertas por teste de valor-limite | Pedido de mudança após a validação |
| R2 | Duas reservas no mesmo horário por condição de corrida | 2 | 5 | 10 | **Mitigar:** operação atômica na porta (ADR-002) + teste com 20 threads | Teste de concorrência vermelho |
| R3 | Limite diário do modelo gratuito do agente (≈ 50 req/dia, Aula 09B) travar a semana final | 4 | 3 | 12 | **Mitigar:** usar `caveman`, pedidos pequenos, não deixar IA para a última semana | 2 dias seguidos com cota estourada |
| R4 | Integrante com pouca disponibilidade na reta final | 3 | 3 | 9 | **Mitigar:** programação em par nas partes críticas; todos revisam PR | Item parado > 5 dias em "Fazendo" |
| R5 | Documento dizer algo que o código não faz (documentação gerada por IA e não conferida) | 3 | 4 | 12 | **Evitar:** matriz de rastreabilidade conferida por script; revisão humana de todo texto gerado | Teste citado na matriz que não existe |

## 4. Estimativa e acompanhamento

Estimativa por *planning poker* em pontos (1, 2, 3, 5). Total de 41 pontos no *backlog*; o grupo mede o **tempo de ciclo** (de "Fazendo" a "Pronto") e acompanha a tendência no quadro.
