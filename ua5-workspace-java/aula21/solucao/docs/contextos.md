# Mapa de Contextos — Locadora

```plantuml
@startuml
skinparam componentStyle rectangle
package "Gestão de Frota\n(CRUD, estado atual)" as frota {
  [FrotaAPI] <<Open Host Service>>
  [Veiculo]
}
package "Locações\n(CQRS + Event Sourcing)" as loc {
  [ManipuladorComandos] <<escrita>>
  [Locacao] <<agregado>>
  [Projeções] <<leitura>>
  [ServicoFrota] <<ACL / porta>>
}
package "Shared Kernel" as sk {
  [Placa]
  [Dinheiro]
  [EventoDominio]
}
[ServicoFrota] --> [FrotaAPI] : precoBase / reservar\n(síncrono)
[Locacao] ..> [FrotaAPI] : VeiculoDevolvido\n-> liberar (por evento)
frota ..> sk
loc ..> sk
@enduml
```

| Relação | Tipo (DDD) | Por quê |
|---|---|---|
| Locações → Frota | **Cliente/Fornecedor** com **Camada Anticorrupção** (`ServicoFrota`) | Locações precisa de preço e disponibilidade, mas não quer depender das classes internas da frota |
| Frota → mundo | **Open Host Service** (`FrotaAPI`) | Uma API pública estável, que vira HTTP na Aula 22 |
| Ambos | **Shared Kernel** (`compartilhado/`) | `Placa`, `Dinheiro` e a base de eventos são combinados pelos dois times — mantenha pequeno |
