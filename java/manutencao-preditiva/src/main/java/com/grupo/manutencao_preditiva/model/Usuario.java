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
        return nomeCompleto + " | Email: " + email;
    }
}