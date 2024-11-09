package com.example.solaris.activitymenu;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solaris.Conexion;
import com.example.solaris.MainActivity;
import com.example.solaris.Menu;
import com.example.solaris.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransferActivity extends AppCompatActivity {
    EditText Cuenta, Cantidad;
    TextView UsuarioEncon;
    Button BtnTransfer;
    FloatingActionButton fabBuscar;
    String cuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer);

        Cuenta = findViewById(R.id.tvBusqueda);
        Cantidad = findViewById(R.id.tvCantidad);
        Cantidad.setEnabled(false);
        UsuarioEncon = findViewById(R.id.textUser);
        fabBuscar = findViewById(R.id.BtnBusqueda);
        BtnTransfer = findViewById(R.id.BtnTransaccion);
        BtnTransfer.setEnabled(false);
    }

    public void BusquedaUser(View view) {
        cuenta = Cuenta.getText().toString().trim();

        String numCuenta = getIntent().getStringExtra("numCuenta");
        if (cuenta.equals(numCuenta)) {
            Toast.makeText(this, "Esta es su cuenta bancaria, escriba una cuenta diferente", Toast.LENGTH_SHORT).show();
            Cuenta.setText("");
            return;
        }

        if (!cuenta.isEmpty()) {
            Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
            SQLiteDatabase BaseDeDatos = conexion.getReadableDatabase();

            Cursor fila = BaseDeDatos.rawQuery("SELECT cliente.nombre, cliente.apellido FROM cuenta INNER JOIN cliente ON cuenta.dpi = cliente.dpi WHERE cuenta.numcuenta = ?",
                    new String[]{cuenta});

            if (fila.moveToFirst()) {
                String nombre = fila.getString(0);
                String apellido = fila.getString(1);

                String usuarioCompleto = nombre + " " + apellido;
                UsuarioEncon.setText(usuarioCompleto);
                BtnTransfer.setEnabled(true);
                fabBuscar.setEnabled(false);
                Cantidad.setEnabled(true);
            } else {
                Toast.makeText(this, "Cuenta bancaria no encontrada", Toast.LENGTH_SHORT).show();
            }

            fila.close();
            BaseDeDatos.close();
        } else {
            Toast.makeText(this, "Por favor, ingrese la cuenta de destino", Toast.LENGTH_SHORT).show();
        }
    }

    public void TransferirSaldo(View view) {
        String saldoRetirarStr = Cantidad.getText().toString();
        String cuentaDestino = Cuenta.getText().toString();
        String cuentaOrigen = getIntent().getStringExtra("numCuenta");

        // Validar que el monto  no esté vacío
        if (saldoRetirarStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el monto", Toast.LENGTH_SHORT).show();
            return;
        }

        float saldoRetiro = Float.parseFloat(saldoRetirarStr);

        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        try {
            // Obtener saldo actual de la cuenta origen
            Cursor cursorOrigen = BaseDeDatos.rawQuery("SELECT saldo FROM cuenta WHERE numcuenta = ?", new String[]{cuentaOrigen});
            if (cursorOrigen.moveToFirst()) {
                float saldoActualOrigen = cursorOrigen.getFloat(0);

                // Verificar que haya suficiente saldo para el retiro
                if (saldoActualOrigen >= saldoRetiro) {
                    // Calcular el nuevo saldo de la cuenta origen
                    float nuevoSaldoOrigen = saldoActualOrigen - saldoRetiro;
                    BigDecimal nuevoSaldo = new BigDecimal(nuevoSaldoOrigen).setScale(2, RoundingMode.HALF_UP);

                    // Retirar dinero
                    BaseDeDatos.execSQL("UPDATE cuenta SET saldo = ? WHERE numcuenta = ?", new Object[]{nuevoSaldo.toString(), cuentaOrigen});

                    // Obtener el saldo actual de la cuenta destino
                    Cursor cursorDestino = BaseDeDatos.rawQuery("SELECT saldo FROM cuenta WHERE numcuenta = ?", new String[]{cuentaDestino});
                    if (cursorDestino.moveToFirst()) {
                        float saldoActualDestino = cursorDestino.getFloat(0);

                        // Calcular el nuevo saldo de la cuenta destino
                        float nuevoSaldoDestino = saldoActualDestino + saldoRetiro;
                        BigDecimal nuevoSaldoDes = new BigDecimal(nuevoSaldoDestino).setScale(2, RoundingMode.HALF_UP);

                        // Realizar el deposito
                        BaseDeDatos.execSQL("UPDATE cuenta SET saldo = ? WHERE numcuenta = ?", new Object[]{nuevoSaldoDes.toString(), cuentaDestino});


                        // Insertar datos de la transacción en la tabla transacciones
                        String tipoTransaccion = "Transferencia";
                        String fechaHora = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
                        BaseDeDatos.execSQL("INSERT INTO transacciones(numcuenta, cuenta_destino, monto, fecha, tipo) VALUES (?, ?, ?, ?, ?)",
                                new Object[]{cuentaOrigen, cuentaDestino, saldoRetiro, fechaHora, tipoTransaccion});


                        Toast.makeText(this, "Transferencia realizada exitosamente. Saldo actual: " + nuevoSaldo, Toast.LENGTH_SHORT).show();

                        // Limpiar campos
                        Cantidad.setText("");
                        Cuenta.setText("");
                        UsuarioEncon.setText("");
                        BtnTransfer.setEnabled(false);
                        fabBuscar.setEnabled(true);
                        Cantidad.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Cuenta de destino no encontrada", Toast.LENGTH_SHORT).show();
                    }
                    cursorDestino.close();
                } else {
                    Toast.makeText(this, "El saldo de tu cuenta es insuficiente para realizar esta tansacción", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Cuenta de origen no encontrada", Toast.LENGTH_SHORT).show();
            }
            cursorOrigen.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al realizar la transferencia", Toast.LENGTH_SHORT).show();
        } finally {
            BaseDeDatos.close();
        }
    }
    public void RegMenu(View view){
        onBackPressed();
    }
}