package com.example.fuelmonitorapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fuelmonitorapp.services.CloudStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private TextView txtNombre, txtCorreo;
    private Button btnCambiarFoto;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CloudStorage cloudStorage;

    private String uid;

    // Launcher para seleccionar imagen
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    subirImagen(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgPerfil = findViewById(R.id.imgPerfil);
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cloudStorage = new CloudStorage();

        uid = auth.getCurrentUser().getUid();

        cargarDatosUsuario();

        btnCambiarFoto.setOnClickListener(v -> seleccionarImagen());
    }

    private void cargarDatosUsuario() {
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        txtNombre.setText(doc.getString("nombre"));
                        txtCorreo.setText(doc.getString("correo"));

                        String foto = doc.getString("foto");
                        if (foto != null && !foto.isEmpty()) {
                            Glide.with(this).load(foto).into(imgPerfil);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show());
    }

    private void seleccionarImagen() {
        imagePicker.launch("image/*");
    }

    private void subirImagen(Uri uri) {
        cloudStorage.uploadImage(uri, uid, new CloudStorage.CloudCallback() {
            @Override
            public void onSuccess(String downloadUrl) {

                db.collection("usuarios")
                        .document(uid)
                        .update("foto", downloadUrl)
                        .addOnSuccessListener(v -> {
                            Glide.with(ProfileActivity.this).load(downloadUrl).into(imgPerfil);

                            // Mensaje obligatorio según el LAB
                            Toast.makeText(ProfileActivity.this,
                                    "Imagen subida: " + downloadUrl,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
