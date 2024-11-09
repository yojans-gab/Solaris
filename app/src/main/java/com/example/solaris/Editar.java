package com.example.solaris;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solaris.entidades.Usuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Editar extends AppCompatActivity {

    EditText ViewUsuario, ViewPassword, ViewSaldo;
    Button BtnGuarda;
    FloatingActionButton fabEditar, fabEliminar;
    TextView tvreg;

    boolean correcto = false;
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
        fabEditar.setVisibility(View.INVISIBLE);
        fabEliminar = findViewById(R.id.fabEliminar);
        fabEliminar.setVisibility(View.INVISIBLE);
        tvreg = findViewById(R.id.tvreg);
        tvreg.setVisibility(View.INVISIBLE);

        // Obtener el número de cuenta desde el Intent o el estado guardado
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                numerocuenta = extras.getInt("numerocuenta");
            }
        } else {
            numerocuenta = (int) savedInstanceState.getSerializable("numerocuenta");
        }

        // Llamar a verUsuario para rellenar los campos
        usuarios = verUsuario(numerocuenta);

        if (usuarios != null) {
            ViewUsuario.setText(usuarios.getUsuario());
            ViewPassword.setText(usuarios.getPassword());
            ViewSaldo.setText(usuarios.getSaldo());
        }

        BtnGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ViewUsuario.getText().toString().isEmpty() && !ViewPassword.getText().toString().isEmpty()){
                    correcto = EditarUsuario(numerocuenta, ViewUsuario.getText().toString(), ViewPassword.getText().toString(), ViewSaldo.getText().toString());
                    if(correcto){
                        Toast.makeText(Editar.this, "Registro modificado", Toast.LENGTH_LONG).show();
                        verRegistro();
                    } else {
                        Toast.makeText(Editar.this, "Error al modificar el registro", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Editar.this, "DEBE LLENAR LOS CAMPOS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Usuarios verUsuario(int numerocuenta) {
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getReadableDatabase();

        Usuarios usuarios = null;
        Cursor cursor = BaseDeDatos.rawQuery("SELECT usuario, password, saldo FROM cuenta WHERE numcuenta = ?", new String[]{String.valueOf(numerocuenta)});

        if (cursor.moveToFirst()) {
            usuarios = new Usuarios();
            usuarios.setUsuario(cursor.getString(0));
            usuarios.setPassword(cursor.getString(1));
            usuarios.setSaldo(cursor.getString(2));
        }

        cursor.close();
        BaseDeDatos.close();

        return usuarios;
    }

    private void verRegistro(){
        Intent intent = new Intent(this, VerActivity.class);
        intent.putExtra("numerocuenta", numerocuenta);
        startActivity(intent);
        finish();
    }

    public boolean EditarUsuario(int numerocuenta, String usuario, String password, String saldo) {
        boolean correcto = false;

        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        try {
            BaseDeDatos.execSQL("UPDATE cuenta SET usuario = ?, password = ?, saldo = ? WHERE numcuenta = ?", new Object[]{usuario, password, saldo, numerocuenta});
            correcto = true;
        } catch (Exception ex){
            ex.printStackTrace();
            correcto = false;
        } finally {
            BaseDeDatos.close();
        }

        return correcto;
    }
}