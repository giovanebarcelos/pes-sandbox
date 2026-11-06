"""Repositório em memória da frota (o SQLite da Aula 20 poderia ser plugado aqui)."""


class VeiculoMemoriaRepository:
    def __init__(self):
        self._dados = {}

    def salvar(self, veiculo):
        self._dados[veiculo.placa] = veiculo

    def buscar_por_placa(self, placa):
        return self._dados.get(placa)

    def listar(self):
        return list(self._dados.values())
