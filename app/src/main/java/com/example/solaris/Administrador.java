package com.example.solaris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solaris.clases.ListaUsuariosAdapter;
import com.example.solaris.entidades.Usuarios;

import java.util.ArrayList;

public class Administrador extends AppCompatActivity {

    RecyclerView listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador);

        listaUsuarios = findViewById(R.id.listausuarios);
        listaUsuarios.setLayoutManager(new LinearLayoutManager(this));

        ListaUsuariosAdapter adapter = new ListaUsuariosAdapter(mostrarUsuarios());
        listaUsuarios.setAdapter(adapter);

    }

    public ArrayList<Usuarios> mostrarUsuarios(){
        Conexion conexion = new Conexion(this, "DBSolaris",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        ArrayList<Usuarios>listaUsuarios = new ArrayList<>();
        Usuarios usuarios = null;
        Cursor cursorUsuarios = null;

        cursorUsuarios = BaseDeDatos.rawQuery("select numcuenta, usuario, password, tipoCuenta, saldo from cuenta",null);

        if(cursorUsuarios.moveToFirst()){
            do{
                usuarios = new Usuarios();
                usuarios.setCuenta(cursorUsuarios.getInt(0));
                usuarios.setUsuario(cursorUsuarios.getString(1));
                usuarios.setPassword(cursorUsuarios.getString(2));
                usuarios.setTipoCuenta(cursorUsuarios.getString(3));
                float saldo = cursorUsuarios.getFloat(4);
                usuarios.setSaldo(String.format("%.2f", saldo));
                listaUsuarios.add(usuarios);
            } while (cursorUsuarios.moveToNext());
        }
        cursorUsuarios.close();
        return listaUsuarios;
    }
    public void Reg(View view){
        onBackPressed();
    }
}