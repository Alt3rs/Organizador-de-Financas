package com.organizadororcamentopessoal.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.organizadororcamentopessoal.entities.Limite;
import com.organizadororcamentopessoal.datasource.DatabaseContract.LimiteTable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LimiteDaoLocal extends SQLiteOpenHelper implements LimiteDao {
    public static final int DATABASE_VERSION = DatabaseContract.DATABASE_VERSION;
    public static final String DBName = DatabaseContract.DATABASE_NAME;
    private static LimiteDaoLocal instance;

    public LimiteDaoLocal(Context context, String dbName, int dbVersion) { // Para testes
        super(context.getApplicationContext(), dbName, null, dbVersion);
    }
    public LimiteDaoLocal(Context context) {
        super(context.getApplicationContext(), DBName, null, DATABASE_VERSION);
    }

    public static LimiteDaoLocal getInstance(Context context) {
        if(instance == null) {
            instance = new LimiteDaoLocal(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL(LimiteTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS " +  LimiteTable.TABLE_NAME);
    }


    public boolean criarLimite(long idUsuario, double valor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LimiteTable.ID_USUARIO , idUsuario);
        contentValues.put(LimiteTable.VALOR, valor);
        long result = db.insert(LimiteTable.TABLE_NAME, null, contentValues);
        return  result != -1;
    }

    public Limite obterLimite(long idLimite) {
        final String command = "SELECT " +
                LimiteTable.ID_LIMITE + "," +
                LimiteTable.ID_USUARIO + "," +
                LimiteTable.VALOR + " FROM " + LimiteTable.TABLE_NAME +
                " WHERE " + LimiteTable.ID_LIMITE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idLimite)});

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

    @NotNull
    public List<Limite> listarTodosLimitesDoUsuario(long idUsuario) {
        final String command = "SELECT " +
                LimiteTable.ID_LIMITE + "," +
                LimiteTable.ID_USUARIO + "," +
                LimiteTable.VALOR + " FROM " + LimiteTable.TABLE_NAME +
                " WHERE " + LimiteTable.ID_USUARIO + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(command, new String[]{Long.toString(idUsuario)});

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

    @Override
    public boolean excluirLimite(long idLimite) {
        SQLiteDatabase db = this.getWritableDatabase();
        long deletedRows = db.delete(
                LimiteTable.TABLE_NAME,
                LimiteTable.ID_LIMITE + " = ?",
                new String[] {Long.toString(idLimite)}
        );
        return deletedRows != 0;
    }
}
