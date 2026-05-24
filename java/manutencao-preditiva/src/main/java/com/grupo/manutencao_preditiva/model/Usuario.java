package com.grupo.manutencao_preditiva.model;

public class Usuario {

    private int id;
    private String nomeCompleto;
    private String email;
    private String senha;
    private String cpf;
    private String cargo;
    private Empresa empresa;

    public Usuario() {}

    public Usuario(int id, String nomeCompleto, String email) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
    }

    public Usuario(int id, String nomeCompleto, String email, Empresa empresa) {
        this(id, nomeCompleto, email);
        this.empresa = empresa;
    }

    public String exibirUsuario() {
        return String.format("  Nome    : %s%n  Email   : %s%n  Cargo   : %s%n  Empresa : %s",
            nomeCompleto, email,
            cargo   != null ? cargo   : "Nao informado",
            empresa != null ? empresa.getNome() : "Nao vinculado");
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail()        { return email; }
    public String getCargo()        { return cargo; }
    public void   setCargo(String cargo) { this.cargo = cargo; }
    public int    getId()           { return id; }
}
