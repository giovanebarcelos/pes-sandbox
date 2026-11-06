"""
Adaptador da porta ServicoFrota que chama o MICROSSERVIÇO de Frota por HTTP (Aula 22).

Compare com frota_local.py: mesma porta, mesmos erros de saída — o
ManipuladorComandos não sabe se a Frota está no mesmo processo ou do outro
lado da rede. É o retorno do investimento em Hexagonal (Aula 20) + ACL (Aula 21).
"""

import json
import urllib.error
import urllib.request
from urllib.parse import quote

from ..dominio.erros import FrotaForaDoAr, VeiculoDesconhecido, VeiculoNaoDisponivel
from ..dominio.portas import ServicoFrota


class FrotaHttp(ServicoFrota):
    def __init__(self, url_base, timeout=2.0):
        self._url = url_base.rstrip("/")
        self._timeout = timeout

    def preco_base(self, placa, dias):
        return self._chamar("GET", f"/v1/veiculos/{quote(placa)}/preco?dias={int(dias)}")["preco_base"]

    def reservar(self, placa):
        self._chamar("POST", f"/v1/veiculos/{quote(placa)}/reserva")

    def liberar(self, placa):
        self._chamar("DELETE", f"/v1/veiculos/{quote(placa)}/reserva")

    def _chamar(self, metodo, caminho):
        req = urllib.request.Request(self._url + caminho, method=metodo, data=b"" if metodo != "GET" else None)
        try:
            with urllib.request.urlopen(req, timeout=self._timeout) as r:
                return json.loads(r.read() or b"{}")
        except urllib.error.HTTPError as e:
            erro = json.loads(e.read() or b"{}").get("erro", str(e))
            if e.code == 404:
                raise VeiculoDesconhecido(erro) from None
            if e.code == 409:
                raise VeiculoNaoDisponivel(erro) from None
            raise FrotaForaDoAr(f"Frota respondeu {e.code}: {erro}") from None
        except (urllib.error.URLError, TimeoutError, ConnectionError) as e:
            raise FrotaForaDoAr(f"Frota inacessível em {self._url}: {e}") from None
