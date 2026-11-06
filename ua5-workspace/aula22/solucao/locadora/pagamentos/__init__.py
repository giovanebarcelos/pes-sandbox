"""
Módulo PAGAMENTOS — arquitetura MICROKERNEL (plugins).

O núcleo (nucleo.py) não conhece nenhuma forma de pagamento. Cada forma é um
plugin em plugins/*.py que expõe `PLUGIN`. Para aceitar boleto, basta criar
plugins/boleto.py — sem alterar uma linha do núcleo (OCP em nível de arquitetura).
"""
