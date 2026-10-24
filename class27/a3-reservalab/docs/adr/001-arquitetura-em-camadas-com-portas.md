# ADR-001: Arquitetura em camadas com portas (hexagonal leve)

**Status:** Aceito

## Contexto

O núcleo do ReservaLab são regras de negócio sutis (intervalos, antecedência, penalidade) que precisam de testes exatos e rápidos. A persistência vai mudar: começamos em memória, passamos a SQLite e a coordenação já falou em "integrar ao sistema acadêmico". A interface hoje é uma CLI; não há telas.

## Decisão

Três camadas com a dependência apontando sempre para dentro: `dominio` ← `aplicacao` ← `infra`/`cli`. A aplicação declara **portas** (`RepositorioReservas`, `Relogio`) e a infraestrutura as implementa (adaptadores). O relógio é injetado para que os testes controlem "agora".

## Alternativas consideradas

| Alternativa | Por que foi rejeitada |
|---|---|
| **MVC** (Aula 19) | Resolve a separação de *apresentação*; nosso problema é isolar regra de negócio da *persistência*. Sem interface gráfica, a View seria artificial. |
| **Tudo em um módulo** | Rápido no começo, mas as regras ficariam presas ao `sqlite3` e os testes precisariam de banco. |
| **Clean Architecture completa** (Aula 20) | Camadas extras (entidades × casos de uso × adaptadores de interface) sem ganho para ~300 linhas; viraria cerimônia. |

## Consequências

- (+) Regras testadas sem banco; troca memória ↔ SQLite sem tocar o domínio (RNF-02, RNF-03).
- (+) A regra de dependência é verificada por teste automático.
- (−) Mais arquivos e uma indireção (porta) a explicar para quem chega.
- (−) Invariantes que exigem atomicidade (sem conflito) precisaram entrar **na porta** (`adicionar_sem_conflito`) — ver ADR-002.
