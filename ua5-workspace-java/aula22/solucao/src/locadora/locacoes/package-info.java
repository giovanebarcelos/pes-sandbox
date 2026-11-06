/**
 * Bounded context LOCAÇÕES — "o veículo como item de um contrato": locação, prazo, multa.
 * Event Sourcing (o estado é a soma dos eventos) + CQRS (escrita e leitura separadas).
 * Não importa nada de locadora.frota: fala com a Frota pela porta ServicoFrota.
 */
package locadora.locacoes;
