package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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

    public long criarMovimentacao(long idUsuario, double valor, String descricao, Date dataMovimentacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovimentacaoTable.ID_USUARIO , idUsuario);
        contentValues.put(MovimentacaoTable.VALOR, valor);
        contentValues.put(MovimentacaoTable.DESCRICAO, descricao);
        contentValues.put(MovimentacaoTable.DATA_MOVIMENTACAO, dateToEpochSeconds(dataMovimentacao));
        try {
            long result = db.insert(MovimentacaoTable.TABLE_NAME, null, contentValues);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long criarMovimentacao(String username, double valor, String descricao, Date dataMovimentacao) {
        final String sql = "INSERT INTO " + MovimentacaoTable.TABLE_NAME + " (" +
                MovimentacaoTable.ID_USUARIO + "," +
                MovimentacaoTable.VALOR + "," +
                MovimentacaoTable.DESCRICAO + "," +
                MovimentacaoTable.DATA_MOVIMENTACAO +
                ") SELECT u."+ UsuarioTable.ID_USUARIO +", ?, ?, ? FROM "+ UsuarioTable.TABLE_NAME +
                " u WHERE u."+ UsuarioTable.USERNAME +" = ? LIMIT 1";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindDouble(1, valor); // indice comeca no 1
            statement.bindString(2, descricao);
            statement.bindLong(3, dateToEpochSeconds(dataMovimentacao));
            statement.bindString(4, username);
            return statement.executeInsert();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Tecnicamente, deveria haver autenticação para esse método
    public Movimentacao obterMovimentacao(long idMovimentacao) {
        final String command = "SELECT " +
                MovimentacaoTable.ID_MOVIMENTACAO + "," +
                MovimentacaoTable.ID_USUARIO + "," +
                MovimentacaoTable.VALOR + "," +
                MovimentacaoTable.DESCRICAO + "," +
                MovimentacaoTable.DATA_MOVIMENTACAO +
                MovimentacaoTable.TABLE_NAME +
                " WHERE " + MovimentacaoTable.ID_MOVIMENTACAO + " = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idMovimentacao)})) {
            if(cursor.moveToFirst()) {
                Movimentacao movimentacao = new Movimentacao(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.ID_MOVIMENTACAO)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.ID_USUARIO)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(MovimentacaoTable.VALOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovimentacaoTable.DESCRICAO)),
                        epochSecondsToDate(cursor.getLong(cursor.getColumnIndexOrThrow(MovimentacaoTable.DATA_MOVIMENTACAO)))
                );
                return movimentacao;
            }
            return null;
        }
    }

    @NotNull
    public List<Movimentacao> obterMovimentacaoNoIntervalo(String username, Date inicio, Date fim, int ordem) {
        String command = "SELECT " +
                "m." + MovimentacaoTable.ID_MOVIMENTACAO + "," +
                "m." + MovimentacaoTable.ID_USUARIO + "," +
                "m." + MovimentacaoTable.VALOR + "," +
                "m." + MovimentacaoTable.DESCRICAO + "," +
                "m." + MovimentacaoTable.DATA_MOVIMENTACAO +
                " FROM " + MovimentacaoTable.TABLE_NAME + " m " +
                " JOIN " + UsuarioTable.TABLE_NAME + " u ON u." + UsuarioTable.ID_USUARIO +
                " = m." + MovimentacaoTable.ID_USUARIO + " WHERE " +
                "u." + UsuarioTable.USERNAME + " = ?" +
                " AND " + MovimentacaoTable.DATA_MOVIMENTACAO + " BETWEEN ? AND ?";
        if(ordem == ASC) {
            command += " ORDER BY m." + MovimentacaoTable.DATA_MOVIMENTACAO + " ASC";
        } else if (ordem == DESC) {
            command += " ORDER BY m." + MovimentacaoTable.DATA_MOVIMENTACAO + " DESC";
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{username,
                Long.toString(dateToEpochSeconds(inicio)), Long.toString(dateToEpochSeconds(fim))})) {
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
    }


    public boolean atualizarMovimentacao(long idMovimentacao, double valor, String descricao, Date dataMovimentacao) {
        ContentValues values = new ContentValues();
        values.put(MovimentacaoTable.VALOR, valor);
        values.put(MovimentacaoTable.DESCRICAO, descricao);
        values.put(MovimentacaoTable.DATA_MOVIMENTACAO, dateToEpochSeconds(dataMovimentacao));

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long updatedRows = db.update(MovimentacaoTable.TABLE_NAME,
                    values,
                    MovimentacaoTable.ID_MOVIMENTACAO + " = ?",
                    new String[]{ Long.toString(idMovimentacao)});
            return updatedRows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirMovimentacao(long idMovimentacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
        long deletedRows = db.delete(
                MovimentacaoTable.TABLE_NAME,
                MovimentacaoTable.ID_MOVIMENTACAO + " = ?",
                new String[]{Long.toString(idMovimentacao)}
        );
        return deletedRows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double totalBalancoNoIntervalo(String username, Date inicio, Date fim) {
        final String resultAlias = "sum";
        final String command = "SELECT SUM(" + MovimentacaoTable.VALOR + ") AS " + resultAlias +
                " FROM " + MovimentacaoTable.TABLE_NAME + " m " +
                " JOIN " + UsuarioTable.TABLE_NAME + " u ON u." + UsuarioTable.ID_USUARIO +
                " = m." + MovimentacaoTable.ID_USUARIO + " WHERE " +
                "u." + UsuarioTable.USERNAME + " = ?" +
                " AND " + MovimentacaoTable.DATA_MOVIMENTACAO + " BETWEEN ? AND ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{username,
                Long.toString(dateToEpochSeconds(inicio)), Long.toString(dateToEpochSeconds(fim))})) {
            if(cursor.moveToFirst()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(resultAlias));
            }
            return 0;
        }
    }

    public double totalGastoNoIntervalo(String username, Date inicio, Date fim) {
        final String resultAlias = "sum";
        final String command = "SELECT SUM(" + MovimentacaoTable.VALOR + ") as "+ resultAlias +
                " FROM " + MovimentacaoTable.TABLE_NAME + " m " +
                " JOIN " + UsuarioTable.TABLE_NAME + " u ON u." + UsuarioTable.ID_USUARIO +
                " = m." + MovimentacaoTable.ID_USUARIO + " WHERE " +
                "u." + UsuarioTable.USERNAME + " = ?" +
                " AND " + MovimentacaoTable.VALOR + " < 0 " +
                " AND " + MovimentacaoTable.DATA_MOVIMENTACAO + " BETWEEN ? AND ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{username,
                Long.toString(dateToEpochSeconds(inicio)), Long.toString(dateToEpochSeconds(fim))})){
            if(cursor.moveToFirst()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(resultAlias));
            }
            return 0;
        }
    }

    @Override
    public double totalRecebimentoNoIntervalo(String username, Date inicio, Date fim) {
        final String resultAlias = "sum";
        final String command = "SELECT SUM(" + MovimentacaoTable.VALOR + ") as "+ resultAlias +
                " FROM " + MovimentacaoTable.TABLE_NAME + " m " +
                " JOIN " + UsuarioTable.TABLE_NAME + " u ON u." + UsuarioTable.ID_USUARIO +
                " = m." + MovimentacaoTable.ID_USUARIO + " WHERE " +
                "u." + UsuarioTable.USERNAME + " = ?" +
                " AND " + MovimentacaoTable.VALOR + " > 0 " +
                " AND " + MovimentacaoTable.DATA_MOVIMENTACAO + " BETWEEN ? AND ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{username,
                Long.toString(dateToEpochSeconds(inicio)), Long.toString(dateToEpochSeconds(fim))})) {
            if(cursor.moveToFirst()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(resultAlias));
            }
            return 0;
        }
    }

    public List<Movimentacao> listarMovimentacoesSomarMesmaData(String username, Date inicio, Date fim, int tipo, int unidade) {
        final String statement = "SELECT SUM(m.valor), m.data AS data FROM Movimentacao m WHERE m.data BETWEEN ? and ? GROUP BY m.data";
        return null;
    }

    public List<Movimentacao> obterMovimentacaoNoIntervaloAgrupado(String username, Date inicio, Date fim, int tipo, int grupo){
//        final String command = "SELECT SUM("+MovimentacaoTable.VALOR+") AS total, " +
//                " unixepoch(strftime(?, "+MovimentacaoTable.DATA_MOVIMENTACAO+", 'unixepoch', 'localtime')) AS epoch " +
//                " FROM "+MovimentacaoTable.TABLE_NAME+" m"+
//                " JOIN "+UsuarioTable.TABLE_NAME+" AS u ON u."+UsuarioTable.ID_USUARIO+" = m."+MovimentacaoTable.ID_USUARIO+
//                " WHERE "+MovimentacaoTable.DATA_MOVIMENTACAO+" BETWEEN ? AND ? AND u."+UsuarioTable.USERNAME+" = ? %s"+
//                " GROUP BY strftime(?, dataMovimentacao, 'unixepoch', 'localtime') " +
//                " ORDER BY "+MovimentacaoTable.DATA_MOVIMENTACAO+" ASC";
            final String command = "SELECT SUM("+MovimentacaoTable.VALOR+") AS total, " +
                    " strftime('%%s', strftime(?, "+MovimentacaoTable.DATA_MOVIMENTACAO+", 'unixepoch', 'localtime'), 'localtime') AS epoch " +
                    " FROM "+MovimentacaoTable.TABLE_NAME+" m"+
                    " JOIN "+UsuarioTable.TABLE_NAME+" AS u ON u."+UsuarioTable.ID_USUARIO+" = m."+MovimentacaoTable.ID_USUARIO+
                    " WHERE "+MovimentacaoTable.DATA_MOVIMENTACAO+" BETWEEN ? AND ? AND u."+UsuarioTable.USERNAME+" = ? %s"+
                    " GROUP BY strftime(?, dataMovimentacao, 'unixepoch', 'localtime') " +
                    " ORDER BY "+MovimentacaoTable.DATA_MOVIMENTACAO+" ASC";
        String filterByType = "";
        if(tipo == ALL) {
        } else if (tipo == RECEBIMENTO) {
            filterByType = " AND "+ MovimentacaoTable.VALOR+" >= 0";
        } else if (tipo == GASTO) {
            filterByType = " AND "+ MovimentacaoTable.VALOR+" <= 0";
        } else {
            throw new IllegalArgumentException();
        }

        String[] groupByArgs = new String[2];
        if(grupo == MovimentacaoDao.MINUTO) {
            groupByArgs[0] = "%Y-%m-%d %H:%M:00";
            groupByArgs[1] = "%Y-%m-%d %H:%M:00";
        } else if(grupo == MovimentacaoDao.HORA){
            groupByArgs[0] = "%Y-%m-%d %H:00:00";
            groupByArgs[1] = "%Y-%m-%d %H:00:00";
        } else if(grupo == MovimentacaoDao.DIA){
            groupByArgs[0] = "%Y-%m-%d";
            groupByArgs[1] = "%Y-%m-%d";
        } else if(grupo == MovimentacaoDao.SEMANA){
            groupByArgs[0] = "%Y-%m-%d";
            groupByArgs[1] = "%Y-%w";
        } else if(grupo == MovimentacaoDao.MES){
            groupByArgs[0] = "%Y-%m-01";
            groupByArgs[1] = "%Y-%m-01";
        } else if(grupo == MovimentacaoDao.ANO){
            groupByArgs[0] = "%Y-01-01";
            groupByArgs[1] = "%Y-01-01";
        } else {
            throw new IllegalArgumentException();
        }

        String[] bindArgs = new String[]{
                groupByArgs[0],
                Long.toString(dateToEpochSeconds(inicio)),
                Long.toString(dateToEpochSeconds(fim)),
                username,
                groupByArgs[1]
        };

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(String.format(command,filterByType), bindArgs)){
            List<Movimentacao> resultado = new ArrayList<>(cursor.getCount());
            while ( cursor.moveToNext() ) {
                Movimentacao movimentacao = new Movimentacao(
                        0,
                        0,
                        cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                        "",
                        epochSecondsToDate(cursor.getLong(cursor.getColumnIndexOrThrow("epoch")))
                );
                resultado.add(movimentacao);
            }
            return resultado;
        } catch (SQLException e) {
            return new ArrayList<>(0);
        }
    }

    protected static long dateToEpochSeconds(Date data) {
        return data.getTime() / 1000;
    }

    protected static Date epochSecondsToDate(long epoch) {
        return new Date(epoch * 1000);
    }
}
