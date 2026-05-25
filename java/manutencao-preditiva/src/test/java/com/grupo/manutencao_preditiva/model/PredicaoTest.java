package com.grupo.manutencao_preditiva.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para {@link Predicao}.
 * Cobertura rigorosa de boundary testing para cada limiar de cada fator.
 * Sem contexto Spring — plain JUnit 5.
 */
@DisplayName("Predicao")
class PredicaoTest {

    private Maquina maquina;

    /** Cria uma maquina base com todos os fatores em zero (risco minimo). */
    @BeforeEach
    void setUp() {
        maquina = new Maquina(1, "Maquina Teste", "Fabricante");
        maquina.atualizarLeituras(0.0, 0.0, 0.0, 0);
    }

    /** Utilitario: cria Predicao e calcula risco para a maquina fornecida. */
    private Predicao calcular(Maquina m) {
        Predicao p = new Predicao(m);
        p.calcularRisco(m);
        return p;
    }

    // =========================================================================
    // Testes de boundary — Temperatura
    // Limiares: >90 => 40, >75 => 30, >60 => 18, >45 => 8, >35 => 2, else => 0
    // =========================================================================

    @Test
    @DisplayName("Temperatura: 0.0 -> contribuicao 0")
    void temperatura_0_contribuicaoZero() {
        maquina.setTemperatura(0.0);
        Predicao p = calcular(maquina);
        // Com todos os outros fatores zero, risco total == contribuicao da temperatura
        assertEquals(0.0, p.getRisco(), 0.001);
        assertEquals("NORMAL", p.getStatus());
    }

    @Test
    @DisplayName("Temperatura: 35.0 (limite exato) -> contribuicao 0")
    void temperatura_35_limiteInferiorDoBlocoAtencao_contribuicaoZero() {
        maquina.setTemperatura(35.0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 35.1 (acima de 35) -> contribuicao 2")
    void temperatura_acimaDE35_contribuicao2() {
        maquina.setTemperatura(35.1);
        Predicao p = calcular(maquina);
        assertEquals(2.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 45.0 (limite exato do bloco 2) -> contribuicao 2")
    void temperatura_45_limiteBloco2_contribuicao2() {
        maquina.setTemperatura(45.0);
        Predicao p = calcular(maquina);
        assertEquals(2.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 45.1 (acima de 45) -> contribuicao 8")
    void temperatura_acimaDE45_contribuicao8() {
        maquina.setTemperatura(45.1);
        Predicao p = calcular(maquina);
        assertEquals(8.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 60.0 (limite exato do bloco 3) -> contribuicao 8")
    void temperatura_60_limiteBloco3_contribuicao8() {
        maquina.setTemperatura(60.0);
        Predicao p = calcular(maquina);
        assertEquals(8.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 60.1 (acima de 60) -> contribuicao 18")
    void temperatura_acimaDE60_contribuicao18() {
        maquina.setTemperatura(60.1);
        Predicao p = calcular(maquina);
        assertEquals(18.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 75.0 (limite exato do bloco 4) -> contribuicao 18")
    void temperatura_75_limiteBloco4_contribuicao18() {
        maquina.setTemperatura(75.0);
        Predicao p = calcular(maquina);
        assertEquals(18.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 75.1 (acima de 75) -> contribuicao 30")
    void temperatura_acimaDE75_contribuicao30() {
        maquina.setTemperatura(75.1);
        Predicao p = calcular(maquina);
        assertEquals(30.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 90.0 (limite exato do bloco 5) -> contribuicao 30")
    void temperatura_90_limiteBloco5_contribuicao30() {
        maquina.setTemperatura(90.0);
        Predicao p = calcular(maquina);
        assertEquals(30.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Temperatura: 90.1 (acima de 90) -> contribuicao 40")
    void temperatura_acimaDE90_contribuicao40() {
        maquina.setTemperatura(90.1);
        Predicao p = calcular(maquina);
        assertEquals(40.0, p.getRisco(), 0.001);
    }

    // =========================================================================
    // Testes de boundary — Vibracao
    // Limiares: >15 => 35, >10 => 25, >5 => 14, >2 => 5, else => 0
    // =========================================================================

    @Test
    @DisplayName("Vibracao: 0.0 -> contribuicao 0")
    void vibracao_0_contribuicaoZero() {
        maquina.setVibracao(0.0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 2.0 (limite exato) -> contribuicao 0")
    void vibracao_2_limiteExato_contribuicaoZero() {
        maquina.setVibracao(2.0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 2.1 (acima de 2) -> contribuicao 5")
    void vibracao_acimaDE2_contribuicao5() {
        maquina.setVibracao(2.1);
        Predicao p = calcular(maquina);
        assertEquals(5.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 5.0 (limite exato do bloco 2) -> contribuicao 5")
    void vibracao_5_limiteBloco2_contribuicao5() {
        maquina.setVibracao(5.0);
        Predicao p = calcular(maquina);
        assertEquals(5.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 5.1 (acima de 5) -> contribuicao 14")
    void vibracao_acimaDE5_contribuicao14() {
        maquina.setVibracao(5.1);
        Predicao p = calcular(maquina);
        assertEquals(14.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 10.0 (limite exato do bloco 3) -> contribuicao 14")
    void vibracao_10_limiteBloco3_contribuicao14() {
        maquina.setVibracao(10.0);
        Predicao p = calcular(maquina);
        assertEquals(14.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 10.1 (acima de 10) -> contribuicao 25")
    void vibracao_acimaDE10_contribuicao25() {
        maquina.setVibracao(10.1);
        Predicao p = calcular(maquina);
        assertEquals(25.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 15.0 (limite exato do bloco 4) -> contribuicao 25")
    void vibracao_15_limiteBloco4_contribuicao25() {
        maquina.setVibracao(15.0);
        Predicao p = calcular(maquina);
        assertEquals(25.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Vibracao: 15.1 (acima de 15) -> contribuicao 35")
    void vibracao_acimaDE15_contribuicao35() {
        maquina.setVibracao(15.1);
        Predicao p = calcular(maquina);
        assertEquals(35.0, p.getRisco(), 0.001);
    }

    // =========================================================================
    // Testes de boundary — Carga
    // Limiares: >95 => 15, >85 => 10, >70 => 5, >50 => 1, else => 0
    // =========================================================================

    @Test
    @DisplayName("Carga: 0.0 -> contribuicao 0")
    void carga_0_contribuicaoZero() {
        maquina.setCarga(0.0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 50.0 (limite exato) -> contribuicao 0")
    void carga_50_limiteExato_contribuicaoZero() {
        maquina.setCarga(50.0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 50.1 (acima de 50) -> contribuicao 1")
    void carga_acimaDE50_contribuicao1() {
        maquina.setCarga(50.1);
        Predicao p = calcular(maquina);
        assertEquals(1.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 70.0 (limite exato do bloco 2) -> contribuicao 1")
    void carga_70_limiteBloco2_contribuicao1() {
        maquina.setCarga(70.0);
        Predicao p = calcular(maquina);
        assertEquals(1.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 70.1 (acima de 70) -> contribuicao 5")
    void carga_acimaDE70_contribuicao5() {
        maquina.setCarga(70.1);
        Predicao p = calcular(maquina);
        assertEquals(5.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 85.0 (limite exato do bloco 3) -> contribuicao 5")
    void carga_85_limiteBloco3_contribuicao5() {
        maquina.setCarga(85.0);
        Predicao p = calcular(maquina);
        assertEquals(5.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 85.1 (acima de 85) -> contribuicao 10")
    void carga_acimaDE85_contribuicao10() {
        maquina.setCarga(85.1);
        Predicao p = calcular(maquina);
        assertEquals(10.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 95.0 (limite exato do bloco 4) -> contribuicao 10")
    void carga_95_limiteBloco4_contribuicao10() {
        maquina.setCarga(95.0);
        Predicao p = calcular(maquina);
        assertEquals(10.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 95.1 (acima de 95) -> contribuicao 15")
    void carga_acimaDE95_contribuicao15() {
        // Nota: Maquina.setCarga clampeia a 100. 95.1 ainda esta dentro do limite.
        maquina.setCarga(95.1);
        Predicao p = calcular(maquina);
        assertEquals(15.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("Carga: 100.0 (maximo) -> contribuicao 15")
    void carga_100_contribuicao15() {
        maquina.setCarga(100.0);
        Predicao p = calcular(maquina);
        assertEquals(15.0, p.getRisco(), 0.001);
    }

    // =========================================================================
    // Testes de boundary — Horas de Uso
    // Limiares: >5000 => 10, >2000 => 7, >1000 => 4, >500 => 2, else => 0
    // =========================================================================

    @Test
    @DisplayName("HorasUso: 0 -> contribuicao 0")
    void horasUso_0_contribuicaoZero() {
        maquina.setHorasUso(0);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 500 (limite exato) -> contribuicao 0")
    void horasUso_500_limiteExato_contribuicaoZero() {
        maquina.setHorasUso(500);
        Predicao p = calcular(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 501 (acima de 500) -> contribuicao 2")
    void horasUso_acimaDE500_contribuicao2() {
        maquina.setHorasUso(501);
        Predicao p = calcular(maquina);
        assertEquals(2.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 1000 (limite exato do bloco 2) -> contribuicao 2")
    void horasUso_1000_limiteBloco2_contribuicao2() {
        maquina.setHorasUso(1000);
        Predicao p = calcular(maquina);
        assertEquals(2.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 1001 (acima de 1000) -> contribuicao 4")
    void horasUso_acimaDE1000_contribuicao4() {
        maquina.setHorasUso(1001);
        Predicao p = calcular(maquina);
        assertEquals(4.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 2000 (limite exato do bloco 3) -> contribuicao 4")
    void horasUso_2000_limiteBloco3_contribuicao4() {
        maquina.setHorasUso(2000);
        Predicao p = calcular(maquina);
        assertEquals(4.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 2001 (acima de 2000) -> contribuicao 7")
    void horasUso_acimaDE2000_contribuicao7() {
        maquina.setHorasUso(2001);
        Predicao p = calcular(maquina);
        assertEquals(7.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 5000 (limite exato do bloco 4) -> contribuicao 7")
    void horasUso_5000_limiteBloco4_contribuicao7() {
        maquina.setHorasUso(5000);
        Predicao p = calcular(maquina);
        assertEquals(7.0, p.getRisco(), 0.001);
    }

    @Test
    @DisplayName("HorasUso: 5001 (acima de 5000) -> contribuicao 10")
    void horasUso_acimaDE5000_contribuicao10() {
        maquina.setHorasUso(5001);
        Predicao p = calcular(maquina);
        assertEquals(10.0, p.getRisco(), 0.001);
    }

    // =========================================================================
    // Testes de status (definirStatus)
    // Limiares: >=70 => CRITICO, >=40 => MODERADO, >=20 => ATENCAO, else => NORMAL
    // =========================================================================

    @Test
    @DisplayName("Status NORMAL quando risco < 20")
    void status_normal_riscoMenorQue20() {
        // temp=35.1 => 2pts; todos os outros zero => total 2 < 20
        maquina.setTemperatura(35.1);
        Predicao p = calcular(maquina);
        assertEquals("NORMAL", p.getStatus());
    }

    @Test
    @DisplayName("Status ATENCAO quando risco >= 20 e < 40")
    void status_atencao_risco20a39() {
        // temp=60.1 => 18pts + vibracao=2.1 => 5pts = 23pts => ATENCAO
        maquina.setTemperatura(60.1);
        maquina.setVibracao(2.1);
        Predicao p = calcular(maquina);
        assertEquals(23.0, p.getRisco(), 0.001);
        assertEquals("ATENCAO", p.getStatus());
    }

    @Test
    @DisplayName("Status MODERADO quando risco >= 40 e < 70")
    void status_moderado_risco40a69() {
        // temp=75.1 => 30pts + vibracao=10.1 => 25pts = 55pts => MODERADO
        maquina.setTemperatura(75.1);
        maquina.setVibracao(10.1);
        Predicao p = calcular(maquina);
        assertEquals(55.0, p.getRisco(), 0.001);
        assertEquals("MODERADO", p.getStatus());
    }

    @Test
    @DisplayName("Status CRITICO quando risco >= 70")
    void status_critico_riscoMaiorOuIgualA70() {
        // temp=90.1 => 40pts + vibracao=15.1 => 35pts = 75pts => CRITICO
        maquina.setTemperatura(90.1);
        maquina.setVibracao(15.1);
        Predicao p = calcular(maquina);
        assertEquals(75.0, p.getRisco(), 0.001);
        assertEquals("CRITICO", p.getStatus());
    }

    // =========================================================================
    // Risco total limitado a 100
    // =========================================================================

    @Test
    @DisplayName("Risco total nunca ultrapassa 100.0 mesmo com todos os fatores no maximo")
    void riscoTotal_naoUltrapassa100() {
        // temp=90.1 => 40, vib=15.1 => 35, carga=95.1 => 15, horas=5001 => 10 = 100
        maquina.setTemperatura(90.1);
        maquina.setVibracao(15.1);
        maquina.setCarga(95.1);
        maquina.setHorasUso(5001);
        Predicao p = calcular(maquina);
        assertEquals(100.0, p.getRisco(), 0.001);
        assertEquals("CRITICO", p.getStatus());
    }

    @Test
    @DisplayName("Soma acima de 100 e clampada para 100.0")
    void riscoTotal_somaAcimaDe100_clampada() {
        // Qualquer combinacao que some > 100 deve resultar em 100
        maquina.setTemperatura(200.0); // 40pts
        maquina.setVibracao(100.0);   // 35pts
        maquina.setCarga(100.0);      // 15pts
        maquina.setHorasUso(10000);   // 10pts => total 100 (ja no limite)
        Predicao p = calcular(maquina);
        assertTrue(p.getRisco() <= 100.0, "Risco nao deve ultrapassar 100.0");
    }

    // =========================================================================
    // gerarRelatorio
    // =========================================================================

    @Test
    @DisplayName("gerarRelatorio apos calcularRisco retorna conteudo completo")
    void gerarRelatorio_aposCalcularRisco_conteudoCompleto() {
        maquina.setTemperatura(90.1);
        maquina.setVibracao(15.1);
        Predicao p = calcular(maquina);
        String relatorio = p.gerarRelatorio();
        assertNotNull(relatorio);
        assertTrue(relatorio.contains("Maquina Teste"), "Deve conter nome da maquina");
        assertTrue(relatorio.contains("CRITICO"), "Deve conter o status");
        assertTrue(relatorio.contains("RISCO TOTAL"), "Deve conter a barra de risco total");
    }

    // BUG-10 CORRIGIDO: gerarRelatorio() lanca IllegalStateException antes de calcularRisco()
    @Test
    @DisplayName("BUG-10 corrigido: gerarRelatorio antes de calcularRisco lanca IllegalStateException")
    void gerarRelatorio_antesDeCalcularRisco_lancaIllegalState() {
        Predicao p = new Predicao(maquina);
        assertNull(p.getStatus(), "status deve ser null antes de calcularRisco()");
        assertEquals(0.0, p.getRisco(), 0.001, "risco deve ser 0.0 antes de calcularRisco()");

        assertThrows(IllegalStateException.class, p::gerarRelatorio,
            "BUG-10 corrigido: gerarRelatorio() deve lancar IllegalStateException " +
            "se calcularRisco() nao foi chamado previamente");

        // Apos calcularRisco(), gerarRelatorio() deve funcionar normalmente
        p.calcularRisco(maquina);
        assertDoesNotThrow(p::gerarRelatorio,
            "Apos calcularRisco(), gerarRelatorio() deve executar sem excecao");
    }

    // =========================================================================
    // Construtores
    // =========================================================================

    @Test
    @DisplayName("Construtor Predicao(Maquina) cria instancia com risco 0 e status null")
    void construtor_maquina_valoresDefault() {
        Predicao p = new Predicao(maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
        assertNull(p.getStatus());
    }

    @Test
    @DisplayName("Construtor Predicao(id, Maquina) inicializa corretamente")
    void construtor_idEMaquina_valoresDefault() {
        Predicao p = new Predicao(42, maquina);
        assertEquals(0.0, p.getRisco(), 0.001);
        assertNull(p.getStatus());
    }
}
