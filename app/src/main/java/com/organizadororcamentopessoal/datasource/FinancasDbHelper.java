package com.organizadororcamentopessoal.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FinancasDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = DatabaseContract.DATABASE_VERSION;
    public static final String DBName = DatabaseContract.DATABASE_NAME;
    private static FinancasDbHelper cachedInstance;

    public FinancasDbHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public FinancasDbHelper(Context context) {
        super(context, DBName, null, DATABASE_VERSION);
    }


    public static FinancasDbHelper getCachedInstance(Context context) {
        if(cachedInstance == null) {
            cachedInstance = new FinancasDbHelper(context);
        }
        return cachedInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL(DatabaseContract.UsuarioTable.CREATE_TABLE);
        MyDB.execSQL(DatabaseContract.MovimentacaoTable.CREATE_TABLE);
        MyDB.execSQL(DatabaseContract.LimiteTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS " +  DatabaseContract.UsuarioTable.TABLE_NAME);
        MyDB.execSQL("DROP TABLE IF EXISTS " +  DatabaseContract.MovimentacaoTable.TABLE_NAME);
        MyDB.execSQL("DROP TABLE IF EXISTS " +  DatabaseContract.LimiteTable.TABLE_NAME);
    }

    public static UserDao getUserDao(Context context) {
        return new UserDaoLocal(getCachedInstance(context));
    }

    public static MovimentacaoDao getMovimentacaoDao(Context context) {
        return new MovimentacaoDaoLocal(getCachedInstance(context));
    }

    public static LimiteDaoLocal getLimiteDao(Context context) {
        return new LimiteDaoLocal(getCachedInstance(context));
    }

}
