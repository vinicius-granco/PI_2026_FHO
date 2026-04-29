package com.grupo.manutencao_preditiva;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.grupo.manutencao_preditiva.model.*;

@SpringBootApplication
public class ManutencaoPreditivaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ManutencaoPreditivaApplication.class, args);
    }

    @Override
    public void run(String... args) {

        System.out.println("\n===== INICIANDO SISTEMA DE MANUTENÇÃO PREDITIVA =====\n");

        // 🟦 EMPRESA
        Empresa empresa = new Empresa(1, "Tech Indústrias", "123456789");
        System.out.println("Empresa: " + empresa.exibirEmpresa());

        // 🟦 USUÁRIO
        Usuario usuario = new Usuario(1, "Carlos Silva", "carlos@email.com", empresa);
        System.out.println("Usuário logado: " + usuario.exibirUsuario());

        // 🟦 MÁQUINA
        Maquina maquina = new Maquina(1, "Prensa Hidráulica", "PH-200", empresa);
        System.out.println("\nMáquina cadastrada: " + maquina.getNome());

        // 🟦 SENSORES
        Sensor sensorTemp = new Sensor(1, "Temperatura", "°C");
        Sensor sensorVib = new Sensor(2, "Vibração", "mm/s");

        sensorTemp.registrarLeitura(85.0);
        sensorVib.registrarLeitura(12.5);

        System.out.println("\nLeituras dos sensores:");
        System.out.println(sensorTemp.exibir());
        System.out.println(sensorVib.exibir());

        // 🟦 ATUALIZA MÁQUINA
        maquina.atualizarLeituras(
                sensorTemp.getValor(),
                sensorVib.getValor(),
                78.0,
                1200
        );

        System.out.println("\nResumo da máquina:");
        System.out.println(maquina.resumo());

        // 🟦 DADOS COLETADOS
        DadosMaquina dado1 = new DadosMaquina(1, sensorTemp.getValor(), sensorTemp);
        DadosMaquina dado2 = new DadosMaquina(2, sensorVib.getValor(), sensorVib);

        System.out.println("\nDados coletados:");
        System.out.println(dado1.exibirDado());
        System.out.println(dado2.exibirDado());

        // 🟦 PREDIÇÃO
        Predicao predicao = new Predicao(1, maquina);
        double risco = predicao.calcularRisco(maquina);

        System.out.println("\nPredição:");
        System.out.println(predicao.exibir());

        // 🟦 ALERTA
        Alerta alerta = new Alerta(1, "Falha", "Possível falha detectada!");
        alerta.gerarAlerta(maquina, risco);

        System.out.println("\nAlerta:");
        System.out.println(alerta.exibir());

        // 🟦 MANUTENÇÃO
        Manutencao manutencao = new Manutencao(1, "Preventiva", maquina);
        manutencao.finalizar(1500.0, "Troca de componentes desgastados");

        System.out.println("\nManutenção:");
        System.out.println(manutencao.resumo());

        // 🟦 LOG
        LogAtividade log = new LogAtividade(1, "Máquina", "Manutenção realizada", usuario);

        System.out.println("\nLog do sistema:");
        System.out.println(log.exibirLog());

        System.out.println("\n===== SIMULAÇÃO FINALIZADA =====\n");
    }
}