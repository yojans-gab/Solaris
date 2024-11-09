package com.example.solaris.activitymenu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solaris.Conexion;
import com.example.solaris.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class depositoActivity extends AppCompatActivity {

    private EditText dep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposito);

        dep = (EditText)findViewById(R.id.tvdeposito);

    }

    public void Depositar(View view){
        String saldoDepositoStr = dep.getText().toString();

        // Validar que el valor de depósito no esté vacío
        if (saldoDepositoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        float saldoDeposito = Float.parseFloat(saldoDepositoStr);
        // Obtener el número de cuenta desde el intent
        String numCuenta = getIntent().getStringExtra("numCuenta");

        // Conectar a la base de datos
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        try {
            // Obtener el saldo actual de la base de datos
            Cursor cursor = BaseDeDatos.rawQuery("SELECT saldo FROM cuenta WHERE numcuenta = ?", new String[]{numCuenta});
            if (cursor.moveToFirst()) {
                float saldoActual = cursor.getFloat(0);
                // Sumar el depósito al saldo actual
                float nuevoS = saldoActual + saldoDeposito;

                //Manejar solo 2 decimales
                BigDecimal nuevoSaldo = new BigDecimal(nuevoS).setScale(2, RoundingMode.HALF_UP);

                // Actualizar el saldo en la base de datos como cadena
                BaseDeDatos.execSQL("UPDATE cuenta SET saldo = ? WHERE numcuenta = ?", new Object[]{nuevoSaldo.toString(), numCuenta});

                // Insertar la transacción en la tabla de transacciones
                String tipoTransaccion = "Depósito";
                String fechaHora = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

                BaseDeDatos.execSQL("INSERT INTO transacciones(numcuenta, cuenta_destino, monto, fecha, tipo) VALUES (?, ?, ?, ?, ?)",
                        new Object[]{numCuenta, null, saldoDeposito, fechaHora, tipoTransaccion});

                Toast.makeText(this, "Depósito realizado. Nuevo saldo: " + nuevoSaldo, Toast.LENGTH_SHORT).show();
                // Limpiar el campo de depósito
                dep.setText("");
            } else {
                Toast.makeText(this, "Número de cuenta no encontrado", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al realizar el depósito: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            BaseDeDatos.close();
        }

    }

    public void RegMenu(View view){
        onBackPressed();
    }
}