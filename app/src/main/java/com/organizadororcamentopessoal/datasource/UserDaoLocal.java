package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.datasource.DatabaseContract.UsuarioTable;
import com.organizadororcamentopessoal.entities.Usuario;

public class UserDaoLocal implements UserDao {
    private SQLiteOpenHelper dbHelper;

    public UserDaoLocal(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    public boolean createUser(String email, String senha, String nome){
        SQLiteDatabase MyDB = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UsuarioTable.EMAIL, email);
        contentValues.put(UsuarioTable.SENHA, senha);
        contentValues.put(UsuarioTable.USERNAME, nome);
        try {
            long result = MyDB.insert(UsuarioTable.TABLE_NAME, null, contentValues);
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameRegistered(String username){
        final String command = "Select COUNT(*) as 'n' from " + UsuarioTable.TABLE_NAME + " where " + UsuarioTable.USERNAME + " = ?";
        SQLiteDatabase MyDB = dbHelper.getReadableDatabase();
        try(Cursor cursor = MyDB.rawQuery(command, new String[]{username})) {
            return cursor.moveToFirst() && cursor.getLong(cursor.getColumnIndexOrThrow("n")) > 0;
        }
    }

    public boolean checkUsernameSenha(String username, String senha){
        final String command = "Select Count(*) as 'n' from " + UsuarioTable.TABLE_NAME + " where "+ UsuarioTable.USERNAME + " = ? and " + UsuarioTable.SENHA +" = ?";
        SQLiteDatabase MyDB = dbHelper.getReadableDatabase();
        try (Cursor cursor = MyDB.rawQuery(command, new String[]{username, senha})) {
            return cursor.moveToFirst() && cursor.getLong(cursor.getColumnIndexOrThrow("n")) > 0;
        }
    }

    @Override
    public Usuario getUser(long idUsuario) {
        final String command = "SELECT " +
                UsuarioTable.ID_USUARIO + "," +
                UsuarioTable.USERNAME + "," +
                UsuarioTable.EMAIL +
                " FROM " + UsuarioTable.TABLE_NAME +
                " WHERE " + UsuarioTable.ID_USUARIO + " = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idUsuario)})) {
            Usuario result = null;
            if(cursor.moveToNext()) {
                result = new Usuario(
                        cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.ID_USUARIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(UsuarioTable.USERNAME)),
                        cursor.getString((cursor.getColumnIndexOrThrow(UsuarioTable.EMAIL))),
                        null
                );
            }
            return result;
        }
    }

    @Override
    public Usuario getUser(String userName) {
        final String command = "SELECT " +
                UsuarioTable.ID_USUARIO + "," +
                UsuarioTable.USERNAME + "," +
                UsuarioTable.EMAIL +
                " FROM " + UsuarioTable.TABLE_NAME +
                " WHERE " + UsuarioTable.USERNAME + " = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(command, new String[]{userName})) {
            Usuario result = null;
            if(cursor.moveToNext()) {
                result = new Usuario(
                        cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.ID_USUARIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(UsuarioTable.USERNAME)),
                        cursor.getString((cursor.getColumnIndexOrThrow(UsuarioTable.EMAIL))),
                        null
                );
            }
            return result;
        }
    }
}
