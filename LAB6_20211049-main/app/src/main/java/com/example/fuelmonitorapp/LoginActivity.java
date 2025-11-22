package com.example.fuelmonitorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fuelmonitorapp.services.AuthService;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> loginUsuario());

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUsuario() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.login(email, pass, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Inicio exitoso", Toast.LENGTH_SHORT).show();
                    abrirMain();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    private void abrirMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
