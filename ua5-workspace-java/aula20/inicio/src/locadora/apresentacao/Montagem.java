package locadora.apresentacao;

import locadora.DescontoSemanal;
import locadora.Fabrica;
import locadora.Notificador;
import locadora.NotificadorConsole;
import locadora.NotificadorMemoria;
import locadora.RepositorioMemoria;
import locadora.ServicoLocacao;

/** Monta um Model (ServicoLocacao) com dados de exemplo, compartilhado pelas 3 variantes. */
public final class Montagem {
    private Montagem() {
    }

    public static ServicoLocacao servicoExemplo(boolean silencioso) {
        Notificador notificador = silencioso ? new NotificadorMemoria() : new NotificadorConsole();
        ServicoLocacao servico = new ServicoLocacao(new RepositorioMemoria(), notificador, new DescontoSemanal());
        servico.cadastrar(Fabrica.criarVeiculo("carro", "ABC-1234", "Fiat Uno", 120));
        servico.cadastrar(Fabrica.criarVeiculo("carro", "DEF-5678", "Honda Civic", 200));
        servico.cadastrar(Fabrica.criarVeiculo("moto", "XYZ-9012", "Honda CG 160", 80));
        servico.cadastrar(Fabrica.criarVeiculo("caminhao", "CAM-0001", "VW Delivery", 300));
        return servico;
    }
}
