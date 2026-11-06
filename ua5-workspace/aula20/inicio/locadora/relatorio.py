"""Apresentação em texto separada da regra (SRP) — prepara o terreno da Aula 19 (MVC)."""


def relatorio_frota(servico):
    linhas = ["=== FROTA ==="]
    for v in servico.listar():
        status = "disponível" if v.disponivel else "alugado"
        linhas.append(f"{v.placa} {v.modelo} R$ {v.valor_diaria:.2f}/dia ({status})")
    linhas.append(f"Faturamento: R$ {servico.faturamento_total():.2f}")
    return "\n".join(linhas)
