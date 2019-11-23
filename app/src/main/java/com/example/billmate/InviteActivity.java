package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.billmate.adapter.InviteAdapter;
import com.example.billmate.itemsBean.ItemCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.billmate.MainActivity.beginningGroup;

public class InviteActivity extends AppCompatActivity {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private Button confirmAddNewMember, finishFirstConfiguration;
    private EditText getEmailMember;
    private ArrayList<ItemCardView> mList = new ArrayList<ItemCardView>();
    private RecyclerView mRecyclerView;
    private InviteAdapter mInviteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        confirmAddNewMember = findViewById(R.id.addNewMember);
        finishFirstConfiguration = findViewById(R.id.finish);
        getEmailMember = findViewById(R.id.getEmailMember);
        beginningGroup.setNameOfGroup(getIntent().getExtras().getString(NAME_OF_GROUP));
        setConfirmAddNewMember();
        setFinishFirstConfiguration();
        bulidRecycleView();


    }

    private void setConfirmAddNewMember() {
        confirmAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getEmailMember.getText().toString().trim().length() > 0 && isValid(getEmailMember.getText().toString())) {
                    if (mList.size() != 0) {
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getmText1().equals(getEmailMember.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Dodano już tego usera: " + getEmailMember.getText().toString() + " do grupy: " + beginningGroup.getNameOfGroup(), Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                beginningGroup.addElem(getEmailMember.getText().toString());
                                mList.add(new ItemCardView(getEmailMember.getText().toString()));
                                mInviteAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        beginningGroup.addElem(getEmailMember.getText().toString());
                        mList.add(new ItemCardView(getEmailMember.getText().toString()));
                        mInviteAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.write_something), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFinishFirstConfiguration() {
        finishFirstConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beginningGroup.getMembers().size() > 0) {
                    Toast.makeText(getApplicationContext(), "Pozytywnie utworzono grupe: " + beginningGroup.getSize(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Dodaj przynajmniej jedną osobę do grupy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bulidRecycleView() {
        mRecyclerView = findViewById(R.id.inviteRW);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
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
        mList.remove(position);
        beginningGroup.removeElem(position);
        mInviteAdapter.notifyItemRemoved(position);
    }

    private void uploadNewGroup(){

    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
