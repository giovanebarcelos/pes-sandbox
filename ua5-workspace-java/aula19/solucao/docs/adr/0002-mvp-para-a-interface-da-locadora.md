# ADR-0002: MVP como padrão de apresentação da Locadora

- **Status:** Aceito
- **Contexto:** a Locadora (Model da Aula 18) ganhou interfaces: terminal e uma página web simples. A lógica de tela (validar campos, formatar valores, decidir o que mostrar no erro) precisa ser testada sem abrir uma interface. Não usamos nenhum framework com data binding.
- **Decisão:** usar **MVP** com `LocadoraView` para a tela de locação no terminal; a página web usa **MVC** clássico (o próprio protocolo HTTP já separa requisição → controller → página).
- **Alternativas consideradas:**
  - *MVVM* — rejeitada por enquanto: sem framework de binding, o `Observavel` feito à mão (ver `Observavel.java`) é código extra que alguém precisa manter.
  - *Só MVC também no terminal* — aceitável, mas a validação de campos ficaria no Controller e misturada à leitura do `Scanner`.
- **Consequências:**
  - (+) `test/locadora/apresentacao/MvpTest.java` testa toda a lógica de tela com uma `ViewFalsa`.
  - (+) Trocar o terminal por outra interface = escrever outra classe que implemente `LocadoraView`.
  - (−) Uma interface com 7 métodos para uma tela pequena; se a tela crescer, revisar o ISP.
