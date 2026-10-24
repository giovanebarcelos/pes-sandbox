# Documento de Requisitos — ReservaLab

**Versão:** 1.0 · **Status:** Aprovado pelo grupo e validado com a coordenação (ver "Validação")

## 1. Objetivo

Permitir que alunos, professores e coordenadores reservem salas de estudo, laboratórios e o auditório do campus sem conflito de horário, com regras diferentes por tipo de recurso.

## 2. Partes interessadas

| Parte interessada | Interesse | Como foi ouvida |
|---|---|---|
| Aluno | Achar sala livre rápido, sem depender de papel ou da secretaria | Entrevista |
| Professor | Garantir laboratório e auditório com antecedência | Entrevista |
| Coordenação | Controlar o auditório e coibir "reservas fantasma" | Entrevista + observação da planilha atual |

## 3. Requisitos funcionais

Prioridade MoSCoW: **M**ust, **S**hould, **C**ould.

| ID | Requisito | Prior. | Origem |
|---|---|---|---|
| RF-01 | O sistema deve manter um catálogo de recursos (tipo, nome, capacidade); id repetido e capacidade < 1 são recusados | M | Coordenação |
| RF-02 | O sistema deve mostrar, para um recurso e um dia, as faixas livres entre 08h e 22h | M | Aluno |
| RF-03 | O sistema deve permitir solicitar reserva informando recurso, início e fim | M | Aluno, Professor |
| RF-04 | O sistema não pode aceitar duas reservas ativas (pendente ou confirmada) sobrepostas no mesmo recurso; reservas que apenas se encostam (fim = início) são permitidas | M | Todos |
| RF-05 | O sistema deve aplicar as regras do tipo de recurso: perfil permitido, duração máxima, antecedência mínima e necessidade de aprovação (tabela abaixo) | M | Coordenação |
| RF-06 | O sistema deve permitir cancelar reserva ativa antes do início; cancelar com menos de 2h de antecedência é "cancelamento tardio" | M | Coordenação |
| RF-07 | O sistema deve bloquear novas reservas de quem tem 3 ou mais cancelamentos tardios nos últimos 30 dias | S | Coordenação |
| RF-08 | O sistema deve permitir que o coordenador aprove ou rejeite reservas pendentes | M | Coordenação |
| RF-09 | O sistema deve listar as reservas de um usuário, da mais próxima para a mais distante | S | Aluno |

**Regras por tipo (RF-05):**

| Tipo | Perfis | Duração máx. | Antecedência mín. | Aprovação |
|---|---|---|---|---|
| Sala de estudo | todos | 2 h | 30 min | automática |
| Laboratório | professor, coordenador | 4 h | 24 h | automática |
| Auditório | professor, coordenador | 6 h | 72 h | coordenador |

## 4. Requisitos não funcionais (FURPS+)

| ID | Categoria | Requisito (mensurável) | Prior. |
|---|---|---|---|
| RNF-01 | Desempenho | Verificar conflito e gravar uma reserva em ≤ 200 ms com 10.000 reservas no recurso | M |
| RNF-02 | Manutenibilidade | `dominio/` não importa `aplicacao/`, `infra/` nem `sqlite3`; `aplicacao/` não importa `infra/` — verificado por teste automático | M |
| RNF-03 | Suportabilidade | Trocar o repositório (memória ↔ SQLite) sem alterar domínio nem aplicação; a mesma suíte de testes roda nos dois | S |
| RNF-04 | Confiabilidade | Sob 20 solicitações simultâneas do mesmo horário, exatamente 1 é aceita | M |
| RNF-05 | Legal (LGPD) | Armazenar somente matrícula, nome e perfil; nenhum CPF, e-mail ou telefone | M |

## 5. Critérios de aceitação (Gherkin)

**RF-04 — conflito**

```gherkin
Cenário: Sobreposição parcial é recusada
  Dado que a sala S101 está reservada das 14h às 16h
  Quando Ana solicita a sala S101 das 15h às 17h
  Então o sistema recusa com "já reservado de 14:00 a 16:00"

Esquema do Cenário: Reservas encostadas não conflitam
  Dado que a sala S101 está reservada das 14h às 16h
  Quando Ana solicita a sala S101 das <inicio> às <fim>
  Então o resultado é <resultado>

  Exemplos:
    | inicio | fim | resultado |
    | 16h    | 18h | aceita    |
    | 12h    | 14h | aceita    |
    | 15h59  | 17h | recusada  |
    | 13h    | 14h01 | recusada |
```

**RF-06 — cancelamento tardio (valores-limite)**

```gherkin
Esquema do Cenário: Classificação do cancelamento
  Dado uma reserva que começa às 14h
  Quando o dono cancela às <hora>
  Então o cancelamento é <classe>

  Exemplos:
    | hora  | classe |
    | 11h59 | normal |
    | 12h00 | normal |
    | 12h01 | tardio |
    | 13h59 | tardio |
```

## 6. Fora de escopo (versão 1.0)

Interface gráfica · autenticação real (usuário chega identificado) · persistência do catálogo de recursos (fica em memória, carregado na inicialização) · notificações por e-mail · reservas recorrentes · fuso horário (horário local do campus).

## 7. Rastreabilidade: requisito → caso de uso → teste

| Requisito | Caso de uso | Testes (`tests/`) |
|---|---|---|
| RF-01 | UC06 | `test_catalogo.py::test_registrar_e_listar_ordenado_por_id`, `::test_registrar_id_repetido_e_recusado`, `::test_capacidade_deve_ser_positiva`, `::test_obter_inexistente` |
| RF-02 | UC01 | `test_consultas.py::test_dia_vazio_tem_faixa_unica_08_22`, `::test_horarios_livres_descontam_reservas`, `::test_recurso_inexistente` |
| RF-03 | UC02 | `test_regras_por_tipo.py::test_sala_de_estudo_confirma_na_hora`, `::test_fim_antes_do_inicio_e_recusado` |
| RF-04 | UC02 (include) | `test_conflito.py::test_sobreposicao_parcial_e_recusada`, `::test_reservas_encostadas_nao_conflitam`, `::test_mesmo_horario_em_recursos_diferentes_e_permitido`, `::test_reserva_cancelada_libera_o_horario`, `::test_reserva_pendente_tambem_ocupa_o_horario` |
| RF-05 | UC02 (include) | `test_regras_por_tipo.py::test_sala_aceita_exatamente_2h_e_recusa_2h01`, `::test_sala_exige_30min_de_antecedencia`, `::test_laboratorio_recusa_aluno`, `::test_laboratorio_exige_24h_de_antecedencia`, `::test_auditorio_fica_pendente_ate_aprovacao`, `::test_auditorio_exige_72h` |
| RF-06 | UC03, UC09 | `test_cancelamento_e_penalidade.py::test_cancelar_com_antecedencia_nao_e_tardio`, `::test_cancelar_com_menos_de_2h_e_tardio`, `::test_exatamente_2h_antes_nao_e_tardio`, `::test_nao_cancela_reserva_ja_iniciada`, `::test_nao_cancela_duas_vezes`, `::test_outro_aluno_nao_cancela_reserva_alheia`, `::test_coordenador_pode_cancelar_reserva_alheia` |
| RF-07 | UC02 | `test_cancelamento_e_penalidade.py::test_tres_cancelamentos_tardios_bloqueiam`, `::test_dois_cancelamentos_tardios_nao_bloqueiam`, `::test_bloqueio_expira_apos_30_dias` |
| RF-08 | UC05 | `test_cancelamento_e_penalidade.py::test_coordenador_aprova_reserva_pendente`, `::test_coordenador_rejeita_e_horario_fica_livre`, `::test_professor_nao_aprova`, `::test_nao_aprova_reserva_ja_confirmada` |
| RF-09 | UC04 | `test_consultas.py::test_minhas_reservas_ordenadas_por_inicio` |
| RNF-01 | — | `test_qualidade.py::test_verificar_conflito_em_menos_de_200ms_com_10_mil_reservas` |
| RNF-02 | — | `test_qualidade.py::test_dominio_nao_importa_aplicacao_nem_infra`, `::test_aplicacao_nao_importa_infra` |
| RNF-03 | — | `test_qualidade.py::test_contrato_do_repositorio_ida_e_volta` e todos os testes de `servico`, parametrizados nos dois repositórios |
| RNF-04 | — | `test_conflito.py::test_memoria_20_threads_no_mesmo_horario_so_uma_vence`, `::test_sqlite_20_conexoes_no_mesmo_horario_so_uma_vence` |
| RNF-05 | — | Sem teste automático: verificado por revisão do esquema (`infra/repositorio_sqlite.py`) — **lacuna conhecida** |

## 8. Validação

Revisão com checklist da Aula 12 (requisitos completos, consistentes, verificáveis, rastreáveis) pelo grupo; versão 1.0 apresentada à coordenação (dados fictícios do exemplo).
