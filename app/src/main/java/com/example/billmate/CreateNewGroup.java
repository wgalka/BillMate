package com.example.billmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CreateNewGroup extends AppCompatActivity {

//    private LinearLayout parentLinearLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
//        parentLinearLayout1 = (LinearLayout) findViewById(R.id.parent_linear_layout1);
    }

//    public void onAddField(View v) {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View rowView = inflater.inflate(R.layout.add_email_row, null);
//        // Add the new row before the add field button.
//        parentLinearLayout1.addView(rowView, parentLinearLayout1.getChildCount() - 2);
//    }
//
//    public void onDelete(View v) {
//        parentLinearLayout1.removeView((View) v.getParent());
//    }
}
