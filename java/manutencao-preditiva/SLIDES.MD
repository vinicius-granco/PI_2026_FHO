## Slide 1: Tecnologias Utilizadas

**O que falar:**

"Para garantir que o PREDIX seja um sistema robusto e estável, utilizamos a stack tecnológica mais moderna do ecossistema Java. Escolhemos o Java 17 LTS pela sua estabilidade a longo prazo. O Spring Boot cuida de toda a orquestração e injeção de dependências, facilitando a manutenção. O Maven gerencia nosso build, e o JUnit 5 foi nossa ferramenta de 'seguro', permitindo criar uma suíte de testes que garante que cada linha de código funcione como planejado."

---

## Slide 2: Arquitetura e Pilares do Código

**O que falar:** *(Destaque o código no print)*

"Aqui dividimos o sistema em dois grandes blocos. À esquerda, os Models, onde aplicamos o Encapsulamento: todos os atributos são privados, protegendo a regra de negócio. Também usamos membros estáticos para gerenciar IDs de forma global, simulando um banco de dados em memória. À direita, a MainApplication orquestra tudo através do Spring. O código é limpo e modular, facilitando futuras expansões, como a adição de novos tipos de sensores ou máquinas."

---

## Slide 3: Lógica de Risco

**O que falar:** *(Esse é o "cérebro" do projeto, fale com confiança)*

"O diferencial do PREDIX é que ele não usa uma média simples. Criamos um Algoritmo de Pesos Ponderados. A Temperatura (40%) e a Vibração (35%) são os fatores que mais pesam no risco, pois são os principais sinais de quebra iminente. Como podem ver no trecho de código, a lógica é não-linear: usamos 'thresholds' (faixas). Se a temperatura ultrapassa 90 graus, o risco dá um salto para 40 pontos imediatamente. Isso evita que falhas críticas sejam camufladas por outros dados normais."

---

## Slide 4: Algoritmo de Simulação de Ciclos

**O que falar:**

"Para provar que a predição funciona, criamos um simulador de desgaste industrial. Cada ciclo representa 168 horas (uma semana) de operação contínua. Para tornar a simulação realista e não apenas uma subida matemática perfeita, injetamos 'ruído branco' usando Math.random(). Isso faz com que os valores oscilem levemente, como em um sensor real, permitindo observar a tendência de falha ao longo de meses em apenas alguns segundos de execução."

---

## Slide 5: Resiliência com Tratamento de Erros

**O que falar:** *(Este slide "vende" a qualidade do seu trabalho)*

"Por fim, o sistema foi construído para ser 'inquebrável'. No código à esquerda, mostramos o uso de try/catch para capturar exceções; se o usuário tentar digitar um custo negativo, o sistema não crasha, ele apenas avisa o erro de forma amigável. À direita, usamos Regex para validar CNPJ e e-mails, garantindo que os dados inseridos sejam íntegros. O resultado final está no rodapé: 186 testes automatizados com 100% de aprovação, provando a robustez total do software."
