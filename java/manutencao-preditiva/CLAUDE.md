# PREDIX - Sistema de Manutencao Preditiva Industrial

## Contexto do Projeto

Projeto academico de Sistemas de Informacao desenvolvido em grupo.
O sistema simula um ambiente industrial onde maquinas possuem sensores que
coletam dados em tempo real. Com base nesses dados, o sistema calcula um
indice de risco de falha e gera alertas preventivos automaticos.

**Objetivo pedagogico:** demonstrar conceitos de POO (classes, atributos,
construtores, metodos, sobrecarga), monitoramento inteligente, analise de risco
e simulacao de IA simples via terminal.

---

## Stack e Ambiente

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.5.13
- **Execucao:** `CommandLineRunner` — sistema inteiramente via terminal, sem API REST e sem frontend
- **Build:** Maven
- **Banco de dados:** nenhum — toda a persistencia e em memoria (listas em runtime)
- **OS de desenvolvimento:** Windows (PowerShell) — encoding ASCII puro nas strings para evitar corrupcao de caracteres

---

## Estrutura de Arquivos

```
src/main/java/com/grupo/manutencao_preditiva/
├── ManutencaoPreditivaApplication.java   # Entry point + toda a logica de UI/menus
└── model/
    ├── Maquina.java        # Entidade central; tem lista de Sensor e historico de leituras
    ├── Sensor.java         # Coleta leituras; calcula media, max e min automaticamente
    ├── Predicao.java       # Calcula risco (0-100%) com logica nao-linear por faixas
    ├── Alerta.java         # Gerado automaticamente apos cada predicao; historico estatico
    ├── Manutencao.java     # Ciclo de vida: EM_ANDAMENTO -> CONCLUIDA | CANCELADA
    ├── Empresa.java        # Vinculada ao Usuario e a Maquina
    ├── Usuario.java        # Operador da sessao atual
    ├── LogAtividade.java   # Registro imutavel de cada acao do usuario
    └── DadosMaquina.java   # Snapshot de leitura de um Sensor em um momento
```

---

## Arquitetura de Execucao

O sistema nao usa camada de servico nem repositorios. Toda a logica vive em dois lugares:

1. **`ManutencaoPreditivaApplication`** — UI, navegacao de menus, leitura de input, orquestracao do fluxo
2. **Classes `model/`** — encapsulam dados e logica de negocio propria (calculo de risco, formatacao de relatorios, validacao de setters)

**Restricao importante:** nao adicionar `@Service`, `@Repository`, `@Controller` nem JPA/Hibernate nas entidades. O projeto usa Spring Boot apenas pelo `CommandLineRunner` e pelo servidor Tomcat embutido que sobe junto (inofensivo, pode ser ignorado).

---

## Fluxo Principal

```
run()
 └── cadastrarSessao()          # Coleta Empresa + Usuario da sessao
      └── menuPrincipal()       # Loop principal (opcao 0 encerra)
           ├── menuMaquinas()       # Cadastrar, listar, selecionar maquina ativa
           ├── menuLeituras()       # Inserir leitura manual ou simular degradacao
           ├── menuPredicao()       # Calcular risco e gerar Alerta automaticamente
           ├── menuManutencoes()    # Registrar e finalizar manutencoes
           ├── menuAlertas()        # Exibir historico de alertas da sessao
           ├── menuRelatorios()     # Relatorios de sessao, maquina e manutencoes
           └── menuLogs()           # Log cronologico de todas as acoes
```

---

## Modelo de Dados e Relacionamentos

```
Empresa  <──  Usuario         (1 empresa tem N usuarios)
Empresa  <──  Maquina         (1 empresa tem N maquinas)
Maquina  ──>  List<Sensor>    (cada maquina tem 2 sensores fixos: Temperatura e Vibracao)
Maquina  ──>  List<String>    (historicoLeituras: ultimas N leituras formatadas)
Sensor   ──>  List<Double>    (historico de valores para calcular media/max/min)
Predicao ──>  Maquina         (transiente — criada, calculada e descartada a cada analise)
Alerta   ──>  Maquina         (persistido em lista estatica em Alerta.historico)
Manutencao -> Maquina         (persistida em lista na Application)
LogAtividade -> Usuario       (persistido em lista na Application)
```

---

## Logica de Calculo de Risco (Predicao.java)

Risco total = soma de 4 fatores, cap em 100 pontos.
Cada fator usa faixas nao-lineares (threshold-based), nao formula linear:

| Fator       | Peso max | Faixas de entrada                              |
|-------------|----------|------------------------------------------------|
| Temperatura | 40 pts   | >90C=40, >75C=30, >60C=18, >45C=8, >35C=2    |
| Vibracao    | 35 pts   | >15mm/s=35, >10=25, >5=14, >2=5               |
| Carga Op.   | 15 pts   | >95%=15, >85%=10, >70%=5, >50%=1              |
| Horas de Uso| 10 pts   | >5000h=10, >2000h=7, >1000h=4, >500h=2        |

**Status resultante:**
- 0-19 pts  -> NORMAL
- 20-39 pts -> ATENCAO
- 40-69 pts -> MODERADO
- 70-100 pts -> CRITICO

---

## Convencoes do Codigo

**Encoding:** todas as strings literais que vao para o terminal usam ASCII puro — sem acentos, sem caracteres especiais Unicode. Isso e intencional para compatibilidade com o terminal Windows (PowerShell/CMD usa CP850). Nao restaurar acentos em strings de `println`, `printf` ou `format`.

**Scanner unico:** existe um unico `Scanner sc` estatico em `ManutencaoPreditivaApplication`. Nao criar novos `Scanner(System.in)` em outros lugares — causa conflito de buffer.

**Leitura de input:** usar sempre `lerInt()` e `lerDouble()` (metodos privados da Application) em vez de `sc.nextInt()` direto — eles tem tratamento de `InputMismatchException` e consomem o `\n` residual corretamente.

**Flush do stdin:** no inicio de `run()` ha um `System.in.available() > 0 ? System.in.read()` que descarta o `\n` que o Spring Boot deixa no buffer ao inicializar. Nao remover essa linha.

**Sem persistencia entre sessoes:** ao encerrar o sistema, todos os dados sao perdidos. Isso e esperado — o projeto nao tem banco de dados.

**Contadores de ID:** `contadorMaquina`, `contadorManutencao` e `contadorLog` sao inteiros simples na Application, incrementados manualmente a cada criacao. Nao usar UUID nem auto-increment.

**Sensores fixos:** cada `Maquina` recebe exatamente 2 sensores ao ser cadastrada — `Sensor(1, "Temperatura", "C")` e `Sensor(2, "Vibracao", "mm/s")`. A logica de leitura acessa sempre por indice: `sensores.get(0)` = temperatura, `sensores.get(1)` = vibracao.

---

## Padroes de UI no Terminal

Todos os elementos visuais usam apenas caracteres ASCII:

```
linha('=', 52)     ->  ====================================================
linha('-', 52)     ->  ----------------------------------------------------
linha('!', 52)     ->  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
cabecalho(titulo)  ->  == / || PREDIX >> TITULO / ==
secao(titulo)      ->  -- TITULO --------------------
sucesso(msg)       ->  [OK] msg
aviso(msg)         ->  [!!] msg
info(msg)          ->   [i] msg
barraRisco()       ->  RISCO TOTAL  [########............] 42.0%
barraFator()       ->  [#####.....]  (valor por fator na predicao)
```

Status de simulacao: `[ NORMAL]`, `[MONITOR]`, `[ATENCAO]`, `[CRITICO]`

---

## O Que Nao Mudar Sem Alinhamento

- A ausencia de camada de servico e repositorio e uma restricao do escopo academico atual
- O sistema nao deve ganhar API REST, endpoints ou banco de dados nesta fase
- Nao migrar para modulos separados ou multiplos arquivos de configuracao Spring
- A logica de menus e navegacao vive propositalmente toda na `ManutencaoPreditivaApplication`

---

## Pontos de Extensao Conhecidos

Areas que podem ser melhoradas sem quebrar o escopo:

- `Predicao.calcularRisco()` — a logica de faixas pode ser refinada com mais granularidade
- `simularDegradacao()` — o incremento fixo de `+3C` e `+0.8 mm/s` por ciclo pode receber variacao configuravel pelo usuario
- `Alerta.historico` e uma lista estatica — se o sistema suportar multiplas sessoes no futuro, precisara ser resetado
- `exibirHistorico()` em `Maquina` mostra fixo as ultimas 5 leituras — o limite pode virar parametro
- `DadosMaquina` existe no modelo mas nao e instanciada ainda — pode ser usada para persistir leituras individualmente por sensor