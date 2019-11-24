package com.example.billmate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateBill extends AppCompatActivity {

    private EditText billTitle;
    private EditText billDescription;
    private EditText billTotalPrice;
    private Button saveBill;
    private LinearLayout mGroupMembersLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);
        EditText mBillTotalPrice = findViewById(R.id.mTextBillTotalPrice);
        //Bill Total Price validation
        mBillTotalPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        billTitle = findViewById(R.id.mTextBillTitle);
        billTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
//                    Toast.makeText(getApplicationContext(), "okej text jest wypełniany dla ciebie", Toast.LENGTH_SHORT).show();
                }else{
                    if(billTitle.getText().length()<3){
                        Toast.makeText(getApplicationContext(), "Tytuł rachunku musi zawierać minimum 3 znaki", Toast.LENGTH_SHORT).show();
//                        System.out.println("kolory: "+billTitle.getBackgroundTintList());
//                        billTitle.setBackgroundTintList(getResources().getColorStateList(R.color.outcome));
                    } else {
//                        Toast.makeText(getApplicationContext(), billTitle.getText(), Toast.LENGTH_SHORT).show();
//                        billTitle.setBackgroundTintList(getResources().getColorStateList(R.color.income));
                    }
                }
            }
        });
        billDescription = findViewById(R.id.mTextBillDescription);
        billTotalPrice = findViewById(R.id.mTextBillTotalPrice);
        saveBill = findViewById(R.id.mSaveBill);
        //Layout where group members were loaded
        mGroupMembersLinearLayout = (LinearLayout) findViewById(R.id.mGroupMembersLinearLayout);

        ArrayList<String> groupMembers = new ArrayList<>();
        groupMembers.add("pafsda@gmail.com");
        groupMembers.add("vbuwrbveyu@gmail.com");
        groupMembers.add("bruwuivew@gmail.com");

        final ArrayList<String> billPayers = groupMembers;

        for (int i = 0; i < groupMembers.size(); i++) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(groupMembers.get(i));
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        billPayers.add(checkBox.getText().toString());
                        System.out.println("Checkbox is true: "+billPayers);
                    } else {
                        billPayers.remove(checkBox.getText().toString());
                        System.out.println("Checkbox is false: "+billPayers);
                    }
                }
            });
            mGroupMembersLinearLayout.addView(checkBox, mGroupMembersLinearLayout.getChildCount());
        }

        saveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), billTitle.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveBill() {
        Toast.makeText(getApplicationContext(), getString(R.string.write_something), Toast.LENGTH_SHORT).show();
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