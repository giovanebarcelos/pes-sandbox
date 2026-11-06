"""
Mini "curl" multiplataforma para os experimentos do tutorial (Windows, Linux, macOS).

Uso (na pasta solucao/ ou meu/):
  python -m servicos.chamar GET  http://localhost:8001/v1/veiculos
  python -m servicos.chamar POST http://localhost:8002/v1/locacoes "{\"placa\":\"ABC-1234\",\"cliente\":\"Ana\",\"dias\":5}"
  python -m servicos.chamar POST http://localhost:8002/v1/locacoes placa=ABC-1234 cliente=Ana dias=5
"""

import json
import sys
import urllib.error
import urllib.request


def main():
    if len(sys.argv) < 3:
        sys.exit(__doc__)
    metodo, url, resto = sys.argv[1].upper(), sys.argv[2], sys.argv[3:]
    corpo = None
    if resto:
        if resto[0].lstrip().startswith("{"):
            corpo = json.loads(" ".join(resto))
        else:  # formato chave=valor (mais fácil de digitar no PowerShell)
            corpo = {}
            for par in resto:
                k, v = par.split("=", 1)
                corpo[k] = int(v) if v.isdigit() else v
    dados = json.dumps(corpo).encode() if corpo is not None else (b"" if metodo != "GET" else None)
    req = urllib.request.Request(url, data=dados, method=metodo, headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=5) as r:
            status, texto = r.status, r.read().decode("utf-8")
    except urllib.error.HTTPError as e:
        status, texto = e.code, e.read().decode("utf-8")
    except urllib.error.URLError as e:
        sys.exit(f"Sem resposta de {url}: {e.reason}")
    print(f"HTTP {status}")
    try:
        print(json.dumps(json.loads(texto), ensure_ascii=False, indent=2))
    except json.JSONDecodeError:
        print(texto)


if __name__ == "__main__":
    main()
