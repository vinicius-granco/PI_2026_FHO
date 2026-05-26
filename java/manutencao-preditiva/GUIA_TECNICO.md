# PREDIX — Guia de Estudo e Apresentacao

> **Como usar este guia:** Leia na ordem. A Secao 1 e o seu discurso de abertura.
> As Secoes 2 a 5 sao o vocabulario tecnico que voce precisa dominar.
> A Secao 6 e o treino de arguicao — simule as perguntas em voz alta antes da apresentacao.

---

## 1. Resumo Executivo

### O que e o PREDIX?

O PREDIX e um **sistema de manutencao preditiva industrial** desenvolvido em Java.
Ele simula um ambiente de fabrica onde maquinas possuem sensores (temperatura e vibracao)
que coletam dados continuamente. Com base nesses dados, o sistema calcula um **indice de
risco de falha de 0 a 100%** e gera alertas preventivos automaticamente.

### Qual problema industrial ele resolve?

Em industrias reais, existem tres estrategias de manutencao:

| Estrategia | Como funciona | Problema |
|---|---|---|
| **Corretiva** | Conserta quando quebra | Parada nao planejada, custo alto |
| **Preventiva** | Manutenção por calendario fixo | Troca peca que ainda funciona |
| **Preditiva** | Monitora sinais de degradacao e age antes da falha | **O que o PREDIX faz** |

A manutencao preditiva e a mais eficiente: voce so age quando os dados indicam
necessidade real, eliminando paradas surpresa e desperdicio de pecas.

### Os tres pilares do sistema

```
+------------------+    +------------------+    +------------------+
|  MONITORAMENTO   | -> |    PREDICAO      | -> |   MANUTENCAO     |
|                  |    |                  |    |                  |
| Sensores coletam | -> | Algoritmo calcula| -> | Tecnico registra |
| Temp e Vibracao  |    | risco 0-100%     |    | e finaliza a     |
| a cada leitura   |    | e gera Alerta    |    | intervencao      |
+------------------+    +------------------+    +------------------+
```

---

## 2. Dicionario Tecnico para Leigos

### 2.1 Encapsulamento (`private`)

**Analogia:** Pense numa maquina de cafe. Voce aperta o botao (metodo publico)
e o cafe sai. Voce nao acessa diretamente a resistencia interna (campo privado).

No PREDIX, todos os atributos das classes sao `private`. Para ler ou alterar
um valor, voce passa pelo getter/setter — que pode ter regras de validacao:

```java
// Em Maquina.java:
private double temperatura;  // campo privado: ninguem acessa diretamente

// O setter valida antes de aceitar:
public void setTemperatura(double temperatura) {
    this.temperatura = Math.max(0, temperatura); // nunca aceita negativo
}
```

**Por que isso importa?** Se qualquer parte do codigo pudesse escrever
`maquina.temperatura = -500`, o sistema quebraria silenciosamente.
O encapsulamento garante que dados invalidos nunca entrem no objeto.

---

### 2.2 `static` — o que pertence a classe, nao ao objeto

**Analogia:** O placar de um estadio exibe o mesmo numero para todos no estadio.
Nao e "o meu placar" ou "o seu placar" — e o placar do jogo.

No PREDIX, `Alerta.historico` e uma lista `static`:

```java
// Em Alerta.java:
private static List<Alerta> historico = new ArrayList<>();
private static int contadorId = 1;
```

Isso significa que independente de quantos objetos `Alerta` existam,
todos compartilham a mesma lista. Quando o sistema gera um alerta,
ele vai para essa lista unica, acessivel como `Alerta.getHistorico()`.

**Contraexemplo — o que NAO e static:**
O `List<Double> historico` dentro de `Sensor` **nao e static**. Cada sensor
tem o seu proprio historico de leituras. Sensor 1 (Temperatura) e Sensor 2
(Vibracao) acumulam seus dados separadamente.

---

### 2.3 Construtores e Sobrecarga

**Analogia:** Um formulario de cadastro pode ter versao simplificada (so nome)
ou versao completa (nome, endereco, telefone). Ambos sao o mesmo cadastro,
mas com niveis diferentes de detalhe na criacao.

A classe `Maquina` tem tres construtores — isso e chamado de **sobrecarga**:

```java
// Construtor 1: Basico — nome e fabricante
public Maquina(int id, String nome, String fabricante) { ... }

// Construtor 2: Com empresa vinculada (chama o construtor 1 via this())
public Maquina(int id, String nome, String fabricante, Empresa empresa) {
    this(id, nome, fabricante);   // reusa o codigo do construtor 1
    this.empresa = empresa;
}

// Construtor 3: Padrao (sem argumentos) — obrigatorio para o Spring
public Maquina() {}
```

A chamada `this(id, nome, fabricante)` evita duplicar codigo: o construtor 2
delega para o construtor 1 e depois adiciona apenas o que e especifico a ele.

---

### 2.4 Listas em Memoria (`ArrayList`)

O PREDIX **nao tem banco de dados**. Todos os dados vivem em listas Java que
existem apenas enquanto o programa esta rodando:

```java
// Em ManutencaoPreditivaApplication.java:
private final List<Maquina>      maquinas    = new ArrayList<>();
private final List<Manutencao>   manutencoes = new ArrayList<>();
private final List<LogAtividade> logs        = new ArrayList<>();
```

| Dado | Onde vive | Dura ate quando |
|---|---|---|
| Maquinas cadastradas | `List<Maquina> maquinas` | Fim da sessao |
| Alertas gerados | `Alerta.historico` (static) | Fim da sessao |
| Historico de leituras | `List<String>` dentro de `Maquina` | Fim da sessao |
| Leituras por sensor | `List<Double>` dentro de `Sensor` | Fim da sessao |

**Decisao de projeto:** Para um sistema academico que demonstra POO, guardar em
memoria e suficiente e elimina a complexidade de configurar banco de dados.
O foco e nos conceitos de objeto, nao em persistencia.

---

### 2.5 O `@Bean` do Scanner — Por que isso existe?

**Problema original:** O `Scanner` era declarado como `private static final`:

```java
// ANTES (problematico):
private static final Scanner sc = new Scanner(System.in);
```

Isso criava dois problemas:
1. **Testabilidade zero:** Os testes precisavam de input real do teclado — impossivel em CI/CD.
2. **Acoplamento:** A classe sabia exatamente como obter o Scanner (direto do System.in).

**Solucao com `@Bean` e injecao:**

```java
// Em ManutencaoPreditivaApplication.java:
@Bean
public static Scanner scanner() {
    return new Scanner(System.in);   // producao: le do teclado real
}

private final Scanner sc;

public ManutencaoPreditivaApplication(Scanner sc) {
    this.sc = sc;   // Spring injeta o Scanner pelo construtor
}
```

```java
// Nos testes:
@Bean @Primary
public Scanner testScanner() {
    String input = "EmpresaTeste\n12.345.678/0001-99\n...\n0\n";
    return new Scanner(new ByteArrayInputStream(input.getBytes()));
}
```

**Por que `static @Bean`?** O Spring processaria a classe primeiro para criar o
`@Bean`, mas a classe precisa do `@Bean` para ser criada — dependencia circular.
O modificador `static` quebra esse ciclo: metodos `static @Bean` sao processados
antes da instanciacao da classe.

---

## 3. A Logica de Risco — Como o Algoritmo Funciona

### 3.1 Por que faixas (thresholds) e nao formula linear?

**Exemplo de formula linear (o que NAO usamos):**
```
risco = temperatura * 0.4  // 80°C -> 32%, 90°C -> 36%
```
O problema: a diferenca entre 80°C e 90°C seria de apenas 4 pontos.
Na realidade industrial, **90°C e um estado critico, nao apenas "um pouco pior"
que 80°C**.

**Nossa abordagem: faixas nao-lineares**

```
Temperatura -> pontos de risco:
  > 90°C  = 40 pts  (zona critica)
  > 75°C  = 30 pts  (zona de alerta severo)
  > 60°C  = 18 pts  (zona de atencao)
  > 45°C  = 8 pts   (zona de monitoramento)
  > 35°C  = 2 pts   (levemente elevada)
  <= 35°C = 0 pts   (normal)
```

A diferenca entre 80°C e 90°C agora e de 22 pontos — refletindo a gravidade real.

### 3.2 Os 4 fatores e seus pesos maximos

| Fator | Peso Max | Justificativa |
|---|---|---|
| Temperatura | **40 pts** | Principal indicador de desgaste mecanico |
| Vibracao | **35 pts** | Indica desbalanceamento e fadiga estrutural |
| Carga Operacional | **15 pts** | Fator agravante dos outros dois |
| Horas de Uso | **10 pts** | Desgaste acumulado ao longo do tempo |
| **TOTAL** | **100 pts** | Cap aplicado com `Math.min(soma, 100)` |

### 3.3 Tabela completa de faixas

**Temperatura (max 40 pts):**
```
Temp (°C) |  > 90  |  > 75  |  > 60  |  > 45  |  > 35  | <= 35
Pontos    |   40   |   30   |   18   |    8   |    2   |    0
```

**Vibracao (max 35 pts):**
```
Vib (mm/s)|  > 15  |  > 10  |   > 5  |   > 2  | <= 2
Pontos    |   35   |   25   |   14   |    5   |   0
```

**Carga Operacional (max 15 pts):**
```
Carga (%) |  > 95  |  > 85  |  > 70  |  > 50  | <= 50
Pontos    |   15   |   10   |    5   |    1   |    0
```

**Horas de Uso (max 10 pts):**
```
Horas     | > 5000 | > 2000 | > 1000 |  > 500 | <= 500
Pontos    |   10   |    7   |    4   |    2   |    0
```

### 3.4 Status resultante

```
0  a 19 pts  ->  NORMAL    (operacao segura)
20 a 39 pts  ->  ATENCAO   (monitorar com frequencia maior)
40 a 69 pts  ->  MODERADO  (agendar manutencao preventiva)
70 a 100 pts ->  CRITICO   (parada imediata recomendada)
```

### 3.5 Exemplo de calculo manual

Cenario: maquina com Temp=80°C, Vib=12mm/s, Carga=88%, Horas=3500h

```
Temperatura 80°C  -> faixa >75°C  = 30 pts
Vibracao 12mm/s   -> faixa >10    = 25 pts
Carga 88%         -> faixa >85%   = 10 pts
Horas 3500h       -> faixa >2000h =  7 pts
                                  --------
SOMA                              = 72 pts  ->  STATUS: CRITICO
```

---

## 4. A Simulacao de Degradacao

### 4.1 A formula matematica

A simulacao imita a degradacao progressiva de uma maquina ao longo de semanas:

```
Para cada ciclo i:

  tempAtual[i]  = tempAtual[i-1] + 3.0 + Random(-1.0, +1.0)
  vibAtual[i]   = vibAtual[i-1]  + 0.8 + Random(-0.2, +0.2)
  horas[i]      = horas[i-1]     + 168
```

Em codigo Java (trecho de `simularDegradacao()`):

```java
tempAtual += 3.0 + (Math.random() * 2 - 1);    // +3°C ± 1°C de ruido
vibAtual  += 0.8 + (Math.random() * 0.4 - 0.2); // +0.8mm/s ± 0.2 de ruido
horas     += 168;                                // 1 semana = 7 dias x 24h
```

### 4.2 Por que 168 horas por ciclo?

**1 ciclo = 1 semana de operacao continua.**
168 = 7 dias × 24 horas. Em industrias de processo continuo (siderurgicas,
petroquimicas, usinas), a maquina roda 24h por dia, 7 dias por semana.

### 4.3 Por que adicionar ruido aleatorio?

A degradacao real nao e perfeitamente linear. Fatores como variacao de carga,
temperatura ambiente e qualidade do lubrificante criam flutuacoes. O ruido
`Random(-1, +1)` simula esse comportamento estocastico e torna os graficos
mais realistas — a temperatura nao sobe exatamente 3°C toda semana.

### 4.4 O que acontece ao longo dos ciclos?

Com os incrementos fixos, um ciclo de 10 semanas partindo de Temp=40°C:

```
Semana 1:  ~43°C  -> NORMAL
Semana 3:  ~49°C  -> ATENCAO (cruza 45°C)
Semana 5:  ~55°C  -> ATENCAO
Semana 7:  ~61°C  -> MODERADO (cruza 60°C)
Semana 10: ~70°C  -> MODERADO / proximo de CRITICO
```

A progressao demonstra visualmente como um equipamento se deteriora sem intervencao.

---

## 5. O Ciclo de Vida da Manutencao

### 5.1 A maquina de estados

```
              abrir()
    INEXISTENTE --------> EM_ANDAMENTO
                               |
              finalizar()  ----|----  cancelar()
                   |                      |
                   v                      v
              CONCLUIDA              CANCELADA
```

**Regra de ouro:** Uma vez CONCLUIDA ou CANCELADA, a manutencao nao pode
mais ser alterada. O status e definitivo.

### 5.2 Os guardas de integridade

A classe `Manutencao.java` implementa dois guardas que lancom excecoes
controladas caso o codigo tente violar as regras:

**Guarda 1 — Transicao invalida (`IllegalStateException`):**
```java
public void finalizar(double custo, String descricao) {
    if (!"EM_ANDAMENTO".equals(status))
        throw new IllegalStateException("Transicao invalida: status atual=" + status);
    // ... resto do metodo
}
```
Se alguem tentar finalizar uma manutencao ja CONCLUIDA, o sistema lanca
a excecao ao inves de corromper os dados.

**Guarda 2 — Custo negativo (`IllegalArgumentException`):**
```java
    if (custo < 0)
        throw new IllegalArgumentException("Custo nao pode ser negativo: " + custo);
```
Custo negativo nao faz sentido de negocio. Em vez de aceitar e gerar
relatorios errados, a excecao e lancada e o status permanece EM_ANDAMENTO.

**Como a Application lida com as excecoes:**
```java
try {
    abertas.get(idx).finalizar(custo, desc);
    sucesso("Manutencao finalizada com sucesso!");
} catch (IllegalArgumentException | IllegalStateException e) {
    aviso(e.getMessage());   // exibe mensagem amigavel, nao trava o sistema
}
```

### 5.3 Por que usar excecoes ao inves de `if/else` e retorno booleano?

Com retorno booleano (`return false`), o chamador pode ignorar o resultado.
Com excecao, o sistema **forcosamente** interrompe o fluxo incorreto.
E uma garantia de integridade, nao uma sugestao.

---

## 6. Prováveis Perguntas do Professor — Respostas Sugeridas

---

### P1: "Por que voces usaram Spring Boot se o sistema nao tem API REST?"

**Resposta:**
> "Usamos o Spring Boot como infraestrutura de execucao, nao para web.
> A interface `CommandLineRunner` do Spring nos permite usar a injecao de
> dependencias — especificamente o Scanner injetado via construtor — sem
> precisar de endpoints REST. O Tomcat sobe junto mas fica ocioso: toda
> a interacao e pelo terminal. Isso demonstra que o Spring e um framework
> de aplicacao geral, nao exclusivamente web."

---

### P2: "O que e injecao de dependencias e onde voces aplicaram?"

**Resposta:**
> "Injecao de dependencias e quando um objeto recebe suas colaboradoras
> de fora em vez de cria-las internamente. No nosso projeto, o `Scanner`
> e declarado como `@Bean` — o Spring gerencia sua criacao — e injetado
> pelo construtor da `ManutencaoPreditivaApplication`. A vantagem direta
> foi a testabilidade: nos testes, substituimos o `Scanner(System.in)`
> por um `Scanner(ByteArrayInputStream)` sem alterar uma linha da logica
> de negocio."

---

### P3: "Por que a lista de alertas e `static`?"

**Resposta:**
> "A lista `Alerta.historico` e static porque representa um registro
> global da sessao, nao o estado de um objeto individual. Qualquer
> predicao gerada em qualquer ponto do sistema precisa acrescentar
> seu alerta nessa mesma lista central, que e depois consultada no
> menu de historico. E o mesmo raciocinio do placar de um jogo: nao
> faz sentido cada torcedor ter o seu proprio placar."

---

### P4: "Como voces garantem que o algoritmo de risco nao ultrapasse 100%?"

**Resposta:**
> "Aplicamos `Math.min(soma, 100.0)` apos somar os quatro fatores.
> Matematicamente, o maximo possivel e 40 + 35 + 15 + 10 = 100, entao
> o cap so ativaria em cenarios onde os arredondamentos internos
> excedessem 100. E uma garantia defensiva: qualquer futura alteracao
> nas faixas nao vai gerar um indice de 103%, tornando os relatorios
> incoerentes."

---

### P5: "O que e sobrecarga de metodos e onde voces usaram?"

**Resposta:**
> "Sobrecarga e ter dois ou mais metodos com o mesmo nome mas assinaturas
> diferentes — o compilador escolhe o correto pelo numero e tipo dos
> argumentos. Usamos em varios lugares. Em `Maquina`, o metodo
> `atualizarLeituras` tem duas versoes: uma aceita apenas temperatura
> e vibracao (para leitura manual simples) e outra aceita os quatro
> parametros (para a simulacao completa). Em `Sensor`, `registrarLeitura`
> tem uma versao simples e uma que tambem associa a maquina ao sensor."

---

### P6: "Por que voces nao persistem os dados em banco de dados?"

**Resposta:**
> "Foi uma decisao de escopo academico deliberada. O objetivo do projeto
> e demonstrar conceitos de POO — encapsulamento, heranca, composicao,
> construtores, sobrecarga — e o fluxo de um sistema de monitoramento.
> Adicionar JPA e banco de dados deslocaria o foco para mapeamento
> objeto-relacional, que e materia de outro modulo. Toda a estrutura
> de dados — listas, relacoes entre objetos, historico — foi projetada
> para ser facilmente migravel para banco em uma segunda fase."

---

### P7: "Explique a relacao entre as classes do modelo."

**Resposta:**
> "A Empresa e a raiz: ela agrega Usuarios e Maquinas. Cada Maquina
> e criada com dois Sensores fixos — Temperatura e Vibracao — que
> acumulam leituras individualmente. Quando pedimos uma analise,
> criamos um objeto Predicao transiente, passamos a Maquina para
> `calcularRisco()` e o objeto Predicao calcula e descarta. O resultado
> persiste como um Alerta na lista estatica. A Manutencao referencia
> a Maquina sobre a qual atua. E o LogAtividade registra cada acao
> do Usuario — quem fez, o que fez, quando fez."

---

### P8: "O que significa o `Math.random() * 2 - 1` na simulacao?"

**Resposta:**
> "`Math.random()` retorna um numero entre 0.0 (inclusivo) e 1.0
> (exclusivo). Multiplicando por 2 temos o intervalo [0, 2).
> Subtraindo 1 temos [-1, 1). Isso e o ruido da temperatura:
> cada ciclo adiciona exatamente 3 graus mais ou menos ate 1 grau
> de variacao aleatoria. Para a vibracao, `Math.random() * 0.4 - 0.2`
> produz ruido em [-0.2, 0.2) — proporcional ao incremento menor
> de 0.8 mm/s."

---

### P9: "Como os testes unitarios funcionam? O que eles validam?"

**Resposta:**
> "Usamos JUnit 5 puro para os modelos e `@SpringBootTest` para a
> Application. Os testes de modelo validam: que o construtor inicializa
> corretamente, que as transicoes de estado seguem as regras (tentar
> finalizar uma manutencao ja cancelada deve lancar excecao), que os
> calculos de risco atingem exatamente os pontos esperados em cada
> threshold. O teste da Application injeta um Scanner com input
> simulado via `ByteArrayInputStream` para confirmar que o sistema
> inicia, processa uma sessao completa e encerra sem bloquear o stdin.
> Ao todo sao 186 testes, todos passando."

---

### P10: "Se voces fossem escalar esse sistema para producao, o que mudaria?"

**Resposta:**
> "Tres mudancas principais. Primeira, banco de dados: substituir as
> listas em memoria por JPA/Hibernate com PostgreSQL, mantendo as
> entidades praticamente identicas. Segunda, API REST: transformar
> os menus de terminal em endpoints, com a logica de negocio migrando
> para classes `@Service`. Terceira, multi-sessao: o `Alerta.historico`
> estatico precisaria ser isolado por sessao ou empresa, provavelmente
> via banco. A arquitetura atual foi projetada com essa evolucao em
> mente: modelos sem anotacoes JPA sao mais faceis de migrar do que
> modelos ja acoplados a um ORM especifico."

---

### P11: "Por que os campos `private` com getters/setters sao melhores do que campos `public`?"

**Resposta:**
> "Com campo public, qualquer codigo pode escrever `maquina.temperatura = -999`
> e nao ha como reagir. Com o setter, podemos aplicar regras:
> `this.temperatura = Math.max(0, temperatura)` garante que o valor
> nunca seja negativo, independente de quem chame o setter.
> Se no futuro precisarmos adicionar um log cada vez que a temperatura
> mudar, so alteramos o setter — sem tocar em nenhum chamador.
> Isso e o principio do encapsulamento: esconder a implementacao
> e controlar o acesso."

---

### P12: "O que acontece tecnicamente quando um Alerta e gerado?"

**Resposta:**
> "Apos `calcularRisco()`, chamamos `Alerta.gerar(maquina, risco)`.
> Esse metodo estatico cria um objeto Alerta, determina o nivel
> (NORMAL, ATENCAO, MODERADO ou CRITICO) e a mensagem com base
> nas mesmas faixas de risco, incrementa o contador estatico de ID
> para dar identidade unica ao alerta, registra a data/hora com
> `LocalDateTime.now()` e adiciona o objeto a lista estatica
> `Alerta.historico`. Essa lista e acessada pelo menu de alertas,
> pelos relatorios e pelo `Alerta.totalAlertas()` que aparece no
> resumo de sessao."

---

## Apendice — Numeros para Memorizar

| Item | Valor |
|---|---|
| Total de testes automatizados | **186** (0 falhas) |
| Classes no pacote `model/` | **9** |
| Sensores por maquina | **2** (fixos: Temperatura e Vibracao) |
| Fatores de risco | **4** (Temp, Vib, Carga, Horas) |
| Pontuacao maxima possivel | **100 pts** (40+35+15+10) |
| Incremento de temperatura/ciclo | **+3°C** (± 1°C de ruido) |
| Horas por ciclo de simulacao | **168h** (1 semana) |
| Ultima leituras exibidas no historico | **5** |
| Versao Java | **17** |
| Framework | **Spring Boot 3.5.13** |
