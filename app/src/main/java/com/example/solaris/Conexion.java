package com.example.solaris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Conexion extends SQLiteOpenHelper{
    public Conexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        //Crear la tabla cliente
        BaseDeDatos.execSQL("create table cliente(dpi int primary key, nombre text, apellido text, edad int)");
        //Crear la tabla cuenta
        BaseDeDatos.execSQL("create table cuenta(numcuenta int primary key, usuario text, password text, tipoCuenta text, saldo real, dpi text, FOREIGN KEY (dpi) REFERENCES cliente(dpi))");
        //Crear la tabla transacciones
        BaseDeDatos.execSQL("CREATE TABLE transacciones(idtransaccion INTEGER PRIMARY KEY AUTOINCREMENT, numcuenta INTEGER, cuenta_destino TEXT, monto REAL, fecha TEXT, tipo TEXT, FOREIGN KEY (numcuenta) REFERENCES cuenta(numcuenta))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
