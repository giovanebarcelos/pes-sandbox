# Comunicação entre os serviços (versão microsserviços)

```plantuml
@startuml
actor Cliente
participant "Locações\n:8002" as L
participant "Frota\n:8001" as F
queue "Outbox\n(dentro de Locações)" as O
participant "Notificação\n:8003" as N

Cliente -> L : POST /v1/locacoes
L -> F : GET /v1/veiculos/{placa}/preco (síncrono)
F --> L : 200 preco_base
L -> F : POST /v1/veiculos/{placa}/reserva (síncrono)
F --> L : 200 / 409
L -> L : grava LocacaoIniciada (event store)
L -> O : tarefa "notificar"
L --> Cliente : 201 Created
... alguns instantes depois (assíncrono) ...
O -> N : POST /v1/notificacoes (com nova tentativa)
N --> O : 201 (ou 200 duplicada)
@enduml
```

| Interação | Estilo | Por quê | O que acontece se o outro lado cair |
|---|---|---|---|
| Locações → Frota (preço, reserva) | **Síncrono** (HTTP/JSON) | Sem a resposta não dá para fechar a locação | Locações responde **503** — falha visível, nada é gravado |
| Locações → Frota (liberar na devolução) | **Assíncrono** (outbox) | A devolução já aconteceu; liberar pode esperar | Fica pendente em `GET /v1/outbox` e é feito quando a Frota volta |
| Locações → Notificação | **Assíncrono** (outbox) | Aviso ao cliente não é crítico | Locação conclui normalmente; o aviso chega depois |

**Contratos versionados:** todas as rotas começam com `/v1/`. Mudança incompatível = nova rota `/v2/...`, mantendo a `/v1` enquanto houver consumidor.
