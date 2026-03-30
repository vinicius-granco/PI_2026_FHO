package com.grupo.manutencao_preditiva.model;

public class Sensor {

    private int id;
    private String tipo;
    private double valor;

    public Sensor() {}

    public Sensor(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public Sensor(int id, String tipo, double valor) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
    }

    public void registrarLeitura(double valor) {
        this.valor = valor;
    }

    public String exibirLeitura() {
        return "Sensor: " + tipo + " | Valor: " + valor;
    }

    public double getValor() {
        return valor;
    }
}