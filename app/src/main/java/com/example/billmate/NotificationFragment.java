package com.example.billmate;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import static com.example.billmate.MainActivity.beginningGroup;


public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private BookOfBillAdapter mBillAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> mList = new ArrayList<Bill>();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

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
                Toast.makeText(getContext(), "New Activity", Toast.LENGTH_LONG).show();
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
                    Bill billLocal = documentSnapshot.toObject(Bill.class);
                    //dalsze przygotowania
                }
            }
        });
    }


}
