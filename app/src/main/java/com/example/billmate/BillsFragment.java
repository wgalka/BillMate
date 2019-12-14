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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.example.billmate.MainActivity.beginningGroup;
import static com.example.billmate.MainActivity.bills;
import static com.example.billmate.MainActivity.conection;
import static com.example.billmate.MainActivity.idDocBills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BillsFragment extends Fragment {

    private static final String TAG = BillsFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BillAdapter mBillAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> mList = new ArrayList<Bill>();

    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_bills, container, false);
        bulidRecycleView(mainView);
        swipeFragmentBills(mainView);
        loadObject();
        return mainView;
    }

    private void bulidRecycleView(View mainView) {

        mRecyclerView = mainView.findViewById(R.id.BillsRecycleView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mBillAdapter = new BillAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mBillAdapter);
        mBillAdapter.setOnItemClickListener(new BillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
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

    private void loadObject() {
        if (conection.containsKey(beginningGroup.getIdDocFirebase())) {
            mList.clear();
            for (int i = 0; i < conection.get(beginningGroup.getIdDocFirebase()).size(); i++) {
                for (int j = 0; j < idDocBills.size(); j++) {
                    Log.d(TAG, "Mapy " + conection.get(beginningGroup.getIdDocFirebase()).containsKey(idDocBills.get(j)));
                    Log.d(TAG, "MapyCon " + conection.get(beginningGroup.getIdDocFirebase()).get(idDocBills.get(j)).getBillTitle());
                    mList.add(new Bill(R.drawable.ic_format_list,
                            conection.get(beginningGroup.getIdDocFirebase()).get(idDocBills.get(j)).getBillTitle(),
                            conection.get(beginningGroup.getIdDocFirebase()).get(idDocBills.get(j)).getBillOwner(),
                            conection.get(beginningGroup.getIdDocFirebase()).get(idDocBills.get(j)).getBillTotal(),
                            conection.get(beginningGroup.getIdDocFirebase()).get(idDocBills.get(j)).getBillOwes()));
                    mBillAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Toast.makeText(getContext(), "Brawo! Nie masz zaległości", Toast.LENGTH_LONG).show();
        }
//        if (idDocBills.size() != 0) {
//            mList.clear();
//            for (int i = 0; i < idDocBills.size(); i++) {
//                mList.add(new Bill(R.drawable.ic_format_list, bills.get(idDocBills.get(i)).getBillTitle(),
//                        bills.get(idDocBills.get(i)).getBillOwner(), bills.get(idDocBills.get(i)).getBillTotal(), bills.get(idDocBills.get(i)).getBillOwes()));
//                mBillAdapter.notifyDataSetChanged();
//            }
//        } else {
//            Toast.makeText(getContext(), "Brawo! Nie masz zaległości", Toast.LENGTH_LONG).show();
//        }
    }

    private void loadingObjectBillAgain() {
        if (!idDocBills.isEmpty()) {
            for (int i = 0; i < idDocBills.size(); i++) {
                documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bills/" + idDocBills.get(i));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Bill billLocal = documentSnapshot.toObject(Bill.class);
                        bills.put(documentSnapshot.getId(), billLocal);
                        Log.d(TAG, "Dane zostały wczytane");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd wczytywania danych: " + e.toString());
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        loadObject();
                    }
                });
            }
        }
    }
}
