package locadora.locacoes.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import locadora.compartilhado.Dinheiro;
import locadora.compartilhado.EventoDominio;
import locadora.compartilhado.Placa;

/**
 * AGREGADO Locacao com EVENT SOURCING.
 *
 * - Não há "UPDATE": cada mudança é um evento novo (registrar -> aplicar).
 * - O estado atual é reconstruído aplicando os eventos em ordem (reconstituir).
 * - Invariantes (regras que nunca podem ser quebradas) são verificadas AQUI:
 *     * pelo menos 1 dia; devolução não pode ser antes do início
 *     * não se devolve duas vezes
 *     * atraso gera multa de 1,5 × diária média por dia (atividade do Slide 30)
 */
public class Locacao {
    public static final double FATOR_MULTA = 1.5;

    private final String id;
    private int versao;                                    // quantos eventos já foram aplicados
    private final List<EventoDominio> pendentes = new ArrayList<>(); // eventos novos, ainda não gravados
    private String placa;
    private String cliente;
    private LocalDate dataInicio;
    private int diasPrevistos;
    private Dinheiro valorPrevisto = new Dinheiro(0);
    private Dinheiro multa = new Dinheiro(0);
    private boolean encerrada;

    private Locacao(String id) {
        this.id = id;
    }

    // ------------------------------------------------------------ comandos
    public static Locacao iniciar(String id, String placa, String cliente, LocalDate dataInicio,
                                  int diasPrevistos, Dinheiro valorPrevisto) {
        if (diasPrevistos < 1) {
            throw new DataInvalida("A locação precisa de pelo menos 1 dia");
        }
        Locacao locacao = new Locacao(id);
        locacao.registrar(new LocacaoIniciada(id, new Placa(placa).valor(), cliente, dataInicio.toString(),
                diasPrevistos, valorPrevisto.centavos()));
        return locacao;
    }

    public void registrarDevolucao(LocalDate dataDevolucao) {
        if (encerrada) {
            throw new LocacaoJaEncerrada("Locação " + id + " já foi encerrada");
        }
        if (dataDevolucao.isBefore(dataInicio)) {
            throw new DataInvalida("Devolução antes do início da locação");
        }
        int diasUtilizados = (int) Math.max(1, ChronoUnit.DAYS.between(dataInicio, dataDevolucao));
        registrar(new VeiculoDevolvido(id, placa, dataDevolucao.toString(), diasUtilizados));
        int atraso = diasUtilizados - diasPrevistos;
        if (atraso > 0) {
            Dinheiro diariaMedia = valorPrevisto.vezes(1.0 / diasPrevistos);
            Dinheiro valorMulta = diariaMedia.vezes(FATOR_MULTA * atraso);
            registrar(new MultaPorAtrasoAplicada(id, atraso, valorMulta.centavos()));
        }
    }

    // ------------------------------------------------------ event sourcing
    public static Locacao reconstituir(String id, List<? extends EventoDominio> eventos) {
        Locacao locacao = new Locacao(id);
        for (EventoDominio e : eventos) {
            locacao.aplicar(e);
        }
        return locacao;
    }

    private void registrar(EventoDominio evento) {
        aplicar(evento);
        pendentes.add(evento);
    }

    private void aplicar(EventoDominio e) {
        if (e instanceof LocacaoIniciada i) {
            placa = i.placa();
            cliente = i.cliente();
            dataInicio = LocalDate.parse(i.dataInicio());
            diasPrevistos = i.diasPrevistos();
            valorPrevisto = new Dinheiro(i.valorPrevistoCentavos());
        } else if (e instanceof VeiculoDevolvido) {
            encerrada = true;
        } else if (e instanceof MultaPorAtrasoAplicada m) {
            multa = new Dinheiro(m.valorCentavos());
        }
        versao++;
    }

    /** Eventos ainda não gravados; quem grava chama limparPendentes() depois. */
    public List<EventoDominio> pendentes() {
        return List.copyOf(pendentes);
    }

    public void limparPendentes() {
        pendentes.clear();
    }

    public Dinheiro valorTotal() {
        return valorPrevisto.mais(multa);
    }

    public String id() { return id; }
    public int versao() { return versao; }
    public String placa() { return placa; }
    public String cliente() { return cliente; }
    public Dinheiro valorPrevisto() { return valorPrevisto; }
    public Dinheiro multa() { return multa; }
    public boolean encerrada() { return encerrada; }
}
