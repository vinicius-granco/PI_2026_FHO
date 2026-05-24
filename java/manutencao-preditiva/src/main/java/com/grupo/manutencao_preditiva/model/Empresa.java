package com.grupo.manutencao_preditiva.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Empresa {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private String nome;
    private String cnpj;
    private String setorIndustrial;
    private LocalDate dataCriacao;

    public Empresa() {}

    public Empresa(int id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.dataCriacao = LocalDate.now();
    }

    public Empresa(int id, String nome, String cnpj, String setorIndustrial, LocalDate dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.setorIndustrial = setorIndustrial;
        this.dataCriacao = dataCriacao;
    }

    public String exibirEmpresa() {
        return String.format("  Nome   : %s%n  CNPJ   : %s%n  Setor  : %s%n  Desde  : %s",
            nome,
            cnpj,
            setorIndustrial != null ? setorIndustrial : "Nao informado",
            dataCriacao != null ? dataCriacao.format(FMT) : "N/A");
    }

    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public int    getId()   { return id; }
}
