package locadora.apresentacao;

import static teste.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import teste.BeforeEach;
import teste.Test;

/** MVVM: testamos o ViewModel mudando propriedades e observando o estado — sem View. */
class MvvmTest {
    private LocadoraViewModel vm;

    @BeforeEach
    void preparar() {
        vm = new LocadoraViewModel(Montagem.servicoExemplo(true));
    }

    @Test
    void observavel_avisa_inscritos_so_quando_muda() {
        Observavel<Integer> o = new Observavel<>(1);
        List<Integer> recebidos = new ArrayList<>();
        o.inscrever(recebidos::add);
        o.set(2);
        o.set(2);
        assertEquals(List.of(1, 2), recebidos);
    }

    @Test
    void cotacao_reage_a_digitacao() {
        vm.placa.set("XYZ-9012");
        vm.dias.set(7);
        assertEquals("R$ 504.00", vm.cotacao.get());
        vm.placa.set("NAO-EXISTE");
        assertEquals("—", vm.cotacao.get());
    }

    @Test
    void botao_so_habilita_com_campos_validos() {
        vm.placa.set("ABC-1234");
        assertFalse(vm.podeAlugar.get());
        vm.cliente.set("Ana");
        vm.email.set("a@x.com");
        assertTrue(vm.podeAlugar.get());
    }

    @Test
    void comando_alugar_atualiza_frota_e_mensagem() {
        vm.placa.set("ABC-1234");
        vm.dias.set(5);
        vm.cliente.set("Ana");
        vm.email.set("a@x.com");
        vm.comandoAlugar();
        assertEquals("OK: ABC-1234 alugado — R$ 660.00", vm.mensagem.get());
        assertTrue(vm.frota.get().contains("ABC-1234 Fiat Uno (alugado)"), vm.frota.get().toString());
    }
}
