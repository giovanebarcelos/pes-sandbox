"""
MICROSSERVIÇOS da Locadora (Aula 22) — só biblioteca padrão.

  frota_servico.py       porta 8001  — contexto Gestão de Frota (estado atual)
  locacoes_servico.py    porta 8002  — contexto Locações (CQRS + Event Sourcing)
  notificacao_servico.py porta 8003  — recebe avisos de forma ASSÍNCRONA (outbox)

Cada serviço tem os SEUS dados (um serviço, um banco). Locações chama a Frota de
forma SÍNCRONA (precisa da resposta) e avisa a Notificação de forma ASSÍNCRONA.
"""
