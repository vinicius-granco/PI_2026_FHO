package com.grupo.manutencao_preditiva;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.grupo.manutencao_preditiva.model.Alerta;
import com.grupo.manutencao_preditiva.model.Empresa;
import com.grupo.manutencao_preditiva.model.LogAtividade;
import com.grupo.manutencao_preditiva.model.Maquina;
import com.grupo.manutencao_preditiva.model.Manutencao;
import com.grupo.manutencao_preditiva.model.Predicao;
import com.grupo.manutencao_preditiva.model.Sensor;
import com.grupo.manutencao_preditiva.model.Usuario;

@SpringBootApplication
public class ManutencaoPreditivaApplication implements CommandLineRunner {

    // --- Scanner injetado via construtor (BUG-11/12 corrigido) ---------------

    @Bean
    public static Scanner scanner() {
        return new Scanner(System.in);
    }

    private final Scanner sc;

    public ManutencaoPreditivaApplication(Scanner sc) {
        this.sc = sc;
    }

    // --- Estado global da sessao ------------------------------------------

    private final List<Maquina>      maquinas    = new ArrayList<>();
    private final List<Manutencao>   manutencoes = new ArrayList<>();
    private final List<LogAtividade> logs        = new ArrayList<>();

    private Maquina maquinaAtiva = null;
    private Empresa empresa      = null;
    private Usuario usuario      = null;

    private int contadorMaquina    = 1;
    private int contadorManutencao = 1;
    private int contadorLog        = 1;

    // --- Entry point -------------------------------------------------------

    public static void main(String[] args) {
        SpringApplication.run(ManutencaoPreditivaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Descarta qualquer \n residual que o Spring deixa no stdin
        try { if (System.in.available() > 0) System.in.read(); } catch (Exception ignored) {}

        telaBoasVindas();
        cadastrarSessao();
        menuPrincipal();
        linha('=', 52);
        System.out.println("  Sessao encerrada. Ate logo!");
        linha('=', 52);
        sc.close();
    }

    // =========================================================================
    // TELAS DE INICIO
    // =========================================================================

    private void telaBoasVindas() {
        limpar();
        linha('=', 52);
        System.out.println("  |" + centralizar("PREDIX", 48) + "|");
        System.out.println("  |" + centralizar("Sistema de Manutencao Preditiva", 48) + "|");
        linha('=', 52);
        System.out.println();
    }

    private void cadastrarSessao() {
        secao("IDENTIFICACAO DA EMPRESA");
        String nomeEmpresa = lerStringObrigatoria("Nome da empresa  ");
        String cnpj        = lerCNPJ();
        String setor       = lerStringObrigatoria("Setor industrial ");
        empresa = new Empresa(1, nomeEmpresa, cnpj, setor, java.time.LocalDate.now());

        System.out.println();
        secao("IDENTIFICACAO DO OPERADOR");
        String nomeUser = lerStringObrigatoria("Seu nome         ");
        String email    = lerEmail();
        usuario = new Usuario(1, nomeUser, email, empresa);

        log("Sessao", "Login de " + nomeUser);
        pausar("\n  Sessao iniciada! Pressione ENTER para continuar...");
    }

    // =========================================================================
    // MENU PRINCIPAL
    // =========================================================================

    private void menuPrincipal() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("MENU PRINCIPAL");
            statusSessao();
            System.out.println();
            System.out.println("  [ 1 ]  Gerenciar Maquinas");
            System.out.println("  [ 2 ]  Inserir Leituras de Sensores");
            System.out.println("  [ 3 ]  Analise Preditiva");
            System.out.println("  [ 4 ]  Manutencoes");
            System.out.println("  [ 5 ]  Alertas e Historico");
            System.out.println("  [ 6 ]  Relatorios");
            System.out.println("  [ 7 ]  Log de Atividades");
            System.out.println();
            System.out.println("  [ 0 ]  Encerrar sessao");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> menuMaquinas();
                case 2 -> menuLeituras();
                case 3 -> menuPredicao();
                case 4 -> menuManutencoes();
                case 5 -> menuAlertas();
                case 6 -> menuRelatorios();
                case 7 -> menuLogs();
                case 0 -> {}
                default -> aviso("Opcao invalida. Tente novamente.");
            }
        }
    }

    // =========================================================================
    // MENU 1 - MAQUINAS
    // =========================================================================

    private void menuMaquinas() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("GERENCIAR MAQUINAS");
            indicadorAtiva();
            System.out.println();
            System.out.println("  [ 1 ]  Cadastrar nova maquina");
            System.out.println("  [ 2 ]  Listar todas as maquinas");
            System.out.println("  [ 3 ]  Selecionar maquina ativa");
            System.out.println("  [ 4 ]  Ver detalhes da maquina ativa");
            System.out.println("  [ 5 ]  Historico de leituras da ativa");
            System.out.println();
            System.out.println("  [ 0 ]  Voltar");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> cadastrarMaquina();
                case 2 -> listarMaquinas();
                case 3 -> selecionarMaquina();
                case 4 -> detalharMaquinaAtiva();
                case 5 -> historicoLeiturasMaquinaAtiva();
                case 0 -> {}
                default -> aviso("Opcao invalida.");
            }
        }
    }

    private void cadastrarMaquina() {
        cabecalho("CADASTRAR MAQUINA");
        String nome       = lerStringObrigatoria("Nome da maquina  ");
        String fabricante = lerStringObrigatoria("Fabricante       ");
        String tipo       = lerStringObrigatoria("Tipo/modelo      ");

        Maquina m = new Maquina(contadorMaquina++, nome, fabricante, empresa);
        m.setTipo(tipo);
        m.adicionarSensor(new Sensor(1, "Temperatura", "C"));
        m.adicionarSensor(new Sensor(2, "Vibracao",    "mm/s"));
        maquinas.add(m);

        if (maquinaAtiva == null) maquinaAtiva = m;

        log("Maquina", "Cadastro: " + nome);
        sucesso("Maquina \"" + nome + "\" cadastrada com sucesso!");
        if (maquinas.size() == 1) info("Esta maquina foi definida como ativa automaticamente.");
        pausar(null);
    }

    private void listarMaquinas() {
        cabecalho("LISTA DE MAQUINAS");
        if (maquinas.isEmpty()) {
            aviso("Nenhuma maquina cadastrada ainda.");
            pausar(null);
            return;
        }
        System.out.printf("  %-4s  %-22s  %-16s  %-8s  %s%n",
            "#", "Nome", "Fabricante", "Leituras", "Ativa?");
        divisor();
        for (Maquina m : maquinas) {
            String ativa = (m == maquinaAtiva) ? "<< ATIVA" : "";
            System.out.printf("  %-4d  %-22s  %-16s  %-8d  %s%n",
                m.getId(), m.getNome(), m.getFabricante(),
                m.getTotalLeituras(), ativa);
        }
        pausar(null);
    }

    private void selecionarMaquina() {
        cabecalho("SELECIONAR MAQUINA ATIVA");
        if (maquinas.isEmpty()) {
            aviso("Cadastre ao menos uma maquina primeiro.");
            pausar(null);
            return;
        }
        for (int i = 0; i < maquinas.size(); i++) {
            Maquina m = maquinas.get(i);
            String marca = (m == maquinaAtiva) ? " << ATIVA" : "";
            System.out.printf("  [ %d ]  %s | %s%s%n", i + 1, m.getNome(), m.getFabricante(), marca);
        }
        System.out.println();
        System.out.print("  Numero da maquina: ");
        int idx = lerInt() - 1;
        if (idx >= 0 && idx < maquinas.size()) {
            maquinaAtiva = maquinas.get(idx);
            log("Maquina", "Selecionada: " + maquinaAtiva.getNome());
            sucesso("Maquina ativa: " + maquinaAtiva.getNome());
        } else {
            aviso("Numero invalido.");
        }
        pausar(null);
    }

    private void detalharMaquinaAtiva() {
        cabecalho("DETALHES DA MAQUINA");
        if (!verificarMaquinaAtiva()) return;
        System.out.println(maquinaAtiva.exibirDados());
        pausar(null);
    }

    private void historicoLeiturasMaquinaAtiva() {
        cabecalho("HISTORICO DE LEITURAS");
        if (!verificarMaquinaAtiva()) return;
        System.out.println("  Ultimas 5 leituras registradas:\n");
        System.out.println(maquinaAtiva.exibirHistorico());
        pausar(null);
    }

    // =========================================================================
    // MENU 2 - LEITURAS DE SENSORES
    // =========================================================================

    private void menuLeituras() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("LEITURAS DE SENSORES");
            indicadorAtiva();
            System.out.println();
            System.out.println("  [ 1 ]  Inserir nova leitura manual");
            System.out.println("  [ 2 ]  Simular degradacao progressiva");
            System.out.println("  [ 3 ]  Ver estatisticas dos sensores");
            System.out.println();
            System.out.println("  [ 0 ]  Voltar");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> inserirLeitura();
                case 2 -> simularDegradacao();
                case 3 -> estatisticasSensores();
                case 0 -> {}
                default -> aviso("Opcao invalida.");
            }
        }
    }

    private void inserirLeitura() {
        cabecalho("INSERIR LEITURA");
        if (!verificarMaquinaAtiva()) return;

        System.out.printf("  Maquina: %s%n%n", maquinaAtiva.getNome());

        System.out.print("  Temperatura (C)      : ");
        double temp = lerDouble();
        System.out.print("  Vibracao (mm/s)      : ");
        double vib = lerDouble();
        System.out.print("  Carga operacional (%%): ");
        double carga = lerDouble();
        System.out.print("  Horas de uso (total) : ");
        int horas = lerInt();

        List<Sensor> sensores = maquinaAtiva.getSensores();
        sensores.get(0).registrarLeitura(temp, maquinaAtiva);
        sensores.get(1).registrarLeitura(vib,  maquinaAtiva);
        maquinaAtiva.atualizarLeituras(
            sensores.get(0).getValor(),
            sensores.get(1).getValor(),
            carga, horas
        );

        log("Sensor", "Leitura registrada em " + maquinaAtiva.getNome());
        sucesso("Leitura registrada com sucesso!");
        System.out.printf("  Total de leituras: %d%n", maquinaAtiva.getTotalLeituras());
        pausar(null);
    }

    private void simularDegradacao() {
        cabecalho("SIMULACAO DE DEGRADACAO");
        if (!verificarMaquinaAtiva()) return;

        System.out.printf("  Maquina: %s%n%n", maquinaAtiva.getNome());
        System.out.print("  Quantos ciclos simular (1-10): ");
        int ciclos = Math.min(10, Math.max(1, lerInt()));

        System.out.print("  Temperatura inicial (C)  : ");
        double tempBase = lerDouble();
        System.out.print("  Vibracao inicial (mm/s)  : ");
        double vibBase  = lerDouble();
        System.out.print("  Carga operacional (%%)    : ");
        double carga    = lerDouble();
        System.out.print("  Horas iniciais de uso    : ");
        int horas       = lerInt();

        System.out.println();
        divisor();
        System.out.printf("  %-6s  %-12s  %-14s  %-12s%n",
            "Ciclo", "Temp (C)", "Vib (mm/s)", "Risco");
        divisor();

        List<Sensor> sensores = maquinaAtiva.getSensores();
        double tempAtual = tempBase;
        double vibAtual  = vibBase;

        for (int i = 1; i <= ciclos; i++) {
            tempAtual += 3.0 + (Math.random() * 2 - 1);
            vibAtual  += 0.8 + (Math.random() * 0.4 - 0.2);
            horas     += 168;

            sensores.get(0).registrarLeitura(tempAtual, maquinaAtiva);
            sensores.get(1).registrarLeitura(vibAtual,  maquinaAtiva);
            maquinaAtiva.atualizarLeituras(tempAtual, vibAtual, carga, horas);

            Predicao p = new Predicao(maquinaAtiva);
            p.calcularRisco(maquinaAtiva);

            String status;
            if      (p.getRisco() >= 70) status = "[CRITICO]";
            else if (p.getRisco() >= 40) status = "[ATENCAO]";
            else if (p.getRisco() >= 20) status = "[MONITOR]";
            else                         status = "[ NORMAL]";

            System.out.printf("  %-6d  %-12.1f  %-14.2f  %s %.1f%%%n",
                i, tempAtual, vibAtual, status, p.getRisco());
        }
        divisor();
        log("Simulacao", ciclos + " ciclos em " + maquinaAtiva.getNome());
        pausar(null);
    }

    private void estatisticasSensores() {
        cabecalho("ESTATISTICAS DOS SENSORES");
        if (!verificarMaquinaAtiva()) return;

        System.out.printf("  Maquina: %s  |  %d leituras totais%n%n",
            maquinaAtiva.getNome(), maquinaAtiva.getTotalLeituras());

        List<Sensor> sensores = maquinaAtiva.getSensores();
        if (sensores.isEmpty()) {
            aviso("Nenhum sensor encontrado.");
        } else {
            System.out.printf("  %-16s  %-6s  %-8s  %-8s  %-8s  %-8s%n",
                "Sensor", "Leit.", "Atual", "Media", "Maximo", "Minimo");
            divisor();
            for (Sensor s : sensores) {
                if (s.getTotalLeituras() > 0) {
                    System.out.printf("  %-16s  %-6d  %-8.2f  %-8.2f  %-8.2f  %-8.2f%n",
                        s.getTipo() + "(" + s.getUnidade() + ")",
                        s.getTotalLeituras(),
                        s.getValor(), s.getMedia(),
                        s.getValorMaximo(), s.getValorMinimo());
                } else {
                    System.out.printf("  %-16s  sem leituras%n", s.getTipo());
                }
            }
        }
        pausar(null);
    }

    // =========================================================================
    // MENU 3 - ANALISE PREDITIVA
    // =========================================================================

    private void menuPredicao() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("ANALISE PREDITIVA");
            indicadorAtiva();
            System.out.println();
            System.out.println("  [ 1 ]  Gerar predicao da maquina ativa");
            System.out.println("  [ 2 ]  Analisar todas as maquinas");
            System.out.println();
            System.out.println("  [ 0 ]  Voltar");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> gerarPredicaoAtiva();
                case 2 -> analisarTodasMaquinas();
                case 0 -> {}
                default -> aviso("Opcao invalida.");
            }
        }
    }

    private void gerarPredicaoAtiva() {
        if (!verificarMaquinaAtiva()) return;
        cabecalho("PREDICAO: " + maquinaAtiva.getNome().toUpperCase());

        if (maquinaAtiva.getTotalLeituras() == 0) {
            aviso("Insira pelo menos uma leitura antes de gerar a predicao.");
            pausar(null);
            return;
        }

        Predicao predicao = new Predicao(maquinaAtiva);
        predicao.calcularRisco(maquinaAtiva);

        System.out.println(predicao.gerarRelatorio());
        System.out.println();

        Alerta alerta = Alerta.gerar(maquinaAtiva, predicao.getRisco());
        exibirBannerAlerta(predicao.getRisco(), alerta);

        log("Predicao", "Analise: " + maquinaAtiva.getNome() + " - " + predicao.getStatus());
        pausar(null);
    }

    private void analisarTodasMaquinas() {
        cabecalho("ANALISE GERAL: TODAS AS MAQUINAS");
        if (maquinas.isEmpty()) {
            aviso("Nenhuma maquina cadastrada.");
            pausar(null);
            return;
        }

        System.out.printf("  %-22s  %-10s  %-10s  %s%n",
            "Maquina", "Risco", "Status", "Leituras");
        divisor();

        for (Maquina m : maquinas) {
            if (m.getTotalLeituras() == 0) {
                System.out.printf("  %-22s  sem dados%n", m.getNome());
                continue;
            }
            Predicao p = new Predicao(m);
            p.calcularRisco(m);
            String icone;
            if      (p.getRisco() >= 70) icone = "[CRITICO]";
            else if (p.getRisco() >= 40) icone = "[ATENCAO]";
            else if (p.getRisco() >= 20) icone = "[MONITOR]";
            else                         icone = "[ NORMAL]";
            System.out.printf("  %-22s  %s %5.1f%%  %-10s  %d leit.%n",
                m.getNome(), icone, p.getRisco(), p.getStatus(), m.getTotalLeituras());
        }
        pausar(null);
    }

    // =========================================================================
    // MENU 4 - MANUTENCOES
    // =========================================================================

    private void menuManutencoes() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("MANUTENCOES");
            indicadorAtiva();
            System.out.println();
            System.out.println("  [ 1 ]  Registrar nova manutencao");
            System.out.println("  [ 2 ]  Finalizar manutencao em aberto");
            System.out.println("  [ 3 ]  Listar todas as manutencoes");
            System.out.println();
            System.out.println("  [ 0 ]  Voltar");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> registrarManutencao();
                case 2 -> finalizarManutencao();
                case 3 -> listarManutencoes();
                case 0 -> {}
                default -> aviso("Opcao invalida.");
            }
        }
    }

    private void registrarManutencao() {
        cabecalho("REGISTRAR MANUTENCAO");
        if (!verificarMaquinaAtiva()) return;

        System.out.println("  Tipo de manutencao:");
        System.out.println("  [ 1 ]  Preventiva");
        System.out.println("  [ 2 ]  Corretiva");
        System.out.println("  [ 3 ]  Preditiva");
        System.out.print("  Tipo: ");
        int tipo = lerInt();
        String[] tipos = {"Preventiva", "Corretiva", "Preditiva"};
        String tipoStr = (tipo >= 1 && tipo <= 3) ? tipos[tipo - 1] : "Geral";

        Manutencao m = new Manutencao(contadorManutencao++, tipoStr, maquinaAtiva);
        manutencoes.add(m);

        log("Manutencao", "Iniciada (" + tipoStr + ") em " + maquinaAtiva.getNome());
        sucesso("Manutencao " + tipoStr + " registrada e em andamento.");
        pausar(null);
    }

    private void finalizarManutencao() {
        cabecalho("FINALIZAR MANUTENCAO");
        List<Manutencao> abertas = new ArrayList<>();
        for (Manutencao m : manutencoes) {
            if ("EM_ANDAMENTO".equals(m.getStatus())) abertas.add(m);
        }
        if (abertas.isEmpty()) {
            aviso("Nenhuma manutencao em andamento no momento.");
            pausar(null);
            return;
        }
        for (int i = 0; i < abertas.size(); i++) {
            System.out.printf("  [ %d ]  %s%n", i + 1, abertas.get(i).resumo());
        }
        System.out.print("\n  Numero: ");
        int idx = lerInt() - 1;
        if (idx < 0 || idx >= abertas.size()) {
            aviso("Numero invalido.");
            pausar(null);
            return;
        }
        System.out.print("  Custo total (R$)    : ");
        double custo = lerDouble();
        System.out.print("  Descricao do servico: ");
        String desc = sc.nextLine().trim();
        try {
            abertas.get(idx).finalizar(custo, desc);
            log("Manutencao", "Finalizada: #" + abertas.get(idx).getId());
            sucesso("Manutencao finalizada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            aviso(e.getMessage());
        }
        pausar(null);
    }

    private void listarManutencoes() {
        cabecalho("HISTORICO DE MANUTENCOES");
        if (manutencoes.isEmpty()) {
            aviso("Nenhuma manutencao registrada.");
            pausar(null);
            return;
        }
        for (Manutencao m : manutencoes) {
            System.out.println(m.resumo());
        }
        pausar(null);
    }

    // =========================================================================
    // MENU 5 - ALERTAS
    // =========================================================================

    private void menuAlertas() {
        cabecalho("ALERTAS GERADOS NA SESSAO");
        List<Alerta> alertas = Alerta.getHistorico();
        if (alertas.isEmpty()) {
            aviso("Nenhum alerta gerado ate o momento.");
            info("Execute uma predicao para gerar alertas automaticos.");
            pausar(null);
            return;
        }
        System.out.printf("  Total de alertas: %d%n%n", alertas.size());
        for (Alerta a : alertas) {
            divisor();
            System.out.println(a.exibirCompleto());
        }
        divisor();
        pausar(null);
    }

    // =========================================================================
    // MENU 6 - RELATORIOS
    // =========================================================================

    private void menuRelatorios() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho("RELATORIOS");
            System.out.println();
            System.out.println("  [ 1 ]  Relatorio geral da sessao");
            System.out.println("  [ 2 ]  Relatorio da maquina ativa");
            System.out.println("  [ 3 ]  Resumo de manutencoes");
            System.out.println();
            System.out.println("  [ 0 ]  Voltar");
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> relatorioSessao();
                case 2 -> relatorioMaquinaAtiva();
                case 3 -> relatorioManutencoes();
                case 0 -> {}
                default -> aviso("Opcao invalida.");
            }
        }
    }

    private void relatorioSessao() {
        cabecalho("RELATORIO GERAL DA SESSAO");

        secao("EMPRESA");
        System.out.println(empresa.exibirEmpresa());

        secao("OPERADOR");
        System.out.println(usuario.exibirUsuario());

        secao("RESUMO OPERACIONAL");
        System.out.printf("  Maquinas cadastradas   : %d%n", maquinas.size());
        System.out.printf("  Manutencoes abertas    : %d%n",
            manutencoes.stream().filter(m -> "EM_ANDAMENTO".equals(m.getStatus())).count());
        System.out.printf("  Manutencoes concluidas : %d%n",
            manutencoes.stream().filter(m -> "CONCLUIDA".equals(m.getStatus())).count());
        System.out.printf("  Alertas gerados        : %d%n", Alerta.totalAlertas());
        System.out.printf("  Acoes registradas      : %d%n", logs.size());
        pausar(null);
    }

    private void relatorioMaquinaAtiva() {
        cabecalho("RELATORIO DA MAQUINA");
        if (!verificarMaquinaAtiva()) return;

        secao("DADOS DA MAQUINA");
        System.out.println(maquinaAtiva.exibirDados());

        secao("ESTATISTICAS DOS SENSORES");
        for (Sensor s : maquinaAtiva.getSensores()) {
            System.out.println(s.exibirEstatisticas());
        }

        secao("ULTIMAS LEITURAS");
        System.out.println(maquinaAtiva.exibirHistorico());

        if (maquinaAtiva.getTotalLeituras() > 0) {
            secao("PREDICAO ATUAL");
            Predicao p = new Predicao(maquinaAtiva);
            p.calcularRisco(maquinaAtiva);
            System.out.println(p.gerarRelatorio());
        }
        pausar(null);
    }

    private void relatorioManutencoes() {
        cabecalho("RELATORIO DE MANUTENCOES");
        if (manutencoes.isEmpty()) {
            aviso("Nenhuma manutencao registrada.");
            pausar(null);
            return;
        }
        double totalCusto = 0;
        for (Manutencao m : manutencoes) {
            System.out.println(m.resumo());
            totalCusto += m.getCusto();
        }
        divisor();
        System.out.printf("  Custo total acumulado : R$ %.2f%n", totalCusto);
        pausar(null);
    }

    // =========================================================================
    // MENU 7 - LOGS
    // =========================================================================

    private void menuLogs() {
        cabecalho("LOG DE ATIVIDADES DA SESSAO");
        if (logs.isEmpty()) {
            aviso("Nenhuma atividade registrada.");
            pausar(null);
            return;
        }
        System.out.printf("  Total de registros: %d%n%n", logs.size());
        for (LogAtividade l : logs) {
            System.out.println(l.exibirLog());
        }
        pausar(null);
    }

    // =========================================================================
    // UTILITARIOS DE TERMINAL
    // =========================================================================

    private void cabecalho(String titulo) {
        limpar();
        linha('=', 52);
        System.out.println("  || PREDIX >> " + titulo);
        linha('=', 52);
        System.out.println();
    }

    private void secao(String titulo) {
        System.out.println();
        System.out.println("  -- " + titulo + " " + "-".repeat(Math.max(0, 44 - titulo.length())));
    }

    private void statusSessao() {
        if (empresa != null)
            System.out.printf("  Empresa : %s  |  Operador: %s%n",
                empresa.getNome(),
                usuario != null ? usuario.getNomeCompleto() : "-");
        indicadorAtiva();
    }

    private void indicadorAtiva() {
        if (maquinaAtiva != null)
            System.out.printf("  Ativa   : %s (%d leituras)%n",
                maquinaAtiva.getNome(), maquinaAtiva.getTotalLeituras());
        else
            System.out.println("  Ativa   : (nenhuma selecionada)");
    }

    private void exibirBannerAlerta(double risco, Alerta alerta) {
        if (risco >= 70) {
            linha('!', 52);
            System.out.println("  [CRITICO] " + alerta.getMensagem());
            linha('!', 52);
        } else if (risco >= 40) {
            linha('-', 52);
            System.out.println("  [ATENCAO] " + alerta.getMensagem());
            linha('-', 52);
        } else if (risco >= 20) {
            linha('-', 52);
            System.out.println("  [MONITOR] " + alerta.getMensagem());
            linha('-', 52);
        } else {
            System.out.println("  [ OK ] " + alerta.getMensagem());
        }
    }

    private boolean verificarMaquinaAtiva() {
        if (maquinaAtiva == null) {
            aviso("Nenhuma maquina ativa selecionada.");
            info("Va em Gerenciar Maquinas para cadastrar ou selecionar uma.");
            pausar(null);
            return false;
        }
        return true;
    }

    private void sucesso(String msg) { System.out.println("\n  [OK] " + msg); }
    private void aviso(String msg)   { System.out.println("\n  [!!] " + msg); }
    private void info(String msg)    { System.out.println("   [i] " + msg); }

    private void linha(char c, int n) {
        System.out.println("  " + String.valueOf(c).repeat(n));
    }

    private void divisor() {
        System.out.println("  " + "-".repeat(52));
    }

    private String centralizar(String texto, int largura) {
        int padding = Math.max(0, (largura - texto.length()) / 2);
        return " ".repeat(padding) + texto + " ".repeat(largura - padding - texto.length());
    }

    private void limpar() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausar(String msg) {
        System.out.print(msg != null ? msg : "\n  Pressione ENTER para continuar...");
        sc.nextLine();
    }

    private void log(String entidade, String acao) {
        logs.add(new LogAtividade(contadorLog++, entidade, acao, usuario));
    }

    // -- Leitura segura de inputs -------------------------------------------

    private int lerOpcao() {
        System.out.print("\n  >> Opcao: ");
        return lerInt();
    }

    private int lerInt() {
        while (true) {
            try {
                int v = sc.nextInt();
                sc.nextLine();
                return v;
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.print("  Valor invalido. Digite um numero inteiro: ");
            }
        }
    }

    private double lerDouble() {
        while (true) {
            try {
                double v = sc.nextDouble();
                sc.nextLine();
                return v;
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.print("  Valor invalido. Use ponto ou virgula (ex: 72.5): ");
            }
        }
    }

    private String lerStringObrigatoria(String label) {
        while (true) {
            System.out.print("  " + label + ": ");
            String v = sc.nextLine().trim();
            if (!v.isEmpty()) return v;
            aviso("Campo obrigatorio. Digite um valor valido.");
        }
    }

    private String lerEmail() {
        while (true) {
            System.out.print("  E-mail           : ");
            String v = sc.nextLine().trim();
            if (!v.isEmpty() && v.contains("@")) return v;
            aviso("E-mail invalido. O endereco deve conter '@'.");
        }
    }

    private String lerCNPJ() {
        while (true) {
            System.out.print("  CNPJ             : ");
            String v = sc.nextLine().trim();
            if (!v.isEmpty() && v.matches("[0-9.\\-/]+")) return v;
            aviso("CNPJ invalido. Use apenas numeros e os caracteres '.' '-' '/'.");
        }
    }
}
