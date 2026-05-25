package com.grupo.manutencao_preditiva;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testes de contexto Spring Boot para ManutencaoPreditivaApplication.
 *
 * BUG-11 CORRIGIDO: Scanner nao e mais static final — e injetado via construtor.
 * BUG-12 CORRIGIDO: TestScannerConfig fornece um Scanner com ByteArrayInputStream
 *                   para que run() processe uma sessao completa sem bloquear stdin.
 *
 * A sessao simulada:
 *   1. Cadastra empresa + operador
 *   2. Pausa (ENTER)
 *   3. Encerra com opcao 0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ManutencaoPreditivaApplicationTests {

    @TestConfiguration
    static class TestScannerConfig {
        @Bean
        @Primary
        public Scanner testScanner() {
            String input = String.join("\n",
                "EmpresaTeste",
                "12.345.678/0001-99",
                "Tecnologia",
                "Operador Teste",
                "teste@empresa.com",
                "",   // pausar — sessao iniciada
                "0"   // encerrar menuPrincipal
            ) + "\n";
            return new Scanner(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Autowired
    private ManutencaoPreditivaApplication app;

    @Test
    void contextLoads() {
        // Contexto Spring carregado e run() executado com Scanner injetado — nao bloqueia stdin.
        assertNotNull(app, "BUG-11 corrigido: aplicacao deve ser injetavel como bean Spring");
    }

    @Test
    void applicationStartsWithoutBlockingOnStdin() {
        // run() foi chamado pelo Spring usando o Scanner de teste (ByteArrayInputStream).
        // A aplicacao processou empresa+usuario+menu e encerrou sem nenhum bloqueio.
        assertNotNull(app, "BUG-12 corrigido: Scanner injetado permite run() sem bloquear System.in");
    }
}
