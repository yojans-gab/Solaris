package com.example.solaris;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    boolean Dobletoque = false;
    EditText ValUsuario, ValPassword;
    TextView text;
    Button btnAdmin, info;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ValUsuario = findViewById(R.id.vrUsuario);
        ValPassword = findViewById(R.id.vrPassword);
        text = findViewById(R.id.textView);
        btnAdmin = findViewById(R.id.btnAdmin);
        info = findViewById(R.id.info);

        
        boolean accesoDenegado = Acceso.acceso;
        if(accesoDenegado){
            btnAdmin.setEnabled(false);
        }
    }

    public void Registra(View view){
        Intent intent = new Intent(MainActivity.this, DatosUsuario.class);
        startActivity(intent);
    }

    public void Validar(View view){
        Conexion conexion = new Conexion(this, "DBSolaris",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        String usuario = ValUsuario.getText().toString().trim();
        String password = ValPassword.getText().toString().trim();

        if(!usuario.isEmpty() && !password.isEmpty()){
            Cursor fila = BaseDeDatos.rawQuery("SELECT password FROM cuenta WHERE usuario = ?", new String[]{usuario});

            if (fila.moveToFirst()) {
                String passwordDB = fila.getString(0);  // Obtener la contraseña almacenada en la base de datos

                // Comparar la contraseña ingresada con la que está en la base de datos
                if (password.equals(passwordDB)) {
                    Toast.makeText(this, "Usuario y contraseña válidos", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    intent.putExtra("EnvUsuario", usuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    ValPassword.setText("");
                }
            } else {
                // El usuario no existe en la base de datos
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }

            fila.close();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

        BaseDeDatos.close();  // Cerrar base de datos

    }


    public void Administrar(View view){
        Intent intent = new Intent(MainActivity.this, Acceso.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (Dobletoque){
            super.onBackPressed();
            Toast.makeText(this, "Saliste de la aplicación", Toast.LENGTH_SHORT).show();
            return;
        }
        //Al precionar una vez la boton de retoseso
        this.Dobletoque = true;
        Toast.makeText(this, "Presione 2 veces para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Dobletoque = false;
            }
        },2000);
    }

    public void informacion(View view){
        ConstraintLayout InfoLayaut = findViewById(R.id.succesConstraintLayout);
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.success_dialog, InfoLayaut);
        Button Info = view.findViewById(R.id.info);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        Info.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}