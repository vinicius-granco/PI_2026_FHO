package com.grupo.manutencao_preditiva.model;

import java.util.ArrayList;
import java.util.List;

public class Maquina {

    private int id;
    private String nome;
    private String fabricante;
    private String tipo;
    private Empresa empresa;

    private double temperatura;
    private double vibracao;
    private double carga;
    private int horasUso;

    private List<Sensor> sensores = new ArrayList<>();
    private List<String> historicoLeituras = new ArrayList<>();

    public Maquina() {}

    public Maquina(int id, String nome, String fabricante) {
        this.id = id;
        this.nome = nome;
        this.fabricante = fabricante;
    }

    public Maquina(int id, String nome, String fabricante, Empresa empresa) {
        this(id, nome, fabricante);
        this.empresa = empresa;
    }

    public void atualizarLeituras(double temperatura, double vibracao) {
        setTemperatura(temperatura);
        setVibracao(vibracao);
        registrarHistorico();
    }

    public void atualizarLeituras(double temperatura, double vibracao, double carga, int horasUso) {
        setTemperatura(temperatura);
        setVibracao(vibracao);
        setCarga(carga);
        setHorasUso(horasUso);
        registrarHistorico();
    }

    private void registrarHistorico() {
        String entrada = String.format(
            "Temp: %.1fC | Vib: %.2f mm/s | Carga: %.1f%% | Horas: %dh",
            temperatura, vibracao, carga, horasUso
        );
        historicoLeituras.add(entrada);
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public String resumo() {
        return String.format("%-20s | Temp: %5.1fC | Vib: %5.2f mm/s | Horas: %dh",
            nome, temperatura, vibracao, horasUso);
    }

    public String exibirDados() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Nome         : %s%n", nome));
        sb.append(String.format("  Fabricante   : %s%n", fabricante));
        sb.append(String.format("  Tipo         : %s%n", tipo != null ? tipo : "Nao informado"));
        sb.append(String.format("  Temperatura  : %.1f C%n", temperatura));
        sb.append(String.format("  Vibracao     : %.2f mm/s%n", vibracao));
        sb.append(String.format("  Carga        : %.1f%%%n", carga));
        sb.append(String.format("  Horas de Uso : %d h%n", horasUso));
        if (empresa != null)
            sb.append(String.format("  Empresa      : %s%n", empresa.getNome()));
        return sb.toString();
    }

    public String exibirHistorico() {
        if (historicoLeituras.isEmpty()) return "  Nenhuma leitura registrada ainda.";
        StringBuilder sb = new StringBuilder();
        int inicio = Math.max(0, historicoLeituras.size() - 5);
        for (int i = inicio; i < historicoLeituras.size(); i++) {
            sb.append(String.format("  [%d] %s%n", i + 1, historicoLeituras.get(i)));
        }
        return sb.toString();
    }

    public int getTotalLeituras() { return historicoLeituras.size(); }
    public List<Sensor> getSensores() { return sensores; }

    public void setTemperatura(double temperatura) { this.temperatura = Math.max(0, temperatura); }
    public void setVibracao(double vibracao)       { this.vibracao = Math.max(0, vibracao); }
    public void setCarga(double carga)             { this.carga = Math.min(100, Math.max(0, carga)); }
    public void setHorasUso(int horasUso)          { this.horasUso = Math.max(0, horasUso); }
    public void setTipo(String tipo)               { this.tipo = tipo; }

    public double getTemperatura() { return temperatura; }
    public double getVibracao()    { return vibracao; }
    public double getCarga()       { return carga; }
    public int    getHorasUso()    { return horasUso; }
    public String getNome()        { return nome; }
    public String getFabricante()  { return fabricante; }
    public int    getId()          { return id; }
    public String getTipo()        { return tipo; }
}
