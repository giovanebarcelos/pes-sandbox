"""
Sobe os 3 microsserviços em processos separados — SEM Docker.

Executar (na pasta solucao/):  python -m servicos.iniciar_todos
Parar: Ctrl+C (encerra os três).

Dica para os experimentos de falha do tutorial: em vez deste script, abra
3 terminais e rode cada serviço separadamente — assim dá para derrubar um só.
"""

import os
import subprocess
import sys
import time
import urllib.request

SERVICOS = [
    ("frota", "servicos.frota_servico", 8001),
    ("notificacao", "servicos.notificacao_servico", 8003),
    ("locacoes", "servicos.locacoes_servico", 8002),
]


def esperar(url, segundos=10):
    limite = time.time() + segundos
    while time.time() < limite:
        try:
            urllib.request.urlopen(url, timeout=0.5).close()
            return True
        except OSError:
            time.sleep(0.2)
    return False


def main():
    processos = []
    try:
        for nome, modulo, porta in SERVICOS:
            env = dict(os.environ, PORTA=str(porta))
            processos.append(subprocess.Popen([sys.executable, "-m", modulo], env=env))
            ok = esperar(f"http://localhost:{porta}/saude")
            print(f"  {nome:<12} http://localhost:{porta}  {'OK' if ok else 'NÃO RESPONDEU'}", flush=True)
        print("\nServiços no ar. Em outro terminal:  python -m servicos.demo_cliente   (Ctrl+C aqui para parar)")
        for p in processos:
            p.wait()
    except KeyboardInterrupt:
        pass
    finally:
        for p in processos:
            p.terminate()


if __name__ == "__main__":
    main()
