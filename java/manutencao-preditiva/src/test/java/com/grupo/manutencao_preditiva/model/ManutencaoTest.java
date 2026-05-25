package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Manutencao}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Manutencao")
class ManutencaoTest {

    private Maquina maquina;

    @BeforeEach
    void setUp() {
        maquina = new Maquina(1, "Prensa Hidraulica", "HidroTech");
    }

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor padrao cria instancia sem lancar excecao")
    void construtorPadrao_semExcecao() {
        assertDoesNotThrow((Executable) Manutencao::new);
    }

    @Test
    @DisplayName("Construtor id/tipo/maquina inicializa status como EM_ANDAMENTO")
    void construtorComParametros_statusEmAndamento() {
        Manutencao m = new Manutencao(1, "PREVENTIVA", maquina);
        assertEquals("EM_ANDAMENTO", m.getStatus());
        assertEquals("PREVENTIVA", m.getTipo());
        assertEquals(1, m.getId());
        assertEquals(0.0, m.getCusto(), 0.001);
    }

    // =========================================================================
    // finalizar()
    // =========================================================================

    @Test
    @DisplayName("finalizar() transiciona EM_ANDAMENTO -> CONCLUIDA corretamente")
    void finalizar_emAndamento_tornaConcluida() {
        Manutencao m = new Manutencao(1, "CORRETIVA", maquina);
        m.finalizar(1500.0, "Substituicao de rolamento");
        assertEquals("CONCLUIDA", m.getStatus());
        assertEquals(1500.0, m.getCusto(), 0.001);
    }

    @Test
    @DisplayName("finalizar() define dataFim (resumo nao lanca excecao apos finalizar)")
    void finalizar_defineDataFim_resumoFunciona() {
        Manutencao m = new Manutencao(1, "PREVENTIVA", maquina);
        m.finalizar(500.0, "Lubrificacao geral");
        assertDoesNotThrow(m::resumo);
        assertDoesNotThrow(m::exibirCompleto);
    }

    // BUG-09 CORRIGIDO: finalizar() rejeita custo negativo com IllegalArgumentException
    @Test
    @DisplayName("BUG-09 corrigido: finalizar() lanca IllegalArgumentException para custo negativo")
    void finalizar_custoNegativo_lancaIllegalArgument() {
        Manutencao m = new Manutencao(1, "CORRETIVA", maquina);
        assertThrows(IllegalArgumentException.class,
            () -> m.finalizar(-500.0, "Custo invalido"),
            "BUG-09 corrigido: custo negativo deve lancar IllegalArgumentException");
        assertEquals("EM_ANDAMENTO", m.getStatus(),
            "Status deve permanecer EM_ANDAMENTO quando finalizar() lanca excecao");
    }

    // BUG-07 CORRIGIDO: finalizar() sobre CONCLUIDA lanca IllegalStateException
    @Test
    @DisplayName("BUG-07 corrigido: finalizar() sobre CONCLUIDA lanca IllegalStateException")
    void finalizar_sobreConcluida_lancaIllegalState() {
        Manutencao m = new Manutencao(1, "PREVENTIVA", maquina);
        m.finalizar(1000.0, "Primeira finalizacao");
        assertEquals("CONCLUIDA", m.getStatus());

        assertThrows(IllegalStateException.class,
            () -> m.finalizar(2000.0, "Segunda finalizacao invalida"),
            "BUG-07 corrigido: finalizar() sobre CONCLUIDA deve lancar IllegalStateException");
        assertEquals(1000.0, m.getCusto(), 0.001,
            "Custo original deve ser preservado — dado nao foi corrompido");
    }

    // BUG-07 CORRIGIDO: finalizar() sobre CANCELADA lanca IllegalStateException
    @Test
    @DisplayName("BUG-07 corrigido: finalizar() sobre CANCELADA lanca IllegalStateException")
    void finalizar_sobreCancelada_lancaIllegalState() {
        Manutencao m = new Manutencao(1, "CORRETIVA", maquina);
        m.cancelar();
        assertEquals("CANCELADA", m.getStatus());

        assertThrows(IllegalStateException.class,
            () -> m.finalizar(800.0, "Finalizacao apos cancelamento"),
            "BUG-07 corrigido: finalizar() sobre CANCELADA deve lancar IllegalStateException");
        assertEquals("CANCELADA", m.getStatus(),
            "Status deve permanecer CANCELADA apos tentativa invalida");
    }

    // =========================================================================
    // cancelar()
    // =========================================================================

    @Test
    @DisplayName("cancelar() transiciona EM_ANDAMENTO -> CANCELADA corretamente")
    void cancelar_emAndamento_tornaCancelada() {
        Manutencao m = new Manutencao(2, "PREDITIVA", maquina);
        m.cancelar();
        assertEquals("CANCELADA", m.getStatus());
    }

    @Test
    @DisplayName("cancelar() define dataFim (resumo nao lanca excecao apos cancelar)")
    void cancelar_defineDataFim_resumoFunciona() {
        Manutencao m = new Manutencao(2, "PREDITIVA", maquina);
        m.cancelar();
        assertDoesNotThrow(m::resumo);
        assertDoesNotThrow(m::exibirCompleto);
    }

    // BUG-08 CORRIGIDO: cancelar() sobre CONCLUIDA lanca IllegalStateException
    @Test
    @DisplayName("BUG-08 corrigido: cancelar() sobre CONCLUIDA lanca IllegalStateException")
    void cancelar_sobreConcluida_lancaIllegalState() {
        Manutencao m = new Manutencao(1, "PREVENTIVA", maquina);
        m.finalizar(1000.0, "Trabalho concluido");
        assertEquals("CONCLUIDA", m.getStatus());

        assertThrows(IllegalStateException.class, m::cancelar,
            "BUG-08 corrigido: cancelar() sobre CONCLUIDA deve lancar IllegalStateException");
        assertEquals("CONCLUIDA", m.getStatus(),
            "Status deve permanecer CONCLUIDA apos tentativa invalida de cancelamento");
    }

    // BUG-08 CORRIGIDO: cancelar() sobre CANCELADA lanca IllegalStateException
    @Test
    @DisplayName("BUG-08 corrigido: cancelar() sobre CANCELADA lanca IllegalStateException")
    void cancelar_sobreCancelada_lancaIllegalState() {
        Manutencao m = new Manutencao(1, "CORRETIVA", maquina);
        m.cancelar();
        assertEquals("CANCELADA", m.getStatus());

        assertThrows(IllegalStateException.class, m::cancelar,
            "BUG-08 corrigido: segundo cancelar() deve lancar IllegalStateException");
    }

    // =========================================================================
    // resumo() e exibirCompleto()
    // =========================================================================

    @Test
    @DisplayName("resumo() contem id, tipo, status e custo")
    void resumo_conteudoEsperado() {
        Manutencao m = new Manutencao(5, "PREVENTIVA", maquina);
        m.finalizar(750.0, "Revisao semestral");
        String resumo = m.resumo();
        assertTrue(resumo.contains("5"), "Deve conter o id");
        assertTrue(resumo.contains("PREVENTIVA"), "Deve conter o tipo");
        assertTrue(resumo.contains("CONCLUIDA"), "Deve conter o status");
        assertTrue(resumo.contains("750"), "Deve conter o custo");
    }

    @Test
    @DisplayName("exibirCompleto() contem tipo, status, dataInicio e descricao apos finalizar")
    void exibirCompleto_aposFinalizacao_conteudoCompleto() {
        Manutencao m = new Manutencao(3, "CORRETIVA", maquina);
        m.finalizar(300.0, "Troca de correia");
        String completo = m.exibirCompleto();
        assertTrue(completo.contains("CORRETIVA"), "Deve conter o tipo");
        assertTrue(completo.contains("CONCLUIDA"), "Deve conter o status");
        assertTrue(completo.contains("Troca de correia"), "Deve conter a descricao");
        assertTrue(completo.contains("300"), "Deve conter o custo");
    }

    @Test
    @DisplayName("exibirCompleto() apos cancelar nao lanca excecao e contem CANCELADA")
    void exibirCompleto_aposCancelamento_semExcecao() {
        Manutencao m = new Manutencao(4, "PREDITIVA", maquina);
        m.cancelar();
        assertDoesNotThrow(m::exibirCompleto);
        assertTrue(m.exibirCompleto().contains("CANCELADA"));
    }
}
