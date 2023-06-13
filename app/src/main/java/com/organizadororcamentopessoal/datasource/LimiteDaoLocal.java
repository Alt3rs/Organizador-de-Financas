package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.organizadororcamentopessoal.entities.Limite;
import com.organizadororcamentopessoal.datasource.DatabaseContract.LimiteTable;
import com.organizadororcamentopessoal.datasource.DatabaseContract.UsuarioTable;
import com.organizadororcamentopessoal.entities.Usuario;

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

    public List<Limite> listarTodosLimitesDoUsuario(String username) {
        final String command = "SELECT " +
                "l." + LimiteTable.ID_LIMITE + "," +
                "l." + LimiteTable.ID_USUARIO + "," +
                "l." + LimiteTable.VALOR +
                " FROM " + LimiteTable.TABLE_NAME +
                " AS l WHERE " + LimiteTable.ID_USUARIO +
                " IN (SELECT " + LimiteTable.ID_USUARIO +
                " FROM " + UsuarioTable.TABLE_NAME +
                " AS u WHERE u." +  UsuarioTable.USERNAME + " = ?)";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{username})) {
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
    public boolean atualizarLimite(String username, double valor) {
        final String command = "UPDATE "+ LimiteTable.TABLE_NAME +
                " SET " + LimiteTable.VALOR + " = ?" +
                " WHERE " + LimiteTable.ID_USUARIO +
                " IN (SELECT " + UsuarioTable.ID_USUARIO +
                " FROM " + UsuarioTable.TABLE_NAME +" AS u where u."+ UsuarioTable.USERNAME+ " = ?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            SQLiteStatement statement = db.compileStatement(command);
            statement.bindDouble(1, valor);
            statement.bindString(2, username);
            long affectedRows = statement.executeUpdateDelete();
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
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

    public boolean habilitarLimite(String username, boolean estaHabilitado) {
        ContentValues values = new ContentValues();
        //final String command = "UPDATE "+ UsuarioTable.TABLE_NAME + " SET " + UsuarioTable.LIMITE_HABILITADO + " = ?" + " WHERE " + UsuarioTable.USERNAME + " = ?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        values.put(UsuarioTable.LIMITE_HABILITADO, estaHabilitado ? 1 : 0);
        try{
            long affectedRows = db.update(UsuarioTable.TABLE_NAME, values, UsuarioTable.USERNAME + " = ?", new String[]{username});
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean limiteEstaHabilitado(String username) {
        final String command = "SELECT " + UsuarioTable.LIMITE_HABILITADO +
                " FROM " + UsuarioTable.TABLE_NAME +
                " WHERE " + UsuarioTable.USERNAME + " = ?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try(Cursor cursor = db.rawQuery(command, new String[]{username})){
            return cursor.moveToFirst() && cursor.getInt(0) == 1;
        }
    }
}
