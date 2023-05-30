package com.organizadororcamentopessoal.datasource;

import com.organizadororcamentopessoal.entities.Usuario;

public interface UserDao {
    boolean createUser(String email, String senha, String nome);
    boolean checkEmail(String email);
    boolean checkUsernameSenha(String email, String senha);
    Usuario getUser(String userName);
    Usuario getUser(long idUsuario);
}