package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;

public class DadosMaquina {

    private int id;
    private double valor;
    private LocalDateTime dataColeta;
    private Sensor sensor;

    public DadosMaquina() {}

    public DadosMaquina(int id, double valor, Sensor sensor) {
        this.id = id;
        this.valor = valor;
        this.sensor = sensor;
        this.dataColeta = LocalDateTime.now();
    }

    public String exibirDado() {
        return "Valor: " + valor + " em " + dataColeta;
    }
}