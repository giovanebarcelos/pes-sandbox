# Glossário — Linguagem Ubíqua da Locadora

> Os termos são os que o **cliente** usa nas conversas (Aula 17 + regras novas). Cada termo pertence a um contexto; o mesmo nome pode significar coisas diferentes em contextos diferentes.

| Termo | Contexto | Significado | No código |
|---|---|---|---|
| **Veículo** | Frota | Bem da empresa, com placa, modelo, tipo e diária | `frota/dominio/Veiculo.java` |
| **Veículo** | Locações | Só uma placa com um preço — o resto não interessa a este contexto | `placa` em `LocacaoIniciada` |
| **Placa** | Ambos | Identificador do veículo, padrão antigo (`ABC-1234`) ou Mercosul (`BRA2E19`) | `compartilhado/Placa.java` |
| **Diária** | Frota | Preço de um dia de uso, sem seguro nem desconto | `Veiculo.valorDiaria()` |
| **Preço-base** | Frota | Diária × dias + seguro/taxas do tipo de veículo | `FrotaAPI.precoBase` |
| **Desconto semanal** | Locações | 10% para locações de 7 dias ou mais | `locacoes/dominio/DescontoSemanal.java` |
| **Locação** | Locações | Contrato de uso de um veículo por um cliente, por um período previsto | `Locacao` (raiz do agregado) |
| **Período previsto** | Locações | Quantidade de dias combinada no início | `diasPrevistos` |
| **Devolução** | Locações | Entrega do veículo; encerra a locação | `registrarDevolucao` → `VeiculoDevolvido` |
| **Atraso** | Locações | Dias usados além do período previsto | `MultaPorAtrasoAplicada.diasAtraso` |
| **Multa por atraso** | Locações | 1,5 × diária média × dias de atraso | `Locacao.FATOR_MULTA` |
| **Disponível / alugado** | Frota | Se o veículo pode ser reservado agora | `Veiculo.disponivel()` |
| **Reservar / liberar** | Frota | Marcar o veículo como alugado / disponível | `FrotaAPI.reservar` / `liberar` |
