package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Usuario}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Usuario")
class UsuarioTest {

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor padrao cria instancia com valores null/zero")
    void construtorPadrao_valoresDefault() {
        Usuario u = new Usuario();
        assertEquals(0, u.getId());
        assertNull(u.getNomeCompleto());
        assertNull(u.getEmail());
        assertNull(u.getCargo());
    }

    @Test
    @DisplayName("Construtor id/nome/email inicializa campos corretamente")
    void construtorComIdNomeEmail_inicializaCampos() {
        Usuario u = new Usuario(1, "Joao Silva", "joao.silva@empresa.com");
        assertEquals(1, u.getId());
        assertEquals("Joao Silva", u.getNomeCompleto());
        assertEquals("joao.silva@empresa.com", u.getEmail());
        assertNull(u.getCargo());
    }

    @Test
    @DisplayName("Construtor com Empresa vincula empresa corretamente")
    void construtorComEmpresa_vinculaEmpresa() {
        Empresa empresa = new Empresa(10, "Industria XYZ", "00.111.222/0001-33");
        Usuario u = new Usuario(2, "Maria Souza", "maria@empresa.com", empresa);
        assertEquals("Maria Souza", u.getNomeCompleto());
        // Verifica que exibirUsuario inclui o nome da empresa
        String exibicao = u.exibirUsuario();
        assertTrue(exibicao.contains("Industria XYZ"),
            "exibirUsuario deve conter o nome da empresa vinculada");
    }

    // =========================================================================
    // setCargo() / getCargo()
    // =========================================================================

    @Test
    @DisplayName("setCargo define o cargo corretamente")
    void setCargo_defineCargo() {
        Usuario u = new Usuario(3, "Carlos Lima", "carlos@teste.com");
        u.setCargo("Engenheiro");
        assertEquals("Engenheiro", u.getCargo());
    }

    @Test
    @DisplayName("setCargo pode ser atualizado multiplas vezes")
    void setCargo_atualizacaoMultipla() {
        Usuario u = new Usuario(4, "Ana Ferreira", "ana@teste.com");
        u.setCargo("Tecnico");
        u.setCargo("Supervisor");
        assertEquals("Supervisor", u.getCargo());
    }

    @Test
    @DisplayName("setCargo com null limpa o cargo")
    void setCargo_null_limpaCargo() {
        Usuario u = new Usuario(5, "Pedro Costa", "pedro@teste.com");
        u.setCargo("Gerente");
        u.setCargo(null);
        assertNull(u.getCargo());
    }

    // =========================================================================
    // exibirUsuario()
    // =========================================================================

    @Test
    @DisplayName("exibirUsuario() contem nome, email, cargo e empresa")
    void exibirUsuario_comTodosCampos_conteudoCompleto() {
        Empresa empresa = new Empresa(5, "Empresa ABC", "33.444.555/0001-66");
        Usuario u = new Usuario(6, "Lucas Mendes", "lucas@abc.com", empresa);
        u.setCargo("Analista");
        String exibicao = u.exibirUsuario();
        assertTrue(exibicao.contains("Lucas Mendes"), "Deve conter o nome");
        assertTrue(exibicao.contains("lucas@abc.com"), "Deve conter o email");
        assertTrue(exibicao.contains("Analista"), "Deve conter o cargo");
        assertTrue(exibicao.contains("Empresa ABC"), "Deve conter a empresa");
    }

    @Test
    @DisplayName("exibirUsuario() mostra 'Nao informado' quando cargo eh null")
    void exibirUsuario_semCargo_mostraNaoInformado() {
        Usuario u = new Usuario(7, "Fernanda Dias", "fernanda@teste.com");
        String exibicao = u.exibirUsuario();
        assertTrue(exibicao.contains("Nao informado"),
            "cargo null deve exibir 'Nao informado'");
    }

    @Test
    @DisplayName("exibirUsuario() mostra 'Nao vinculado' quando empresa eh null")
    void exibirUsuario_semEmpresa_mostraNaoVinculado() {
        Usuario u = new Usuario(8, "Roberto Alves", "roberto@teste.com");
        String exibicao = u.exibirUsuario();
        assertTrue(exibicao.contains("Nao vinculado"),
            "empresa null deve exibir 'Nao vinculado'");
    }

    @Test
    @DisplayName("exibirUsuario() nao lanca excecao com todos os campos null (construtor padrao)")
    void exibirUsuario_construtorPadrao_semExcecao() {
        // Nota: construtor padrao deixa nome e email null, o que pode causar NPE
        // em String.format se os campos forem desreferenciados diretamente.
        // Aqui verificamos que a implementacao atual se comporta de forma aceitavel.
        Usuario u = new Usuario();
        // Se a implementacao usar String.format com null, o Java 17+ formata como "null"
        assertDoesNotThrow(u::exibirUsuario);
    }

    // =========================================================================
    // Getters
    // =========================================================================

    @Test
    @DisplayName("getId retorna o id correto")
    void getId_retornaCorreto() {
        Usuario u = new Usuario(99, "Nome", "email@test.com");
        assertEquals(99, u.getId());
    }

    @Test
    @DisplayName("getNomeCompleto retorna o nome correto")
    void getNomeCompleto_retornaCorreto() {
        Usuario u = new Usuario(1, "Fulano de Tal", "fulano@test.com");
        assertEquals("Fulano de Tal", u.getNomeCompleto());
    }

    @Test
    @DisplayName("getEmail retorna o email correto")
    void getEmail_retornaCorreto() {
        Usuario u = new Usuario(1, "Beltrano", "beltrano@dominio.com");
        assertEquals("beltrano@dominio.com", u.getEmail());
    }
}
