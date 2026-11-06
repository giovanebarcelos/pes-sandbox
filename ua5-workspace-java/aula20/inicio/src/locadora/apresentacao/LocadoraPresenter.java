package locadora.apresentacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import locadora.Locacao;
import locadora.ServicoLocacao;
import locadora.Veiculo;

/**
 * MVP — a View é PASSIVA e conversa com o Presenter por uma interface.
 *
 *   View      -> implementa LocadoraView (só lê campos e mostra coisas)
 *   Presenter -> LocadoraPresenter: TODA a lógica de apresentação
 *                (validação de campos, formatação, o que mostrar em caso de erro)
 *   Model     -> ServicoLocacao
 */
public class LocadoraPresenter {
    private final LocadoraView view;
    private final ServicoLocacao modelo;

    public LocadoraPresenter(LocadoraView view, ServicoLocacao modelo) {
        this.view = view;
        this.modelo = modelo;
    }

    public void carregarFrota() {
        List<String> linhas = new ArrayList<>();
        for (Veiculo v : modelo.listar()) {
            String marca = v.disponivel() ? "[ ]" : "[X]";
            linhas.add(String.format(Locale.ROOT, "%s %s %s — R$ %.2f/dia", marca, v.placa(), v.modelo(), v.valorDiaria()));
        }
        view.mostrarFrota(linhas);
    }

    public void aoClicarAlugar() {
        String placa = view.obterPlaca().trim().toUpperCase();
        String cliente = view.obterCliente().trim();
        String email = view.obterEmail().trim();
        String diasTxt = view.obterDias().trim();
        if (placa.isEmpty() || cliente.isEmpty()) {
            view.mostrarErro("Informe placa e cliente");
            return;
        }
        if (!email.contains("@")) {
            view.mostrarErro("E-mail inválido");
            return;
        }
        if (!diasTxt.matches("\\d+") || Integer.parseInt(diasTxt) < 1) {
            view.mostrarErro("Dias deve ser um número inteiro maior que zero");
            return;
        }
        Locacao loc;
        try {
            loc = modelo.alugar(placa, cliente, email, Integer.parseInt(diasTxt));
        } catch (NoSuchElementException | IllegalStateException e) {
            view.mostrarErro(e.getMessage());
            return;
        }
        view.mostrarSucesso(String.format(Locale.ROOT, "%s alugado para %s: R$ %.2f", loc.placa(), loc.cliente(), loc.valor()));
        carregarFrota();
    }
}
