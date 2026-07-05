"""
PES2701 - Geração Automática de Documentação (Docstrings)
Aula 27: Documentação Técnica e Projeto Integrador Final
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra documentação inline com docstrings formatadas e
extração automática de informações para documentação.
"""

import inspect


def extrair_docstrings(modulo_ou_classe):
    """
    Extrai docstrings de todas as funções e métodos públicos
    de um módulo ou classe.

    Args:
        modulo_ou_classe: Módulo ou classe a ser inspecionado.

    Returns:
        List[dict]: Lista com nome, docstring e assinatura de cada callable.
    """
    resultado = []
    for nome, obj in inspect.getmembers(modulo_ou_classe):
        if nome.startswith("_"):
            continue
        if callable(obj):
            try:
                sig = str(inspect.signature(obj))
            except (ValueError, TypeError):
                sig = "()"
            doc = inspect.getdoc(obj) or "(sem documentação)"
            resultado.append({"nome": nome, "assinatura": sig, "docstring": doc})
    return resultado


# ===== Funções de exemplo com docstrings =====


def calcular_frete(peso_kg, distancia_km, urgente=False):
    """
    Calcula o valor do frete com base no peso e distância.

    Args:
        peso_kg (float): Peso do pacote em quilogramas.
        distancia_km (float): Distância da entrega em quilômetros.
        urgente (bool): Se True, adiciona taxa de urgência de 50%.

    Returns:
        float: Valor do frete calculado.
    """
    base = 10.0 + (peso_kg * 2.5) + (distancia_km * 0.50)
    return base * 1.5 if urgente else base


def validar_email(email):
    """
    Valida se uma string tem formato de email.

    Args:
        email (str): String a ser validada.

    Returns:
        bool: True se o formato for válido, False caso contrário.
    """
    import re
    padrao = r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
    return bool(re.match(padrao, email))


if __name__ == "__main__":
    import __main__

    print("=" * 60)
    print("  DOCUMENTAÇÃO EXTRAÍDA AUTOMATICAMENTE")
    print("=" * 60)

    docs = extrair_docstrings(__main__)

    for item in docs:
        print(f"\n--- {item['nome']}{item['assinatura']} ---")
        print(f"  {item['docstring']}")

    print(f"\n✓ {len(docs)} funções documentadas.")
    print("✓ Docstrings bem escritas são a base de uma boa documentação técnica.")
