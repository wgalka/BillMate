package com.example.billmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.billmate.adapter.InviteAdapter;
import com.example.billmate.itemsBean.ItemCardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.billmate.MainActivity.beginningGroup;

public class InviteActivity extends AppCompatActivity {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private Button confirmAddNewMember, finishFirstConfiguration;
    private EditText getEmailMember;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mInviteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups");
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        confirmAddNewMember = findViewById(R.id.addNewMember);
        finishFirstConfiguration = findViewById(R.id.finish);
        getEmailMember = findViewById(R.id.getEmailMember);
        setConfirmAddNewMember();
        setFinishFirstConfiguration();
        bulidRecycleView();
        ifUpdateGroup(getIntent().getExtras().getString(NAME_OF_GROUP));
    }

    private void ifUpdateGroup(String action){
        if(action.equals("UPDATE")){

        } else {
            prepareObjectGroup();
        }
    }

    private void setConfirmAddNewMember() {
        confirmAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getEmailMember.getText().toString().trim().length() > 0 && isValid(getEmailMember.getText().toString())) {
                    if (mList.size() != 0) {
                        int isDuplicated = 0;
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getmText1().equals(getEmailMember.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Dodano już tego usera: " + getEmailMember.getText().toString() + " do grupy: " + beginningGroup.getNameOfGroup(), Toast.LENGTH_SHORT).show();
                                isDuplicated = 1;
                                break;
                            }
                        }
                        if (isDuplicated == 0) {
                            beginningGroup.addElem(getEmailMember.getText().toString());
                            mList.add(new ItemCardView(getEmailMember.getText().toString()));
                            mInviteAdapter.notifyDataSetChanged();
                            getEmailMember.getText().clear();
                        }
                    } else {
                        beginningGroup.addElem(getEmailMember.getText().toString());
                        mList.add(new ItemCardView(getEmailMember.getText().toString()));
                        mInviteAdapter.notifyDataSetChanged();
                        getEmailMember.getText().clear();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.write_something), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFinishFirstConfiguration() {
        finishFirstConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beginningGroup.getMembers().size() > 1) {
                    Toast.makeText(getApplicationContext(), "Pozytywnie utworzono grupe: " + beginningGroup.getSize(), Toast.LENGTH_SHORT).show();
                    beginningGroup.setIdDocFirebase(null);
                    uploadNewGroup();
                    beginningGroup.getMembers().clear();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Dodaj przynajmniej jedną osobę do grupy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bulidRecycleView() {
        mRecyclerView = findViewById(R.id.inviteRW);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mInviteAdapter = new InviteAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mInviteAdapter);
        mInviteAdapter.setOnItemClickListener(new InviteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    private void removeItem(int position) {
        if (position != 0) {
            mList.remove(position);
            beginningGroup.removeElem(position);
            mInviteAdapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(getApplicationContext(), "Administratora nie można usunąć", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNewGroup() {
        collectionReference.add(beginningGroup).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Dane zostały zapisane");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
            }
        });
    }

    private void updateExistGroup() {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase());
        documentReference.update("members", beginningGroup.getMembers());
    }

    private void prepareObjectGroup() {
        beginningGroup.getMembers().clear();
        beginningGroup.setNameOfGroup(getIntent().getExtras().getString(NAME_OF_GROUP));
        beginningGroup.addElem(user_google_information.getEmail());
        mList.add(new ItemCardView(user_google_information.getEmail()));
    }

    static boolean isValid(String email) {
        String regex = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$";
        return email.matches(regex);
    }
}
