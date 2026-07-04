"""
PES1101 - Classificador RF/RNF (FURPS+)
Aula 11: Requisitos Funcionais e Não Funcionais
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Classifica requisitos como Funcionais (RF) ou Não Funcionais (RNF)
usando palavras-chave do modelo FURPS+.
"""

CATEGORIAS_RNF = {
    "desempenho": ["tempo", "segundo", "milissegundo", "rápido", "simultâneo",
                   "concorrente", "vazão", "throughput", "latência"],
    "usabilidade": ["fácil", "intuitivo", "acessível", "usabilidade", "UX",
                    "interface", "acessibilidade", "WCAG"],
    "confiabilidade": ["disponível", "tolerante", "falha", "recuperação",
                       "backup", "redundante", "uptime", "MTBF"],
    "seguranca": ["criptografado", "autenticado", "autorizado", "senha",
                  "token", "permissão", "LGPD", "GDPR", "log", "auditoria"],
    "suportabilidade": ["compatível", "portável", "multiplataforma", "navegador",
                        "sistema operacional", "SO"],
}


def classificar_requisito(descricao):
    """Classifica um requisito como RF ou RNF com categoria FURPS+."""
    texto = descricao.lower()

    for categoria, palavras in CATEGORIAS_RNF.items():
        if any(p in texto for p in palavras):
            return "RNF", categoria

    # Se descreve uma ação (verbo no início), provavelmente é RF
    return "RF", "funcional"


if __name__ == "__main__":
    requisitos = [
        "O sistema deve permitir cadastro de usuários com email e senha",
        "O tempo de resposta para qualquer operação deve ser inferior a 2 segundos",
        "A interface deve ser intuitiva e acessível conforme WCAG 2.1",
        "O sistema deve estar disponível 99.9% do tempo (uptime)",
        "Todos os dados devem ser criptografados em repouso e em trânsito",
        "O sistema deve gerar relatório mensal de vendas em PDF",
        "O sistema deve ser compatível com Chrome, Firefox e Safari",
        "O sistema deve suportar até 1000 usuários simultâneos",
    ]

    print("=" * 60)
    print("  CLASSIFICADOR RF / RNF (FURPS+)")
    print("=" * 60)
    print()

    print(f"{'Tipo':<6} {'Categoria FURPS+':<20} {'Requisito'}")
    print("-" * 80)
    for req in requisitos:
        tipo, categoria = classificar_requisito(req)
        icone = "⚙️" if tipo == "RF" else "🔧"
        print(f"{icone} {tipo:<3} {categoria:<20} {req}")
