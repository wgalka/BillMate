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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.billmate.MainActivity.beginningGroup;

public class MembersFragment extends Fragment {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private Button deleteGroup, renameGroup;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mInviteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_members, container, false);
        bulidRecycleView(mainView);
        deleteGroup = mainView.findViewById(R.id.deleteGroup); deleteGroup.setVisibility(View.GONE);
        renameGroup = mainView.findViewById(R.id.renameGroup); renameGroup.setVisibility(View.GONE);
        setDeleteGroup();
        setRenameGroup();
        setDataCompletion();
        return mainView;
    }

    private void setDeleteGroup() {
        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), beginningGroup.getIdDocFirebase(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setRenameGroup() {
        renameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), beginningGroup.getIdDocFirebase(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDataCompletion() {
        mList.clear();
        for (int i = 0; i < beginningGroup.getSize(); i++) {
            mList.add(new ItemCardView(beginningGroup.getMembers().get(i)));
        }
        if(beginningGroup.getMembers().get(0).equals(user_google_information.getEmail())){
            deleteGroup.setVisibility(View.VISIBLE); renameGroup.setVisibility(View.VISIBLE);
        }
        mInviteAdapter.notifyDataSetChanged();
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
        if(beginningGroup.getMembers().get(0).equals(user_google_information.getEmail())){
            if (position != 0) {
                //if user uregulował wszystkie zobowiązania
                mList.remove(position);
                beginningGroup.removeElem(position);
                mInviteAdapter.notifyItemRemoved(position);
            } else {
                Toast.makeText(getContext(), "Administratora nie można usunąć", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Tylko administrator może usuwać", Toast.LENGTH_SHORT).show();
        }

    }

}
