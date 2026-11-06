"""MVP: o Presenter é testado com uma View FALSA — prova de que a View é passiva."""

import unittest

from locadora.apresentacao.montagem import servico_exemplo
from locadora.apresentacao.mvp import LocadoraPresenter, LocadoraViewInterface


class ViewFalsa(LocadoraViewInterface):
    def __init__(self, placa="", dias="", cliente="", email=""):
        self.campos = dict(placa=placa, dias=dias, cliente=cliente, email=email)
        self.frota, self.sucessos, self.erros = [], [], []

    def obter_placa(self):
        return self.campos["placa"]

    def obter_dias(self):
        return self.campos["dias"]

    def obter_cliente(self):
        return self.campos["cliente"]

    def obter_email(self):
        return self.campos["email"]

    def mostrar_frota(self, linhas):
        self.frota = linhas

    def mostrar_sucesso(self, mensagem):
        self.sucessos.append(mensagem)

    def mostrar_erro(self, mensagem):
        self.erros.append(mensagem)


class TestPresenter(unittest.TestCase):
    def presenter(self, **campos):
        view = ViewFalsa(**campos)
        return view, LocadoraPresenter(view, servico_exemplo(silencioso=True))

    def test_carrega_frota_formatada(self):
        view, p = self.presenter()
        p.carregar_frota()
        self.assertIn("[ ] ABC-1234 Fiat Uno — R$ 120.00/dia", view.frota)

    def test_alugar_com_sucesso_atualiza_frota(self):
        view, p = self.presenter(placa="abc-1234", dias="5", cliente="Ana", email="a@x.com")
        p.ao_clicar_alugar()
        self.assertEqual(view.sucessos, ["ABC-1234 alugado para Ana: R$ 660.00"])
        self.assertIn("[X] ABC-1234 Fiat Uno — R$ 120.00/dia", view.frota)

    def test_validacoes_de_tela_ficam_no_presenter(self):
        casos = [
            (dict(placa="", dias="1", cliente="Ana", email="a@x"), "Informe placa e cliente"),
            (dict(placa="ABC-1234", dias="1", cliente="Ana", email="sem-arroba"), "E-mail inválido"),
            (dict(placa="ABC-1234", dias="zero", cliente="Ana", email="a@x"),
             "Dias deve ser um número inteiro maior que zero"),
        ]
        for campos, erro in casos:
            with self.subTest(erro=erro):
                view, p = self.presenter(**campos)
                p.ao_clicar_alugar()
                self.assertEqual(view.erros, [erro])

    def test_erro_de_negocio_vira_mensagem(self):
        view, p = self.presenter(placa="NAO-0000", dias="1", cliente="Ana", email="a@x")
        p.ao_clicar_alugar()
        self.assertIn("não encontrado", view.erros[0])


if __name__ == "__main__":
    unittest.main()
