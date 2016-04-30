package com.gg.moviesmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseConnection extends SQLiteOpenHelper {

    private static final String DB_NAME = "main.db";
    private static final int DB_VERSION = 1;
    private static DataBaseConnection instance;

    private DataBaseConnection(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        /*getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_MOVIE);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_GENRE);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_MOVIES_GENRES);
        onCreate(getWritableDatabase());*/
    }

    public static synchronized DataBaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseConnection(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ContractClass.DBEntry.T_MOVIE +
                " (" + ContractClass.DBEntry._ID + " INTEGER PRIMARY KEY, " +
                ContractClass.DBEntry.C_TITLE + " TEXT NOT NULL, " +
                ContractClass.DBEntry.C_RELEASE + " TEXT, " +
                ContractClass.DBEntry.C_RATING + " REAL, " +
                ContractClass.DBEntry.C_OVERVIEW + " TEXT, " +
                ContractClass.DBEntry.C_LANGUAGE + " TEXT, " +
                ContractClass.DBEntry.C_RUNTIME + " INTEGER, " +
                ContractClass.DBEntry.C_POPULARITY + " REAL, " +
                ContractClass.DBEntry.C_CAST + " TEXT, " +
                ContractClass.DBEntry.C_DIRECTOR + " TEXT, " +
                ContractClass.DBEntry.C_TRAILER + " TEXT, " +
                ContractClass.DBEntry.C_IMPOSTER + " TEXT, " +
                ContractClass.DBEntry.C_IMBACK + " TEXT, " +
                ContractClass.DBEntry.C_WATCHLIST + " INTEGER, " +
                ContractClass.DBEntry.C_WATCHED + " INTEGER, " +
                ContractClass.DBEntry.C_LOADED + " INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ContractClass.DBEntry.T_GENRE +
                " (" + ContractClass.DBEntry._ID + " INTEGER PRIMARY KEY, " +
                ContractClass.DBEntry.C_NAME + " TEXT UNIQUE NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ContractClass.DBEntry.T_MOVIES_GENRES +
                " (" + ContractClass.DBEntry._ID + " INTEGER PRIMARY KEY, " +
                ContractClass.DBEntry.C_FK_MOVIE + " INTEGER, " +
                ContractClass.DBEntry.C_FK_GENRE + " INTEGER, " +
                "FOREIGN KEY(" + ContractClass.DBEntry.C_FK_MOVIE + ") " +
                "REFERENCES " + ContractClass.DBEntry.T_MOVIE + "(" + ContractClass.DBEntry._ID + "), " +
                "FOREIGN KEY(" + ContractClass.DBEntry.C_FK_GENRE + ") " +
                "REFERENCES " + ContractClass.DBEntry.T_GENRE + "(" + ContractClass.DBEntry._ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_GENRE);
        db.execSQL("DROP TABLE IF EXISTS " + ContractClass.DBEntry.T_MOVIES_GENRES);
        onCreate(db);
    }
}