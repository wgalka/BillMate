package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        confirmCreateNewGroup = findViewById(R.id.nextStepCreateGroup);
        nameOfgroup = findViewById(R.id.nameNewGroup);
        setConfirmCreateNewGroup();
    }

    private void setConfirmCreateNewGroup() {
        confirmCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMembers = new Intent(CreateNewGroup.this, InviteActivity.class);
                if (nameOfgroup.getText().toString().trim().length() > 0) {
                    addMembers.putExtra(NAME_OF_GROUP, nameOfgroup.getText().toString());
                    startActivity(addMembers);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.write_something), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent backToHomeFragment = new Intent(this,MainActivity.class);
        setResult(R.id.nav_home,backToHomeFragment);
        super.onBackPressed();
    }
}
