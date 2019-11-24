package com.example.billmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billmate.adapter.BillAdapter;
import com.example.billmate.itemsBean.Bill;

import java.util.ArrayList;

public class BillsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Bill> examplelist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_bills, container, false);
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bills, container, false);

        examplelist = new ArrayList<Bill>();
        examplelist.add(new Bill(R.drawable.ic_format_list,"Gaz marzec","wgalka2@gamil.com","100,99 PLN","10 PLN"));

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
}
