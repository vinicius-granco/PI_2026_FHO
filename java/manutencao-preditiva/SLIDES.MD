## Slide 1: Tecnologias Utilizadas

**O que falar:**

"Para garantir que o PREDIX seja um sistema robusto e estável, utilizamos a stack tecnológica mais moderna do ecossistema Java. Escolhemos o Java 17 LTS pela sua estabilidade a longo prazo. O Spring Boot cuida de toda a orquestração e injeção de dependências, facilitando a manutenção. O Maven gerencia nosso build, e o JUnit 5 foi nossa ferramenta de 'seguro', permitindo criar uma suíte de testes que garante que cada linha de código funcione como planejado."

---

## Slide 2: Arquitetura e Pilares do Código

**O que falar:** *(Destaque o código no print)*

"Professor, neste slide detalhamos a engenharia por trás do PREDIX. À esquerda, no código dos Models, aplicamos o Encapsulamento com atributos private, garantindo que os dados da máquina só sejam acessados e alterados de forma segura.
Notem também o uso da Sobrecarga de Construtores. Criamos múltiplas formas de inicializar um objeto: podemos criar uma máquina apenas com dados básicos ou já vinculá-la a uma empresa no momento do cadastro. Isso dá versatilidade ao código e facilita a manutenção.
Para a gestão dos dados, implementamos uma Camada de Persistência em Memória utilizando ArrayLists. Elas simulam nosso banco de dados, armazenando dinamicamente o histórico de sensores e leituras de cada máquina. Além disso, utilizamos membros estáticos para controlar a geração de IDs únicos de forma global.
À direita, a MainApplication utiliza o Spring Boot para orquestrar esses objetos. Essa estrutura modular prova que o sistema é escalável: a lógica de negócio está protegida nos modelos, enquanto a aplicação gerencia o fluxo e a interface com o usuário."

---

## Slide 3: Lógica de Risco

**O que falar:** *(Esse é o "cérebro" do projeto, fale com confiança)*

"Professor, este slide apresenta o 'cérebro' do PREDIX e onde aplicamos a real inteligência do sistema.
1. Superando a Média Simples: O grande diferencial aqui é que não utilizamos uma média aritmética simples. Em uma média comum, um sensor em estado perfeito poderia 'mascarar' ou esconder um sensor que está prestes a explodir. No PREDIX, criamos um Algoritmo de Pesos Ponderados.
2. Priorização Técnica (Pesos): Atribuímos pesos diferentes baseados na realidade física industrial: a Temperatura (40%) e a Vibração (35%) são os fatores soberanos, pois indicam falhas iminentes. Carga e Horas de Uso complementam o diagnóstico com pesos menores.
3. Lógica Não-Linear (Thresholds): Como podem ver no trecho de código à direita, a lógica é não-linear. Trabalhamos com 'thresholds', ou faixas de tolerância. Isso simula o comportamento real dos materiais: o risco não sobe devagar e constante, ele dá 'saltos' nos pontos de ruptura.
4. O Pulo do Gato: Se a temperatura ultrapassa os 90 graus, o sistema injeta 40 pontos de risco instantaneamente. Isso garante que a sensibilidade do software seja altíssima para o que realmente importa, impedindo que uma falha crítica seja camuflada por outros dados que ainda estejam normais. É um sistema programado para pensar como um especialista em manutenção."

---

## Slide 4: Algoritmo de Simulação de Ciclos

**O que falar:**

"Para provar que a predição funciona, criamos um simulador de desgaste industrial. 

1. A Escala de Tempo (Destaque 168h): Notem no canto superior que cada 'passo' da simulação representa 168 horas. Isso não é um número aleatório: equivale a uma semana inteira de operação contínua em regime industrial (24/7). Em segundos, conseguimos projetar o que aconteceria com o equipamento ao longo de meses.
2. A Lógica do Código (Bloco de Incrementos): Se olharmos para o trecho de código, vemos como o desgaste é calculado. Não é uma subida linear simples. Utilizamos um incremento fixo (de 3.0 para temperatura e 0.8 para vibração) somado a uma função estocástica usando Math.random().
3. O Ruído Branco (Matemática): Como detalhado no slide, essa fórmula gera um 'ruído branco' no intervalo de -1 a +1. Isso é fundamental para simular a realidade: sensores reais sofrem interferências de carga, temperatura ambiente e até lubrificação, fazendo os valores oscilarem.

Nós controlamos a amplitude do ruído através da matemática, professor. No caso da temperatura, usamos a fórmula Math.random() * 2 - 1 para garantir que a oscilação aleatória seja sempre pequena, variando no máximo 1 grau para cima ou para baixo em relação ao incremento fixo de 3 graus. Isso mantém a tendência de subida constante, mas com a 'sujeira' natural de um sensor real.

O método Math.random() sempre gera um número entre 0.0 e 0.999

É por isso que o resultado final sempre flutua entre -1 e +1 na temp e -0.2 e + 0.2 na vib


4. O Resultado no Gráfico: O gráfico à esquerda traduz exatamente essa execução. As linhas roxa (Temperatura) e azul (Vibração) mostram essa tendência de subida 'tremida' e realista. Vejam que, ao atingir a nona semana (W9), o sistema já cruza a linha pontilhada e dispara o status CRÍTICO, permitindo que a manutenção seja planejada com antecedência, antes da quebra total"

---

## Slide 5: Resiliência com Tratamento de Erros

**O que falar:** *(Este slide "vende" a qualidade do seu trabalho)*

"Por fim, o sistema foi construído para ser 'inquebrável'. No código à esquerda, mostramos o uso de try/catch para capturar exceções; se o usuário tentar digitar um custo negativo, o sistema não crasha, ele apenas avisa o erro de forma amigável. À direita, usamos Regex para validar CNPJ e e-mails, garantindo que os dados inseridos sejam íntegros. O resultado final está no rodapé: 186 testes automatizados com 100% de aprovação, provando a robustez total do software."
