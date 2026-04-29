package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;

public class LogAtividade {

    private int id;
    private String entidade;
    private String acao;
    private LocalDateTime data;
    private Usuario usuario;

    public LogAtividade() {}

    public LogAtividade(int id, String entidade, String acao, Usuario usuario) {
        this.id = id;
        this.entidade = entidade;
        this.acao = acao;
        this.usuario = usuario;
        this.data = LocalDateTime.now();
    }

    public String exibirLog() {
        return entidade + " | " + acao + " | " + data;
    }
}