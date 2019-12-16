package com.example.billmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billmate.adapter.BookOfBillAdapter;
import com.example.billmate.itemsBean.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static com.example.billmate.MainActivity.beginningGroup;


public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BookOfBillAdapter mBillAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> mList = new ArrayList<Bill>();
    private ArrayList<String> arraybillpayers = new ArrayList<String>();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private HashMap<String, Bill> bills = new HashMap<String, Bill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_notification, container, false);
        bulidRecycleView(mainView);
        swipeFragmentBills(mainView);
        loadingBookOfBills();
        return mainView;
    }

    private void bulidRecycleView(View mainView) {
        mRecyclerView = mainView.findViewById(R.id.NotificationRecycleView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mBillAdapter = new BookOfBillAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mBillAdapter);
        mBillAdapter.setOnItemClickListener(new BookOfBillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle extra = new Bundle();
                extra.putSerializable("objectBill", mList.get(position).getArraybillpayers());
                Intent addMembers = new Intent(getContext(), RevertBill.class)
                        .putExtra("documentID", mList.get(position).getDocumentID())
                        .putExtra("extra", extra);
                startActivityForResult(addMembers, RESULT_CANCELED);
            }
        });
    }

    private void swipeFragmentBills(View mainView) {
        final SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayoutNotification);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //loadingBills
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadingBookOfBills() {
        collectionReference = db.collection("groups/" + beginningGroup.getIdDocFirebase() +
                "/bookOfBills/" + user_google_information.getEmail() + "/bills");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ArrayList<ArrayList<String>> listapomocnicza = new ArrayList<>();
                    listapomocnicza.add((ArrayList<String>) documentSnapshot.get("payersARRAY"));
                    Log.d(TAG, "listapomocnicza " + listapomocnicza.get(0));
                    Bill billLocal = documentSnapshot.toObject(Bill.class);
                    billLocal.setArraybillpayers(listapomocnicza.get(0));
                    Log.d(TAG, "Bill pobrany do obiektu " + billLocal.getArraybillpayers().toString());
                    bills.put(documentSnapshot.getId(), billLocal);
                    mList.add(new Bill(
                            billLocal.getBillTitle(),
                            billLocal.getBillOwner(),
                            billLocal.getBillPayers(),
                            billLocal.getBillDescription(),
                            billLocal.getBillTotal(),
                            billLocal.getBillStatus(),
                            billLocal.getTime(),
                            billLocal.getDocumentID(),
                            billLocal.getArraybillpayers(),
                            billLocal.getBillOwes()));
                    mBillAdapter.notifyDataSetChanged();
                }
                Log.d(TAG, "Dane zostały wczytane");
            }
        });
    }


}
