package com.example.billmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.billmate.adapter.InviteAdapter;
import com.example.billmate.itemsBean.ItemCardView;

import java.util.ArrayList;

import static com.example.billmate.MainActivity.beginningGroup;

public class MembersFragment extends Fragment {

    private Button deleteGroup;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mInviteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView =inflater.inflate(R.layout.fragment_members, container, false);
        bulidRecycleView(mainView);
        deleteGroup = mainView.findViewById(R.id.deleteGroup);
        setDeleteGroup();

        return mainView;
    }

    private void setDeleteGroup(){
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setDataCompletion(){

    }

    private void bulidRecycleView(View mainView) {
        mRecyclerView = mainView.findViewById(R.id.group_managment);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
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
            Toast.makeText(getContext(), "Administratora nie można usunąć", Toast.LENGTH_SHORT).show();
        }
    }

}
