import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Locadora de Veículos — VERSÃO LEGADA (ponto de partida da Aula 18)
 * Projeto e Engenharia de Software - Prof. Giovane Barcelos
 *
 * Origem: regras de negócio da Aula 17 (repository/class17/java/PES1701-Locadora.java)
 *   - Carro: diária × dias × 1.10 (+10% de seguro)
 *   - Moto:  diária × dias
 *   + regras novas pedidas pelo "cliente" depois da Aula 17:
 *   - Locação de 7 dias ou mais ganha 10% de desconto
 *   - Cada locação gera um "e-mail" de confirmação ao cliente
 *   - A frota é gravada em um arquivo CSV (frota.csv)
 *
 * Este código FUNCIONA, mas concentra tudo em uma única classe.
 * Sua tarefa no tutorial é encontrar as violações SOLID e refatorar
 * sem mudar o comportamento (os testes em test/ provam isso).
 *
 * Executar (na pasta da aula):  java ../../ferramentas/Rodar.java LocadoraLegado
 */
public class LocadoraLegado {
    // "registros" soltos em mapas: nenhum tipo protege as regras
    final List<Map<String, Object>> veiculos = new ArrayList<>();
    final List<Map<String, Object>> locacoes = new ArrayList<>();
    final List<String[]> emailsEnviados = new ArrayList<>();
    private final String arquivo;

    public LocadoraLegado(String arquivo) {
        this.arquivo = arquivo;
    }

    // ---------------- cadastro + persistência ----------------
    public void cadastrar(String tipo, String placa, String modelo, double diaria) {
        if (!tipo.equals("carro") && !tipo.equals("moto")) {
            throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        }
        Map<String, Object> v = new HashMap<>();
        v.put("tipo", tipo);
        v.put("placa", placa);
        v.put("modelo", modelo);
        v.put("diaria", diaria);
        v.put("disponivel", true);
        veiculos.add(v);
        salvar();
    }

    public void salvar() {
        StringBuilder csv = new StringBuilder("tipo;placa;modelo;diaria;disponivel\n");
        for (Map<String, Object> v : veiculos) {
            csv.append(v.get("tipo")).append(';').append(v.get("placa")).append(';')
               .append(v.get("modelo")).append(';').append(v.get("diaria")).append(';')
               .append(v.get("disponivel")).append('\n');
        }
        try {
            Files.writeString(Path.of(arquivo), csv, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ---------------- regra de preço ----------------
    public double calcular(Map<String, Object> veiculo, int dias) {
        double diaria = (double) veiculo.get("diaria");
        double valor;
        if (veiculo.get("tipo").equals("carro")) {
            valor = diaria * dias * 1.10;
        } else if (veiculo.get("tipo").equals("moto")) {
            valor = diaria * dias;
        } else {
            throw new IllegalArgumentException("Tipo desconhecido");
        }
        if (dias >= 7) {
            valor = valor * 0.90;
        }
        return Math.round(valor * 100) / 100.0;
    }

    // ---------------- caso de uso ----------------
    public double alugar(String placa, String cliente, String email, int dias) {
        Map<String, Object> veiculo = null;
        for (Map<String, Object> v : veiculos) {
            if (v.get("placa").equals(placa)) {
                veiculo = v;
            }
        }
        if (veiculo == null) {
            throw new NoSuchElementException("Veículo " + placa + " não encontrado");
        }
        if (!(boolean) veiculo.get("disponivel")) {
            throw new IllegalStateException("Veículo " + placa + " já está alugado");
        }
        double valor = calcular(veiculo, dias);
        veiculo.put("disponivel", false);
        Map<String, Object> locacao = new HashMap<>();
        locacao.put("placa", placa);
        locacao.put("cliente", cliente);
        locacao.put("dias", dias);
        locacao.put("valor", valor);
        locacoes.add(locacao);
        salvar();
        // "envio de e-mail" acoplado ao caso de uso
        String mensagem = String.format(Locale.ROOT, "Olá %s, sua locação do %s por %d dia(s) custa R$ %.2f.",
                cliente, veiculo.get("modelo"), dias, valor);
        System.out.println("[SMTP] para " + email + ": " + mensagem);
        emailsEnviados.add(new String[] {email, mensagem});
        return valor;
    }

    public void devolver(String placa) {
        for (Map<String, Object> v : veiculos) {
            if (v.get("placa").equals(placa)) {
                v.put("disponivel", true);
                salvar();
                return;
            }
        }
        throw new NoSuchElementException("Veículo " + placa + " não encontrado");
    }

    // ---------------- apresentação ----------------
    public String relatorio() {
        StringBuilder sb = new StringBuilder("=== FROTA ===");
        for (Map<String, Object> v : veiculos) {
            String status = (boolean) v.get("disponivel") ? "disponível" : "alugado";
            sb.append(String.format(Locale.ROOT, "%n%s %s R$ %.2f/dia (%s)",
                    v.get("placa"), v.get("modelo"), (double) v.get("diaria"), status));
        }
        double total = 0;
        for (Map<String, Object> l : locacoes) {
            total += (double) l.get("valor");
        }
        sb.append(String.format(Locale.ROOT, "%nFaturamento: R$ %.2f", total));
        return sb.toString();
    }

    public static void main(String[] args) {
        LocadoraLegado loc = new LocadoraLegado("frota.csv");
        loc.cadastrar("carro", "ABC-1234", "Fiat Uno", 120);
        loc.cadastrar("carro", "DEF-5678", "Honda Civic", 200);
        loc.cadastrar("moto", "XYZ-9012", "Honda CG 160", 80);
        loc.alugar("ABC-1234", "Ana Silva", "ana@exemplo.com", 5);
        loc.alugar("XYZ-9012", "Carlos Santos", "carlos@exemplo.com", 7);
        System.out.println(loc.relatorio());
    }
}
