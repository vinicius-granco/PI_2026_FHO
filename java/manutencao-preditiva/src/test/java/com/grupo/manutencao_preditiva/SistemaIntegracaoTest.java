package com.grupo.manutencao_preditiva;

import com.grupo.manutencao_preditiva.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao de sistema — sem contexto Spring.
 *
 * Simula fluxos completos de sessao usando apenas as classes de modelo diretamente:
 * caminho feliz, entradas invalidas, transicoes de estado, acumulacao de estado estatico.
 *
 * Bugs documentados neste arquivo:
 *  BUG-04: Sensor.getValorMaximo()/getValorMinimo() retornam sentinelas antes da 1a leitura
 *  BUG-05: Alerta.contadorId e Alerta.historico sao static — acumulam estado entre testes
 *  BUG-07: Manutencao.finalizar() sem guard de transicao de estado
 *  BUG-08: Manutencao.cancelar() sem guard — transicao CONCLUIDA->CANCELADA ilegal
 *  BUG-10: Predicao.gerarRelatorio() antes de calcularRisco() retorna status null e risco=0
 */
@DisplayName("SistemaIntegracaoTest — fluxos completos sem Spring")
class SistemaIntegracaoTest {

    @BeforeEach
    void resetEstaticoAlerta() {
        Alerta.resetar();
    }

    // =========================================================================
    // Caminho feliz — fluxo completo de sessao
    // =========================================================================

    @Test
    @DisplayName("sessaoCompletaCaminhoFeliz: cria empresa+usuario+maquina+sensor, registra leituras, "
        + "gera predicao, gera alerta, registra manutencao e finaliza")
    void sessaoCompletaCaminhoFeliz() {
        // 1. Cria empresa e usuario
        Empresa empresa = new Empresa(1, "Industria Alpha", "12.345.678/0001-99");
        Usuario usuario = new Usuario(1, "Engenheiro Pedro", "pedro@alpha.com", empresa);
        usuario.setCargo("Engenheiro de Manutencao");
        assertEquals("Engenheiro de Manutencao", usuario.getCargo());
        assertEquals("Industria Alpha", empresa.getNome());

        // 2. Cria maquina e associa sensores
        Maquina maquina = new Maquina(1, "Compressor Industrial", "AcmeCorp", empresa);
        Sensor sensorTemp = new Sensor(1, "Temperatura", "C");
        Sensor sensorVib  = new Sensor(2, "Vibracao", "mm/s");
        maquina.adicionarSensor(sensorTemp);
        maquina.adicionarSensor(sensorVib);
        assertEquals(2, maquina.getSensores().size());

        // 3. Registra leituras nos sensores
        sensorTemp.registrarLeitura(72.0, maquina);
        sensorVib.registrarLeitura(6.5, maquina);
        assertEquals(72.0, sensorTemp.getValor(), 0.001);
        assertEquals(6.5, sensorVib.getValor(), 0.001);
        assertEquals(1, sensorTemp.getTotalLeituras());

        // 4. Atualiza leituras na maquina
        maquina.atualizarLeituras(72.0, 6.5, 75.0, 1100);
        assertEquals(72.0, maquina.getTemperatura(), 0.001);
        assertEquals(6.5, maquina.getVibracao(), 0.001);
        assertEquals(75.0, maquina.getCarga(), 0.001);
        assertEquals(1100, maquina.getHorasUso());

        // 5. Calcula predicao de risco
        // temp=72.0 -> 18pts (>60), vib=6.5 -> 14pts (>5), carga=75.0 -> 5pts (>70), horas=1100 -> 4pts (>1000)
        // total = 18 + 14 + 5 + 4 = 41 pts -> MODERADO
        Predicao predicao = new Predicao(1, maquina);
        double risco = predicao.calcularRisco(maquina);
        assertEquals(41.0, risco, 0.001);
        assertEquals("MODERADO", predicao.getStatus());

        // 6. Gera alerta baseado no risco
        Alerta alerta = Alerta.gerar(maquina, risco);
        assertNotNull(alerta);
        assertEquals("MODERADO", alerta.getNivel());
        assertEquals(1, Alerta.totalAlertas());

        // 7. Registra e finaliza manutencao
        Manutencao manutencao = new Manutencao(1, "PREVENTIVA", maquina);
        assertEquals("EM_ANDAMENTO", manutencao.getStatus());
        manutencao.finalizar(850.0, "Lubrificacao e alinhamento");
        assertEquals("CONCLUIDA", manutencao.getStatus());
        assertEquals(850.0, manutencao.getCusto(), 0.001);

        // 8. Registra log de atividade
        LogAtividade log = new LogAtividade(1, "Manutencao", "Finalizada com custo R$850.00", usuario);
        assertEquals("Manutencao", log.getEntidade());
        assertTrue(log.exibirLog().contains("Engenheiro Pedro"));
    }

    // =========================================================================
    // Entradas invalidas — clamp
    // =========================================================================

    @Test
    @DisplayName("inputsInvalidosClampam: temperatura negativa, carga > 100, horas negativas")
    void inputsInvalidosClampam() {
        Maquina maquina = new Maquina(2, "Turbina B", "TurboMaker");

        // Temperatura negativa -> clampeia para 0
        maquina.setTemperatura(-50.0);
        assertEquals(0.0, maquina.getTemperatura(), 0.001,
            "Temperatura negativa deve ser clampada para 0");

        // Carga acima de 100 -> clampeia para 100
        maquina.setCarga(150.0);
        assertEquals(100.0, maquina.getCarga(), 0.001,
            "Carga acima de 100 deve ser clampada para 100");

        // Horas negativas -> clampeia para 0
        maquina.setHorasUso(-200);
        assertEquals(0, maquina.getHorasUso(),
            "Horas negativas devem ser clampadas para 0");

        // Vibracao negativa -> clampeia para 0
        maquina.setVibracao(-5.0);
        assertEquals(0.0, maquina.getVibracao(), 0.001,
            "Vibracao negativa deve ser clampada para 0");

        // Apos clamp, predicao nao deve estourar
        Predicao p = new Predicao(maquina);
        double risco = p.calcularRisco(maquina);
        // carga=100 -> 15pts, todos outros zero -> total=15pts -> NORMAL (<20)
        assertEquals(15.0, risco, 0.001);
        assertEquals("NORMAL", p.getStatus());
    }

    // =========================================================================
    // Todos os fatores no patamar maximo
    // =========================================================================

    @Test
    @DisplayName("predicaoValoresMaximosAtingem100Pontos: risco = 100.0 e status = CRITICO")
    void predicaoValoresMaximosAtingem100Pontos() {
        Maquina maquina = new Maquina(3, "Prensa Critica", "Fabricante X");
        // temp=91 -> 40pts, vib=16 -> 35pts, carga=96 -> 15pts, horas=5001 -> 10pts = 100pts
        maquina.setTemperatura(91.0);
        maquina.setVibracao(16.0);
        maquina.setCarga(96.0);
        maquina.setHorasUso(5001);

        Predicao p = new Predicao(maquina);
        double risco = p.calcularRisco(maquina);

        assertEquals(100.0, risco, 0.001,
            "Todos os fatores no maximo devem resultar em risco = 100.0");
        assertEquals("CRITICO", p.getStatus(),
            "Risco 100.0 deve resultar em status CRITICO");
    }

    // =========================================================================
    // Todos os fatores zerados
    // =========================================================================

    @Test
    @DisplayName("predicaoValoresNulosSaoNormal: risco = 0.0 e status = NORMAL")
    void predicaoValoresNulosSaoNormal() {
        Maquina maquina = new Maquina(4, "Maquina Parada", "Fabricante Y");
        maquina.setTemperatura(0.0);
        maquina.setVibracao(0.0);
        maquina.setCarga(0.0);
        maquina.setHorasUso(0);

        Predicao p = new Predicao(maquina);
        double risco = p.calcularRisco(maquina);

        assertEquals(0.0, risco, 0.001,
            "Todos os fatores zerados devem resultar em risco = 0.0");
        assertEquals("NORMAL", p.getStatus(),
            "Risco 0.0 deve resultar em status NORMAL");
    }

    // =========================================================================
    // Ciclo de vida de manutencao
    // =========================================================================

    @Test
    @DisplayName("manutencaoCicloDeVidaCompleto: cria->finaliza e cria->cancela; testa BUG-07/BUG-08")
    void manutencaoCicloDeVidaCompleto() {
        Maquina maquina = new Maquina(5, "Motor DC", "ElectroCorp");

        // Fluxo 1: EM_ANDAMENTO -> CONCLUIDA
        Manutencao m1 = new Manutencao(1, "CORRETIVA", maquina);
        assertEquals("EM_ANDAMENTO", m1.getStatus());
        m1.finalizar(1200.0, "Substituicao de escovas");
        assertEquals("CONCLUIDA", m1.getStatus());
        assertEquals(1200.0, m1.getCusto(), 0.001);

        // Fluxo 2: EM_ANDAMENTO -> CANCELADA
        Manutencao m2 = new Manutencao(2, "PREDITIVA", maquina);
        assertEquals("EM_ANDAMENTO", m2.getStatus());
        m2.cancelar();
        assertEquals("CANCELADA", m2.getStatus());

        // BUG-07 CORRIGIDO: finalizar() sobre CONCLUIDA lanca IllegalStateException
        assertThrows(IllegalStateException.class,
            () -> m1.finalizar(9999.0, "Finalizacao invalida"),
            "BUG-07 corrigido: finalizar() sobre CONCLUIDA deve lancar IllegalStateException");
        assertEquals(1200.0, m1.getCusto(), 0.001,
            "Custo original preservado — dado nao foi corrompido");

        // BUG-08 CORRIGIDO: cancelar() sobre CONCLUIDA lanca IllegalStateException
        Manutencao m3 = new Manutencao(3, "PREVENTIVA", maquina);
        m3.finalizar(300.0, "Revisao completa");
        assertEquals("CONCLUIDA", m3.getStatus());
        assertThrows(IllegalStateException.class, m3::cancelar,
            "BUG-08 corrigido: cancelar() sobre CONCLUIDA deve lancar IllegalStateException");
        assertEquals("CONCLUIDA", m3.getStatus(),
            "Status permanece CONCLUIDA apos tentativa invalida de cancelamento");
    }

    // =========================================================================
    // Estado estatico de Alerta — acumulacao e reset
    // =========================================================================

    @Test
    @DisplayName("alertaEstaticoAcumulaEResetaComMetodoPublico: gera 3 alertas, verifica total=3, reset, verifica total=0")
    void alertaEstaticoAcumulaEResetaComMetodoPublico() {
        Maquina maquina = new Maquina(6, "CNC Laser", "LaserTech");

        assertEquals(0, Alerta.totalAlertas(),
            "Historico deve estar vazio apos Alerta.resetar() no @BeforeEach");

        Alerta.gerar(maquina, 15.0);
        Alerta.gerar(maquina, 40.0);
        Alerta.gerar(maquina, 75.0);
        assertEquals(3, Alerta.totalAlertas(),
            "Apos 3 chamadas a gerar(), totalAlertas deve ser 3");

        // BUG-05 CORRIGIDO: reset via metodo publico Alerta.resetar() em vez de reflection
        Alerta.resetar();

        assertEquals(0, Alerta.totalAlertas(),
            "BUG-05 corrigido: apos Alerta.resetar(), totalAlertas deve ser 0");
        assertEquals(0, Alerta.getHistorico().size(),
            "BUG-05 corrigido: lista de historico deve estar vazia apos resetar()");
    }

    // =========================================================================
    // Sensor sem leituras — sentinelas expostos (BUG-04)
    // =========================================================================

    @Test
    @DisplayName("sensorSemLeituras: getMedia()=0, getTotalLeituras()=0, getValorMaximo()=Double.MIN_VALUE (BUG-04)")
    void sensorSemLeituras() {
        Sensor sensor = new Sensor(1, "Pressao", "bar");

        assertEquals(0.0, sensor.getMedia(), 0.001,
            "getMedia() sem leituras deve retornar 0.0");
        assertEquals(0, sensor.getTotalLeituras(),
            "getTotalLeituras() sem leituras deve retornar 0");

        // BUG-04 CORRIGIDO: retorna 0.0 antes da primeira leitura (sem sentinelas expostos)
        assertEquals(0.0, sensor.getValorMaximo(), 0.001,
            "BUG-04 corrigido: getValorMaximo() antes da 1a leitura deve retornar 0.0");
        assertEquals(0.0, sensor.getValorMinimo(), 0.001,
            "BUG-04 corrigido: getValorMinimo() antes da 1a leitura deve retornar 0.0");

        // Apos uma leitura, os sentinelas sao substituidos pelo valor real
        sensor.registrarLeitura(5.0);
        assertEquals(5.0, sensor.getValorMaximo(), 0.001,
            "Apos primeira leitura, getValorMaximo() deve retornar o valor real");
        assertEquals(5.0, sensor.getValorMinimo(), 0.001,
            "Apos primeira leitura, getValorMinimo() deve retornar o valor real");
    }

    // =========================================================================
    // Historico de maquina — janela das ultimas 5 leituras
    // =========================================================================

    @Test
    @DisplayName("historicoMaquinaLimiteUltimasCincoLeituras: registra 7 leituras, exibirHistorico mostra apenas as ultimas 5")
    void historicoMaquinaLimiteUltimasCincoLeituras() {
        Maquina maquina = new Maquina(7, "Retifica CNC", "RetifTech");

        for (int i = 1; i <= 7; i++) {
            maquina.atualizarLeituras(30.0 + i, 1.0 + i * 0.1);
        }

        assertEquals(7, maquina.getTotalLeituras(),
            "Deve ter registrado exatamente 7 leituras no historico total");

        String historico = maquina.exibirHistorico();

        // As ultimas 5 leituras correspondem aos indices 3-7 (1-based)
        assertTrue(historico.contains("[3]"), "Deve exibir leitura de indice 3 (inicio da janela de 5)");
        assertTrue(historico.contains("[4]"), "Deve exibir leitura de indice 4");
        assertTrue(historico.contains("[5]"), "Deve exibir leitura de indice 5");
        assertTrue(historico.contains("[6]"), "Deve exibir leitura de indice 6");
        assertTrue(historico.contains("[7]"), "Deve exibir leitura de indice 7 (ultima)");
        assertFalse(historico.contains("[1]"), "Nao deve exibir leitura fora da janela — indice 1");
        assertFalse(historico.contains("[2]"), "Nao deve exibir leitura fora da janela — indice 2");
    }

    // =========================================================================
    // Predicao antes de calcularRisco (BUG-10)
    // =========================================================================

    @Test
    @DisplayName("predicaoAntesDe_calcularRisco_RetornaValoresDefault: status=null, risco=0.0 (BUG-10)")
    void predicaoAntesDe_calcularRisco_RetornaValoresDefault() {
        Maquina maquina = new Maquina(8, "Bomba Hidraulica", "HidroMax");
        maquina.setTemperatura(95.0);
        maquina.setVibracao(18.0);

        Predicao p = new Predicao(maquina);

        assertNull(p.getStatus(), "Status deve ser null antes de calcularRisco()");
        assertEquals(0.0, p.getRisco(), 0.001, "Risco deve ser 0.0 antes de calcularRisco()");

        // BUG-10 CORRIGIDO: gerarRelatorio() lanca IllegalStateException antes de calcularRisco()
        assertThrows(IllegalStateException.class, p::gerarRelatorio,
            "BUG-10 corrigido: gerarRelatorio() deve lancar IllegalStateException " +
            "quando calcularRisco() nao foi chamado previamente");

        // Apos calcularRisco(), tudo deve funcionar corretamente
        p.calcularRisco(maquina);
        assertNotNull(p.getStatus(), "Apos calcularRisco(), status nao deve ser null");
        assertTrue(p.getRisco() > 0.0, "Apos calcularRisco() com fatores altos, risco deve ser > 0");
        assertDoesNotThrow(p::gerarRelatorio, "Apos calcularRisco(), gerarRelatorio() deve funcionar");
    }

    // =========================================================================
    // Integracao: DadosMaquina vinculado a Sensor
    // =========================================================================

    @Test
    @DisplayName("dadosMaquinaVinculadoASensor: cria DadosMaquina, verifica valor e sensor referenciado")
    void dadosMaquinaVinculadoASensor() {
        Sensor sensor = new Sensor(1, "Corrente", "A");
        DadosMaquina dado = new DadosMaquina(1, 12.4, sensor);

        assertEquals(12.4, dado.getValor(), 0.001);
        assertNotNull(dado.getSensor());
        assertEquals("Corrente", dado.getSensor().getTipo());
        assertNotNull(dado.getDataColeta());

        String exibicao = dado.exibirDado();
        assertTrue(exibicao.contains("Corrente"), "exibirDado deve conter o tipo do sensor");
        assertTrue(exibicao.contains("A"), "exibirDado deve conter a unidade do sensor");
    }

    // =========================================================================
    // Integracao: LogAtividade referenciando usuario e entidade
    // =========================================================================

    @Test
    @DisplayName("logAtividadeRastreiaAcoesDeUsuario: cria log com usuario e verifica exibicao")
    void logAtividadeRastreiaAcoesDeUsuario() {
        Empresa empresa = new Empresa(1, "LogEmpresa", "00.000.001/0001-01");
        Usuario usuario = new Usuario(1, "Auditora Silvana", "silvana@logempresa.com", empresa);

        LogAtividade log1 = new LogAtividade(1, "Maquina", "Cadastro realizado", usuario);
        LogAtividade log2 = new LogAtividade(2, "Predicao", "Risco calculado");

        assertTrue(log1.exibirLog().contains("Auditora Silvana"),
            "Log com usuario deve exibir nome do usuario");
        assertTrue(log2.exibirLog().contains("Sistema"),
            "Log sem usuario deve exibir 'Sistema'");

        assertEquals("Maquina", log1.getEntidade());
        assertEquals("Predicao", log2.getEntidade());
    }
}
