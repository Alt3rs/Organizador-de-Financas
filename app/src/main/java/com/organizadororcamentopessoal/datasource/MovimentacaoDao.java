package com.organizadororcamentopessoal.datasource;


import com.organizadororcamentopessoal.entities.Movimentacao;

import java.util.Date;
import java.util.List;

public interface MovimentacaoDao {
    boolean criarMovimentacao(long idUsuario, double valor, String descricao, Date dataMovimentacao);
    boolean criarMovimentacao(String username, double valor, String descricao, Date dataMovimentacao);
    List<Movimentacao> obterMovimentacaoNoIntervalo(String username, Date inicio, Date fim);
    boolean atualizarMovimentacao(long idMovimentacao, double valor, String descricao, Date dataMovimentacao);
    boolean excluirMovimentacao(long idMovimentacao);

    double totalRecebimentoNoIntervalo(String username, Date inicio, Date fim);
    double totalGastoNoIntervalo(String username, Date inicio, Date fim);
    double totalBalancoNoIntervalo(String username, Date inicio, Date fim);
}