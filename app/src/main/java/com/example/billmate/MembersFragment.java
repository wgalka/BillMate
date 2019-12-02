package com.example.billmate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.billmate.MainActivity.beginningGroup;
import static com.example.billmate.MainActivity.groups;

public class MembersFragment extends Fragment {

    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private Button deleteGroup, renameGroup;
    private EditText newNameGroup;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    protected static InviteAdapter mMembersFragment;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups");
    private DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_members, container, false);
        bulidRecycleView(mainView);
        deleteGroup = mainView.findViewById(R.id.deleteGroup);
        deleteGroup.setVisibility(View.GONE);
        renameGroup = mainView.findViewById(R.id.renameGroup);
        renameGroup.setVisibility(View.GONE);
        newNameGroup = mainView.findViewById(R.id.getNewNameGroup);
        newNameGroup.setVisibility(View.GONE);
        setDeleteGroup();
        setRenameGroup();
        setDataCompletion();
        return mainView;
    }

    private void setDeleteGroup() {
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference = db.collection("groups/").document(beginningGroup.getIdDocFirebase());
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Grupa: " + beginningGroup.getNameOfGroup() + " usunięta", Toast.LENGTH_LONG).show();
                        deleteGroupFromListId(beginningGroup.getIdDocFirebase(), beginningGroup.getMembers());
                        mMembersFragment.notifyDataSetChanged();
                        //wrócić do zakładki home!
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd w usuwaniu danych: " + e.toString());
                    }
                });
            }
            //odswieżanie po usuwaniu załadowanie nowego beginingGroup
        });
    }

    private void setRenameGroup() {
        renameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newNameGroup.getVisibility() == GONE) {
                    newNameGroup.setVisibility(View.VISIBLE);
                } else if (newNameGroup.getVisibility() == VISIBLE && newNameGroup.getText().toString().trim().length() > 0) {
                    Toast.makeText(getContext(), beginningGroup.getIdDocFirebase(), Toast.LENGTH_LONG).show();
                    beginningGroup.setNameOfGroup(newNameGroup.getText().toString());
                    updateNameGroup();
                    newNameGroup.getText().clear();
                    newNameGroup.setVisibility(GONE);
                    Toast.makeText(getContext(), "Nazwa grupy zmieniona", Toast.LENGTH_LONG).show();
                } else {
                    newNameGroup.setVisibility(GONE);
                }
            }
        });
    }

    private void setDataCompletion() {
        mList.clear();
        for (int i = 0; i < beginningGroup.getSize(); i++) {
            mList.add(new ItemCardView(beginningGroup.getMembers().get(i)));
        }
        if (beginningGroup.getMembers().get(0).equals(user_google_information.getEmail())) {
            deleteGroup.setVisibility(View.VISIBLE);
            renameGroup.setVisibility(View.VISIBLE);
        }
        mMembersFragment.notifyDataSetChanged();
    }

    private void bulidRecycleView(View mainView) {
        mRecyclerView = mainView.findViewById(R.id.group_managment);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mMembersFragment = new InviteAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMembersFragment);
        mMembersFragment.setOnItemClickListener(new InviteAdapter.OnItemClickListener() {
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
        if (beginningGroup.getMembers().get(0).equals(user_google_information.getEmail())) {
            if (position != 0) {
                //if user uregulował wszystkie zobowiązania
                mList.remove(position);
                deleteOneUserFromListId(beginningGroup.getIdDocFirebase(), beginningGroup.getMembers().get(position));
                beginningGroup.removeElem(position);
                mMembersFragment.notifyItemRemoved(position);
                updateExistGroup();
            } else {
                Toast.makeText(getContext(), "Administratora nie można usunąć", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Tylko administrator może usuwać", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExistGroup() {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase());
        documentReference.update("members", beginningGroup.getMembers());
    }

    private void updateNameGroup() {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase());
        documentReference.update("nameOfGroup", beginningGroup.getNameOfGroup());
    }

    private void deleteGroupFromListId(final String id, ArrayList<String> members) {
        for (int i = 0; i < members.size(); i++) {
            documentReference = db.collection("list").document(members.get(i));
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DOC istnieje dla " + documentSnapshot.getId());
                        IdDocsForUser idDocsForUser = documentSnapshot.toObject(IdDocsForUser.class);
                        for (int i = 0; i < idDocsForUser.getSize(); i++) {
                            if (idDocsForUser.getIdDocs().get(i).equals(id)) ;
                            idDocsForUser.removeElem(i);
                        }
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

    private void deleteOneUserFromListId(final String id, String email) {
        documentReference = db.collection("list").document(email);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "DOC istnieje dla " + documentSnapshot.getId());
                    IdDocsForUser idDocsForUser = documentSnapshot.toObject(IdDocsForUser.class);
                    for (int i = 0; i < idDocsForUser.getSize(); i++) {
                        if (idDocsForUser.getIdDocs().get(i).equals(id)) ;
                        idDocsForUser.removeElem(i);
                    }
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
