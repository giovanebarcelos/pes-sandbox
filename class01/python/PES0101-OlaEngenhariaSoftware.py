"""
PES0101 - Olá, Engenharia de Software
Aula 01: Boas-vindas e Introdução à Engenharia de Software
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Exemplo introdutório que exibe data, versão e uma mensagem de boas-vindas,
ilustrando versionamento e registro de informações básicas de um projeto.
"""

import datetime


def exibir_info_projeto(nome_projeto, versao, autor):
    """Exibe informações de um projeto de software."""
    data_atual = datetime.datetime.now().strftime("%d/%m/%Y %H:%M")
    print("=" * 50)
    print(f"  {nome_projeto}")
    print(f"  Versão: {versao}")
    print(f"  Autor: {autor}")
    print(f"  Data de execução: {data_atual}")
    print("=" * 50)
    print()
    print("Bem-vindo(a) à disciplina de Projeto e Engenharia de Software!")
    print("Neste curso, você aprenderá a gerenciar, projetar e construir")
    print("software com qualidade, aplicando metodologias e boas práticas.")
    print()


if __name__ == "__main__":
    exibir_info_projeto(
        nome_projeto="PES - Projeto e Engenharia de Software",
        versao="1.0.0",
        autor="Prof. Giovane Barcelos"
    )
