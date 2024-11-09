package com.example.solaris;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solaris.activitymenu.consultaActivity;
import com.example.solaris.activitymenu.depositoActivity;
import com.example.solaris.activitymenu.TransferActivity;

public class Menu extends AppCompatActivity {

    TextView tvmostrar;
    String Ncuenta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        tvmostrar = findViewById(R.id.tvmostrarcuenta);

        //Conexion a la base de datos
        String Usuario = getIntent().getStringExtra("EnvUsuario");
        Conexion conexion = new Conexion(this, "DBSolaris",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        //Busqueda por nombre
        Cursor fila = BaseDeDatos.rawQuery("SELECT numcuenta FROM cuenta WHERE usuario = ?", new String[]{Usuario});

        //Vericar si se encuentra el usuario
        if (fila.moveToFirst()) {
            String Ncuenta = fila.getString(0);
            tvmostrar.setText(Ncuenta);
        } else {
            tvmostrar.setText("Número de cuenta no encontrado");
        }

        fila.close();
        BaseDeDatos.close();

    }

    //Metodo deposito
    public void Depositos(View view){
        Ncuenta = tvmostrar.getText().toString();
        Intent intent = new Intent(Menu.this, depositoActivity.class);
        intent.putExtra("numCuenta", Ncuenta); // enviar el numero de cuenta Deposito
        startActivity(intent);
        //finish();
    }

    //Metodo Tranferencia
    public void Tranferencia(View view){
        Ncuenta = tvmostrar.getText().toString();
        Intent intent = new Intent(Menu.this, TransferActivity.class);
        intent.putExtra("numCuenta", Ncuenta); // enviar el numero de cuenta a Transferencia
        startActivity(intent);
    }

    //Metodo Consulta
    public void Consulta(View view){
        Ncuenta = tvmostrar.getText().toString();
        Intent intent = new Intent(Menu.this, consultaActivity.class);
        intent.putExtra("numCuenta", Ncuenta);  // enviar el numero de cuenta a Consulta
        startActivity(intent);
    }

    //Metodo para cerrar la secion (Volver a MainActivity)
    public void CerrarSecion(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setMessage("¿Desea Cerrar Seción?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Menu.this, MainActivity.class);
                    startActivity(intent);
                    finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }
}