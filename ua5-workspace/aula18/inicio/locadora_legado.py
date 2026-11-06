"""
Locadora de Veículos — VERSÃO LEGADA (ponto de partida da Aula 18)
Projeto e Engenharia de Software - Prof. Giovane Barcelos

Origem: regras de negócio da Aula 17 (repository/class17/python/PES1701-Locadora.py)
  - Carro: diária × dias × 1.10 (+10% de seguro)
  - Moto:  diária × dias
  + regras novas pedidas pelo "cliente" depois da Aula 17:
  - Locação de 7 dias ou mais ganha 10% de desconto
  - Cada locação gera um "e-mail" de confirmação ao cliente
  - A frota é gravada em um arquivo JSON

Este código FUNCIONA, mas concentra tudo em uma única classe.
Sua tarefa no tutorial é encontrar as violações SOLID e refatorar
sem mudar o comportamento (os testes em tests/ provam isso).

Executar:  python locadora_legado.py
"""

import json


class Locadora:
    def __init__(self, arquivo="frota.json"):
        self.veiculos = []      # lista de dicionários
        self.locacoes = []
        self.arquivo = arquivo
        self.emails_enviados = []

    # ---------------- cadastro + persistência ----------------
    def cadastrar(self, tipo, placa, modelo, diaria):
        if tipo not in ("carro", "moto"):
            raise ValueError(f"Tipo desconhecido: {tipo}")
        self.veiculos.append({
            "tipo": tipo, "placa": placa, "modelo": modelo,
            "diaria": float(diaria), "disponivel": True,
        })
        self.salvar()

    def salvar(self):
        with open(self.arquivo, "w", encoding="utf-8") as f:
            json.dump(self.veiculos, f, ensure_ascii=False, indent=2)

    # ---------------- regra de preço ----------------
    def calcular(self, veiculo, dias):
        if veiculo["tipo"] == "carro":
            valor = veiculo["diaria"] * dias * 1.10
        elif veiculo["tipo"] == "moto":
            valor = veiculo["diaria"] * dias
        else:
            raise ValueError("Tipo desconhecido")
        if dias >= 7:
            valor = valor * 0.90
        return round(valor, 2)

    # ---------------- caso de uso ----------------
    def alugar(self, placa, cliente, email, dias):
        veiculo = None
        for v in self.veiculos:
            if v["placa"] == placa:
                veiculo = v
        if veiculo is None:
            raise KeyError(f"Veículo {placa} não encontrado")
        if not veiculo["disponivel"]:
            raise RuntimeError(f"Veículo {placa} já está alugado")
        valor = self.calcular(veiculo, dias)
        veiculo["disponivel"] = False
        self.locacoes.append({"placa": placa, "cliente": cliente,
                              "dias": dias, "valor": valor})
        self.salvar()
        # "envio de e-mail" acoplado ao caso de uso
        mensagem = (f"Olá {cliente}, sua locação do {veiculo['modelo']} "
                    f"por {dias} dia(s) custa R$ {valor:.2f}.")
        print(f"[SMTP] para {email}: {mensagem}")
        self.emails_enviados.append((email, mensagem))
        return valor

    def devolver(self, placa):
        for v in self.veiculos:
            if v["placa"] == placa:
                v["disponivel"] = True
                self.salvar()
                return
        raise KeyError(f"Veículo {placa} não encontrado")

    # ---------------- apresentação ----------------
    def relatorio(self):
        linhas = ["=== FROTA ==="]
        for v in self.veiculos:
            status = "disponível" if v["disponivel"] else "alugado"
            linhas.append(f"{v['placa']} {v['modelo']} R$ {v['diaria']:.2f}/dia ({status})")
        total = sum(l["valor"] for l in self.locacoes)
        linhas.append(f"Faturamento: R$ {total:.2f}")
        return "\n".join(linhas)


if __name__ == "__main__":
    loc = Locadora()
    loc.cadastrar("carro", "ABC-1234", "Fiat Uno", 120)
    loc.cadastrar("carro", "DEF-5678", "Honda Civic", 200)
    loc.cadastrar("moto", "XYZ-9012", "Honda CG 160", 80)
    loc.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5)
    loc.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7)
    print(loc.relatorio())
