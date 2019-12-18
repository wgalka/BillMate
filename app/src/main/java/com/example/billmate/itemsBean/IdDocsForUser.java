package com.example.billmate.itemsBean;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

public class IdDocsForUser {

    private static final String TAG = IdDocsForUser.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("list");

    private ArrayList<String> idDocs = new ArrayList<String>();

    public ArrayList<String> getIdDocs() {
        return idDocs;
    }

    public void setIdDocs(ArrayList<String> idDocs) {
        this.idDocs = idDocs;
    }

    public void addElem(String member) {
        this.idDocs.add(member);
    }

    public void removeElem(int position) {
        this.idDocs.remove(position);
    }

    public int getSize() {
        return idDocs.size();
    }

    public void userUpdate(String email) {
        db.collection("list").document(email).set(this, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Dane zostały zapisane");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
                    }
                });
    }
}
