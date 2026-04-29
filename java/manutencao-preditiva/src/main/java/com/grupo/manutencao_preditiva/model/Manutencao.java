package com.grupo.manutencao_preditiva.model;

import java.time.LocalDate;

public class Manutencao {

    private int id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double custo;
    private String descricao;
    private String tipo;
    private Maquina maquina;

    public Manutencao() {}

    public Manutencao(int id, String tipo, Maquina maquina) {
        this.id = id;
        this.tipo = tipo;
        this.maquina = maquina;
        this.dataInicio = LocalDate.now();
    }

    public void finalizar(double custo, String descricao) {
        this.dataFim = LocalDate.now();
        this.custo = custo;
        this.descricao = descricao;
    }

    public String resumo() {
        return "Manutenção " + tipo + " | Custo: " + custo;
    }
}