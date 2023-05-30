package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.entities.Movimentacao;
import com.organizadororcamentopessoal.datasource.DatabaseContract.MovimentacaoTable;
import com.organizadororcamentopessoal.datasource.DatabaseContract.UsuarioTable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimentacaoDaoLocal  implements MovimentacaoDao {
    private SQLiteOpenHelper dbHelper;

    public MovimentacaoDaoLocal(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean criarMovimentacao(long idUsuario, double valor, String descricao, Date dataMovimentacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovimentacaoTable.ID_USUARIO , idUsuario);
        contentValues.put(MovimentacaoTable.VALOR, valor);
        contentValues.put(MovimentacaoTable.DESCRICAO, descricao);
        contentValues.put(MovimentacaoTable.DATA_MOVIMENTACAO, dateToEpochSeconds(dataMovimentacao));
        long result = db.insert(MovimentacaoTable.TABLE_NAME, null, contentValues);
        return  result != -1;
    }

    public boolean criarMovimentacao(String userName, double valor, String descricao, Date dataMovimentacao) {
        final String command = "INSERT INTO " + MovimentacaoTable.TABLE_NAME + " (" +
                MovimentacaoTable.ID_USUARIO + "," +
                MovimentacaoTable.VALOR + "," +
                MovimentacaoTable.DESCRICAO + "," +
                MovimentacaoTable.DATA_MOVIMENTACAO +
                ") SELECT u."+ UsuarioTable.ID_USUARIO +", ?, ?, ? FROM "+ UsuarioTable.TABLE_NAME +
                " u WHERE u."+ UsuarioTable.USERNAME +" = ?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(command, new String[] {Double.toString(valor), descricao,
                Long.toString(dateToEpochSeconds(dataMovimentacao)), userName});
        return  cursor.getCount() >= 1;
    }

    @NotNull
    public List<Movimentacao> obterMovimentacaoNoIntervalo(String userName, Date inicio, Date fim) {
        final String command = "SELECT " + MovimentacaoTable.ID_MOVIMENTACAO + "," +
                MovimentacaoTable.ID_USUARIO + "," +
                MovimentacaoTable.VALOR + "," +
                MovimentacaoTable.DESCRICAO + "," +
                MovimentacaoTable.DATA_MOVIMENTACAO +
                " FROM " + MovimentacaoTable.TABLE_NAME + " m " +
                " JOIN " + UsuarioTable.TABLE_NAME + " u ON u." + UsuarioTable.ID_USUARIO +
                " = m." + MovimentacaoTable.ID_USUARIO + " WHERE " +
                "u." + UsuarioTable.USERNAME + " = ?" +
                " AND " + MovimentacaoTable.DATA_MOVIMENTACAO + " BETWEEN ? AND ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(command, new String[]{ userName,
                Long.toString(dateToEpochSeconds(inicio)), Long.toString(dateToEpochSeconds(fim))});

        List<Movimentacao> result = new ArrayList<>(cursor.getCount());
        while ( cursor.moveToNext() ) {
            Movimentacao movimentacao = new Movimentacao(
                    cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.ID_MOVIMENTACAO)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.ID_USUARIO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MovimentacaoTable.VALOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MovimentacaoTable.DESCRICAO)),
                    epochSecondsToDate(cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.DATA_MOVIMENTACAO)))
            );
            result.add(movimentacao);
        }
        return result;
    }


    public boolean atualizarMovimentacao(long idMovimentacao, double valor, String descricao, Date dataMovimentacao) {
        ContentValues values = new ContentValues();
        values.put(MovimentacaoTable.VALOR, valor);
        values.put(MovimentacaoTable.DESCRICAO, descricao);
        values.put(MovimentacaoTable.DATA_MOVIMENTACAO, dateToEpochSeconds(dataMovimentacao));

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long updatedRows = db.update(MovimentacaoTable.TABLE_NAME,
                values,
                MovimentacaoTable.ID_MOVIMENTACAO + " = ?",
                new String[]{ Long.toString(idMovimentacao)});
        return updatedRows != 0;
    }

    public boolean excluirMovimentacao(long idMovimentacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long deletedRows = db.delete(
                MovimentacaoTable.TABLE_NAME,
                MovimentacaoTable.ID_MOVIMENTACAO + " = ?",
                new String[]{Long.toString(idMovimentacao)}
        );
        return deletedRows != 0;
    }

    protected static long dateToEpochSeconds(Date data) {
        return data.getTime() / 1000;
    }

    protected static Date epochSecondsToDate(long epoch) {
        return new Date(epoch * 1000);
    }
}
