package locadora.apresentacao;

import static teste.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import teste.Test;

/** MVP: o Presenter é testado com uma View FALSA — prova de que a View é passiva. */
class MvpTest {
    /** View falsa: devolve campos fixos e guarda o que o Presenter mandou mostrar. */
    static class ViewFalsa implements LocadoraView {
        final String placa, dias, cliente, email;
        List<String> frota = new ArrayList<>();
        final List<String> sucessos = new ArrayList<>();
        final List<String> erros = new ArrayList<>();

        ViewFalsa(String placa, String dias, String cliente, String email) {
            this.placa = placa;
            this.dias = dias;
            this.cliente = cliente;
            this.email = email;
        }

        public String obterPlaca() { return placa; }
        public String obterDias() { return dias; }
        public String obterCliente() { return cliente; }
        public String obterEmail() { return email; }
        public void mostrarFrota(List<String> linhas) { frota = linhas; }
        public void mostrarSucesso(String mensagem) { sucessos.add(mensagem); }
        public void mostrarErro(String mensagem) { erros.add(mensagem); }
    }

    private static LocadoraPresenter presenter(ViewFalsa view) {
        return new LocadoraPresenter(view, Montagem.servicoExemplo(true));
    }

    @Test
    void carrega_frota_formatada() {
        ViewFalsa view = new ViewFalsa("", "", "", "");
        presenter(view).carregarFrota();
        assertTrue(view.frota.contains("[ ] ABC-1234 Fiat Uno — R$ 120.00/dia"), view.frota.toString());
    }

    @Test
    void alugar_com_sucesso_atualiza_frota() {
        ViewFalsa view = new ViewFalsa("abc-1234", "5", "Ana", "a@x.com");
        presenter(view).aoClicarAlugar();
        assertEquals(List.of("ABC-1234 alugado para Ana: R$ 660.00"), view.sucessos);
        assertTrue(view.frota.contains("[X] ABC-1234 Fiat Uno — R$ 120.00/dia"), view.frota.toString());
    }

    @Test
    void validacoes_de_tela_ficam_no_presenter() {
        Object[][] casos = {
            {new ViewFalsa("", "1", "Ana", "a@x"), "Informe placa e cliente"},
            {new ViewFalsa("ABC-1234", "1", "Ana", "sem-arroba"), "E-mail inválido"},
            {new ViewFalsa("ABC-1234", "zero", "Ana", "a@x"), "Dias deve ser um número inteiro maior que zero"},
        };
        for (Object[] caso : casos) {
            ViewFalsa view = (ViewFalsa) caso[0];
            presenter(view).aoClicarAlugar();
            assertEquals(List.of(caso[1]), view.erros);
        }
    }

    @Test
    void erro_de_negocio_vira_mensagem() {
        ViewFalsa view = new ViewFalsa("NAO-0000", "1", "Ana", "a@x");
        presenter(view).aoClicarAlugar();
        assertTrue(view.erros.get(0).contains("não encontrado"), view.erros.toString());
    }
}
