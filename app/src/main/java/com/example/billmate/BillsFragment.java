package com.example.billmate;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.billmate.adapter.BillAdapter;
import com.example.billmate.itemsBean.Bill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import static com.example.billmate.MainActivity.beginningGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BillsFragment extends Fragment {

    private static final String GROUP_NOT_EXIST = "GROUP_NOT_EXIST";
    private final String TAG = BillsFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BillAdapter mBillAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> mList = new ArrayList<Bill>();
    private ArrayList<String> idDocBills = new ArrayList<String>();
    private HashMap<String, Bill> bills = new HashMap<String, Bill>();

    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_bills, container, false);
        bulidRecycleView(mainView);
        swipeFragmentBills(mainView);
        downloadListenerIdDocBills();
        return mainView;
    }

    private void bulidRecycleView(final View mainView) {
        mRecyclerView = mainView.findViewById(R.id.BillsRecycleView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mBillAdapter = new BillAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mBillAdapter);
        mBillAdapter.setOnItemClickListener(new BillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Snackbar.make(mainView, mList.get(position).getDocumentID(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onPayClick(int position) {
                //Toast.makeText(getContext(), "Pay Click! " + mList.get(position).getDocumentID(), Toast.LENGTH_LONG).show();
                updateBillPayers(mList.get(position).getDocumentID());
            }
        });
    }

    private void updateBillPayers(String documentID) {
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bills/" + documentID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String, Boolean> billpayers = new HashMap<String, Boolean>();
                Bill fastBill = documentSnapshot.toObject(Bill.class);
                billpayers = fastBill.getBillPayers();
                billpayers.put(user_google_information.getEmail(), true);
                documentReference.update("billPayers", billpayers);
                loadingObjectBillAgain();
            }
        });
    }

    private void swipeFragmentBills(View mainView) {
        final SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayoutBills);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadingObjectBillAgain();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void downloadListenerIdDocBills() {
        idDocBills.clear();
        mList.clear();
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bookOfAccounts/" + user_google_information.getEmail());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ((e) != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Doc_Exist");
                    idDocBills.addAll((Collection<? extends String>) documentSnapshot.get("idDocs"));
                    Set<String> set = new HashSet<String>(idDocBills);
                    idDocBills = new ArrayList<String>(set);
                    loadingObjectBillAgain();
                } else {
                    Log.d(TAG, "Doc_Not_Exist");
                    if (!beginningGroup.getNameOfGroup().equals(GROUP_NOT_EXIST)) {
                        Toast.makeText(getContext(), getString(R.string.nie_masz_zaleglosci), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void loadingObjectBillAgain() {
        if (!idDocBills.isEmpty()) {
            mList.clear();
            for (int i = 0; i < idDocBills.size(); i++) {
                documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bills/" + idDocBills.get(i));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Bill billLocal = documentSnapshot.toObject(Bill.class);
                            bills.put(documentSnapshot.getId(), billLocal);
                            mList.add(new Bill(
                                    billLocal.getBillTitle(),
                                    billLocal.getBillOwner(),
                                    billLocal.getBillPayers(),
                                    billLocal.getBillDescription(),
                                    billLocal.getBillTotal(),
                                    billLocal.getBillOwes(),
                                    billLocal.getTime(),
                                    billLocal.getDocumentID()
                            ));
                            mBillAdapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "Data_Save");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data_Error_Save" + e.toString());
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.nie_masz_zaleglosci), Toast.LENGTH_LONG).show();
        }
    }
}
