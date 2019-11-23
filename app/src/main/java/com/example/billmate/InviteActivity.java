package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class InviteActivity extends AppCompatActivity {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private Button confirmAddNewMember, finishFirstConfiguration;
    private EditText getEmailMember;
    private ArrayList<String> member = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

    }
}
