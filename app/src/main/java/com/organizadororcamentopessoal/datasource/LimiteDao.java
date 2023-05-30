package com.organizadororcamentopessoal.datasource;

import com.organizadororcamentopessoal.entities.Limite;

import java.util.List;

public interface LimiteDao {
    boolean criarLimite(long idUsuario, double valor);
    Limite obterLimite(long idLimite);
    List<Limite> listarTodosLimitesDoUsuario(long idUsuario);
    boolean excluirLimite(long idLimite);
}
