# Tutorial — Aula 19: MVC, MVP e MVVM  

**Projeto e Engenharia de Software** · UA5 — Projeto e Arquitetura de Software · Prof. Giovane Barcelos  
**Slides:** `Aula_19_MVC_MVP_MVVM` · **Duração estimada:** 2h30 em sala + extraclasse · **Workspace:** [`ua5-workspace/`](https://github.com/giovanebarcelos/pes-sandbox/tree/main/ua5-workspace)  

## O que você vai construir  

Três interfaces para **o mesmo Model**: um terminal em **MVC**, uma página web em **MVC** (só com a biblioteca padrão), uma tela em **MVP** com a View passiva testada por uma View falsa, e uma tela em **MVVM** com *data binding* feito à mão (padrão Observer). O objetivo é **sentir** a diferença de testabilidade entre os três, não decorar siglas.  

## De onde vêm os dados desta aula  

| O que | Origem |  
|---|---|  
| **Model**: `ServicoLocacao`, veículos, preços, repositório, notificador | **Aula 18** — `aula19/inicio` é cópia de `aula18/solucao` (ou a sua `aula18/meu`, com `--da-minha`) |  
| Frota de exemplo (Uno, Civic, CG 160, VW Delivery) | Aula 17 + Caminhão da Aula 18 (Passo 8) |  
| Observer (base do MVVM) | Padrões GoF da **Aula 18** (Slides 22-26) |  
| **O que sai daqui** | Os adaptadores de terminal e web viram **adaptadores de entrada** na **Aula 20** |  

## Mapa do tutorial  

| Passo | Assunto | Slides |  
|---|---|---|  
| 0 | Preparar o ambiente | — |  
| 1 | Reconhecer o Model | 6-7 |  
| 2 | Uma fábrica de dados de exemplo | — |  
| 3 | MVC no terminal | 8-14 |  
| 4 | MVC na web (`http.server`) | 9, 14 |  
| 5 | MVP: View passiva + Presenter testável | 15-18, 25 |  
| 6 | MVVM: estado observável + comandos | 19-22 |  
| 7 | Comparar os três | 23-27 |  
| 8 | Registrar a escolha em um ADR | 33 |  
| 🤖 | Com o Agente | 34 |  

---  

## Passo 0 — Preparar o ambiente  

### Opção A (principal): local  

Na pasta `ua5-workspace/`:  

```bash
python ferramentas/verificar.py 19
python ferramentas/preparar.py 19            # parte da solução oficial da Aula 18
# ou: python ferramentas/preparar.py 19 --da-minha   (parte da SUA aula18/meu)
cd aula19/meu
python -m unittest -v
```

✅ **Confira:** `Ran 17 tests ... OK`, que são os testes da Aula 18.  

> **Linux/macOS:** `python3` no lugar de `python`. Se escolheu `--da-minha`, apague o `locadora_legado.py` e o `tests/test_caracterizacao.py` antigos (o legado já cumpriu seu papel).  

### Opção B (alternativa): Docker  

```bash
docker compose run --rm aula19                       # testes
docker compose up aula19-web                         # página web em http://localhost:8019
docker compose run --rm aula19 python -m locadora.apresentacao.mvc_console   # terminal interativo
```

Para usar a sua versão: `PASTA=meu docker compose ...` (Linux/macOS) ou `$env:PASTA="meu"; docker compose ...` (PowerShell).  

---  

## Passo 1 — Reconhecer o Model  

Nos três padrões, o **Model é o mesmo** e **nunca conhece a tela** (Slide 32). O nosso Model é o `ServicoLocacao` da Aula 18:  

| Operação do Model | Para que a tela usa |  
|---|---|  
| `listar()` | mostrar a frota |  
| `cotar(placa, dias)` | mostrar o preço antes de confirmar |  
| `alugar(placa, cliente, email, dias)` | confirmar a locação |  
| `devolver(placa)` | encerrar a locação |  

Confira que `locadora/servico.py` **não** tem `print` nem `input`. Se tivesse, não daria para trocar de interface.  

---  

## Passo 2 — Uma fábrica de dados de exemplo  

As três telas precisam de um Model com frota. Crie a pasta e o arquivo:  

```bash
python -c "import os; os.makedirs('locadora/apresentacao', exist_ok=True); open('locadora/apresentacao/__init__.py','a').close()"
```

`locadora/apresentacao/montagem.py`:  

```python
from ..fabrica import criar_veiculo
from ..notificacao import NotificadorConsole, NotificadorMemoria
from ..precos import DescontoSemanal
from ..repositorio import RepositorioMemoria
from ..servico import ServicoLocacao

FROTA_EXEMPLO = [
    ("carro", "ABC-1234", "Fiat Uno", 120),
    ("carro", "DEF-5678", "Honda Civic", 200),
    ("moto", "XYZ-9012", "Honda CG 160", 80),
    ("caminhao", "CAM-0001", "VW Delivery", 300),
]


def servico_exemplo(silencioso=False):
    notificador = NotificadorMemoria() if silencioso else NotificadorConsole()
    servico = ServicoLocacao(RepositorioMemoria(), notificador, DescontoSemanal())
    for tipo, placa, modelo, diaria in FROTA_EXEMPLO:
        servico.cadastrar(criar_veiculo(tipo, placa, modelo, diaria))
    return servico
```

`silencioso=True` troca o notificador por um *fake*, para os testes não imprimirem "[SMTP]".  

---  

## Passo 3 — MVC no terminal  

| Papel | Classe | Responsabilidade |  
|---|---|---|  
| Model | `ServicoLocacao` | regras e estado |  
| View | `VisaoConsole` | **só** transforma dados em texto |  
| Controller | `ControladorConsole` | interpreta o comando, chama o Model, escolhe a View |  

Crie `locadora/apresentacao/mvc_console.py`:  

```python
from ..erros import LocadoraErro
from .montagem import servico_exemplo


class VisaoConsole:
    def frota(self, veiculos):
        linhas = ["PLACA     MODELO            DIÁRIA     STATUS"]
        for v in veiculos:
            status = "disponível" if v.disponivel else "alugado"
            linhas.append(f"{v.placa:<9} {v.modelo:<17} R$ {v.valor_diaria:>7.2f} {status}")
        return "\n".join(linhas)

    def cotacao(self, placa, dias, valor):
        return f"Cotação {placa} por {dias} dia(s): R$ {valor:.2f}"

    def locacao(self, locacao):
        return f"OK: {locacao.placa} alugado para {locacao.cliente} — R$ {locacao.valor:.2f}"

    def mensagem(self, texto):
        return texto

    def erro(self, texto):
        return f"ERRO: {texto}"

    def ajuda(self):
        return ("Comandos: listar | cotar PLACA DIAS | alugar PLACA DIAS NOME EMAIL | "
                "devolver PLACA | sair")


class ControladorConsole:
    def __init__(self, modelo, visao):
        self.modelo = modelo
        self.visao = visao

    def executar(self, linha):
        """Recebe uma linha digitada e devolve o texto a exibir."""
        partes = linha.split()
        if not partes:
            return self.visao.ajuda()
        comando, args = partes[0].lower(), partes[1:]
        try:
            if comando == "listar":
                return self.visao.frota(self.modelo.listar())
            if comando == "cotar" and len(args) == 2:
                placa, dias = args[0], int(args[1])
                return self.visao.cotacao(placa, dias, self.modelo.cotar(placa, dias))
            if comando == "alugar" and len(args) == 4:
                placa, dias, nome, email = args[0], int(args[1]), args[2], args[3]
                return self.visao.locacao(self.modelo.alugar(placa, nome, email, dias))
            if comando == "devolver" and len(args) == 1:
                self.modelo.devolver(args[0])
                return self.visao.mensagem(f"OK: {args[0]} devolvido")
        except (LocadoraErro, ValueError) as e:
            return self.visao.erro(str(e).strip("'\""))
        return self.visao.ajuda()


def main():
    controlador = ControladorConsole(servico_exemplo(), VisaoConsole())
    print(controlador.visao.ajuda())
    while True:
        try:
            linha = input("mvc> ")
        except EOFError:
            break
        if linha.strip().lower() == "sair":
            break
        print(controlador.executar(linha))


if __name__ == "__main__":
    main()
```

Rode e experimente:  

```bash
python -m locadora.apresentacao.mvc_console
```

```
mvc> listar
mvc> cotar XYZ-9012 7
mvc> alugar ABC-1234 5 Ana ana@x.com
mvc> alugar ABC-1234 5 Bia bia@x.com
mvc> sair
```

✅ **Confira:** a cotação da moto por 7 dias é `R$ 504.00`, e o segundo `alugar` responde `ERRO: Veículo ABC-1234 já está alugado`.  

> 💡 Repare que `executar()` **devolve** texto em vez de imprimir. Por isso dá para testá-lo sem digitar nada: copie `aula19/solucao/tests/test_mvc.py` e rode `python -m unittest -v`.  

---  

## Passo 4 — MVC na web  

A web é o lugar natural do MVC (Slide 9): a requisição HTTP chega ao Controller, que chama o Model e devolve uma página (View). Vamos fazer isso **sem framework**, com `http.server`.  

Copie `aula19/solucao/locadora/apresentacao/web_mvc.py` para a sua pasta e leia-o em três blocos:  

1. **View**: `pagina_frota(veiculos, mensagem)` devolve **texto HTML** e não toma nenhuma decisão de negócio. Repare no `html.escape()`, que impede que um nome digitado vire código na página (segurança básica).  
2. **Controller**: `ControladorWeb.tratar(metodo, caminho, formulario)` devolve `(status, html)`. É ele quem decide: `GET /` lista, `POST /alugar` aluga, `POST /devolver` devolve, e um erro de negócio vira `400`.  
3. **Adaptador HTTP**: `criar_handler()` só traduz a requisição da rede para uma chamada a `tratar()`. Por ser tão fino, o Controller é testável **sem subir servidor**.  

```bash
python -m locadora.apresentacao.web_mvc
```

Abra **http://localhost:8019**, alugue um veículo, devolva-o e tente alugar uma placa inexistente. Pare com **Ctrl+C**.  

✅ **Confira:** copie `aula19/solucao/tests/test_web_mvc.py` e rode os testes. Um teste chama `tratar()` direto e outro sobe um servidor real numa porta livre (teste de fumaça).  

> Porta ocupada? Use outra: `PORT=8090 python -m locadora.apresentacao.web_mvc` (Linux/macOS) ou `$env:PORT=8090; python -m locadora.apresentacao.web_mvc` (PowerShell).  

---  

## Passo 5 — MVP: View passiva + Presenter testável  

**Atividade do Slide 30, itens 2 e 3.** No MVP, a View é **burra**: só lê campos e mostra coisas. Toda a lógica de apresentação (validar, formatar, decidir o que mostrar no erro) vai para o **Presenter**, que conversa com a View por uma **interface**.  

Crie `locadora/apresentacao/mvp.py`:  

```python
from abc import ABC, abstractmethod

from ..erros import LocadoraErro
from .montagem import servico_exemplo


class LocadoraViewInterface(ABC):
    @abstractmethod
    def obter_placa(self): ...

    @abstractmethod
    def obter_dias(self): ...

    @abstractmethod
    def obter_cliente(self): ...

    @abstractmethod
    def obter_email(self): ...

    @abstractmethod
    def mostrar_frota(self, linhas): ...

    @abstractmethod
    def mostrar_sucesso(self, mensagem): ...

    @abstractmethod
    def mostrar_erro(self, mensagem): ...


class LocadoraPresenter:
    def __init__(self, view, modelo):
        self.view = view
        self.modelo = modelo

    def carregar_frota(self):
        linhas = []
        for v in self.modelo.listar():
            marca = "[ ]" if v.disponivel else "[X]"
            linhas.append(f"{marca} {v.placa} {v.modelo} — R$ {v.valor_diaria:.2f}/dia")
        self.view.mostrar_frota(linhas)

    def ao_clicar_alugar(self):
        placa = self.view.obter_placa().strip().upper()
        cliente = self.view.obter_cliente().strip()
        email = self.view.obter_email().strip()
        dias_txt = self.view.obter_dias().strip()
        if not placa or not cliente:
            self.view.mostrar_erro("Informe placa e cliente")
            return
        if "@" not in email:
            self.view.mostrar_erro("E-mail inválido")
            return
        if not dias_txt.isdigit() or int(dias_txt) < 1:
            self.view.mostrar_erro("Dias deve ser um número inteiro maior que zero")
            return
        try:
            loc = self.modelo.alugar(placa, cliente, email, int(dias_txt))
        except LocadoraErro as e:
            self.view.mostrar_erro(str(e).strip("'\""))
            return
        self.view.mostrar_sucesso(f"{loc.placa} alugado para {loc.cliente}: R$ {loc.valor:.2f}")
        self.carregar_frota()
```

Agora a View real (terminal). Ela **não tem nenhum `if` de regra**:  

```python
class ConsoleViewMVP(LocadoraViewInterface):
    def obter_placa(self):
        return input("Placa: ")

    def obter_dias(self):
        return input("Dias: ")

    def obter_cliente(self):
        return input("Cliente: ")

    def obter_email(self):
        return input("E-mail: ")

    def mostrar_frota(self, linhas):
        print("\n".join(linhas))

    def mostrar_sucesso(self, mensagem):
        print(f"OK: {mensagem}")

    def mostrar_erro(self, mensagem):
        print(f"ERRO: {mensagem}")


def main():
    presenter = LocadoraPresenter(ConsoleViewMVP(), servico_exemplo())
    presenter.carregar_frota()
    while input("\nAlugar um veículo? (s/n) ").strip().lower() == "s":
        presenter.ao_clicar_alugar()


if __name__ == "__main__":
    main()
```

```bash
python -m locadora.apresentacao.mvp
```

Repare na ordem das perguntas: **placa, cliente, e-mail, dias**. Quem decide essa ordem é o Presenter.  

### O teste com View falsa (item 3 da atividade)  

**Escreva você** `tests/test_mvp.py` antes de olhar a solução. A ideia:  

```python
class ViewFalsa(LocadoraViewInterface):
    def __init__(self, placa="", dias="", cliente="", email=""):
        self.campos = dict(placa=placa, dias=dias, cliente=cliente, email=email)
        self.frota, self.sucessos, self.erros = [], [], []

    def obter_placa(self):
        return self.campos["placa"]
    # ... os outros três "obter_" iguais ...

    def mostrar_frota(self, linhas):
        self.frota = linhas

    def mostrar_sucesso(self, mensagem):
        self.sucessos.append(mensagem)

    def mostrar_erro(self, mensagem):
        self.erros.append(mensagem)
```

Casos que valem a pena testar: aluguel com sucesso (a frota é recarregada com `[X]`), cada validação de campo e um erro de negócio (placa inexistente). Compare com `aula19/solucao/tests/test_mvp.py`.  

✅ **Confira:** o teste **roda sem nenhum `input()`**. Se precisasse de UI, a separação não teria acontecido (Slide 34).  

---  

## Passo 6 — MVVM: estado observável + comandos  

**Bônus do Slide 30, item 4.** No MVVM, a View **se liga** a propriedades do ViewModel e se redesenha quando elas mudam. Frameworks como WPF, Jetpack Compose e Angular fazem o *binding* para você; sem framework, ele é feito à mão com o **Observer** (Aula 18).  

Copie `aula19/solucao/locadora/apresentacao/mvvm.py` e estude as duas peças.  

**1. `Observavel`**: guarda um valor e avisa os inscritos quando ele muda.  

```python
class Observavel:
    def __init__(self, valor=None):
        self._valor = valor
        self._inscritos = []

    def inscrever(self, funcao):
        self._inscritos.append(funcao)
        funcao(self._valor)

    @property
    def valor(self):
        return self._valor

    @valor.setter
    def valor(self, novo):
        if novo != self._valor:
            self._valor = novo
            for f in self._inscritos:
                f(novo)
```

**2. `LocadoraViewModel`**: separa claramente o **estado** do **comando**.  

| Estado (propriedades observáveis) | Derivado? | Comando |  
|---|---|---|  
| `placa`, `dias`, `cliente`, `email` | Não: a View escreve | |  
| `cotacao` | **Sim**: recalcula a cada digitação | |  
| `pode_alugar` | **Sim**: habilita ou desabilita o "botão" | |  
| `frota`, `mensagem` | Sim: mudam após o comando | `comando_alugar()` |  

```bash
python -m locadora.apresentacao.mvvm
```

Digite a placa e os dias e veja a **cotação mudar sozinha** a cada campo: isso é o binding.  

✅ **Confira:** copie `aula19/solucao/tests/test_mvvm.py`. O teste muda `vm.placa.valor` e verifica `vm.cotacao.valor`, sem View nenhuma.  

---  

## Passo 7 — Comparar os três  

Preencha com base no que **você** escreveu:  

| Pergunta | MVC (terminal) | MVP | MVVM |  
|---|---|---|---|  
| Onde fica a validação do e-mail? | | | |  
| Quem conhece quem? (View ↔ lógica) | | | |  
| Como o teste "aperta o botão"? | | | |  
| Linhas de código "de cola" | | | |  
| Precisa de framework para valer a pena? | | | |  

**Atividade do Slide 24:** para cada cenário, qual padrão você escolheria e por quê?  

1. Página web de consulta da frota, renderizada no servidor.  
2. App desktop de balcão com muitos campos que se recalculam (cotação, seguro, desconto).  
3. Tela de terminal para o gerente, que precisa de testes automatizados de validação.  

---  

## Passo 8 — Registrar a escolha em um ADR  

Crie `docs/adr/0002-<padrao>-para-a-interface-da-locadora.md` justificando **qual** padrão você adotaria para a Locadora e **por que não** os outros dois. Compare com `aula19/solucao/docs/adr/0002-mvp-para-a-interface-da-locadora.md`.  

**Conexão com a A3 (Slide 33):** se o seu projeto tem interface, o padrão de apresentação escolhido e o porquê vão para um ADR como este.  

### Checklist de entrega  

- [ ] `python -m unittest -v` verde (Aula 18 + MVC + web + MVP + MVVM)  
- [ ] Terminal MVC e página web funcionando  
- [ ] Presenter testado com `ViewFalsa`, sem UI real  
- [ ] Tabela comparativa do Passo 7 + escolhas do estudo de caso  
- [ ] ADR-0002  

---  

## 🤖 Com o Agente  

> **Primeiro à mão, depois o agente** (Aula 01B, Slide 32).  

**Setup:** o da **Aula 01B**. Rode `opencode` na pasta `ua5-workspace/`; o `AGENTS.md` já orienta o agente a trabalhar em `aula19/meu` e a não instalar Flask, React nem nenhum framework. Para só analisar, sem editar arquivos, aperte **Tab** e mude para o agente **plan**.  

### Prompts por passo  

| Passo | Modo | Prompt |  
|---|---|---|  
| 1 | plan | *"Classifique cada arquivo de aula19/meu/locadora em Model, View ou Controller e aponte qualquer lógica de negócio que esteja na apresentação."* |  
| 3-4 | plan | *"Revise ControladorConsole e ControladorWeb: alguma regra de negócio vazou para o Controller? Alguma decisão de tela vazou para a View?"* |  
| 5 | build | *"Escreva testes adicionais para LocadoraPresenter usando a ViewFalsa de tests/test_mvp.py, cobrindo dias negativos e placa em minúsculas. Não altere o Presenter."* |  
| 6 | plan | *"Compare MVP e MVVM para a Locadora com os critérios do Slide 27 da Aula 19: qual faz sentido sem um framework com data binding?"* |  
| 8 | build | *"Use a skill grill-with-docs para me questionar sobre o padrão de apresentação da Locadora e registrar o ADR em aula19/meu/docs/adr/."* |  

### Você valida  

- O teste do Presenter **roda sem UI**? Se o agente usou `input()` ou `print()` no teste, a separação não aconteceu.  
- **O agente tende a sugerir MVVM para tudo** (e a propor um framework). Confira contra os critérios do Slide 27: sem binding automático, MVVM é código manual a mais.  
- Na página web, peça ao agente para explicar o `html.escape()`. Se ele sugerir removê-lo "para simplificar", **recuse**: sem ele, um nome digitado pelo usuário pode virar código na página (XSS).  
- **Ligação com a A3:** faça o mesmo diagnóstico (Passo 1) no seu projeto.  

### Registro  

| Prompt (resumo) | O que o agente acertou | O que errou ou omitiu | O que você corrigiu |  
|---|---|---|---|  
| | | | |  

---  

## Problemas comuns  

| Sintoma | Causa provável | Solução |  
|---|---|---|  
| `OSError: [Errno 98] Address already in use` | Outra instância do servidor está aberta | Feche-a (Ctrl+C) ou mude a porta com `PORT` |  
| A página web não abre no Docker | Serviço iniciado com `run` em vez de `up` | Use `docker compose up aula19-web` (o `run` não publica a porta) |  
| `ImportError` em `from ..erros` | Arquivo rodado como script | Use `python -m locadora.apresentacao.xxx` |  
| O teste web fica parado | Firewall bloqueando `127.0.0.1` | Rode só `python -m unittest tests.test_mvp tests.test_mvvm tests.test_mvc` |  

## Próxima aula  

Na **Aula 20**, a pergunta muda de "como separar a tela?" para "**como proteger o domínio de tudo que é externo?**". O terminal e a web deste tutorial viram **adaptadores de entrada** de uma Arquitetura Hexagonal, e o repositório ganha uma versão **SQLite** de verdade. O ponto de partida (`aula20/inicio`) é cópia de `aula19/solucao`; para partir do seu código, use `python ferramentas/preparar.py 20 --da-minha`.  
