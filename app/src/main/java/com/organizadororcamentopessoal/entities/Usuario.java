package com.organizadororcamentopessoal.entities;

public class Usuario {
    private long idUsuario;
    private String userName;
    private String email;
    private String senha;


    public Usuario(long idUsuario, String userName, String email, String senha) {
        this.idUsuario = idUsuario;
        this.userName = userName;
        this.email = email;
        this.senha = senha;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
