package com.grupo.manutencao_preditiva.model;

public class Maquina {

    private int id;
    private String nome;
    private String fabricante;
    private double temperatura;
    private double vibracao;
    private double cargaOperacional;
    private int horasUso;

    // Construtor vazio
    public Maquina() {
    }

    // Construtor simples
    public Maquina(int id, String nome, String fabricante) {
        this.id = id;
        this.nome = nome;
        this.fabricante = fabricante;
    }

    // Construtor completo
    public Maquina(int id, String nome, String fabricante,
                   double temperatura, double vibracao,
                   double cargaOperacional, int horasUso) {
        this.id = id;
        this.nome = nome;
        this.fabricante = fabricante;
        this.temperatura = temperatura;
        this.vibracao = vibracao;
        this.cargaOperacional = cargaOperacional;
        this.horasUso = horasUso;
    }

    // Método normal
    public void atualizarLeituras(double temperatura, double vibracao) {
        this.temperatura = temperatura;
        this.vibracao = vibracao;
    }

    // Sobrecarga
    public void atualizarLeituras(double temperatura, double vibracao,
                                  double cargaOperacional, int horasUso) {
        this.temperatura = temperatura;
        this.vibracao = vibracao;
        this.cargaOperacional = cargaOperacional;
        this.horasUso = horasUso;
    }

    public String exibirDados() {
        return "Máquina: " + nome +
               "\nFabricante: " + fabricante +
               "\nTemperatura: " + temperatura +
               "\nVibração: " + vibracao +
               "\nCarga: " + cargaOperacional +
               "\nHoras de Uso: " + horasUso;
    }

    // Getters
    public double getTemperatura() { return temperatura; }
    public double getVibracao() { return vibracao; }
    public double getCargaOperacional() { return cargaOperacional; }
    public int getHorasUso() { return horasUso; }
    public String getNome() { return nome; }
}
