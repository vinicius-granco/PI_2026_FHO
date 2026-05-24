package com.grupo.manutencao_preditiva.model;

import java.util.ArrayList;
import java.util.List;

public class Sensor {

    private int id;
    private String tipo;
    private String unidade;
    private double valor;
    private double valorMaximo;
    private double valorMinimo;
    private Maquina maquina;
    private List<Double> historico = new ArrayList<>();

    public Sensor() {}

    public Sensor(int id, String tipo, String unidade) {
        this.id = id;
        this.tipo = tipo;
        this.unidade = unidade;
        this.valorMaximo = Double.MIN_VALUE;
        this.valorMinimo = Double.MAX_VALUE;
    }

    public void registrarLeitura(double valor) {
        this.valor = valor;
        this.historico.add(valor);
        if (valor > valorMaximo) valorMaximo = valor;
        if (valor < valorMinimo) valorMinimo = valor;
    }

    public void registrarLeitura(double valor, Maquina maquina) {
        this.maquina = maquina;
        registrarLeitura(valor);
    }

    public double getMedia() {
        return historico.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public String exibir() {
        return String.format("%s: %.2f %s", tipo, valor, unidade);
    }

    public String exibirEstatisticas() {
        if (historico.isEmpty())
            return String.format("  %s: sem leituras", tipo);
        return String.format("  %-14s | Atual: %6.2f %s | Media: %6.2f | Max: %6.2f | Min: %6.2f",
            tipo, valor, unidade, getMedia(), valorMaximo, valorMinimo);
    }

    public double getValor()         { return valor; }
    public String getTipo()          { return tipo; }
    public String getUnidade()       { return unidade; }
    public int    getId()            { return id; }
    public double getValorMaximo()   { return valorMaximo; }
    public double getValorMinimo()   { return valorMinimo; }
    public int    getTotalLeituras() { return historico.size(); }
}
