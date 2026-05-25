package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link LogAtividade}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("LogAtividade")
class LogAtividadeTest {

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor padrao cria instancia sem lancamento de excecao")
    void construtorPadrao_semExcecao() {
        assertDoesNotThrow((Executable) LogAtividade::new);
    }

    @Test
    @DisplayName("Construtor com id/entidade/acao inicializa campos corretamente")
    void construtorSemUsuario_inicializaCampos() {
        LogAtividade log = new LogAtividade(1, "Maquina", "Leitura registrada");
        assertEquals("Maquina", log.getEntidade());
        assertEquals("Leitura registrada", log.getAcao());
    }

    @Test
    @DisplayName("Construtor com usuario vincula usuario corretamente")
    void construtorComUsuario_vinculaUsuario() {
        Usuario usuario = new Usuario(5, "Operador Teste", "operador@teste.com");
        LogAtividade log = new LogAtividade(2, "Sensor", "Calibracao", usuario);
        assertEquals("Sensor", log.getEntidade());
        assertEquals("Calibracao", log.getAcao());
        // Verifica que o nome do usuario aparece no log
        String exibicao = log.exibirLog();
        assertTrue(exibicao.contains("Operador Teste"),
            "O log deve exibir o nome do usuario vinculado");
    }

    // =========================================================================
    // exibirLog()
    // =========================================================================

    @Test
    @DisplayName("exibirLog() sem usuario exibe 'Sistema'")
    void exibirLog_semUsuario_exibeSistema() {
        LogAtividade log = new LogAtividade(3, "Predicao", "Risco calculado");
        String exibicao = log.exibirLog();
        assertTrue(exibicao.contains("Sistema"),
            "Sem usuario, exibirLog deve exibir 'Sistema'");
    }

    @Test
    @DisplayName("exibirLog() contem entidade e acao formatadas")
    void exibirLog_conteudoCompleto() {
        LogAtividade log = new LogAtividade(4, "Alerta", "Alerta critico gerado");
        String exibicao = log.exibirLog();
        assertTrue(exibicao.contains("Alerta"), "Deve conter a entidade");
        assertTrue(exibicao.contains("Alerta critico gerado"), "Deve conter a acao");
    }

    @Test
    @DisplayName("exibirLog() contem data no formato esperado (dd/MM HH:mm:ss)")
    void exibirLog_contemData() {
        LogAtividade log = new LogAtividade(5, "Manutencao", "Iniciada");
        String exibicao = log.exibirLog();
        // A data sempre tem o formato [dd/MM HH:mm:ss] — verifica pelo delimitador
        assertTrue(exibicao.contains("[") && exibicao.contains("]"),
            "exibirLog deve conter a data entre colchetes");
    }

    @Test
    @DisplayName("exibirLog() com usuario exibe nome do usuario em vez de 'Sistema'")
    void exibirLog_comUsuario_exibeNomeDoUsuario() {
        Usuario u = new Usuario(10, "Joana Paiva", "joana@empresa.com");
        LogAtividade log = new LogAtividade(6, "Empresa", "Cadastro", u);
        String exibicao = log.exibirLog();
        assertTrue(exibicao.contains("Joana Paiva"), "Deve conter o nome do usuario");
        assertFalse(exibicao.contains("Sistema"),
            "Com usuario definido, nao deve exibir 'Sistema'");
    }

    // =========================================================================
    // Getters
    // =========================================================================

    @Test
    @DisplayName("getEntidade retorna a entidade correta")
    void getEntidade_retornaCorreto() {
        LogAtividade log = new LogAtividade(7, "Sensor", "Leitura");
        assertEquals("Sensor", log.getEntidade());
    }

    @Test
    @DisplayName("getAcao retorna a acao correta")
    void getAcao_retornaCorreto() {
        LogAtividade log = new LogAtividade(8, "Maquina", "Desligamento de emergencia");
        assertEquals("Desligamento de emergencia", log.getAcao());
    }

    // =========================================================================
    // Valores de borda
    // =========================================================================

    @Test
    @DisplayName("Construtor sem usuario (3 params) delega para construtor com usuario null")
    void construtorSemUsuario_delegaParaConstrutorComNull() {
        // Verifica que o construtor de 3 parametros resulta em usuario=null (via exibirLog -> "Sistema")
        LogAtividade log = new LogAtividade(9, "Empresa", "Exclusao");
        assertTrue(log.exibirLog().contains("Sistema"),
            "Construtor sem usuario deve resultar em usuario null -> exibir 'Sistema'");
    }

    @Test
    @DisplayName("LogAtividade com entidade e acao vazias nao lanca excecao")
    void entidadeEAcaoVazias_semExcecao() {
        assertDoesNotThrow(() -> {
            LogAtividade log = new LogAtividade(10, "", "");
            log.exibirLog();
        });
    }
}
