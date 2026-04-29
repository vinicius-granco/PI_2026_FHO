package com.grupo.manutencao_preditiva.model;

import java.time.LocalDate;

public class Empresa {

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
        return nome + " | CNPJ: " + cnpj + " | Setor: " + setorIndustrial;
    }

    public String getNome() { return nome; }
}
