package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewGroup extends AppCompatActivity {

    private static final String TAG = CreateNewGroup.class.getSimpleName();
    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private Button confirmCreateNewGroup;
    private EditText nameOfgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
    }
}
