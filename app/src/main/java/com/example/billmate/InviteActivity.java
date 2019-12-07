package com.example.billmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.billmate.MainActivity.beginningGroup;
import static com.example.billmate.MainActivity.groups;
import static com.example.billmate.MembersFragment.mMembersFragment;

public class InviteActivity extends AppCompatActivity {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private static final String TAG = InviteActivity.class.getSimpleName();
    private Button confirmAddNewMember, finishFirstConfiguration;
    private EditText getEmailMember;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mInviteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        confirmAddNewMember = findViewById(R.id.addNewMember);
        finishFirstConfiguration = findViewById(R.id.finish);
        getEmailMember = findViewById(R.id.getEmailMember);
        setConfirmAddNewMember();
        bulidRecycleView();
        ifUpdateGroup(getIntent().getExtras().getString(NAME_OF_GROUP));
    }

    private void ifUpdateGroup(String action) {
        if (action.equals("UPDATE")) {
            setFinishUpdateConfiguration();
        } else {
            setFinishFirstConfiguration();
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
                    //oneUserAddToListId(beginningGroup.getIdDocFirebase(),mList);
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

    private void setFinishUpdateConfiguration() {
        finishFirstConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!beginningGroup.getNameOfGroup().equals("GROUP_NOT_EXIST")) {
                    if (groups.get(beginningGroup.getIdDocFirebase()).getSize() + beginningGroup.getSize() > groups.get(beginningGroup.getIdDocFirebase()).getSize() && mList.size() > 0) {
                        updateExistGroup();
                        oneUserAddToListId(beginningGroup.getIdDocFirebase(), mList);
                        finish();
                        //refresh mlist
                        mMembersFragment.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Dodaj przynajmniej jedną osobę do grupy", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Uwtórz grupę w zakładce HOME", Toast.LENGTH_SHORT).show();
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
        collectionReference = db.collection("groups");
        collectionReference.add(beginningGroup).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Dane zostały zapisane");
                oneUserAddToListId(documentReference.getId(), mList);
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
        documentReference.update("size", beginningGroup.getSize());
    }

    private void oneUserAddToListId(final String id, ArrayList<ItemCardView> members) {
        for (int i = 0; i < members.size(); i++) {
            documentReference = db.collection("list").document(members.get(i).getmText1());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DOC istnieje dla " + documentSnapshot.getId());
                        IdDocsForUser idDocsForUser = documentSnapshot.toObject(IdDocsForUser.class);
                        idDocsForUser.addElem(id);
                        idDocsForUser.userUpdate(documentSnapshot.getId());
                    } else {
                        Log.d(TAG, "DOC nie istnieje dla " + documentSnapshot.getId());
                        IdDocsForUser idDocsForUser = new IdDocsForUser();
                        idDocsForUser.addElem(id);
                        idDocsForUser.userUpdate(documentSnapshot.getId());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
                }
            });
        }
    }

    private void prepareObjectGroup() {
        beginningGroup.getMembers().clear();
        beginningGroup.setNameOfGroup(getIntent().getExtras().getString(NAME_OF_GROUP));
        beginningGroup.addElem(user_google_information.getEmail());
        mList.add(new ItemCardView(user_google_information.getEmail()));
    }

    static boolean isValid(String email) {
//        String regex = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$"; // tylko gmail i google mail
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"; // wszystkie domeny
        return email.matches(regex);
    }
}
