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
import com.example.billmate.itemsBean.IdDocsForUser;
import com.example.billmate.itemsBean.ItemCardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.billmate.MainActivity.beginningGroup;

public class MembersFragment extends Fragment {

    private static final String GROUP_NOT_EXIST = "GROUP_NOT_EXIST";
    private final String TAG = MembersFragment.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private Button deleteGroup, renameGroup;
    private EditText newNameGroup;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mMembersFragment;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups");
    private DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_members, container, false);
        bulidRecycleView(mainView);
        initVariables(mainView);
        if (!beginningGroup.getNameOfGroup().equals(GROUP_NOT_EXIST)) {
            setDeleteGroup();
            setRenameGroup();
            setDataCompletion();
        } else {
            Toast.makeText(getContext(), "Pusto! Uwtórz grupę!", Toast.LENGTH_LONG).show();
        }
        return mainView;
    }

    private void initVariables(View mainView) {
        deleteGroup = mainView.findViewById(R.id.deleteGroup);
        deleteGroup.setVisibility(View.GONE);
        renameGroup = mainView.findViewById(R.id.renameGroup);
        renameGroup.setVisibility(View.GONE);
        newNameGroup = mainView.findViewById(R.id.getNewNameGroup);
        newNameGroup.setVisibility(View.GONE);
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
                        //wrócić do zakładki home!
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MyGroupsFragment()).commit();
                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                        navigationView.setCheckedItem(R.id.nav_home);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data_Not_Save" + e.toString());
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
                    Toast.makeText(getContext(), getString(R.string.change_name_group), Toast.LENGTH_LONG).show();
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
                ifIdDocBillsExist(position);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.admin_operation_alert), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromDatabase(int position) {
        mList.remove(position);
        deleteOneUserFromListId(beginningGroup.getIdDocFirebase(), beginningGroup.getMembers().get(position));
        beginningGroup.removeElem(position);
        mMembersFragment.notifyItemRemoved(position);
        updateExistGroup();
    }

    private void ifIdDocBillsExist(final int position) {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bookOfAccounts/" + user_google_information.getEmail());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    removeFromDatabase(position);
                } else {
                    ArrayList idDocs = new ArrayList<String>();
                    idDocs.addAll((Collection<? extends String>) documentSnapshot.get("idDocs"));
                    if (idDocs.size() == 0) {
                        removeFromDatabase(position);
                    } else {
                        Toast.makeText(getContext(), "User ma niezapłacone rachunki.", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d(TAG, "Data_Save");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Data_not_Save" + e.toString());
            }
        });
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
                        Log.d(TAG, "Doc_Exist" + documentSnapshot.getId());
                        IdDocsForUser idDocsForUser = documentSnapshot.toObject(IdDocsForUser.class);
                        for (int i = 0; i < idDocsForUser.getSize(); i++) {
                            if (idDocsForUser.getIdDocs().get(i).equals(id)) ;
                            idDocsForUser.removeElem(i);
                            break;
                        }
                        idDocsForUser.userUpdate(documentSnapshot.getId());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Data_Not_Save" + e.toString());
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
                    Log.d(TAG, "Doc_Exist" + documentSnapshot.getId());
                    IdDocsForUser idDocsForUser = documentSnapshot.toObject(IdDocsForUser.class);
                    for (int i = 0; i < idDocsForUser.getSize(); i++) {
                        if (idDocsForUser.getIdDocs().get(i).equals(id)) ;
                        idDocsForUser.removeElem(i);
                        break;
                    }
                    idDocsForUser.userUpdate(documentSnapshot.getId());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Data_Not_Save" + e.toString());
            }
        });
    }

}
