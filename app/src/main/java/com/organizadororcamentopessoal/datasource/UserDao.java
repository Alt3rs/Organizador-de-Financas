package com.organizadororcamentopessoal.datasource;

import com.organizadororcamentopessoal.entities.Usuario;

public interface UserDao {
    boolean createUser(String email, String senha, String nome);
    boolean isUsernameRegistered(String username);
    boolean checkUsernameSenha(String username, String senha);
    Usuario getUser(String userName);
    Usuario getUser(long idUsuario);
}