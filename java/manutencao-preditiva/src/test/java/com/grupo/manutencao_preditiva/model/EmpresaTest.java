package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Empresa}.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Empresa")
class EmpresaTest {

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor padrao cria instancia com valores null/zero")
    void construtorPadrao_valoresDefault() {
        Empresa e = new Empresa();
        assertEquals(0, e.getId());
        assertNull(e.getNome());
        assertNull(e.getCnpj());
    }

    @Test
    @DisplayName("Construtor id/nome/cnpj inicializa campos e define dataCriacao como hoje")
    void construtorComIdNomeCnpj_inicializaCampos() {
        Empresa e = new Empresa(1, "Industria Alpha", "12.345.678/0001-99");
        assertEquals(1, e.getId());
        assertEquals("Industria Alpha", e.getNome());
        assertEquals("12.345.678/0001-99", e.getCnpj());
    }

    @Test
    @DisplayName("Construtor completo inicializa todos os campos incluindo setorIndustrial e dataCriacao")
    void construtorCompleto_inicializaTodosCampos() {
        LocalDate data = LocalDate.of(2020, 3, 15);
        Empresa e = new Empresa(2, "Industria Beta", "98.765.432/0001-11", "Quimico", data);
        assertEquals(2, e.getId());
        assertEquals("Industria Beta", e.getNome());
        assertEquals("98.765.432/0001-11", e.getCnpj());
    }

    // =========================================================================
    // exibirEmpresa()
    // =========================================================================

    @Test
    @DisplayName("exibirEmpresa() contem nome, CNPJ, setor e data")
    void exibirEmpresa_conteudoCompleto() {
        LocalDate data = LocalDate.of(2015, 6, 1);
        Empresa e = new Empresa(3, "Metalurgica Gamma", "11.222.333/0001-44", "Metalurgico", data);
        String exibicao = e.exibirEmpresa();
        assertTrue(exibicao.contains("Metalurgica Gamma"), "Deve conter o nome");
        assertTrue(exibicao.contains("11.222.333/0001-44"), "Deve conter o CNPJ");
        assertTrue(exibicao.contains("Metalurgico"), "Deve conter o setor industrial");
        assertTrue(exibicao.contains("2015") || exibicao.contains("01/06/2015"),
            "Deve conter o ano ou data formatada");
    }

    @Test
    @DisplayName("exibirEmpresa() mostra 'Nao informado' quando setorIndustrial eh null")
    void exibirEmpresa_semSetor_mostraNaoInformado() {
        Empresa e = new Empresa(4, "Empresa Sem Setor", "00.000.000/0001-00");
        String exibicao = e.exibirEmpresa();
        assertTrue(exibicao.contains("Nao informado"),
            "setorIndustrial null deve exibir 'Nao informado'");
    }

    @Test
    @DisplayName("exibirEmpresa() mostra 'N/A' quando dataCriacao eh null")
    void exibirEmpresa_semData_mostraNa() {
        // Construtor padrao nao define dataCriacao, nem o construtor completo quando data=null
        Empresa e = new Empresa(5, "Empresa Sem Data", "00.000.000/0001-01", "Textil", null);
        String exibicao = e.exibirEmpresa();
        assertTrue(exibicao.contains("N/A"),
            "dataCriacao null deve exibir 'N/A'");
    }

    // =========================================================================
    // Getters
    // =========================================================================

    @Test
    @DisplayName("getNome retorna o nome correto")
    void getNome_retornaCorreto() {
        Empresa e = new Empresa(6, "Empresa Teste", "99.999.999/0001-00");
        assertEquals("Empresa Teste", e.getNome());
    }

    @Test
    @DisplayName("getCnpj retorna o CNPJ correto")
    void getCnpj_retornaCorreto() {
        Empresa e = new Empresa(7, "Qualquer", "55.555.555/0001-55");
        assertEquals("55.555.555/0001-55", e.getCnpj());
    }

    @Test
    @DisplayName("getId retorna o id correto")
    void getId_retornaCorreto() {
        Empresa e = new Empresa(42, "Empresa 42", "42.000.000/0001-00");
        assertEquals(42, e.getId());
    }

    // =========================================================================
    // Valor de borda
    // =========================================================================

    @Test
    @DisplayName("Empresa com nome vazio nao lanca excecao ao exibir")
    void nomeVazio_semExcecao() {
        Empresa e = new Empresa(8, "", "");
        assertDoesNotThrow(e::exibirEmpresa);
    }
}
