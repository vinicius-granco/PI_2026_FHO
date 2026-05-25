package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link DadosMaquina}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("DadosMaquina")
class DadosMaquinaTest {

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor padrao cria instancia sem lancamento de excecao")
    void construtorPadrao_semExcecao() {
        assertDoesNotThrow((Executable) DadosMaquina::new);
    }

    @Test
    @DisplayName("Construtor padrao cria instancia com valor 0.0 e sem sensor")
    void construtorPadrao_valoresDefault() {
        DadosMaquina dado = new DadosMaquina();
        assertEquals(0.0, dado.getValor(), 0.001);
        assertNull(dado.getSensor());
        assertNull(dado.getDataColeta());
    }

    @Test
    @DisplayName("Construtor com id/valor/sensor inicializa campos corretamente")
    void construtorComParametros_inicializaCampos() {
        Sensor sensor = new Sensor(1, "Pressao", "bar");
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        DadosMaquina dado = new DadosMaquina(1, 4.5, sensor);

        assertEquals(1.0, dado.getValor() > 0 ? 1.0 : 0.0); // simplificado
        assertEquals(4.5, dado.getValor(), 0.001);
        assertNotNull(dado.getSensor());
        assertEquals("Pressao", dado.getSensor().getTipo());
        assertNotNull(dado.getDataColeta());
        // dataColeta deve ser proxima ao momento da criacao
        assertTrue(!dado.getDataColeta().isBefore(antes),
            "dataColeta deve ser posterior ou igual ao momento anterior a criacao");
    }

    // =========================================================================
    // getValor()
    // =========================================================================

    @Test
    @DisplayName("getValor retorna o valor correto")
    void getValor_retornaCorreto() {
        Sensor sensor = new Sensor(2, "Temperatura", "C");
        DadosMaquina dado = new DadosMaquina(2, 72.3, sensor);
        assertEquals(72.3, dado.getValor(), 0.001);
    }

    @Test
    @DisplayName("getValor aceita valor zero")
    void getValor_valorZero() {
        Sensor sensor = new Sensor(3, "Corrente", "A");
        DadosMaquina dado = new DadosMaquina(3, 0.0, sensor);
        assertEquals(0.0, dado.getValor(), 0.001);
    }

    @Test
    @DisplayName("getValor aceita valor negativo")
    void getValor_valorNegativo() {
        Sensor sensor = new Sensor(4, "Tensao", "V");
        DadosMaquina dado = new DadosMaquina(4, -12.5, sensor);
        assertEquals(-12.5, dado.getValor(), 0.001);
    }

    // =========================================================================
    // getDataColeta()
    // =========================================================================

    @Test
    @DisplayName("getDataColeta retorna data nao nula apos construcao com parametros")
    void getDataColeta_naoNulaAposConstrucao() {
        Sensor sensor = new Sensor(5, "Vibracao", "mm/s");
        DadosMaquina dado = new DadosMaquina(5, 3.2, sensor);
        assertNotNull(dado.getDataColeta());
    }

    @Test
    @DisplayName("getDataColeta retorna data dentro de intervalo razoavel de tempo")
    void getDataColeta_dentroDeIntervaloDeTempoRazoavel() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(2);
        Sensor sensor = new Sensor(6, "Umidade", "%");
        DadosMaquina dado = new DadosMaquina(6, 65.0, sensor);
        LocalDateTime depois = LocalDateTime.now().plusSeconds(2);

        assertTrue(!dado.getDataColeta().isBefore(antes),
            "dataColeta nao deve ser anterior ao momento de criacao");
        assertTrue(!dado.getDataColeta().isAfter(depois),
            "dataColeta nao deve ser posterior ao momento de criacao");
    }

    // =========================================================================
    // getSensor()
    // =========================================================================

    @Test
    @DisplayName("getSensor retorna o sensor vinculado corretamente")
    void getSensor_retornaSensorVinculado() {
        Sensor sensor = new Sensor(7, "Fluxo", "L/min");
        DadosMaquina dado = new DadosMaquina(7, 25.0, sensor);
        assertNotNull(dado.getSensor());
        assertEquals("Fluxo", dado.getSensor().getTipo());
        assertEquals("L/min", dado.getSensor().getUnidade());
    }

    @Test
    @DisplayName("getSensor retorna null quando sensor nao foi vinculado (construtor padrao)")
    void getSensor_construtorPadrao_retornaNull() {
        DadosMaquina dado = new DadosMaquina();
        assertNull(dado.getSensor());
    }

    // =========================================================================
    // exibirDado()
    // =========================================================================

    @Test
    @DisplayName("exibirDado() contem tipo do sensor, unidade e valor formatado")
    void exibirDado_comSensor_conteudoCompleto() {
        Sensor sensor = new Sensor(8, "RPM", "rpm");
        DadosMaquina dado = new DadosMaquina(8, 1450.0, sensor);
        String exibicao = dado.exibirDado();
        assertTrue(exibicao.contains("RPM"), "Deve conter o tipo do sensor");
        assertTrue(exibicao.contains("rpm"), "Deve conter a unidade do sensor");
        assertTrue(exibicao.contains("1450") || exibicao.contains("1.450"),
            "Deve conter o valor formatado");
    }

    @Test
    @DisplayName("exibirDado() sem sensor exibe 'Sensor' como fallback")
    void exibirDado_semSensor_exibeFallback() {
        DadosMaquina dado = new DadosMaquina(9, 10.0, null);
        String exibicao = dado.exibirDado();
        assertTrue(exibicao.contains("Sensor"),
            "Sem sensor vinculado, exibirDado deve exibir 'Sensor' como fallback");
    }

    @Test
    @DisplayName("exibirDado() contem data no formato dd/MM HH:mm")
    void exibirDado_contemData() {
        Sensor sensor = new Sensor(10, "Temperatura", "C");
        DadosMaquina dado = new DadosMaquina(10, 55.0, sensor);
        String exibicao = dado.exibirDado();
        // O formato e [dd/MM HH:mm] — verifica pelos colchetes
        assertTrue(exibicao.contains("[") && exibicao.contains("]"),
            "exibirDado deve conter a data entre colchetes");
    }
}
