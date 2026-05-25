package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Alerta}.
 *
 * BUG-05: contadorId e historico sao campos static em Alerta — acumulam estado
 * entre instancias e entre execucoes de testes. Sem reset, a ordem dos testes
 * afeta os resultados. Por isso, @BeforeEach faz reset via reflection.
 *
 * BUG-06: gerarAlerta() (instancia) nao adiciona ao historico estatico,
 * divergindo do comportamento de gerar() estatico.
 *
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Alerta")
class AlertaTest {

    private Maquina maquina;

    @BeforeEach
    void resetEstadoEstatico() {
        Alerta.resetar();
        maquina = new Maquina(1, "Turbina Central", "FabricanteZ");
    }

    // =========================================================================
    // Metodo estatico gerar()
    // =========================================================================

    @Test
    @DisplayName("gerar() com risco < 20 cria alerta NORMAL")
    void gerar_riscoMenorQue20_nivelNormal() {
        Alerta alerta = Alerta.gerar(maquina, 0.0);
        assertNotNull(alerta);
        assertEquals("NORMAL", alerta.getNivel());
        assertEquals("Status OK", alerta.getTipo());
        assertNotNull(alerta.getMensagem());
    }

    @Test
    @DisplayName("gerar() com risco == 19.9 (abaixo de 20) -> NORMAL")
    void gerar_risco19_9_nivelNormal() {
        Alerta alerta = Alerta.gerar(maquina, 19.9);
        assertEquals("NORMAL", alerta.getNivel());
    }

    @Test
    @DisplayName("gerar() com risco == 20.0 (limiar exato) -> ATENCAO")
    void gerar_risco20_nivelAtencao() {
        Alerta alerta = Alerta.gerar(maquina, 20.0);
        assertEquals("ATENCAO", alerta.getNivel());
        assertEquals("Monitoramento", alerta.getTipo());
    }

    @Test
    @DisplayName("gerar() com risco == 39.9 (abaixo de 40) -> ATENCAO")
    void gerar_risco39_9_nivelAtencao() {
        Alerta alerta = Alerta.gerar(maquina, 39.9);
        assertEquals("ATENCAO", alerta.getNivel());
    }

    @Test
    @DisplayName("gerar() com risco == 40.0 (limiar exato) -> MODERADO")
    void gerar_risco40_nivelModerado() {
        Alerta alerta = Alerta.gerar(maquina, 40.0);
        assertEquals("MODERADO", alerta.getNivel());
        assertEquals("Atencao Necessaria", alerta.getTipo());
    }

    @Test
    @DisplayName("gerar() com risco == 69.9 (abaixo de 70) -> MODERADO")
    void gerar_risco69_9_nivelModerado() {
        Alerta alerta = Alerta.gerar(maquina, 69.9);
        assertEquals("MODERADO", alerta.getNivel());
    }

    @Test
    @DisplayName("gerar() com risco == 70.0 (limiar exato) -> CRITICO")
    void gerar_risco70_nivelCritico() {
        Alerta alerta = Alerta.gerar(maquina, 70.0);
        assertEquals("CRITICO", alerta.getNivel());
        assertEquals("Falha Iminente", alerta.getTipo());
    }

    @Test
    @DisplayName("gerar() com risco == 100.0 -> CRITICO")
    void gerar_risco100_nivelCritico() {
        Alerta alerta = Alerta.gerar(maquina, 100.0);
        assertEquals("CRITICO", alerta.getNivel());
    }

    // =========================================================================
    // Historico e contadorId (estaticos — BUG-05)
    // =========================================================================

    @Test
    @DisplayName("gerar() adiciona alerta ao historico estatico")
    void gerar_adicionaAoHistorico() {
        assertEquals(0, Alerta.totalAlertas(), "Historico deve estar vazio apos reset");
        Alerta.gerar(maquina, 50.0);
        assertEquals(1, Alerta.totalAlertas());
        assertEquals(1, Alerta.getHistorico().size());
    }

    @Test
    @DisplayName("gerar() multiplas vezes acumula no historico estatico")
    void gerar_multiplas_acumulaHistorico() {
        Alerta.gerar(maquina, 10.0);
        Alerta.gerar(maquina, 40.0);
        Alerta.gerar(maquina, 80.0);
        assertEquals(3, Alerta.totalAlertas());
    }

    // BUG-05: campos static acumulam estado — sem reset via reflection,
    // testes subsequentes veriam historico com alertas de testes anteriores.
    @Test
    @DisplayName("BUG-05: historico estatico acumula entre chamadas — reset via reflection e necessario")
    void estatico_acumulaEstadoEntreInstancias() {
        // Apos reset no @BeforeEach, historico deve estar vazio
        assertEquals(0, Alerta.totalAlertas(),
            "BUG-05: historico estatico deve estar zerado apos reset via reflection");
        Alerta.gerar(maquina, 30.0);
        assertEquals(1, Alerta.totalAlertas(),
            "BUG-05: historico acumula chamadas de gerar() da sessao atual");
    }

    // =========================================================================
    // Metodo de instancia gerarAlerta() — BUG-06
    // =========================================================================

    // BUG-06 CORRIGIDO: gerarAlerta() (instancia) agora adiciona ao historico estatico
    // e define todos os campos (nivel, tipo, mensagem, data, id), sendo consistente
    // com o comportamento de gerar() estatico.
    @Test
    @DisplayName("BUG-06 corrigido: gerarAlerta() de instancia agora adiciona ao historico estatico")
    void gerarAlerta_instancia_agora_adicionaAoHistorico() {
        Alerta alerta = new Alerta();
        alerta.gerarAlerta(maquina, 75.0);
        assertEquals(1, Alerta.totalAlertas(),
            "Apos correcao do BUG-06: gerarAlerta() deve adicionar ao historico estatico");
        assertEquals("CRITICO", alerta.getNivel());
    }

    @Test
    @DisplayName("BUG-06: gerarAlerta() de instancia define nivel corretamente apesar do bug")
    void gerarAlerta_instancia_definiveNivelCorretamente() {
        Alerta alerta = new Alerta();
        alerta.gerarAlerta(maquina, 75.0);
        // nivel eh definido corretamente mesmo que o historico nao seja atualizado
        assertEquals("CRITICO", alerta.getNivel(),
            "BUG-06: nivel deve ser definido corretamente pela versao de instancia");
    }

    @Test
    @DisplayName("BUG-06: gerarAlerta() instancia com risco MODERADO define nivel MODERADO")
    void gerarAlerta_instancia_nivelModerado() {
        Alerta alerta = new Alerta();
        alerta.gerarAlerta(maquina, 50.0);
        assertEquals("MODERADO", alerta.getNivel());
    }

    @Test
    @DisplayName("BUG-06: gerarAlerta() instancia com risco ATENCAO define nivel ATENCAO")
    void gerarAlerta_instancia_nivelAtencao() {
        Alerta alerta = new Alerta();
        alerta.gerarAlerta(maquina, 25.0);
        assertEquals("ATENCAO", alerta.getNivel());
    }

    @Test
    @DisplayName("BUG-06: gerarAlerta() instancia com risco NORMAL define nivel NORMAL")
    void gerarAlerta_instancia_nivelNormal() {
        Alerta alerta = new Alerta();
        alerta.gerarAlerta(maquina, 5.0);
        assertEquals("NORMAL", alerta.getNivel());
    }

    // =========================================================================
    // exibir() e exibirCompleto()
    // =========================================================================

    @Test
    @DisplayName("exibir() retorna string com nivel, tipo e mensagem")
    void exibir_retornaFormatoCorreto() {
        Alerta alerta = Alerta.gerar(maquina, 80.0);
        String exibicao = alerta.exibir();
        assertTrue(exibicao.contains("CRITICO"), "Deve conter o nivel");
        assertTrue(exibicao.contains("Falha Iminente"), "Deve conter o tipo");
        assertNotNull(alerta.getMensagem());
    }

    @Test
    @DisplayName("exibirCompleto() retorna dados da maquina, tipo, nivel, mensagem e data")
    void exibirCompleto_retornaConteudoCompleto() {
        Alerta alerta = Alerta.gerar(maquina, 40.0);
        String completo = alerta.exibirCompleto();
        assertTrue(completo.contains("Turbina Central"), "Deve conter o nome da maquina");
        assertTrue(completo.contains("MODERADO"), "Deve conter o nivel");
        assertTrue(completo.contains("Atencao Necessaria"), "Deve conter o tipo");
    }

    @Test
    @DisplayName("Construtor com id/tipo/mensagem inicializa campos corretamente")
    void construtorComParametros_inicializaCampos() {
        Alerta alerta = new Alerta(99, "Teste", "Mensagem de teste");
        assertEquals("Teste", alerta.getTipo());
        assertEquals("Mensagem de teste", alerta.getMensagem());
    }

    @Test
    @DisplayName("getHistorico retorna lista nao nula")
    void getHistorico_listaNaoNula() {
        assertNotNull(Alerta.getHistorico());
    }
}
