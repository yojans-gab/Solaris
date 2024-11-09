package com.example.solaris;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DatosUsuario extends AppCompatActivity {

    private EditText dpi, nom, apel, ed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_usuario);

        dpi = (EditText)findViewById(R.id.txtDPI);
        nom = (EditText)findViewById(R.id.txtNombre);
        apel = (EditText)findViewById(R.id.txtApellido);
        ed = (EditText)findViewById(R.id.txtEdad);
    }

    //  De DatosUsuario a Registrar
    public void Siguiente(View view) {
        String dpiP = dpi.getText().toString();
        String nomP = nom.getText().toString();
        String apelP = apel.getText().toString();
        String edadP = ed.getText().toString();

        if (!dpiP.isEmpty() && !nomP.isEmpty() && !apelP.isEmpty() && !edadP.isEmpty()) {
            // Validar si el DPI ya está registrado en la base de datos
            if (!validarDPI(dpiP)) {
                Intent intent = new Intent(DatosUsuario.this, Registrar.class);
                intent.putExtra("DpiEnv", dpi.getText().toString());
                intent.putExtra("NomEnv", nom.getText().toString());
                intent.putExtra("ApelEnv", apel.getText().toString());
                intent.putExtra("EdadEnv", ed.getText().toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "El DPI ya está registrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para validar si el DPI ya existe en la base de datos
    private boolean validarDPI(String dpi) {
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getReadableDatabase();
        boolean existe = false;

        // Consulta para verificar si el DPI ya existe
        Cursor cursor = BaseDeDatos.rawQuery("SELECT dpi FROM cliente WHERE dpi = ?", new String[]{dpi});

        if (cursor.moveToFirst()) {
            existe = true; // El DPI ya está registrado
        }

        cursor.close();
        BaseDeDatos.close();

        return existe;
    }

    public void Regresar(View view) {
        onBackPressed();
    }
}