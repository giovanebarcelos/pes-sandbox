/**
 * MICROSSERVIÇOS da Locadora (Aula 22) — só o JDK.
 *   FrotaServico        porta 8001 — contexto Gestão de Frota (estado atual)
 *   LocacoesServico     porta 8002 — contexto Locações (CQRS + Event Sourcing)
 *   NotificacaoServico  porta 8003 — recebe avisos de forma ASSÍNCRONA (outbox)
 *
 * Cada serviço tem os SEUS dados (um serviço, um banco). Locações chama a Frota de
 * forma SÍNCRONA (precisa da resposta) e avisa a Notificação de forma ASSÍNCRONA.
 * O contrato HTTP/JSON é o MESMO da versão Python: dá para misturar serviços das duas.
 */
package servicos;
