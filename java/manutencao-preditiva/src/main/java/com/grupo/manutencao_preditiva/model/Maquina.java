package com.grupo.manutencao_preditiva.model;

public class Maquina {

    private int id;
    private String nome;
    private String modelo;
    private String tipo;
    private Empresa empresa;

    private double temperatura;
    private double vibracao;
    private double carga;
    private int horasUso;

    public Maquina() {}

    public Maquina(int id, String nome, String modelo) {
        this.id = id;
        this.nome = nome;
        this.modelo = modelo;
    }

    public Maquina(int id, String nome, String modelo, Empresa empresa) {
        this(id, nome, modelo);
        this.empresa = empresa;
    }

    public void atualizarLeituras(double temperatura, double vibracao) {
        this.temperatura = temperatura;
        this.vibracao = vibracao;
    }

    public void atualizarLeituras(double temperatura, double vibracao, double carga, int horasUso) {
        this.temperatura = temperatura;
        this.vibracao = vibracao;
        this.carga = carga;
        this.horasUso = horasUso;
    }

    public String resumo() {
        return nome + " | Temp: " + temperatura + " | Vib: " + vibracao;
    }

    public double getTemperatura() { return temperatura; }
    public double getVibracao() { return vibracao; }
    public double getCarga() { return carga; }
    public int getHorasUso() { return horasUso; }
    public String getNome() { return nome; }
}