package com.example.fuelmonitorapp.services;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CloudStorage {

    private final FirebaseStorage storage;
    private final StorageReference rootRef;

    public interface CloudCallback {
        void onSuccess(String downloadUrl);
        void onFailure(String error);
    }

    // ---------------------------
    // 1. CONEXIÓN AL SERVICIO
    // ---------------------------
    public CloudStorage() {
        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();
    }

    // ---------------------------
    // 2. SUBIR ARCHIVO (IMAGEN)
    // ---------------------------
    public void uploadImage(Uri fileUri, String userId, CloudCallback callback) {

        // Carpeta: profile_images/userId.jpg
        StorageReference imgRef = rootRef.child("profile_images/" + userId + ".jpg");

        UploadTask uploadTask = imgRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()))
        ).addOnFailureListener(e ->
                callback.onFailure(e.getMessage())
        );
    }

    // ---------------------------
    // 3. OBTENER URL DE DESCARGA
    // ---------------------------
    public void getDownloadUrl(String userId, CloudCallback callback) {
        StorageReference imgRef = rootRef.child("profile_images/" + userId + ".jpg");

        imgRef.getDownloadUrl()
                .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
