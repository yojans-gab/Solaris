package com.example.solaris;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solaris.entidades.Usuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VerActivity extends AppCompatActivity {

    EditText ViewUsuario, ViewPassword, ViewSaldo;
    Button BtnGuarda;
    FloatingActionButton fabEditar, fabEliminar;

    Usuarios usuarios;
    int numerocuenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);

        ViewUsuario = findViewById(R.id.editUsuario);
        ViewPassword = findViewById(R.id.editPassword);
        ViewSaldo = findViewById(R.id.editSaldo);
        BtnGuarda = findViewById(R.id.btnGuardar);
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabEliminar);


        // Obtener el número de cuenta desde el Intent o el estado guardado
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                numerocuenta = extras.getInt("numerocuenta");
            }
        } else {
            numerocuenta = (int) savedInstanceState.getSerializable("numerocuenta");
        }

        // Llamar directamente a verUsuario en la misma instancia de la actividad
        usuarios = verUsuario(numerocuenta);

        // Si se encontró el usuario, llenar los campos
        if (usuarios != null) {
            ViewUsuario.setText(usuarios.getUsuario());
            ViewPassword.setText(usuarios.getPassword());
            ViewSaldo.setText(usuarios.getSaldo());
            BtnGuarda.setVisibility(View.INVISIBLE);
            ViewUsuario.setInputType(InputType.TYPE_NULL);
            ViewPassword.setInputType(InputType.TYPE_NULL);
            ViewSaldo.setInputType(InputType.TYPE_NULL);
        }

        fabEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerActivity.this, Editar.class);
                intent.putExtra("numerocuenta",numerocuenta);
                startActivity(intent);
                finish();
            }
        });

        fabEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerActivity.this);
                builder.setMessage("¿Desea eliminar esta cuenta?").setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (EliminarUsuario(numerocuenta)){
                            lista();
                        }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
    }

    public Usuarios verUsuario(int numerocuenta) {
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        Usuarios usuarios = null;
        Cursor cursorUsuarios = BaseDeDatos.rawQuery("select numcuenta, usuario, password, saldo from cuenta where numcuenta=" + numerocuenta + " LIMIT 1", null);

        if (cursorUsuarios.moveToFirst()) {
            usuarios = new Usuarios();
            usuarios.setCuenta(cursorUsuarios.getInt(0));
            usuarios.setUsuario(cursorUsuarios.getString(1));
            usuarios.setPassword(cursorUsuarios.getString(2));
            usuarios.setSaldo(cursorUsuarios.getString(3));
        }
        cursorUsuarios.close();
        BaseDeDatos.close();
        return usuarios;
    }

    public boolean EliminarUsuario(int numerocuenta) {
        boolean correcto = false;

        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        try {
            // Primero obtener el dpi asociado a la cuenta
            Cursor cursor = BaseDeDatos.rawQuery("SELECT dpi FROM cuenta WHERE numcuenta = " + numerocuenta, null);
            String dpi = null;
            if (cursor.moveToFirst()) {
                dpi = cursor.getString(0);  // Obtener el dpi del resultado
            }
            cursor.close();

            if (dpi != null) {
                // Eliminar las transacciones relacionadas
                BaseDeDatos.execSQL("DELETE FROM transacciones WHERE numcuenta = " + numerocuenta);

                // Eliminar la cuenta
                BaseDeDatos.execSQL("DELETE FROM cuenta WHERE numcuenta = " + numerocuenta);

                // Eliminar el cliente relacionado
                BaseDeDatos.execSQL("DELETE FROM cliente WHERE dpi = '" + dpi + "'");

                correcto = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            correcto = false;
        } finally {
            BaseDeDatos.close();
        }

        return correcto;
    }

    private  void lista(){
        Intent intent = new Intent(this, Administrador.class);
        startActivity(intent);
        finish();
    }

    public void regAdmin(View view){
        onBackPressed();
    }
}