package com.organizadororcamentopessoal.entities;

import java.util.Date;

public class Movimentacao {
    private long idMovimentacao;
    private long idUsuario;
    private double valor;
    private String descricao;
    private Date dataMovimentacao;
    private Date dataCriacao;

    public Movimentacao(long idMovimentacao, long idUsuario, double valor, String descricao, Date dataMovimentacao) {
        this.idMovimentacao = idMovimentacao;
        this.idUsuario = idUsuario;
        this.valor = valor;
        this.descricao = descricao;
        this.dataMovimentacao = dataMovimentacao;
    }

    public Movimentacao(long idMovimentacao, long idUsuario, double valor, String descricao) {
        this.idMovimentacao = idMovimentacao;
        this.idUsuario = idUsuario;
        this.valor = valor;
        this.descricao = descricao;
    }

    public long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public Date getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(Date dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
