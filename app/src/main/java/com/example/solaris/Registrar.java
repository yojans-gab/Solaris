package com.example.solaris;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Random;

public class Registrar extends AppCompatActivity {
    private EditText  etPassword, etPassword2, etSaldo;
    private TextView etCuenta, etUsuario, tvVolver;
    private Spinner spiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);


        etCuenta =  findViewById(R.id.txtCuenta);
        etUsuario =  findViewById(R.id.txtUsuario);
        etPassword = findViewById(R.id.txtPassword);
        etPassword2 = findViewById(R.id.txtPassword2);
        etSaldo = findViewById(R.id.txtSaldo);
        spiner = findViewById(R.id.spTCuenta);
        tvVolver =  findViewById(R.id.tvVolver);
        tvVolver.setVisibility(View.INVISIBLE);

        //Configurar Spinner
        String [] tipos = {"Monetaria", "Ahorro"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.spiner_item_solaris, tipos);
        spiner.setAdapter(adapter);


        //Traer datos de DatosUsuario
        String RecibeNom = getIntent().getStringExtra("NomEnv");
        String RecibeApel = getIntent().getStringExtra("ApelEnv");


        //Crear el usuario
        // Extraer el primer nombre y el primer apellido
        String primerNombre = RecibeNom.split(" ")[0]; // Obtener el primer nombre
        String primerApellido = RecibeApel.split(" ")[0]; // Obtener el primer apellido
        // Combinar el primer nombre y primer apellido
        String Usuario = primerNombre + primerApellido;

        // Mostrar el usuario y cuenta creada
       // etCuenta.setText(String.valueOf(numeroCuenta));
        etUsuario.setText(Usuario);
        generarNumeroCuentaUnico();
    }

    //Metodo Regresar de Registrar a DatosUsuario
    public void Regresar(View view){
        etPassword2.setEnabled(true);
        etPassword.setEnabled(true);
        etSaldo.setEnabled(true);
        onBackPressed();
    }

    private void generarNumeroCuentaUnico() {
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        Random random = new Random();
        int numeroCuenta;
        Cursor cursorCuenta;

        // Generar y verificar número de cuenta hasta que sea único
        do {
            numeroCuenta = random.nextInt(90000000) + 10000000;
            cursorCuenta = BaseDeDatos.rawQuery("SELECT numcuenta FROM cuenta WHERE numcuenta = ?", new String[]{String.valueOf(numeroCuenta)});
        } while (cursorCuenta.moveToFirst());

        cursorCuenta.close();
        BaseDeDatos.close();

        // Mostrar el número de cuenta generado en etCuenta
        etCuenta.setText(String.valueOf(numeroCuenta));
    }

    // Método Guardar en base de datos
    public void Insertar(View view) {
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        // Datos para la tabla Cliente
        String DPI = getIntent().getStringExtra("DpiEnv");
        String Nombre = getIntent().getStringExtra("NomEnv");
        String Apellido = getIntent().getStringExtra("ApelEnv");
        String Edad = getIntent().getStringExtra("EdadEnv");

        // Datos para la tabla Cuenta
        String ncuenta = etCuenta.getText().toString();
        String usuario = etUsuario.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();
        String seleccion = spiner.getSelectedItem().toString();
        String saldo = etSaldo.getText().toString();

        //Validar que se haya  ingresado la misma contraseña
        if(!password.equals(password2)){
            Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etPassword2.setText("");
            return;
        }

        // Validar si el número de cuenta ya existe (no debería existir ya que se validó al generar)
        Cursor cursorCuenta = BaseDeDatos.rawQuery("SELECT numcuenta FROM cuenta WHERE numcuenta = ?", new String[]{ncuenta});
        if (cursorCuenta.moveToFirst()) {
            Toast.makeText(this, "El número de cuenta ya está registrado", Toast.LENGTH_SHORT).show();
            cursorCuenta.close();
            BaseDeDatos.close();
            return;
        }
        cursorCuenta.close();

        // Si no existe el número de cuenta, procedemos a insertar
        if (!DPI.isEmpty() && !Nombre.isEmpty() && !Apellido.isEmpty() && !Edad.isEmpty()) {
            ContentValues registroCliente = new ContentValues();
            registroCliente.put("dpi", DPI);
            registroCliente.put("nombre", Nombre);
            registroCliente.put("apellido", Apellido);
            registroCliente.put("edad", Edad);

            long clienteInsertado = BaseDeDatos.insert("cliente", null, registroCliente);

            if (clienteInsertado != -1) {
                if (!usuario.isEmpty() && !password.isEmpty() && !saldo.isEmpty()) {
                    ContentValues registroCuenta = new ContentValues();
                    registroCuenta.put("numcuenta", ncuenta);
                    registroCuenta.put("usuario", usuario);
                    registroCuenta.put("password", password);
                    registroCuenta.put("tipoCuenta", seleccion);
                    registroCuenta.put("saldo", saldo);
                    registroCuenta.put("dpi", DPI);  // DPI como clave foránea

                    long cuentaInsertada = BaseDeDatos.insert("cuenta", null, registroCuenta);

                    if (cuentaInsertada != -1) {
                        Toast.makeText(this, "Registro de cuenta y cliente exitoso", Toast.LENGTH_SHORT).show();
                        tvVolver.setVisibility(View.VISIBLE);
                        etPassword.setEnabled(false);
                        etPassword2.setEnabled(false);
                        etSaldo.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Error al registrar la cuenta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Debes llenar todos los campos de la cuenta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al registrar el cliente", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos del cliente", Toast.LENGTH_SHORT).show();
        }

        BaseDeDatos.close();  // Cerrar base de datos
    }
}