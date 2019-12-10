package com.example.billmate;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

public class IdDocForBills {

    private static final String TAG = IdDocForBills.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String idGroupDoc;
    private String uidUser;
    private ArrayList<String> idDocs = new ArrayList<String>();

    public IdDocForBills() {
    }

    public IdDocForBills(String idGroupDoc, String uidUser) {
        this.idGroupDoc = idGroupDoc;
        this.uidUser = uidUser;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getIdGroupDoc() {
        return idGroupDoc;
    }

    public void setIdGroupDoc(String idGroupDoc) {
        this.idGroupDoc = idGroupDoc;
    }

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

    protected void idDocUpdate(final String email) {
        db.collection("groups").document(getIdGroupDoc()).collection("bookOfAccounts").document(email).set(this, SetOptions.merge())
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
