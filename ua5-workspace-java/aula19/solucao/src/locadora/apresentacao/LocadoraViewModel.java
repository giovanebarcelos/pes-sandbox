package locadora.apresentacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import locadora.Locacao;
import locadora.ServicoLocacao;
import locadora.Veiculo;

/**
 * MVVM — a View se LIGA (data binding) a propriedades observáveis do ViewModel.
 *
 *   ViewModel -> estado da tela (placa, dias, cotacao, mensagem, frota) +
 *                comandos (alugar). Não conhece a View.
 *   View      -> se inscreve nas propriedades e se redesenha sozinha quando mudam.
 *   Model     -> ServicoLocacao
 */
public class LocadoraViewModel {
    private final ServicoLocacao modelo;
    public final Observavel<String> placa = new Observavel<>("");
    public final Observavel<Integer> dias = new Observavel<>(1);
    public final Observavel<String> cliente = new Observavel<>("");
    public final Observavel<String> email = new Observavel<>("");
    public final Observavel<String> cotacao = new Observavel<>("");
    public final Observavel<String> mensagem = new Observavel<>("");
    public final Observavel<List<String>> frota = new Observavel<>(List.of());
    public final Observavel<Boolean> podeAlugar = new Observavel<>(false);

    public LocadoraViewModel(ServicoLocacao modelo) {
        this.modelo = modelo;
        placa.inscrever(v -> recalcular());
        dias.inscrever(v -> recalcular());
        cliente.inscrever(v -> recalcular());
        email.inscrever(v -> recalcular());
        atualizarFrota();
    }

    /** Estado derivado: cotação e habilitação do botão mudam a cada digitação. */
    private void recalcular() {
        try {
            cotacao.set(String.format(Locale.ROOT, "R$ %.2f", modelo.cotar(placa.get(), dias.get())));
        } catch (NoSuchElementException | IllegalArgumentException | NullPointerException e) {
            cotacao.set("—");
        }
        podeAlugar.set(!cotacao.get().equals("—") && !cliente.get().isEmpty() && email.get().contains("@"));
    }

    private void atualizarFrota() {
        List<String> linhas = new ArrayList<>();
        for (Veiculo v : modelo.listar()) {
            linhas.add(v.placa() + " " + v.modelo() + " (" + (v.disponivel() ? "disponível" : "alugado") + ")");
        }
        frota.set(linhas);
    }

    /** Comando: o "botão" da tela se liga a este método. */
    public void comandoAlugar() {
        if (!podeAlugar.get()) {
            mensagem.set("Preencha placa válida, cliente e e-mail");
            return;
        }
        try {
            Locacao loc = modelo.alugar(placa.get(), cliente.get(), email.get(), dias.get());
            mensagem.set(String.format(Locale.ROOT, "OK: %s alugado — R$ %.2f", loc.placa(), loc.valor()));
        } catch (NoSuchElementException | IllegalStateException e) {
            mensagem.set("ERRO: " + e.getMessage());
        }
        atualizarFrota();
        recalcular();
    }
}
