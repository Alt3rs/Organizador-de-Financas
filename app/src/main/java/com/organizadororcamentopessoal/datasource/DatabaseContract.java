package com.organizadororcamentopessoal.datasource;

/**
 * Define constantes do banco de dados
 * <pre>
 * {@value UsuarioTable#CREATE_TABLE}
 * {@value MovimentacaoTable#CREATE_TABLE}
 * {@value LimiteTable#CREATE_TABLE}
 * {@value LimiteTable#CRIAR_LIMITE_PADRAO_TRIGGER}
 * </pre>
 * */
public class DatabaseContract {
    private DatabaseContract() { }
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "organizador-financas.db";

    public static class UsuarioTable {
        private UsuarioTable() { }
        public static final String TABLE_NAME = "Usuario";
        public static final String ID_USUARIO = "idUsuario";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String SENHA = "senha";
        public static final String LIMITE_HABILITADO = "limiteHabilitado";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        ID_USUARIO +" INTEGER PRIMARY KEY, " +
                        USERNAME + " TEXT UNIQUE NOT NULL, " +
                        EMAIL + " TEXT NOT NULL, " +
                        SENHA + " TEXT NOT NULL, " +
                        LIMITE_HABILITADO + " INTEGER " +
                        ");";
    }

    public static class MovimentacaoTable {
        private MovimentacaoTable() { }
        public static final String TABLE_NAME = "Movimentacao";
        public static final String ID_MOVIMENTACAO = "idMovimentacao";
        public static final String ID_USUARIO = "idUsuario";
        public static final String VALOR =  "valor";
        public static final String DESCRICAO =  "descricao";
        public static final String DATA_MOVIMENTACAO =  "dataMovimentacao";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        ID_MOVIMENTACAO + " INTEGER PRIMARY KEY, " +
                        ID_USUARIO + " INTEGER NOT NULL REFERENCES " +
                        UsuarioTable.TABLE_NAME + "(" + UsuarioTable.ID_USUARIO + "), " +
                        VALOR + " REAL NOT NULL, " +
                        DESCRICAO + " TEXT, " +
                        DATA_MOVIMENTACAO + " INTEGER NOT NULL DEFAULT (unixepoch())" +
                        ");";
    }

    public static class LimiteTable {
        private LimiteTable() { }
        public static final String TABLE_NAME = "Limite";
        public static final String ID_LIMITE = "idLimite";
        public static final String ID_USUARIO = "idUsuario";
        public static final String VALOR =  "valor";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        ID_LIMITE +" INTEGER PRIMARY KEY, " +
                        ID_USUARIO + " INTEGER NOT NULL UNIQUE REFERENCES " +
                        UsuarioTable.TABLE_NAME + "(" + UsuarioTable.ID_USUARIO + "), " +
                        VALOR + " REAL NOT NULL " +
                        ");";
        public static final String CRIAR_LIMITE_PADRAO_TRIGGER =
                "CREATE TRIGGER criarLimite AFTER INSERT ON " + UsuarioTable.TABLE_NAME +
                        " Begin INSERT INTO " + LimiteTable.TABLE_NAME + " (" +
                        LimiteTable.ID_USUARIO + "," +
                        LimiteTable.VALOR + ") VALUES (" +
                        "NEW." + UsuarioTable.ID_USUARIO + ", 0.0); END;";
    }
}
