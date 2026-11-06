# Tutorial — Aula 18: Princípios SOLID, Design Patterns e ADR  

**Projeto e Engenharia de Software** · UA5 — Projeto e Arquitetura de Software · Prof. Giovane Barcelos  
**Slides:** `Aula_18_Principios_Projeto_SOLID` · **Duração estimada:** 2h30 em sala + extraclasse · **Workspace:** [`ua5-workspace/`](https://github.com/giovanebarcelos/pes-sandbox/tree/main/ua5-workspace)  

## O que você vai construir  

Você recebe uma Locadora de Veículos que **funciona**, mas foi escrita como uma única classe que faz tudo. Ao fim do tutorial, ela estará dividida em módulos coesos, com polimorfismo no lugar de `if/elif`, uma **Strategy** para descontos, um **Factory Method** para criar veículos e injeção de dependência. Testes vão provar que o comportamento não mudou, e uma decisão arquitetural ficará registrada em um **ADR**.  

## De onde vêm os dados desta aula  

| O que | Origem |  
|---|---|  
| Regras de preço (Carro +10% de seguro, Moto sem acréscimo) e frota de exemplo | **Aula 17** — `repository/class17/python/PES1701-Locadora.py` |  
| Regras novas (desconto de 10% para 7+ dias, e-mail de confirmação, frota em JSON) | Criadas para esta aula, em `aula18/inicio/locadora_legado.py` |  
| Testes de caracterização | `aula18/inicio/tests/test_caracterizacao.py` |  
| **O que sai daqui** | A solução (`aula18/solucao`) é o ponto de partida da **Aula 19** |  

## Mapa do tutorial  

| Passo | Assunto | Slides |  
|---|---|---|  
| 0 | Preparar o ambiente (local ou Docker) | — |  
| 1 | Conhecer o legado e os testes de caracterização | 20 |  
| 2 | Diagnóstico: quais princípios SOLID são violados? | 13-18 |  
| 3 | Veículos com polimorfismo + **Factory Method** (OCP) | 15, 22-25 |  
| 4 | Política de desconto com **Strategy** (OCP + DIP) | 15, 25 |  
| 5 | Repositório e Notificador separados (SRP + ISP) | 14, 17 |  
| 6 | Serviço com injeção de dependência (DIP) | 18, 21 |  
| 7 | Provar que o comportamento não mudou | 38 |  
| 8 | Estender sem modificar: o Caminhão (OCP + LSP) | 15-16 |  
| 9 | Antes × depois: custo e benefício | 36 |  
| 10 | Registrar a decisão em um ADR | 33-34 |  
| 🤖 | Com o Agente | 39 |  

---  

## Passo 0 — Preparar o ambiente  

### Opção A (principal): local, só com Python  

1. Confira o Python (3.10 ou superior):  

   ```bash
   python --version
   ```

   > No **Linux/macOS**, digite `python3` em vez de `python` em todos os comandos deste tutorial. No **Windows**, `py` também funciona.  

2. Obtenha o workspace (se ainda não tem):  

   ```bash
   git clone https://github.com/giovanebarcelos/pes-sandbox.git
   cd pes-sandbox/ua5-workspace
   ```

   Sem Git: no GitHub, **Code → Download ZIP**, descompacte e entre em `pes-sandbox-main/ua5-workspace`.  

3. Confira se está tudo funcionando e crie a **sua** cópia de trabalho:  

   ```bash
   python ferramentas/verificar.py 18
   python ferramentas/preparar.py 18
   cd aula18/meu
   ```

   ✅ **Confira:** o `verificar.py` mostra `aula18/inicio OK` e `aula18/solucao OK`; o `preparar.py` mostra `aula18/inicio -> aula18/meu`.  

> Você vai trabalhar **só** em `aula18/meu`. `inicio/` fica intacto (se errar muito, `python ferramentas/preparar.py 18 --forcar` recomeça) e `solucao/` é o gabarito para comparar **depois** de tentar.  

### Opção B (alternativa): Docker  

Se a máquina tem Docker mas não tem Python, rode da pasta `ua5-workspace/`:  

```bash
docker compose run --rm aula18                           # testes da solução
PASTA=meu docker compose run --rm aula18                 # testes da SUA versão (Linux/macOS)
$env:PASTA="meu"; docker compose run --rm aula18         # idem, no PowerShell
docker compose run --rm aula18 python -m locadora.main   # demonstração
```

Para criar `aula18/meu` sem Python, copie a pasta `aula18/inicio` com o gerenciador de arquivos e renomeie a cópia para `meu`.  

---  

## Passo 1 — Conhecer o legado  

Dentro de `aula18/meu`:  

```bash
python locadora_legado.py
```

✅ **Saída esperada (final):**  

```
=== FROTA ===
ABC-1234 Fiat Uno R$ 120.00/dia (alugado)
DEF-5678 Honda Civic R$ 200.00/dia (disponível)
XYZ-9012 Honda CG 160 R$ 80.00/dia (alugado)
Faturamento: R$ 1164.00
```

Agora rode os testes:  

```bash
python -m unittest -v
```

✅ **Confira:** `Ran 8 tests ... OK`.  

Esses testes são de **caracterização**. Eles não dizem se o código é bom, só registram o que ele **faz hoje**: Carro 5 dias = R$ 660,00; Moto 7 dias = R$ 504,00 (com desconto); veículo alugado não pode ser alugado de novo. Eles são a sua rede de segurança: **refatoração sem teste é aposta** (Slide 38).  

Abra `locadora_legado.py` e leia a classe inteira. Repare quantas coisas diferentes a classe `Locadora` sabe fazer.  

---  

## Passo 2 — Diagnóstico SOLID (à mão, antes de qualquer agente)  

Preencha a tabela no seu caderno ou em `aula18/meu/diagnostico.md`. Para cada princípio, aponte **o trecho** e **o porquê**.  

| Princípio | Violado? | Trecho do legado | Por quê |  
|---|---|---|---|  
| **S**RP — uma razão para mudar | | | |  
| **O**CP — aberto para extensão, fechado para modificação | | | |  
| **L**SP — subtipos substituíveis | | | |  
| **I**SP — interfaces enxutas | | | |  
| **D**IP — depender de abstrações | | | |  

<details>  
<summary>Gabarito (abra só depois de tentar)</summary>  

| Princípio | Violado? | Trecho | Por quê |  
|---|---|---|---|  
| SRP | Sim | `Locadora` inteira | Muda por 5 motivos: regra de preço, formato do arquivo, texto do e-mail, layout do relatório, regra de disponibilidade |  
| OCP | Sim | `calcular()` com `if veiculo["tipo"] == ...` | Tipo novo (caminhão) obriga a editar o `if/elif` e a validação de `cadastrar()` |  
| LSP | Não se aplica (ainda) | — | Não há hierarquia; veículos são dicionários. Vai aparecer no Passo 3 |  
| ISP | Risco | O "cliente" de e-mail e o de arquivo estão dentro da mesma classe | Quem só quer calcular preço depende de uma classe que grava arquivo e envia e-mail |  
| DIP | Sim | `open(self.arquivo, "w")`, `print("[SMTP] ...")` dentro de `alugar()` | O caso de uso depende diretamente de detalhes (arquivo, SMTP); impossível testar sem disco |  

</details>  

---  

## Passo 3 — Veículos com polimorfismo + Factory Method  

**Objetivo:** trocar os dicionários e o `if/elif` por uma hierarquia de classes (como na Aula 17).  

1. Crie a pasta do pacote (funciona em qualquer sistema):  

   ```bash
   python -c "import os; os.makedirs('locadora', exist_ok=True); open('locadora/__init__.py','a').close()"
   ```

2. Crie `locadora/veiculos.py`:  

   ```python
   from abc import ABC, abstractmethod


   class Veiculo(ABC):
       tipo = "veiculo"

       def __init__(self, placa, modelo, valor_diaria):
           if valor_diaria <= 0:
               raise ValueError("A diária deve ser positiva")
           self._placa = placa
           self._modelo = modelo
           self._valor_diaria = float(valor_diaria)
           self._disponivel = True

       @property
       def placa(self):
           return self._placa

       @property
       def modelo(self):
           return self._modelo

       @property
       def valor_diaria(self):
           return self._valor_diaria

       @property
       def disponivel(self):
           return self._disponivel

       def marcar_alugado(self):
           self._disponivel = False

       def marcar_disponivel(self):
           self._disponivel = True

       @abstractmethod
       def preco_base(self, dias):
           """Preço sem descontos — cada tipo tem a sua regra."""


   class Carro(Veiculo):
       tipo = "carro"

       def preco_base(self, dias):
           return self.valor_diaria * dias * 1.10  # +10% de seguro


   class Moto(Veiculo):
       tipo = "moto"

       def preco_base(self, dias):
           return self.valor_diaria * dias
   ```

3. Crie `locadora/fabrica.py`. O **Factory Method** é o único lugar que liga o texto `"carro"` à classe `Carro`. Quem lê dados de fora (arquivo, formulário, API) só conhece o nome:  

   ```python
   from .veiculos import Carro, Moto

   _TIPOS = {"carro": Carro, "moto": Moto}


   def registrar_tipo(nome, classe):
       _TIPOS[nome] = classe


   def tipos_conhecidos():
       return sorted(_TIPOS)


   def criar_veiculo(tipo, placa, modelo, diaria):
       try:
           classe = _TIPOS[tipo]
       except KeyError:
           raise ValueError(f"Tipo desconhecido: {tipo}") from None
       return classe(placa, modelo, diaria)
   ```

4. Teste rápido no terminal:  

   ```bash
   python -c "from locadora.fabrica import criar_veiculo as c; print(c('carro','A','Uno',120).preco_base(5))"
   ```

   ✅ **Confira:** `660.0`. Troque `5` por `3` e veja `396.00000000000006`: é o erro de ponto flutuante do `float`. Por isso o arredondamento fica na política de preço (próximo passo), e na Aula 21 o problema some de vez com o objeto de valor `Dinheiro`, que guarda centavos inteiros.  

> 💡 **Por que não um `if` na fábrica?** Com o dicionário, um tipo novo entra com `registrar_tipo()` ou com uma linha nova no dicionário. Nenhuma lógica existente é editada (OCP).  

---  

## Passo 4 — Política de desconto com Strategy  

O desconto de 7 dias estava escondido dentro de `calcular()`. Se amanhã o cliente quiser "desconto de feriado", o `calcular()` muda de novo. Com **Strategy**, cada política é uma classe trocável.  

Crie `locadora/precos.py`:  

```python
from abc import ABC, abstractmethod


class PoliticaPreco(ABC):
    @abstractmethod
    def aplicar(self, valor, dias):
        """Recebe o preço-base e devolve o preço final."""


class SemDesconto(PoliticaPreco):
    def aplicar(self, valor, dias):
        return round(valor, 2)


class DescontoSemanal(PoliticaPreco):
    """Regra do legado: 7 dias ou mais ganham 10% de desconto."""

    def __init__(self, dias_minimos=7, percentual=0.10):
        self.dias_minimos = dias_minimos
        self.percentual = percentual

    def aplicar(self, valor, dias):
        if dias >= self.dias_minimos:
            valor = valor * (1 - self.percentual)
        return round(valor, 2)
```

✅ **Confira:**  

```bash
python -c "from locadora.precos import DescontoSemanal as D; print(D().aplicar(560, 7), D().aplicar(480, 6))"
```

Saída: `504.0 480`.  

---  

## Passo 5 — Repositório e Notificador separados (SRP + ISP)  

1. `locadora/erros.py`: erros com **nome de negócio**. Eles herdam de `KeyError`/`RuntimeError` para continuar compatíveis com os testes do legado.  

   ```python
   class LocadoraErro(Exception):
       """Base para todos os erros de regra de negócio."""


   class VeiculoNaoEncontrado(LocadoraErro, KeyError):
       pass


   class VeiculoIndisponivel(LocadoraErro, RuntimeError):
       pass
   ```

2. `locadora/notificacao.py`: uma interface **enxuta** (ISP). O serviço só precisa de `notificar`, não de `enviar_sms`, `gerar_pdf`...  

   ```python
   from abc import ABC, abstractmethod


   class Notificador(ABC):
       @abstractmethod
       def notificar(self, destino, mensagem): ...


   class NotificadorConsole(Notificador):
       def notificar(self, destino, mensagem):
           print(f"[SMTP] para {destino}: {mensagem}")


   class NotificadorMemoria(Notificador):
       """Fake para testes: guarda as mensagens em vez de enviar."""

       def __init__(self):
           self.enviadas = []

       def notificar(self, destino, mensagem):
           self.enviadas.append((destino, mensagem))
   ```

3. `locadora/repositorio.py`: uma abstração `RepositorioVeiculos` com duas implementações, `RepositorioMemoria` (para testes) e `RepositorioJson` (mesmo formato de arquivo do legado). Escreva a versão em memória:  

   ```python
   import json
   from abc import ABC, abstractmethod

   from .fabrica import criar_veiculo


   class RepositorioVeiculos(ABC):
       @abstractmethod
       def salvar(self, veiculo): ...

       @abstractmethod
       def buscar(self, placa):
           """Devolve o veículo ou None."""

       @abstractmethod
       def listar(self): ...


   class RepositorioMemoria(RepositorioVeiculos):
       def __init__(self):
           self._dados = {}

       def salvar(self, veiculo):
           self._dados[veiculo.placa] = veiculo

       def buscar(self, placa):
           return self._dados.get(placa)

       def listar(self):
           return list(self._dados.values())
   ```

   Depois **tente escrever `RepositorioJson` sozinho**: ele lê o arquivo no construtor (usando `criar_veiculo`) e regrava a lista inteira a cada `salvar`. Compare com `aula18/solucao/locadora/repositorio.py`.  

---  

## Passo 6 — Serviço com injeção de dependência (DIP)  

O caso de uso agora só **orquestra**: não calcula preço, não grava arquivo e não imprime nada. Tudo chega pronto pelo construtor.  

Crie `locadora/servico.py`:  

```python
from dataclasses import dataclass

from .erros import VeiculoIndisponivel, VeiculoNaoEncontrado
from .precos import SemDesconto


@dataclass(frozen=True)
class Locacao:
    placa: str
    cliente: str
    dias: int
    valor: float


class ServicoLocacao:
    def __init__(self, repositorio, notificador, politica=None):
        self._repo = repositorio
        self._notificador = notificador
        self._politica = politica or SemDesconto()
        self._locacoes = []

    def cadastrar(self, veiculo):
        self._repo.salvar(veiculo)

    def listar(self):
        return self._repo.listar()

    def cotar(self, placa, dias):
        veiculo = self._obter(placa)
        return self._politica.aplicar(veiculo.preco_base(dias), dias)

    def alugar(self, placa, cliente, email, dias):
        veiculo = self._obter(placa)
        if not veiculo.disponivel:
            raise VeiculoIndisponivel(f"Veículo {placa} já está alugado")
        valor = self._politica.aplicar(veiculo.preco_base(dias), dias)
        veiculo.marcar_alugado()
        self._repo.salvar(veiculo)
        locacao = Locacao(placa, cliente, dias, valor)
        self._locacoes.append(locacao)
        self._notificador.notificar(
            email,
            f"Olá {cliente}, sua locação do {veiculo.modelo} "
            f"por {dias} dia(s) custa R$ {valor:.2f}.",
        )
        return locacao

    def devolver(self, placa):
        veiculo = self._obter(placa)
        veiculo.marcar_disponivel()
        self._repo.salvar(veiculo)

    def faturamento_total(self):
        return round(sum(l.valor for l in self._locacoes), 2)

    def _obter(self, placa):
        veiculo = self._repo.buscar(placa)
        if veiculo is None:
            raise VeiculoNaoEncontrado(f"Veículo {placa} não encontrado")
        return veiculo
```

Complete com dois arquivos pequenos (copie de `aula18/solucao/locadora/`):  

- `relatorio.py`: a função `relatorio_frota(servico)`, que monta o mesmo texto do legado. A apresentação fica separada da regra (SRP) e prepara o terreno da Aula 19.  
- `main.py`: a **raiz de composição** (*composition root*), o único lugar que escolhe `RepositorioJson`, `NotificadorConsole` e `DescontoSemanal`.  

```bash
python -m locadora.main
```

✅ **Confira:** o relatório termina em `Faturamento: R$ 1164.00`, igual ao legado.  

---  

## Passo 7 — Provar que o comportamento não mudou  

Copie `aula18/solucao/tests/test_caracterizacao.py` para `aula18/meu/tests/test_refatorado.py`. Ele tem **os mesmos 8 cenários** do legado, agora contra o `ServicoLocacao`. Abra os dois arquivos lado a lado: os valores esperados são idênticos.  

```bash
python -m unittest -v
```

✅ **Confira:** os 8 testes do legado **e** os 8 do refatorado passam (16 no total).  

Repare em `setUp`: o serviço é montado com `RepositorioMemoria()` e `NotificadorMemoria()`, **sem arquivo e sem SMTP**. Isso só é possível por causa do DIP. No legado, todo teste precisava de uma pasta temporária.  

> Quando os dois conjuntos passam, o legado pode ser apagado. Mantenha-o até o fim do tutorial para comparar.  

---  

## Passo 8 — Estender sem modificar: o Caminhão  

**Pedido novo do cliente:** caminhão paga diária × dias + 10% de seguro + **taxa fixa de R$ 50,00**.  

1. Em `veiculos.py`, **acrescente** (não altere nada do que existe):  

   ```python
   class Caminhao(Veiculo):
       tipo = "caminhao"

       def preco_base(self, dias):
           return self.valor_diaria * dias * 1.10 + 50.0
   ```

2. Em `fabrica.py`, acrescente `"caminhao": Caminhao` ao dicionário (e o import).  

3. Copie `aula18/solucao/tests/test_veiculos.py`. A classe `TestContratoLSP` roda **o mesmo teste** para todas as subclasses: preço ≥ 0, preço cresce com os dias, a disponibilidade funciona. Esse é o **LSP** virando teste. E o teste `test_todas_as_subclasses_concretas_estao_na_lista` falha se alguém criar um veículo novo e esquecer de submetê-lo ao contrato.  

✅ **Confira:** `python -m unittest -v` continua verde, e `ServicoLocacao`, `precos.py` e `repositorio.py` **não foram tocados**. Isso é OCP.  

**Experimento LSP (5 minutos):** crie temporariamente uma `Bicicleta` cujo `preco_base` lance `NotImplementedError` ("bicicleta não se aluga por dia") e acrescente-a em `SUBCLASSES`. O teste quebra, mostrando que a `Bicicleta` **não é** um `Veiculo` substituível. Apague-a depois.  

---  

## Passo 9 — Antes × depois: custo e benefício  

Responda por escrito (itens 4 e 5 da atividade do Slide 36):  

| Métrica | Legado | Refatorado |  
|---|---|---|  
| Arquivos `.py` (sem testes) | 1 | ? |  
| Classes | 1 | ? |  
| Linhas para acrescentar o Caminhão | (edita `calcular`, `cadastrar`) | ? |  
| Dá para testar sem disco? | Não | ? |  

1. Qual o **custo** de aplicar SOLID aqui? (pense em quem chega ao projeto amanhã)  
2. Qual o **benefício** que você já sentiu neste tutorial?  
3. **Quando não aplicar?** (protótipo descartável, script de 50 linhas, prazo de horas...)  

---  

## Passo 10 — Registrar a decisão em um ADR  

Crie `aula18/meu/docs/adr/0001-aplicar-solid-na-locadora.md` no formato do Slide 34:  

```markdown
# ADR-0001: <título curto da decisão>

- **Status:** Proposto | Aceito | Substituído
- **Contexto:** o problema e as forças em jogo (o que acontece hoje, o que vem pela frente)
- **Decisão:** o que foi decidido
- **Alternativas consideradas:** pelo menos uma, com o motivo da rejeição
- **Consequências:** (+) ganhos e (−) custos
```

Compare com `aula18/solucao/docs/adr/0001-aplicar-solid-na-locadora.md`. **O seu ADR não precisa ser igual**: a decisão é sua (inclusive "não aplicar" pode ser uma decisão válida, se bem justificada).  

### Checklist de entrega  

- [ ] `python -m unittest -v` verde em `aula18/meu` (legado + refatorado + contrato LSP)  
- [ ] `diagnostico.md` com os 5 princípios  
- [ ] Caminhão adicionado sem alterar `servico.py`  
- [ ] Respostas do Passo 9  
- [ ] ADR-0001  

---  

## 🤖 Com o Agente  

> **Primeiro à mão, depois o agente** (política: Aula 01B, Slide 32). Use o agente **depois** de fazer os Passos 2 a 8, para comparar, revisar e acelerar, nunca para pular o raciocínio.  

### Setup (uma vez)  

O setup é o da **Aula 01B**: OpenCode + modelo gratuito via OpenRouter.  

1. Abra o terminal **na pasta `ua5-workspace/`** e rode `opencode`. O agente lê sozinho o `AGENTS.md` do workspace, que já diz que você trabalha em `aulaXX/meu`, que não há dependências externas e qual o comando de testes.  
2. Skill nova desta aula:  

   ```bash
   npx skills@latest add mattpocock/skills -a opencode -s grill-with-docs -y
   ```

3. Confira com `opencode debug skill`. Para economizar o limite diário, comece com *"Use a skill caveman full para o restante desta sessão."*  

> Usa **Claude Code**? Os mesmos prompts funcionam; o arquivo de instruções do projeto é o mesmo `AGENTS.md` (ou `CLAUDE.md`, Aula 01B, Slide 29).  

### Prompts por passo  

| Passo | Modo | Prompt |  
|---|---|---|  
| 2 | **plan** (Tab) | *"Analise aula18/meu/locadora_legado.py e aponte, para cada princípio SOLID, as violações — com o trecho e o porquê. Não edite nada."* Compare com o **seu** diagnóstico. |  
| 3-6 | build | *"Refatore aula18/meu/locadora_legado.py aplicando um princípio por vez — primeiro SRP, depois DIP —, rodando `python -m unittest -v` a cada passo. Pare e me mostre o diff entre os passos."* |  
| 7 | plan | *"Compare os testes de aula18/meu/tests/test_caracterizacao.py com os do refatorado. Algum cenário do legado ficou sem equivalente?"* |  
| 8 | build | *"Adicione o tipo Caminhao (diária × dias × 1,10 + R$ 50) sem alterar servico.py, precos.py nem repositorio.py. Mostre quais arquivos mudaram."* |  
| 10 | build | *"Use a skill grill-with-docs para me questionar sobre a decisão de aplicar SOLID na Locadora e registrar um ADR em aula18/meu/docs/adr/."* |  

### Você valida  

- **Refatoração sem teste é aposta:** exija `python -m unittest -v` verde **antes** e **depois** de cada mudança do agente.  
- **Padrão GoF só onde resolve um problema real.** O agente adora aplicar Strategy, Factory, Observer e Abstract Factory "por precaução". Pergunte: *qual problema deste código este padrão resolve hoje?*  
- O agente pode "melhorar" valores (arredondar de outro jeito, mudar a mensagem do e-mail). Os testes de caracterização pegam isso; **não aceite mudar o teste para o código passar**.  
- No ADR, **a decisão é sua**: a IA rascunha as alternativas.  

### Registro para a "Parte com o Agente" da atividade  

| Prompt (resumo) | O que o agente acertou | O que errou ou omitiu | O que você corrigiu |  
|---|---|---|---|  
| | | | |  

---  

## Problemas comuns  

| Sintoma | Causa provável | Solução |  
|---|---|---|  
| `python: command not found` | Linux/macOS usam `python3` | Use `python3` |  
| `ModuleNotFoundError: No module named 'locadora'` | Comando rodado fora de `aula18/meu` | `cd aula18/meu` antes de `python -m ...` |  
| `ImportError: attempted relative import with no known parent package` | Rodou `python locadora/main.py` | Use `python -m locadora.main` |  
| `Ran 0 tests` | Falta `tests/__init__.py` ou o arquivo não começa com `test_` | Crie o `__init__.py` vazio; nomeie `test_*.py` |  
| Teste do legado falha depois da refatoração | Você mudou o comportamento | Compare valores; o teste está certo até prova em contrário |  

## Próxima aula  

Na **Aula 19**, o `ServicoLocacao` deste tutorial vira o **Model** de três padrões de apresentação (MVC, MVP e MVVM), no terminal e na web. O ponto de partida (`aula19/inicio`) é cópia de `aula18/solucao`. Se preferir continuar do **seu** código: `python ferramentas/preparar.py 19 --da-minha`.  
