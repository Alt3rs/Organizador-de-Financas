package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.entities.Limite;
import com.organizadororcamentopessoal.datasource.DatabaseContract.LimiteTable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LimiteDaoLocal implements LimiteDao {
    private SQLiteOpenHelper dbHelper;

    public LimiteDaoLocal(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean criarLimite(long idUsuario, double valor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LimiteTable.ID_USUARIO , idUsuario);
        contentValues.put(LimiteTable.VALOR, valor);
        try {
            long result = db.insert(LimiteTable.TABLE_NAME, null, contentValues);
            return  result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Limite obterLimite(long idLimite) {
        final String command = "SELECT " +
                LimiteTable.ID_LIMITE + "," +
                LimiteTable.ID_USUARIO + "," +
                LimiteTable.VALOR + " FROM " + LimiteTable.TABLE_NAME +
                " WHERE " + LimiteTable.ID_LIMITE + " = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idLimite)})) {
            Limite result = null;
            if(cursor.moveToNext()) {
                result = new Limite(
                        cursor.getLong(cursor.getColumnIndexOrThrow(LimiteTable.ID_LIMITE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(LimiteTable.ID_USUARIO)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(LimiteTable.VALOR))
                );
            }
            return result;
        }
    }

    @NotNull
    public List<Limite> listarTodosLimitesDoUsuario(long idUsuario) {
        final String command = "SELECT " +
                LimiteTable.ID_LIMITE + "," +
                LimiteTable.ID_USUARIO + "," +
                LimiteTable.VALOR + " FROM " + LimiteTable.TABLE_NAME +
                " WHERE " + LimiteTable.ID_USUARIO + " = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idUsuario)})) {
            List<Limite> result = new ArrayList<>(cursor.getCount());
            if(cursor.moveToNext()) {
                Limite row = new Limite(
                        cursor.getLong(cursor.getColumnIndexOrThrow(LimiteTable.ID_LIMITE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(LimiteTable.ID_USUARIO)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(LimiteTable.VALOR))
                );
                result.add(row);
            }
            return result;
        }
    }

    @Override
    public boolean excluirLimite(long idLimite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long deletedRows = db.delete(
                    LimiteTable.TABLE_NAME,
                    LimiteTable.ID_LIMITE + " = ?",
                    new String[] {Long.toString(idLimite)}
            );
            return deletedRows != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
