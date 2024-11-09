package com.example.solaris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Acceso extends AppCompatActivity {

    EditText User, Password;
    public String usuario = "Yojans";
    public int Pass = 190503;
    private int intentosFallidos = 0; // Contador de intentos fallidos

    public static boolean acceso = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acceso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User = findViewById(R.id.Usuario);
        Password = findViewById(R.id.Password);
    }

    public void AccesoAdministrador(View view) {

        String usuarioAdim = User.getText().toString().trim();
        String passAdmin = Password.getText().toString().trim();

        if (usuarioAdim.equals(usuario) && passAdmin.equals(String.valueOf(Pass))) {
            Intent intent = new Intent(Acceso.this, Administrador.class);
            startActivity(intent);
            finish();
        } else {
            intentosFallidos++;
            if (intentosFallidos >= 3) {
                Acceso.acceso = true;
                Intent intent = new Intent(Acceso.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Eliminar la actividad actual de la pila
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                User.setText("");
                Password.setText("");
            }
        }
    }
}