package com.example.fuelmonitorapp.services;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final OkHttpClient http;

    private final String BASE_URL = "http://192.168.1.96:8080/registro";

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public AuthService() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        http = new OkHttpClient();
    }

    // CALLBACK
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }

    // LOGIN
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(r -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // RECUPERAR PASSWORD
    public void resetPassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // LOGOUT
    public void logout() {
        auth.signOut();
    }

    // REGISTRO (CON MICROSERVICIO)
    public void registerUser(String name, String dni, String email, String password, AuthCallback callback) {

        try {
            // JSON que enviamos al microservicio
            JSONObject json = new JSONObject();
            json.put("dni", dni);
            json.put("correo", email);

            RequestBody body = RequestBody.create(JSON, json.toString());

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .build();

            http.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure("No se pudo conectar con el microservicio.");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if (response.isSuccessful()) {
                        // Microservicio validó → crear usuario en Firebase
                        crearUsuarioFirebase(name, dni, email, password, callback);
                    } else {
                        // Microservicio devolvió error
                        String error = response.body().string();
                        callback.onFailure(error);
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure("Error al construir JSON");
        }
    }

    // CREAR USUARIO EN FIREBASE
    private void crearUsuarioFirebase(String name, String dni, String email,
                                      String password, AuthCallback callback) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        callback.onFailure("Usuario creado pero UID nulo.");
                        return;
                    }

                    String uid = user.getUid();

                    // Datos para Firestore
                    UserData data = new UserData(name, dni, email);

                    db.collection("usuarios")
                            .document(uid)
                            .set(data)
                            .addOnSuccessListener(v -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // OBJETO PARA GUARDAR EN FIRESTORE
    public static class UserData {
        public String nombre;
        public String dni;
        public String correo;

        public UserData() { }

        public UserData(String nombre, String dni, String correo) {
            this.nombre = nombre;
            this.dni = dni;
            this.correo = correo;
        }
    }
}
