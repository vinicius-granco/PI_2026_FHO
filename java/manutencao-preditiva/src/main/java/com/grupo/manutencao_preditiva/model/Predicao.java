package com.grupo.manutencao_preditiva.model;

public class Predicao {

    private int id;
    private Maquina maquina;
    private double risco;
    private String status;
    private double riscoTemp;
    private double riscoVib;
    private double riscoCarga;
    private double riscoHoras;

    public Predicao() {}

    public Predicao(Maquina maquina) {
        this.maquina = maquina;
    }

    public Predicao(int id, Maquina maquina) {
        this.id = id;
        this.maquina = maquina;
    }

    public double calcularRisco(Maquina m) {
        riscoTemp  = calcularRiscoTemperatura(m.getTemperatura());
        riscoVib   = calcularRiscoVibracao(m.getVibracao());
        riscoCarga = calcularRiscoCarga(m.getCarga());
        riscoHoras = calcularRiscoHoras(m.getHorasUso());

        risco = Math.min(riscoTemp + riscoVib + riscoCarga + riscoHoras, 100.0);
        definirStatus();
        return risco;
    }

    private double calcularRiscoTemperatura(double temp) {
        if (temp > 90) return 40;
        if (temp > 75) return 30;
        if (temp > 60) return 18;
        if (temp > 45) return 8;
        if (temp > 35) return 2;
        return 0;
    }

    private double calcularRiscoVibracao(double vib) {
        if (vib > 15) return 35;
        if (vib > 10) return 25;
        if (vib > 5)  return 14;
        if (vib > 2)  return 5;
        return 0;
    }

    private double calcularRiscoCarga(double carga) {
        if (carga > 95) return 15;
        if (carga > 85) return 10;
        if (carga > 70) return 5;
        if (carga > 50) return 1;
        return 0;
    }

    private double calcularRiscoHoras(int horas) {
        if (horas > 5000) return 10;
        if (horas > 2000) return 7;
        if (horas > 1000) return 4;
        if (horas > 500)  return 2;
        return 0;
    }

    private void definirStatus() {
        if      (risco >= 70) status = "CRITICO";
        else if (risco >= 40) status = "MODERADO";
        else if (risco >= 20) status = "ATENCAO";
        else                  status = "NORMAL";
    }

    public String gerarRelatorio() {
        if (status == null)
            throw new IllegalStateException("calcularRisco() deve ser chamado antes de gerarRelatorio()");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Maquina      : %s%n", maquina.getNome()));
        sb.append(String.format("  Status       : %s%n", status));
        sb.append(String.format("%n  Contribuicao por fator:%n"));
        sb.append(String.format("  %-14s %s (%.0f pts)%n", "Temperatura:",  barraFator(riscoTemp,  40), riscoTemp));
        sb.append(String.format("  %-14s %s (%.0f pts)%n", "Vibracao:",     barraFator(riscoVib,   35), riscoVib));
        sb.append(String.format("  %-14s %s (%.0f pts)%n", "Carga Op.:",    barraFator(riscoCarga, 15), riscoCarga));
        sb.append(String.format("  %-14s %s (%.0f pts)%n", "Horas de Uso:", barraFator(riscoHoras, 10), riscoHoras));
        sb.append(String.format("%n"));
        sb.append(barraRisco());
        return sb.toString();
    }

    private String barraFator(double valor, double max) {
        int cheio = (int) Math.round((valor / max) * 10);
        cheio = Math.min(cheio, 10);
        return "[" + "#".repeat(cheio) + ".".repeat(10 - cheio) + "]";
    }

    private String barraRisco() {
        int cheio = (int) Math.round(risco / 5);
        cheio = Math.min(cheio, 20);
        String barra = "#".repeat(cheio) + ".".repeat(20 - cheio);
        return String.format("  RISCO TOTAL  [%s] %.1f%%", barra, risco);
    }

    public double getRisco()  { return risco; }
    public String getStatus() { return status; }
}
