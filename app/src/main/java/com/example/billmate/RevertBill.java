package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.billmate.MainActivity.beginningGroup;

public class RevertBill extends AppCompatActivity {

    private final String TAG = RevertBill.class.getSimpleName();
    private ArrayList<String> arraybillpayers;
    private String documentID;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout mMembersLinearLayout;
    private Button revertBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revert_bill);
        initVariables();
        setGroupMembers();
    }

    private void setGroupMembers() {
        arraybillpayers.add("przykladowymail@gmail.com");
        for (int i = 0; i < arraybillpayers.size(); i++) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(arraybillpayers.get(i));
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    System.out.println("nazwa grupy:" + beginningGroup.getMembers());
                    if (isChecked) {
                        arraybillpayers.add(checkBox.getText().toString());
                        System.out.println("Checkbox is true: " + arraybillpayers);
                    } else {
                        arraybillpayers.remove(checkBox.getText().toString());
                        System.out.println("Checkbox is false: " + arraybillpayers);
                    }
                }
            });
            mMembersLinearLayout.addView(checkBox, mMembersLinearLayout.getChildCount());
        }
    }

    private void setRevertBill(){

    }

    private void initVariables() {
        mMembersLinearLayout = (LinearLayout) findViewById(R.id.mMembersLinearLayout);
        revertBill = findViewById(R.id.mRevertBill);
        Bundle extra = getIntent().getBundleExtra("extra");
        arraybillpayers = (ArrayList<String>) extra.getSerializable("objectBill");
        documentID = getIntent().getExtras().getString("documentID");
        deleteOwnerFromList();
    }

    private void deleteOwnerFromList() {
        for (int i = 0; i < arraybillpayers.size(); i++) {
            if(user_google_information.getEmail().equals(arraybillpayers.get(i))){
                arraybillpayers.remove(i);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent backToHomeFragment = new Intent(this, MainActivity.class);
        setResult(R.id.nav_notifications, backToHomeFragment);
        super.onBackPressed();
    }
}
