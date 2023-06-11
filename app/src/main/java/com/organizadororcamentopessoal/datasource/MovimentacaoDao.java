package com.organizadororcamentopessoal.datasource;


import com.organizadororcamentopessoal.entities.Movimentacao;

import java.util.Date;
import java.util.List;

public interface MovimentacaoDao {
    public static final int NONE = 0, ASC = 1, DESC = 2;
    long criarMovimentacao(long idUsuario, double valor, String descricao, Date dataMovimentacao);
    long criarMovimentacao(String username, double valor, String descricao, Date dataMovimentacao);
    Movimentacao obterMovimentacao(long idMovimentacao);

    /**
     *
     * @param username
     * @param inicio
     * @param fim
     * @param ordem NONE = 0, ASC = 1, DESC = 2
     * @return
     */
    List<Movimentacao> obterMovimentacaoNoIntervalo(String username, Date inicio, Date fim, int ordem);
    boolean atualizarMovimentacao(long idMovimentacao, double valor, String descricao, Date dataMovimentacao);
    boolean excluirMovimentacao(long idMovimentacao);

    double totalRecebimentoNoIntervalo(String username, Date inicio, Date fim);
    double totalGastoNoIntervalo(String username, Date inicio, Date fim);
    double totalBalancoNoIntervalo(String username, Date inicio, Date fim);
}