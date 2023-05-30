package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.datasource.DatabaseContract.UsuarioTable;

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
        long result = MyDB.insert(UsuarioTable.TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean checkEmail(String email){
        final String command = "Select * from " + UsuarioTable.TABLE_NAME + " where " + UsuarioTable.USERNAME + " = ?";
        SQLiteDatabase MyDB = dbHelper.getReadableDatabase();
        try(Cursor cursor = MyDB.rawQuery(command, new String[]{email})) {
            return cursor.getCount() > 0;
        }
    }

    public boolean checkUsernameSenha(String email, String senha){
        final String command = "Select * from " + UsuarioTable.TABLE_NAME + " where "+ UsuarioTable.USERNAME + " = ? and " + UsuarioTable.SENHA +" = ?";
        SQLiteDatabase MyDB = dbHelper.getReadableDatabase();
        try (Cursor cursor = MyDB.rawQuery(command, new String[]{email, senha})) {
            return cursor.getCount() > 0;
        }
    }
}
