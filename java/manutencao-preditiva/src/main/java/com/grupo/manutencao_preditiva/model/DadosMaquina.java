package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DadosMaquina {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

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
        String sensorInfo = sensor != null
            ? String.format("%s (%s)", sensor.getTipo(), sensor.getUnidade())
            : "Sensor";
        return String.format("  [%s] %s: %.2f", dataColeta.format(FMT), sensorInfo, valor);
    }

    public double getValor()             { return valor; }
    public LocalDateTime getDataColeta() { return dataColeta; }
    public Sensor getSensor()            { return sensor; }
}
