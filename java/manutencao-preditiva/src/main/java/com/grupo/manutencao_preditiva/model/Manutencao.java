package com.grupo.manutencao_preditiva.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Manutencao {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double custo;
    private String descricao;
    private String tipo;
    private String status;
    private Maquina maquina;

    public Manutencao() {}

    public Manutencao(int id, String tipo, Maquina maquina) {
        this.id = id;
        this.tipo = tipo;
        this.maquina = maquina;
        this.dataInicio = LocalDate.now();
        this.status = "EM_ANDAMENTO";
    }

    public void finalizar(double custo, String descricao) {
        this.dataFim = LocalDate.now();
        this.custo = custo;
        this.descricao = descricao;
        this.status = "CONCLUIDA";
    }

    public void cancelar() {
        this.dataFim = LocalDate.now();
        this.status = "CANCELADA";
    }

    public String resumo() {
        return String.format("  [#%d] %-12s | Inicio: %s | Status: %-12s | Custo: R$ %.2f",
            id, tipo, dataInicio.format(FMT), status, custo);
    }

    public String exibirCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Tipo       : %s%n", tipo));
        sb.append(String.format("  Status     : %s%n", status));
        sb.append(String.format("  Inicio     : %s%n", dataInicio.format(FMT)));
        if (dataFim != null)
            sb.append(String.format("  Fim        : %s%n", dataFim.format(FMT)));
        if (descricao != null)
            sb.append(String.format("  Descricao  : %s%n", descricao));
        sb.append(String.format("  Custo      : R$ %.2f%n", custo));
        return sb.toString();
    }

    public String getStatus() { return status; }
    public String getTipo()   { return tipo; }
    public double getCusto()  { return custo; }
    public int    getId()     { return id; }
}
