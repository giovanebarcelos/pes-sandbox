# ADR-002: Verificar e gravar o conflito numa operação só, dentro do repositório

**Status:** Aceito

## Contexto

A primeira ideia era o serviço consultar "há conflito?" e, se não houvesse, gravar. Com dois usuários pedindo o mesmo horário ao mesmo tempo, ambos passariam na consulta e ambos gravariam (condição de corrida): duas reservas sobrepostas, violando RF-04.

## Decisão

A porta expõe `adicionar_sem_conflito`, que verifica e grava **atomicamente**. A memória usa um `Lock`; o SQLite usa `BEGIN IMMEDIATE` (trava de escrita antes de consultar).

## Alternativas consideradas

| Alternativa | Por que foi rejeitada |
|---|---|
| Consultar no serviço e gravar depois | Condição de corrida (verificar-depois-agir). |
| Restrição `UNIQUE` no banco | Não expressa sobreposição de intervalos; só igualdade exata. |
| Lock global no serviço | Só protege um processo; duas instâncias da aplicação continuariam disputando. |

## Consequências

- (+) RNF-04 garantido e testado (20 threads / 20 conexões → exatamente 1 vence).
- (−) Todo novo repositório precisa implementar a atomicidade; ela é parte do contrato da porta.
- (−) `BEGIN IMMEDIATE` serializa as escritas — aceitável no volume do campus.
