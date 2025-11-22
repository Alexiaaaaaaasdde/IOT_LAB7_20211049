package com.example.fuelmonitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fuelmonitorapp.services.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etDni, etCorreo, etPassword;
    private Button btnRegistrar;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = new AuthService();

        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || dni.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.registerUser(nombre, dni, correo, pass, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()
                );
                finish(); // Vuelve al login
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}
