package com.organizadororcamentopessoal.datasource;


import com.organizadororcamentopessoal.entities.Movimentacao;

import java.util.Date;
import java.util.List;

public interface MovimentacaoDao {
    int NONE = 0, ASC = 1, DESC = 2;
    int ALL = 0, GASTO = 1, RECEBIMENTO = 2;
    int MINUTO = 0, HORA = 1, DIA = 2,  SEMANA= 3, MES = 4, ANO = 5;
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

    List<Movimentacao> obterMovimentacaoNoIntervaloAgrupado(String username, Date inicio, Date fim, int tipo, int grupo);
}