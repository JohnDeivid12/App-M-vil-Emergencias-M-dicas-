package com.example.appemergenciasmedicas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLliteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla para los centros médicos
        db.execSQL("CREATE TABLE CentrosMedicos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT UNIQUE, " +
                "tipo TEXT, " +          // hospital, clínica, farmacia
                "direccion TEXT, " +
                "telefono TEXT" +        // ✅ Nueva columna teléfono
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CentrosMedicos");
        onCreate(db);
    }
}
