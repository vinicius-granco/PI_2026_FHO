package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Sensor}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Sensor")
class SensorTest {

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        sensor = new Sensor(1, "Temperatura", "C");
    }

    // -------------------------------------------------------------------------
    // Construtor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Construtor padrao cria sensor com valores zero/null")
    void construtorPadrao_valoresDefault() {
        Sensor s = new Sensor();
        assertEquals(0, s.getId());
        assertNull(s.getTipo());
        assertNull(s.getUnidade());
        assertEquals(0.0, s.getValor());
        assertEquals(0, s.getTotalLeituras());
    }

    @Test
    @DisplayName("Construtor com id/tipo/unidade inicializa campos corretamente")
    void construtorComParametros_inicializaCampos() {
        assertEquals(1, sensor.getId());
        assertEquals("Temperatura", sensor.getTipo());
        assertEquals("C", sensor.getUnidade());
        assertEquals(0.0, sensor.getValor());
        assertEquals(0, sensor.getTotalLeituras());
    }

    // BUG-04 CORRIGIDO: getValorMaximo/getValorMinimo agora retornam 0.0 quando
    // nao ha leituras, eliminando a exposicao dos valores sentinela internos.
    @Test
    @DisplayName("BUG-04 corrigido: getValorMaximo/getValorMinimo retornam 0.0 antes da primeira leitura")
    void semLeituras_getValorMaximoEhZero() {
        assertEquals(0.0, sensor.getValorMaximo(), 0.001,
            "getValorMaximo() sem leituras deve retornar 0.0 (BUG-04 corrigido)");
        assertEquals(0.0, sensor.getValorMinimo(), 0.001,
            "getValorMinimo() sem leituras deve retornar 0.0 (BUG-04 corrigido)");
    }

    // -------------------------------------------------------------------------
    // registrarLeitura(double)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarLeitura atualiza valor atual e historico")
    void registrarLeitura_atualizaValorEHistorico() {
        sensor.registrarLeitura(42.5);
        assertEquals(42.5, sensor.getValor(), 0.001);
        assertEquals(1, sensor.getTotalLeituras());
    }

    @Test
    @DisplayName("registrarLeitura multiplas vezes acumula historico")
    void registrarLeitura_multiplas_acumulaHistorico() {
        sensor.registrarLeitura(10.0);
        sensor.registrarLeitura(20.0);
        sensor.registrarLeitura(30.0);
        assertEquals(3, sensor.getTotalLeituras());
        assertEquals(30.0, sensor.getValor(), 0.001);
    }

    @Test
    @DisplayName("registrarLeitura atualiza valorMaximo corretamente")
    void registrarLeitura_atualizaValorMaximo() {
        sensor.registrarLeitura(50.0);
        sensor.registrarLeitura(80.0);
        sensor.registrarLeitura(60.0);
        assertEquals(80.0, sensor.getValorMaximo(), 0.001);
    }

    @Test
    @DisplayName("registrarLeitura atualiza valorMinimo corretamente")
    void registrarLeitura_atualizaValorMinimo() {
        sensor.registrarLeitura(50.0);
        sensor.registrarLeitura(20.0);
        sensor.registrarLeitura(35.0);
        assertEquals(20.0, sensor.getValorMinimo(), 0.001);
    }

    @Test
    @DisplayName("registrarLeitura com valor unico: max == min == valor")
    void registrarLeitura_umaLeitura_maxEhMin() {
        sensor.registrarLeitura(55.0);
        assertEquals(55.0, sensor.getValorMaximo(), 0.001);
        assertEquals(55.0, sensor.getValorMinimo(), 0.001);
    }

    // -------------------------------------------------------------------------
    // registrarLeitura(double, Maquina)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarLeitura com Maquina delega para registrarLeitura(double)")
    void registrarLeituraComMaquina_delegaParaRegistrarLeitura() {
        Maquina maquina = new Maquina(1, "Compressor", "AcmeCorp");
        sensor.registrarLeitura(75.0, maquina);
        assertEquals(75.0, sensor.getValor(), 0.001);
        assertEquals(1, sensor.getTotalLeituras());
    }

    // -------------------------------------------------------------------------
    // getMedia()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getMedia retorna 0.0 quando nao ha leituras")
    void getMedia_semLeituras_retornaZero() {
        assertEquals(0.0, sensor.getMedia(), 0.001);
    }

    @Test
    @DisplayName("getMedia calcula media aritmetica correta")
    void getMedia_comLeituras_calculaMediaCorreta() {
        sensor.registrarLeitura(10.0);
        sensor.registrarLeitura(20.0);
        sensor.registrarLeitura(30.0);
        assertEquals(20.0, sensor.getMedia(), 0.001);
    }

    @Test
    @DisplayName("getMedia com leitura unica retorna o proprio valor")
    void getMedia_umaLeitura_retornaOProprio() {
        sensor.registrarLeitura(42.0);
        assertEquals(42.0, sensor.getMedia(), 0.001);
    }

    // -------------------------------------------------------------------------
    // exibir()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("exibir formata tipo, valor e unidade")
    void exibir_formatacaoCorreta() {
        sensor.registrarLeitura(37.5);
        String resultado = sensor.exibir();
        assertTrue(resultado.contains("Temperatura"), "Deve conter o tipo");
        assertTrue(resultado.contains("37,50") || resultado.contains("37.50"), "Deve conter o valor formatado");
        assertTrue(resultado.contains("C"), "Deve conter a unidade");
    }

    // -------------------------------------------------------------------------
    // exibirEstatisticas()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("exibirEstatisticas sem leituras retorna mensagem 'sem leituras'")
    void exibirEstatisticas_semLeituras_mensagemEspecial() {
        String resultado = sensor.exibirEstatisticas();
        assertTrue(resultado.contains("sem leituras"), "Deve indicar ausencia de leituras");
        assertTrue(resultado.contains("Temperatura"), "Deve conter o tipo do sensor");
    }

    @Test
    @DisplayName("exibirEstatisticas com leituras contem atual, media, max e min")
    void exibirEstatisticas_comLeituras_conteudoCompleto() {
        sensor.registrarLeitura(40.0);
        sensor.registrarLeitura(60.0);
        String resultado = sensor.exibirEstatisticas();
        assertTrue(resultado.contains("Temperatura"), "Deve conter o tipo");
        assertTrue(resultado.contains("Media") || resultado.contains("Media"), "Deve conter media");
        assertTrue(resultado.contains("Max") || resultado.contains("Max"), "Deve conter maximo");
        assertTrue(resultado.contains("Min") || resultado.contains("Min"), "Deve conter minimo");
    }

    // -------------------------------------------------------------------------
    // Valores de borda
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarLeitura aceita valor zero")
    void registrarLeitura_valorZero() {
        assertDoesNotThrow(() -> sensor.registrarLeitura(0.0));
        assertEquals(0.0, sensor.getValor(), 0.001);
    }

    @Test
    @DisplayName("registrarLeitura aceita valor negativo")
    void registrarLeitura_valorNegativo() {
        assertDoesNotThrow(() -> sensor.registrarLeitura(-10.0));
        assertEquals(-10.0, sensor.getValor(), 0.001);
    }

    @Test
    @DisplayName("registrarLeitura aceita valor muito grande")
    void registrarLeitura_valorGrande() {
        assertDoesNotThrow(() -> sensor.registrarLeitura(Double.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, sensor.getValor());
    }
}
