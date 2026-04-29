package com.grupo.manutencao_preditiva.model;

public class Predicao {

    private int id;
    private Maquina maquina;
    private double risco;
    private String status;

    public Predicao() {}

    public Predicao(int id, Maquina maquina) {
        this.id = id;
        this.maquina = maquina;
    }

    public double calcularRisco(Maquina m) {
        risco = (m.getTemperatura() * 0.3)
              + (m.getVibracao() * 0.3)
              + (m.getCarga() * 0.2)
              + (m.getHorasUso() * 0.01);

        definirStatus();
        return risco;
    }

    private void definirStatus() {
        if (risco > 70) status = "ALTO";
        else if (risco > 40) status = "MÉDIO";
        else status = "BAIXO";
    }

    public String exibir() {
        return "Risco: " + risco + " | Status: " + status;
    }
}