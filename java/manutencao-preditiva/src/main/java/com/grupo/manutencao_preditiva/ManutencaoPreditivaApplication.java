package com.grupo.manutencao_preditiva;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.grupo.manutencao_preditiva.model.Maquina;
import com.grupo.manutencao_preditiva.model.Sensor;
import com.grupo.manutencao_preditiva.model.Predicao;

@SpringBootApplication
public class ManutencaoPreditivaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ManutencaoPreditivaApplication.class, args);
    }

    @Override
    public void run(String... args) {

        Maquina maquina = new Maquina(1, "Prensa Industrial", "Siemens");

        Sensor temp = new Sensor(1, "Temperatura");
        Sensor vib = new Sensor(2, "Vibração");

        temp.registrarLeitura(85);
        vib.registrarLeitura(12);

        maquina.atualizarLeituras(
                temp.getValor(),
                vib.getValor(),
                75,
                1200
        );

        Predicao predicao = new Predicao(maquina);
        predicao.calcularRisco(maquina);

        System.out.println("=== DADOS DA MÁQUINA ===");
        System.out.println(maquina.exibirDados());

        System.out.println("\n=== PREDIÇÃO ===");
        System.out.println(predicao.gerarRelatorio());
    }
}
