package com.grupo.manutencao_preditiva.model;

public class Sensor {

    private int id;
    private String tipo;
    private String unidade;
    private double valor;
    private Maquina maquina;

    public Sensor() {}

    public Sensor(int id, String tipo, String unidade) {
        this.id = id;
        this.tipo = tipo;
        this.unidade = unidade;
    }

    public void registrarLeitura(double valor) {
        this.valor = valor;
    }

    public void registrarLeitura(double valor, Maquina maquina) {
        this.valor = valor;
        this.maquina = maquina;
    }

    public double getValor() { return valor; }

    public String exibir() {
        return tipo + ": " + valor + " " + unidade;
    }
}