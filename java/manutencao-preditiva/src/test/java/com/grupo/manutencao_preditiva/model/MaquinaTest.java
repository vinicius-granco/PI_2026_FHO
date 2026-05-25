package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Maquina}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Maquina")
class MaquinaTest {

    private Maquina maquina;

    @BeforeEach
    void setUp() {
        maquina = new Maquina(1, "Compressor A", "AcmeCorp");
    }

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Construtor padrao cria instancia com valores zero/null")
    void construtorPadrao_valoresDefault() {
        Maquina m = new Maquina();
        assertEquals(0, m.getId());
        assertNull(m.getNome());
        assertNull(m.getFabricante());
        assertEquals(0.0, m.getTemperatura());
        assertEquals(0.0, m.getVibracao());
        assertEquals(0.0, m.getCarga());
        assertEquals(0, m.getHorasUso());
        assertEquals(0, m.getTotalLeituras());
    }

    @Test
    @DisplayName("Construtor id/nome/fabricante inicializa campos")
    void construtorComParametros_inicializaCampos() {
        assertEquals(1, maquina.getId());
        assertEquals("Compressor A", maquina.getNome());
        assertEquals("AcmeCorp", maquina.getFabricante());
    }

    @Test
    @DisplayName("Construtor com Empresa vincula empresa")
    void construtorComEmpresa_vinculaEmpresa() {
        Empresa empresa = new Empresa(10, "Industria X", "00.000.000/0001-00");
        Maquina m = new Maquina(2, "Turbina", "Fabricante Y", empresa);
        assertEquals("Industria X", m.exibirDados().contains("Industria X") ? "Industria X" : null);
        assertTrue(m.exibirDados().contains("Industria X"), "exibirDados deve conter o nome da empresa");
    }

    // -------------------------------------------------------------------------
    // setTemperatura — clamp >= 0
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setTemperatura aceita valor positivo")
    void setTemperatura_valorPositivo() {
        maquina.setTemperatura(75.0);
        assertEquals(75.0, maquina.getTemperatura(), 0.001);
    }

    @Test
    @DisplayName("setTemperatura com valor negativo clampeia para 0")
    void setTemperatura_valorNegativo_clampado() {
        maquina.setTemperatura(-10.0);
        assertEquals(0.0, maquina.getTemperatura(), 0.001);
    }

    @Test
    @DisplayName("setTemperatura com zero permanece zero")
    void setTemperatura_zero() {
        maquina.setTemperatura(0.0);
        assertEquals(0.0, maquina.getTemperatura(), 0.001);
    }

    // -------------------------------------------------------------------------
    // setVibracao — clamp >= 0
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setVibracao aceita valor positivo")
    void setVibracao_valorPositivo() {
        maquina.setVibracao(5.5);
        assertEquals(5.5, maquina.getVibracao(), 0.001);
    }

    @Test
    @DisplayName("setVibracao com valor negativo clampeia para 0")
    void setVibracao_valorNegativo_clampado() {
        maquina.setVibracao(-3.0);
        assertEquals(0.0, maquina.getVibracao(), 0.001);
    }

    // -------------------------------------------------------------------------
    // setCarga — clamp [0, 100]
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setCarga aceita valor dentro do intervalo [0,100]")
    void setCarga_valorNormal() {
        maquina.setCarga(70.0);
        assertEquals(70.0, maquina.getCarga(), 0.001);
    }

    @Test
    @DisplayName("setCarga com valor acima de 100 clampeia para 100")
    void setCarga_acimaDe100_clampado() {
        maquina.setCarga(150.0);
        assertEquals(100.0, maquina.getCarga(), 0.001);
    }

    @Test
    @DisplayName("setCarga com valor negativo clampeia para 0")
    void setCarga_negativo_clampado() {
        maquina.setCarga(-5.0);
        assertEquals(0.0, maquina.getCarga(), 0.001);
    }

    @Test
    @DisplayName("setCarga exatamente 0 e 100 sao valores de borda validos")
    void setCarga_bordas_0_e_100() {
        maquina.setCarga(0.0);
        assertEquals(0.0, maquina.getCarga(), 0.001);
        maquina.setCarga(100.0);
        assertEquals(100.0, maquina.getCarga(), 0.001);
    }

    // -------------------------------------------------------------------------
    // setHorasUso — clamp >= 0
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setHorasUso aceita valor positivo")
    void setHorasUso_valorPositivo() {
        maquina.setHorasUso(2500);
        assertEquals(2500, maquina.getHorasUso());
    }

    @Test
    @DisplayName("setHorasUso com valor negativo clampeia para 0")
    void setHorasUso_negativo_clampado() {
        maquina.setHorasUso(-100);
        assertEquals(0, maquina.getHorasUso());
    }

    // -------------------------------------------------------------------------
    // atualizarLeituras
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("atualizarLeituras(temp, vib) atualiza temperatura e vibracao e registra historico")
    void atualizarLeituras2Params_atualizaCamposEHistorico() {
        maquina.atualizarLeituras(55.0, 3.2);
        assertEquals(55.0, maquina.getTemperatura(), 0.001);
        assertEquals(3.2, maquina.getVibracao(), 0.001);
        assertEquals(1, maquina.getTotalLeituras());
    }

    @Test
    @DisplayName("atualizarLeituras(temp, vib, carga, horas) atualiza todos os campos")
    void atualizarLeituras4Params_atualizaTodosCampos() {
        maquina.atualizarLeituras(65.0, 7.0, 80.0, 1200);
        assertEquals(65.0, maquina.getTemperatura(), 0.001);
        assertEquals(7.0, maquina.getVibracao(), 0.001);
        assertEquals(80.0, maquina.getCarga(), 0.001);
        assertEquals(1200, maquina.getHorasUso());
        assertEquals(1, maquina.getTotalLeituras());
    }

    @Test
    @DisplayName("atualizarLeituras multiplas vezes acumula historico")
    void atualizarLeituras_multiplas_acumulaHistorico() {
        maquina.atualizarLeituras(40.0, 2.0);
        maquina.atualizarLeituras(50.0, 3.0);
        maquina.atualizarLeituras(60.0, 4.0);
        assertEquals(3, maquina.getTotalLeituras());
    }

    // -------------------------------------------------------------------------
    // adicionarSensor / getSensores
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("adicionarSensor adiciona sensor a lista")
    void adicionarSensor_adicionaSensor() {
        Sensor s = new Sensor(1, "Pressao", "bar");
        maquina.adicionarSensor(s);
        List<Sensor> sensores = maquina.getSensores();
        assertEquals(1, sensores.size());
        assertEquals("Pressao", sensores.get(0).getTipo());
    }

    @Test
    @DisplayName("getSensores retorna lista vazia por padrao")
    void getSensores_listaVaziaInicial() {
        assertTrue(maquina.getSensores().isEmpty());
    }

    // -------------------------------------------------------------------------
    // resumo()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("resumo contem nome, temperatura, vibracao e horas")
    void resumo_conteudoEsperado() {
        maquina.atualizarLeituras(55.0, 3.2, 70.0, 500);
        String resumo = maquina.resumo();
        assertTrue(resumo.contains("Compressor A"), "Deve conter o nome");
        assertTrue(resumo.contains("55"), "Deve conter a temperatura");
        assertTrue(resumo.contains("3,20") || resumo.contains("3.20"), "Deve conter a vibracao");
        assertTrue(resumo.contains("500"), "Deve conter as horas de uso");
    }

    // -------------------------------------------------------------------------
    // exibirDados()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("exibirDados contem nome, fabricante e tipo quando definido")
    void exibirDados_comTipo_conteudoCompleto() {
        maquina.setTipo("Industrial");
        String dados = maquina.exibirDados();
        assertTrue(dados.contains("Compressor A"), "Deve conter o nome");
        assertTrue(dados.contains("AcmeCorp"), "Deve conter o fabricante");
        assertTrue(dados.contains("Industrial"), "Deve conter o tipo");
    }

    @Test
    @DisplayName("exibirDados mostra 'Nao informado' quando tipo e null")
    void exibirDados_semTipo_mostraNaoInformado() {
        String dados = maquina.exibirDados();
        assertTrue(dados.contains("Nao informado"), "Tipo null deve exibir 'Nao informado'");
    }

    // -------------------------------------------------------------------------
    // exibirHistorico()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("exibirHistorico sem leituras retorna mensagem especial")
    void exibirHistorico_semLeituras_mensagemEspecial() {
        String h = maquina.exibirHistorico();
        assertTrue(h.contains("Nenhuma leitura"), "Deve indicar ausencia de leituras");
    }

    @Test
    @DisplayName("exibirHistorico com 7 leituras exibe apenas as ultimas 5")
    void exibirHistorico_7Leituras_exibeUltimas5() {
        for (int i = 1; i <= 7; i++) {
            maquina.atualizarLeituras(30.0 + i, 1.0 + i * 0.1);
        }
        String h = maquina.exibirHistorico();
        // Entradas sao indexadas de 1. As ultimas 5 sao indices 3 a 7 (1-based: [3]..[7])
        assertTrue(h.contains("[3]"), "Deve conter a leitura de indice 3 (inicio da janela de 5)");
        assertTrue(h.contains("[7]"), "Deve conter a leitura de indice 7 (ultima)");
        assertFalse(h.contains("[1]"), "Nao deve exibir leituras fora da janela de 5 — indice 1");
        assertFalse(h.contains("[2]"), "Nao deve exibir leituras fora da janela de 5 — indice 2");
    }

    @Test
    @DisplayName("exibirHistorico com exatamente 5 leituras exibe todas")
    void exibirHistorico_5Leituras_exibeTodasAs5() {
        for (int i = 1; i <= 5; i++) {
            maquina.atualizarLeituras(30.0 + i, 1.0);
        }
        String h = maquina.exibirHistorico();
        assertTrue(h.contains("[1]"), "Deve exibir a primeira leitura");
        assertTrue(h.contains("[5]"), "Deve exibir a quinta leitura");
    }
}
