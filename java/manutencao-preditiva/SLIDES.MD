# PREDIX — Roteiro de Apresentação

---

## Slide 1 — Tecnologias Utilizadas

> "Para garantir que o PREDIX seja um sistema robusto e estável, utilizamos a stack tecnológica mais moderna do ecossistema Java."

**Stack utilizada:**

| Tecnologia   | Versão  | Papel                                              |
|--------------|---------|----------------------------------------------------|
| Java         | 17 LTS  | Linguagem principal — estabilidade a longo prazo   |
| Spring Boot  | 3.5.x   | Orquestração e injeção de dependências             |
| Maven        | —       | Gerenciamento de build e dependências              |
| JUnit 5      | —       | Suíte de testes automatizados                      |

**Pontos a destacar:**
- Java 17 LTS foi escolhido pela estabilidade garantida em produção
- Spring Boot elimina boilerplate e facilita manutenção futura
- JUnit 5 como "seguro": garante que cada linha de código funcione como planejado

---

## Slide 2 — Arquitetura e Pilares do Código

> "Dividimos o sistema em dois grandes blocos." *(Destaque o código no print)*

### Bloco 1 — Models (lado esquerdo)

- **Encapsulamento:** todos os atributos são `private`, protegendo a regra de negócio
- **Membros estáticos:** gerenciam IDs de forma global, simulando um banco de dados em memória

### Bloco 2 — MainApplication (lado direito)

- Orquestra tudo via Spring (`CommandLineRunner`)
- Código limpo e modular — facilita adicionar novos tipos de sensores ou máquinas no futuro

**Conceito-chave:** separação de responsabilidades entre dados (`model/`) e fluxo (`Application`)

---

## Slide 3 — Lógica de Risco

> "O diferencial do PREDIX é que ele **não usa uma média simples**." *(Fale com confiança — este é o "cérebro" do projeto)*

### Algoritmo de Pesos Ponderados

| Fator         | Peso Máx | Faixas (threshold-based)                         |
|---------------|----------|--------------------------------------------------|
| Temperatura   | 40 pts   | >90°C = 40 \| >75°C = 30 \| >60°C = 18 \| >45°C = 8 \| >35°C = 2 |
| Vibração      | 35 pts   | >15 mm/s = 35 \| >10 = 25 \| >5 = 14 \| >2 = 5  |
| Carga Op.     | 15 pts   | >95% = 15 \| >85% = 10 \| >70% = 5 \| >50% = 1  |
| Horas de Uso  | 10 pts   | >5000h = 10 \| >2000h = 7 \| >1000h = 4 \| >500h = 2 |

### Status resultante (soma dos fatores, cap em 100)

| Pontuação  | Status    |
|------------|-----------|
| 0 – 19     | NORMAL    |
| 20 – 39    | ATENÇÃO   |
| 40 – 69    | MODERADO  |
| 70 – 100   | CRÍTICO   |

**Por que não-linear?** Se a temperatura ultrapassa 90°C, o risco salta imediatamente para 40 pontos. Isso evita que falhas críticas sejam camufladas por outros dados normais.

---

## Slide 4 — Algoritmo de Simulação de Ciclos

> "Para provar que a predição funciona, criamos um **simulador de desgaste industrial**."

### Como funciona

- **1 ciclo = 168 horas** (uma semana de operação contínua)
- A simulação permite observar a tendência de falha ao longo de meses em segundos

### Por que usar `Math.random()` (ruído branco)?

```
Sem ruído:  20°C → 23°C → 26°C → 29°C  (subida matemática perfeita — irreal)
Com ruído:  20°C → 22.7°C → 25.3°C → 28.9°C  (oscila como sensor real)
```

- Injeta variação realista nos valores gerados
- Simula comportamento de sensores industriais reais
- Permite observar tendência de falha sem valores perfeitamente lineares

---

## Slide 5 — Resiliência com Tratamento de Erros

> "O sistema foi construído para ser **'inquebrável'**." *(Este slide "vende" a qualidade do trabalho)*

### Try/Catch — Sistema não crasha

```java
// Se o usuário digitar um custo negativo:
// → não lança exceção para o usuário
// → exibe mensagem amigável e solicita novo input
```

- Todas as entradas numéricas passam por `lerInt()` e `lerDouble()`
- `InputMismatchException` é capturada e tratada internamente

### Regex — Validação de dados

- **CNPJ:** formato validado antes de aceitar o cadastro
- **E-mail:** formato validado na entrada do usuário

### Resultado final

> **186 testes automatizados com 100% de aprovação**

Prova a robustez total do software e garante que refatorações futuras não quebrem o comportamento existente.
