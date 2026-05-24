package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAtividade {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

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

    public LogAtividade(int id, String entidade, String acao) {
        this(id, entidade, acao, null);
    }

    public String exibirLog() {
        String user = (usuario != null) ? usuario.getNomeCompleto() : "Sistema";
        return String.format("  [%s] %-12s | %-35s | %s",
            data.format(FMT), entidade, acao, user);
    }

    public String getEntidade() { return entidade; }
    public String getAcao()     { return acao; }
}
