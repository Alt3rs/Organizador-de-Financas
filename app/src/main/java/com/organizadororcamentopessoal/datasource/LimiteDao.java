package com.organizadororcamentopessoal.datasource;

import com.organizadororcamentopessoal.entities.Limite;

import java.util.List;

public interface LimiteDao {
    boolean criarLimite(long idUsuario, double valor);
    Limite obterLimite(long idLimite);
    List<Limite> listarTodosLimitesDoUsuario(long idUsuario);
    List<Limite> listarTodosLimitesDoUsuario(String username);
    boolean atualizarLimite(String username, double valor);
    boolean excluirLimite(long idLimite);
    boolean habilitarLimite(String username, boolean estaHabilitado);
    boolean limiteEstaHabilitado(String username);
}
