"""
PES2701 - Geração Automática de Documentação (Docstrings)
Aula 27: Documentação Técnica e Projeto Integrador Final
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra documentação inline com docstrings formatadas e extração
automática de informações para documentação, usando como exemplo o
cálculo de locação de veículos do Projeto Integrador (Sistema Locadora).
"""

import inspect


class Veiculo:
    """Classe base para veículos disponíveis na locadora.

    Attributes:
        placa: Placa de identificação do veículo.
        modelo: Nome/modelo do veículo.
        valor_diaria: Valor cobrado por dia de locação.
    """

    def __init__(self, placa, modelo, valor_diaria):
        self.placa = placa
        self.modelo = modelo
        self.valor_diaria = valor_diaria

    def calcular_locacao(self, dias):
        """Calcula o valor da locação deste veículo para um número de dias.

        Args:
            dias: Número de dias da locação.

        Returns:
            float: Valor total (valor_diaria * dias).
        """
        return self.valor_diaria * dias


class Carro(Veiculo):
    """Veículo do tipo carro, com número de portas."""

    def __init__(self, placa, modelo, valor_diaria, portas):
        super().__init__(placa, modelo, valor_diaria)
        self.portas = portas


class Moto(Veiculo):
    """Veículo do tipo moto, com cilindrada."""

    def __init__(self, placa, modelo, valor_diaria, cilindrada):
        super().__init__(placa, modelo, valor_diaria)
        self.cilindrada = cilindrada


def calcular_locacao(veiculo: Veiculo, dias: int) -> float:
    """
    Calcula o valor total da locação de um veículo.

    Args:
        veiculo: Instância de Veiculo (Carro ou Moto).
        dias: Número de dias da locação (deve ser >= 1).

    Returns:
        float: Valor total da locação em reais.

    Raises:
        ValueError: Se dias < 1.
        TypeError: Se veiculo não for instância de Veiculo.

    Example:
        >>> carro = Carro("ABC-1234", "Gol", 100.0, 4)
        >>> calcular_locacao(carro, 5)
        500.0
    """
    if not isinstance(veiculo, Veiculo):
        raise TypeError("veiculo deve ser uma instância de Veiculo")
    if dias < 1:
        raise ValueError("dias deve ser >= 1")
    return veiculo.calcular_locacao(dias)


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


if __name__ == "__main__":
    import __main__

    print("=" * 60)
    print("  DOCUMENTAÇÃO EXTRAÍDA AUTOMATICAMENTE")
    print("=" * 60)

    carro = Carro("ABC-1234", "Gol", 100.0, 4)
    moto = Moto("XYZ-5678", "CG 160", 60.0, 160)

    print(f"\nLocação do carro por 5 dias: R$ {calcular_locacao(carro, 5):.2f}")
    print(f"Locação da moto por 3 dias: R$ {calcular_locacao(moto, 3):.2f}")

    docs = extrair_docstrings(__main__)
    for item in docs:
        print(f"\n--- {item['nome']}{item['assinatura']} ---")
        print(f"  {item['docstring']}")

    print(f"\n✓ {len(docs)} funções documentadas.")
    print("✓ Docstrings bem escritas são a base de uma boa documentação técnica.")
