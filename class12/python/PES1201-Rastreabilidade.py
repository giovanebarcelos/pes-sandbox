"""
PES1201 - Verificação de Rastreabilidade Requisito ↔ Caso de Uso
Aula 12: Documentação e Validação de Requisitos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Verifica a cobertura: todo requisito está ligado a pelo menos um caso de uso?
E todo caso de uso atende a pelo menos um requisito?
"""


def verificar_rastreabilidade(requisitos, casos_uso, matriz):
    """
    Verifica rastreabilidade bidirecional.
    matriz: dict { req_id: [uc_id, ...] }
    """
    print("=" * 60)
    print("  VERIFICAÇÃO DE RASTREABILIDADE")
    print("=" * 60)

    # Forward trace: todo requisito → pelo menos um caso de uso
    print("\n--- Forward Trace (Requisito → Caso de Uso) ---")
    reqs_sem_cobertura = []
    for req_id in requisitos:
        ucs = matriz.get(req_id, [])
        if not ucs:
            reqs_sem_cobertura.append(req_id)
            print(f"  ⚠️  {req_id}: NÃO coberto por nenhum caso de uso!")
        else:
            print(f"  ✓ {req_id}: coberto por {', '.join(ucs)}")

    # Backward trace: todo caso de uso → pelo menos um requisito
    print("\n--- Backward Trace (Caso de Uso → Requisito) ---")
    uc_cobertos = set()
    for req_id, ucs in matriz.items():
        for uc in ucs:
            uc_cobertos.add(uc)
    for uc_id in casos_uso:
        if uc_id in uc_cobertos:
            print(f"  ✓ {uc_id}: atende a pelo menos 1 requisito")
        else:
            print(f"  ⚠️  {uc_id}: NÃO atende a nenhum requisito!")

    # Resumo
    print(f"\n--- Resumo ---")
    print(f"  Requisitos sem caso de uso: {len(reqs_sem_cobertura)}/{len(requisitos)}")
    print(f"  Casos de uso sem requisito: {len(casos_uso) - len(uc_cobertos)}/{len(casos_uso)}")

    return len(reqs_sem_cobertura) == 0 and len(uc_cobertos) == len(casos_uso)


if __name__ == "__main__":
    requisitos = {
        "RF01": "Login com email/senha",
        "RF02": "Buscar produtos por nome",
        "RF03": "Realizar pedido",
        "RNF01": "Tempo de resposta < 2s",
    }

    casos_uso = {
        "UC01": "Autenticar usuário",
        "UC02": "Buscar produtos",
        "UC03": "Criar pedido",
        "UC04": "Processar pagamento",
    }

    matriz = {
        "RF01": ["UC01"],
        "RF02": ["UC02"],
        "RF03": ["UC03", "UC04"],
        "RNF01": ["UC01", "UC02", "UC03"],
    }

    completo = verificar_rastreabilidade(requisitos, casos_uso, matriz)
    print(f"\n{'✓ Rastreabilidade completa!' if completo else '⚠️  Há lacunas na rastreabilidade.'}")
