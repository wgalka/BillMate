package com.example.billmate;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateBill extends AppCompatActivity {

    private LinearLayout mGroupMembersLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);
        EditText mBillTotalPrice = findViewById(R.id.mBillTotalPrice);
        mBillTotalPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        mGroupMembersLinearLayout = (LinearLayout) findViewById(R.id.mGroupMembersLinearLayout);

        ArrayList<String> groupMembers = new ArrayList<>();
        groupMembers.add("pafsda@gmail.com");
        groupMembers.add("vbuwrbveyu@gmail.com");
        groupMembers.add("bruwuivew@gmail.com");

        for (int i = 0; i < groupMembers.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(groupMembers.get(i));
            mGroupMembersLinearLayout.addView(checkBox, mGroupMembersLinearLayout.getChildCount());
        }
    }

    public void saveBill() {
        return;
    }

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }
}