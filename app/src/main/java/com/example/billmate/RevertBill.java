package com.example.billmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.billmate.itemsBean.Bill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.billmate.MainActivity.beginningGroup;

public class RevertBill extends AppCompatActivity {

    private final String TAG = RevertBill.class.getSimpleName();
    private final String NOT_APPROVED = "NOT_APPROVED";
    private ArrayList<String> arraybillpayers = new ArrayList<>();
    private ArrayList<String> allPayers;
    private String documentID, billOwes, billStatus, billTotal, billDescription, billOwner, billTitle;
    private long time;
    private int sizeArrayListCopy;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private LinearLayout mMembersLinearLayout;
    private Button revertBill, mDeleteBill;
    private HashMap<String, Boolean> payers = new HashMap<String, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revert_bill);
        initVariables();
        setGroupMembers();
        setRevertBill();
    }

    private void setGroupMembers() {
        for (int i = 0; i < allPayers.size(); i++) {
            if (!allPayers.get(i).equals(user_google_information.getEmail())) {
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setText(allPayers.get(i));
                checkBox.setChecked(false);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            arraybillpayers.add(checkBox.getText().toString());
                            Log.d(TAG, "Checkbox is true: " + arraybillpayers);
                        } else {
                            arraybillpayers.remove(checkBox.getText().toString());
                            Log.d(TAG, "Checkbox is false: " + arraybillpayers);
                        }
                    }
                });
                mMembersLinearLayout.addView(checkBox, mMembersLinearLayout.getChildCount());
            }
        }
    }

    private void setRevertBill() {
        revertBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Checkbox grupa: " + arraybillpayers.toString());
                if (sizeArrayListCopy == 1 && arraybillpayers.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Musisz wybrać przynajmniej jedną osobę", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < allPayers.size(); i++) {
                        if (allPayers.get(i).equals(user_google_information.getEmail())) {
                            payers.put(allPayers.get(i), true);
                        } else {
                            if (arraybillpayers.contains(allPayers.get(i))) {
                                payers.put(allPayers.get(i), false);
                            } else {
                                payers.put(allPayers.get(i), true);
                            }
                        }

                    }
                    Log.d(TAG, "Nowa lista osob ktore placą: " + payers.toString());
                    //updateBookOfBillPayers(documentID);
                }
                //tworzenie obiektu (zmiana hashmapy dla usera true/false
                //wysyłanie nowego obiektu
                //usuwanie z bookofbills
            }
        });
    }

    private void setmDeleteBill() {
        mDeleteBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookOfBill(documentID);
            }
        });
    }

    private void updateBookOfBillPayers(String documentID) {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bookOfBills/" + user_google_information.getEmail() + "/bills/" + documentID);
        payers.put(user_google_information.getEmail(), true);
        documentReference.update("billPayers", payers);
        finish();
    }

    private void deleteBookOfBill(String documentID) {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bookOfBills/" + user_google_information.getEmail() + "/bills/" + documentID);
        documentReference.delete();
        finish();
    }

    private void initVariables() {
        mMembersLinearLayout = (LinearLayout) findViewById(R.id.mMembersLinearLayout);
        revertBill = findViewById(R.id.mRevertBill);
        mDeleteBill = findViewById(R.id.mDeleteBill);
        Bundle extra = getIntent().getBundleExtra("extra");
//        arraybillpayers = (ArrayList<String>) extra.getSerializable("objectBill");
        allPayers = (ArrayList<String>) ((ArrayList<String>) extra.getSerializable("objectBill")).clone();
        documentID = getIntent().getExtras().getString("documentID");
        billOwes = getIntent().getExtras().getString("billOwes");
        billStatus = getIntent().getExtras().getString("billStatus");
        billTotal = getIntent().getExtras().getString("billTotal");
        billDescription = getIntent().getExtras().getString("billDescription");
        billOwner = getIntent().getExtras().getString("billOwner");
        billTitle = getIntent().getExtras().getString("billTitle");
        time = getIntent().getExtras().getLong("time");
//        deleteOwnerFromList();
        setmDeleteBill();
    }

    private void deleteOwnerFromList() {
        for (int i = 0; i < arraybillpayers.size(); i++) {
            if (user_google_information.getEmail().equals(arraybillpayers.get(i))) {
                arraybillpayers.remove(i);
                break;
            }
        }
        sizeArrayListCopy = arraybillpayers.size();
    }

    @Override
    public void finish() {
        Intent backToBillsFragment = new Intent(this, MainActivity.class);
        setResult(R.id.nav_notifications, backToBillsFragment);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Intent backToHomeFragment = new Intent(this, MainActivity.class);
        setResult(R.id.nav_notifications, backToHomeFragment);
        super.onBackPressed();
    }
}
