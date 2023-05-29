package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.datasource.DatabaseContract.UsuarioTable;

public class UserDaoLocal extends SQLiteOpenHelper implements UserDao {
    public static final int DATABASE_VERSION = DatabaseContract.DATABASE_VERSION;
    public static final String DBName = DatabaseContract.DATABASE_NAME;
    private static UserDao instance;

    public UserDaoLocal(Context context, String dbName, int dbVersion) {
        super(context.getApplicationContext(), dbName, null, dbVersion);
    }
    public UserDaoLocal(Context context) {
        super(context.getApplicationContext(), DBName, null, DATABASE_VERSION);
    }

    public static UserDao getInstance(Context context) {
        if(instance == null) {
            instance = new UserDaoLocal(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL(UsuarioTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS " +  UsuarioTable.TABLE_NAME);
    }

    public boolean createUser(String email, String senha, String nome){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UsuarioTable.EMAIL, email);
        contentValues.put(UsuarioTable.SENHA, senha);
        contentValues.put(UsuarioTable.USERNAME, nome);
        long result = MyDB.insert(UsuarioTable.TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean checkEmail(String email){
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + UsuarioTable.TABLE_NAME + " where " + UsuarioTable.USERNAME + " = ?", new String[]{email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean checkUsernameSenha(String email, String senha){
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + UsuarioTable.TABLE_NAME + " where "+ UsuarioTable.USERNAME + " = ? and " + UsuarioTable.SENHA +" = ?",
                new String[]{email, senha});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}
