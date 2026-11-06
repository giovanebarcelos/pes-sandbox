package locadora;

import java.util.Locale;

/** Apresentação em texto separada da regra (SRP) — prepara o terreno da Aula 19 (MVC). */
public final class Relatorio {
    private Relatorio() {
    }

    public static String frota(ServicoLocacao servico) {
        StringBuilder sb = new StringBuilder("=== FROTA ===");
        for (Veiculo v : servico.listar()) {
            String status = v.disponivel() ? "disponível" : "alugado";
            sb.append(String.format(Locale.ROOT, "%n%s %s R$ %.2f/dia (%s)", v.placa(), v.modelo(), v.valorDiaria(), status));
        }
        sb.append(String.format(Locale.ROOT, "%nFaturamento: R$ %.2f", servico.faturamentoTotal()));
        return sb.toString();
    }
}
