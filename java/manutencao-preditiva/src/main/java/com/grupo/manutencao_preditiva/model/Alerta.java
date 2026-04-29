package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;

public class Alerta {

    private int id;
    private String tipo;
    private String mensagem;
    private String nivel;
    private LocalDateTime data;
    private Maquina maquina;

    public Alerta() {}

    public Alerta(int id, String tipo, String mensagem) {
        this.id = id;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.data = LocalDateTime.now();
    }

    public void gerarAlerta(Maquina maquina, double risco) {
        this.maquina = maquina;

        if (risco > 70) nivel = "CRÍTICO";
        else if (risco > 40) nivel = "ATENÇÃO";
        else nivel = "NORMAL";
    }

    public String exibir() {
        return mensagem + " | Nível: " + nivel;
    }
}