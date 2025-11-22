package com.example.fuelmonitorapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fuelmonitorapp.services.CloudStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 200;

    private ImageView imgPerfil;
    private TextView txtNombre, txtCorreo;
    private Button btnCambiarFoto;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CloudStorage cloudStorage;

    private String uid;

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

                        String fotoUrl = doc.getString("foto");
                        if (fotoUrl != null) {
                            Glide.with(this).load(fotoUrl).into(imgPerfil);
                        }
                    }
                });
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imgUri = data.getData();
            subirImagen(imgUri);
        }
    }

    private void subirImagen(Uri uri) {
        cloudStorage.uploadImage(uri, uid, new CloudStorage.CloudCallback() {
            @Override
            public void onSuccess(String downloadUrl) {

                // Guardar en Firestore
                db.collection("usuarios")
                        .document(uid)
                        .update("foto", downloadUrl)
                        .addOnSuccessListener(a -> {

                            // Mostrar imagen
                            Glide.with(ProfileActivity.this).load(downloadUrl).into(imgPerfil);

                            // TOAST obligatorio
                            Toast.makeText(ProfileActivity.this,
                                    "Imagen subida: " + downloadUrl,
                                    Toast.LENGTH_LONG).show();
                        });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
