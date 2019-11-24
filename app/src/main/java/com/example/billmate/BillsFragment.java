package com.example.billmate;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billmate.adapter.BillAdapter;
import com.example.billmate.itemsBean.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.billmate.MainActivity.beginningGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BillsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> examplelist;


    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups/"+beginningGroup.getIdDocFirebase()+"/bills");
    protected static HashMap<String, Bill> bills = new HashMap<String, Bill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_bills, container, false);
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bills, container, false);

        examplelist = new ArrayList<Bill>();
        examplelist.add(new Bill(R.drawable.ic_format_list,"Gaz marzec","wgalka2@gamil.com","100,99 PLN","10 PLN"));
        bills = downloadBillsListener(bills);
        Collection<Bill> values = bills.values();
        examplelist = new ArrayList<Bill>(values);
        buildRecycleView(mainView);
        return mainView;
    }

    public void buildRecycleView(View mainView) {
        mRecyclerView = mainView.findViewById(R.id.BillsRecycleView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new BillAdapter(examplelist);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new InviteAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Toast.makeText(getContext(), "Position: " + exampleList.get(position).getmText1(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDeleteClick(int position) {
//                removeItem(position);
//            }
//        });
    }
    private HashMap<String,Bill> downloadBillsListener(final HashMap<String,Bill> map) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Bill billLocal = documentSnapshot.toObject(Bill.class);
                    map.put(documentSnapshot.getId(),billLocal);

                }
                if (!bills.isEmpty()) {
                    //zmienić XML na dodaj grupe
                }
            }
        });
        return map;
    }
}
