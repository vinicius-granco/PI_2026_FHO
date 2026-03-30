package com.grupo.manutencao_preditiva.model;

public class Predicao {

    private Maquina maquina;
    private double risco;
    private String nivel;

    public Predicao() {}

    public Predicao(Maquina maquina) {
        this.maquina = maquina;
    }

    public double calcularRisco(double temperatura, double vibracao,
                                double carga, int horas) {

        risco = (temperatura * 0.3)
              + (vibracao * 0.3)
              + (carga * 0.2)
              + (horas * 0.01);

        definirNivel();
        return risco;
    }

    // Sobrecarga
    public double calcularRisco(Maquina maquina) {
        return calcularRisco(
                maquina.getTemperatura(),
                maquina.getVibracao(),
                maquina.getCargaOperacional(),
                maquina.getHorasUso()
        );
    }

    private void definirNivel() {
        if (risco >= 70) {
            nivel = "ALTO";
        } else if (risco >= 40) {
            nivel = "MODERADO";
        } else {
            nivel = "BAIXO";
        }
    }

    public String gerarRelatorio() {
        return "Máquina: " + maquina.getNome() +
               "\nRisco: " + risco +
               "\nNível: " + nivel;
    }
}