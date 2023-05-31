package com.organizadororcamentopessoal.datasource;


import com.organizadororcamentopessoal.entities.Movimentacao;

import java.util.Date;
import java.util.List;

public interface MovimentacaoDao {
    boolean criarMovimentacao(long idUsuario, double valor, String descricao, Date dataMovimentacao);
    boolean criarMovimentacao(String userName, double valor, String descricao, Date dataMovimentacao);
    List<Movimentacao> obterMovimentacaoNoIntervalo(String userName, Date inicio, Date fim);
    boolean atualizarMovimentacao(long idMovimentacao, double valor, String descricao, Date dataMovimentacao);
    boolean excluirMovimentacao(long idMovimentacao);
}