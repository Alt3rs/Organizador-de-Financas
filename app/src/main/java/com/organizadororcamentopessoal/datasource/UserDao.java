package com.organizadororcamentopessoal.datasource;

public interface UserDao {
    public boolean createUser(String email, String senha, String nome);
    public boolean checkEmail(String email);
    public boolean checkUsernameSenha(String email, String senha);
}