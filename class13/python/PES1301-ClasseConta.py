"""
PES1301 - Classe Conta (Encapsulamento)
Aula 13: Introdução à UML e Orientação a Objetos
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Demonstra encapsulamento: atributos protegidos e acesso via métodos,
com validação nas operações (depositar/sacar).
"""


class Conta:
    def __init__(self, titular, saldo_inicial=0.0):
        self.__titular = titular          # atributo privado (encapsulado)
        self.__saldo = float(saldo_inicial)

    @property
    def titular(self):
        return self.__titular

    @property
    def saldo(self):
        return self.__saldo

    def depositar(self, valor):
        if valor <= 0:
            raise ValueError("O valor do depósito deve ser positivo.")
        self.__saldo += valor

    def sacar(self, valor):
        if valor <= 0:
            raise ValueError("O valor do saque deve ser positivo.")
        if valor > self.__saldo:
            raise ValueError("Saldo insuficiente.")
        self.__saldo -= valor

    def __str__(self):
        return f"Conta[titular={self.__titular}, saldo=R$ {self.__saldo:.2f}]"


if __name__ == "__main__":
    conta = Conta("Ana", 100.0)
    conta.depositar(50.0)
    conta.sacar(30.0)
    print(conta)  # Conta[titular=Ana, saldo=R$ 120.00]
