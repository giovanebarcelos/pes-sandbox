package locadora.adaptadores.entrada;

import java.util.List;
import java.util.Locale;
import locadora.dominio.Locacao;
import locadora.dominio.Veiculo;

/** MVC — VIEW do terminal: só transforma dados em texto (não decide nada). */
public class VisaoConsole {
    public String frota(List<Veiculo> veiculos) {
        StringBuilder sb = new StringBuilder("PLACA     MODELO            DIÁRIA     STATUS");
        for (Veiculo v : veiculos) {
            String status = v.disponivel() ? "disponível" : "alugado";
            sb.append(String.format(Locale.ROOT, "%n%-9s %-17s R$ %7.2f %s", v.placa(), v.modelo(), v.valorDiaria(), status));
        }
        return sb.toString();
    }

    public String cotacao(String placa, int dias, double valor) {
        return String.format(Locale.ROOT, "Cotação %s por %d dia(s): R$ %.2f", placa, dias, valor);
    }

    public String locacao(Locacao l) {
        return String.format(Locale.ROOT, "OK: %s alugado para %s — R$ %.2f", l.placa(), l.cliente(), l.valor());
    }

    public String mensagem(String texto) {
        return texto;
    }

    public String erro(String texto) {
        return "ERRO: " + texto;
    }

    public String ajuda() {
        return "Comandos: listar | cotar PLACA DIAS | alugar PLACA DIAS NOME EMAIL | devolver PLACA | sair";
    }
}
