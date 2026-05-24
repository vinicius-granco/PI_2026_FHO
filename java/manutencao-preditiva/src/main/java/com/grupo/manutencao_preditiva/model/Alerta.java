package com.grupo.manutencao_preditiva.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Alerta {

    private static int contadorId = 1;
    private static final List<Alerta> historico = new ArrayList<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

    public static Alerta gerar(Maquina maquina, double risco) {
        String nivel, tipo, mensagem;

        if (risco >= 70) {
            nivel    = "CRITICO";
            tipo     = "Falha Iminente";
            mensagem = "Risco critico detectado. Parada preventiva recomendada.";
        } else if (risco >= 40) {
            nivel    = "MODERADO";
            tipo     = "Atencao Necessaria";
            mensagem = "Parametros fora do intervalo ideal. Monitorar com atencao.";
        } else if (risco >= 20) {
            nivel    = "ATENCAO";
            tipo     = "Monitoramento";
            mensagem = "Leituras ligeiramente elevadas. Manter observacao.";
        } else {
            nivel    = "NORMAL";
            tipo     = "Status OK";
            mensagem = "Maquina operando dentro dos parametros normais.";
        }

        Alerta alerta = new Alerta(contadorId++, tipo, mensagem);
        alerta.nivel   = nivel;
        alerta.maquina = maquina;
        historico.add(alerta);
        return alerta;
    }

    public void gerarAlerta(Maquina maquina, double risco) {
        this.maquina = maquina;
        if      (risco >= 70) nivel = "CRITICO";
        else if (risco >= 40) nivel = "MODERADO";
        else if (risco >= 20) nivel = "ATENCAO";
        else                  nivel = "NORMAL";
    }

    public String exibir() {
        return String.format("[%s] %s: %s", nivel, tipo, mensagem);
    }

    public String exibirCompleto() {
        return String.format(
            "  Maquina  : %s%n  Tipo     : %s%n  Nivel    : %s%n  Mensagem : %s%n  Data     : %s",
            maquina != null ? maquina.getNome() : "N/A",
            tipo, nivel, mensagem,
            data != null ? data.format(FMT) : "N/A");
    }

    public static List<Alerta> getHistorico() { return historico; }
    public static int totalAlertas()          { return historico.size(); }

    public String getNivel()    { return nivel; }
    public String getMensagem() { return mensagem; }
    public String getTipo()     { return tipo; }
}
