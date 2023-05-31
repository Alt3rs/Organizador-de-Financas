package com.organizadororcamentopessoal.entities;

public class Limite {
    private long idLimite;
    private long idUsuario;
    private double valor;

    public Limite(long idLimite, long idUsuario, double valor) {
        this.idLimite = idLimite;
        this.idUsuario = idUsuario;
        this.valor = valor;
    }


    public long getIdLimite() {
        return idLimite;
    }

    public void setIdLimite(long idLimite) {
        this.idLimite = idLimite;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }


}
