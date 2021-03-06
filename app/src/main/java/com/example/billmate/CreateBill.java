package com.example.billmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.billmate.itemsBean.Bill;
import com.example.billmate.itemsBean.CurrentTime;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.billmate.MainActivity.beginningGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateBill extends AppCompatActivity {

    private final String TAG = CreateBill.class.getSimpleName();
    private final String NOT_APPROVED = "NOT_APPROVED";
    private ArrayList<String> billPayers;
    private EditText billTitle;
    private EditText billDescription;
    private EditText billTotalPrice;
    private EditText mBillTotalPrice;
    private Button saveBill;
    private LinearLayout mGroupMembersLinearLayout;
    private TextView eBillTitle;
    private TextView eBillDescription;
    private TextView eBillTotalPrice;
    private TextView eBillGroupMembers;
    private Bill bill;
    private CurrentTime currentTime = new CurrentTime();

    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups/" + beginningGroup.getIdDocFirebase() + "/bills");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);
        initVariables();
        setBillTitle();
        setBillDescription();
        setBillTotalPrice();
        setGroupMembers();
        setSaveBill();
    }

    private void setSaveBill() {
        saveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.create_bill_for) + beginningGroup.getNameOfGroup(), Toast.LENGTH_SHORT).show();
                    HashMap<String, Boolean> payers = new HashMap<String, Boolean>();
                    for (int i = 0; i < billPayers.size(); i++) {
                        if (billPayers.get(i).equals(user_google_information.getEmail())) {
                            payers.put(billPayers.get(i), true);
                            Log.d(TAG, billPayers.get(i).equals(user_google_information.getEmail()) + " = " + billPayers.get(i) + " | " + user_google_information.getEmail());
                        } else {
                            payers.put(billPayers.get(i), false);
                            Log.d(TAG, billPayers.get(i).equals(user_google_information.getEmail()) + " = " + billPayers.get(i) + " | " + user_google_information.getEmail());
                        }
                    }
                    bill = new Bill(billTitle.getText().toString(), user_google_information.getEmail(),
                            billDescription.getText().toString(), billTotalPrice.getText().toString(), payers, currentTime.milliseconds(), NOT_APPROVED);
                    //wysyłanie do bazy
                    collectionReference.add(bill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Data_Save");
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Data_Not_Save" + e.toString());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.check_something), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setGroupMembers() {
        ArrayList<String> groupMembers = (ArrayList<String>) beginningGroup.getMembers().clone();
        billPayers = (ArrayList<String>) beginningGroup.getMembers().clone();
        for (int i = 0; i < groupMembers.size(); i++) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(groupMembers.get(i));
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        billPayers.add(checkBox.getText().toString());
                        Log.d(TAG, getString(R.string.checkbox_true) + billPayers);
                    } else {
                        billPayers.remove(checkBox.getText().toString());
                        Log.d(TAG, getString(R.string.checkbox_false) + billPayers);
                    }
                }
            });
            mGroupMembersLinearLayout.addView(checkBox, mGroupMembersLinearLayout.getChildCount());
        }
    }

    private void setBillTotalPrice() {
        billTotalPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    try {
                        double price = Double.valueOf(billTotalPrice.getText().toString());
                        if (price <= 0) {
                            throw new NullPointerException("Zero price");
                        }
                        eBillTotalPrice.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.title_3words), Toast.LENGTH_SHORT).show();
                        eBillTotalPrice.setText(getString(R.string.price_error));
                        Log.d(TAG, e.toString());
                    }
                }
            }
        });
    }

    private void setBillDescription() {
        billDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    if (billDescription.getText().length() < 3) {
                        Toast.makeText(getApplicationContext(), getString(R.string.description_too_short), Toast.LENGTH_SHORT).show();
                        eBillDescription.setText(getString(R.string.description_too_short));
                    } else {
                        eBillDescription.setText("");
                    }
                }
            }
        });
    }

    private void setBillTitle() {
        billTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "Okej text jest wypełniany dla ciebie");
                } else {
                    if (billTitle.getText().length() < 3) {
                        Toast.makeText(getApplicationContext(), getString(R.string.title_3words), Toast.LENGTH_SHORT).show();
                        eBillTitle.setText(getString(R.string.title_toot_short));
                    } else {
                        eBillTitle.setText("");
                    }
                }
            }
        });
    }

    private boolean isFormValid() {
        int isFormValid = 0;
        if (billTitle.getText().toString().length() > 2) {
            //resetowanie pola bledu
            eBillTitle.setText("");
        } else {
            eBillTitle.setText(getString(R.string.title_3words));
            ++isFormValid;
        }
        if (billDescription.getText().toString().length() > 2) {
            //resetowanie pola bledu
            eBillDescription.setText("");
        } else {
            eBillDescription.setText(getString(R.string.description_too_short));
            ++isFormValid;
        }
        if (checkEditTextGreaterThanZero(billTotalPrice)) {
            //resetowanie pola bledu
            eBillTotalPrice.setText("");
        } else {
            eBillTotalPrice.setText(getString(R.string.price_should_be));
        }
        if (billPayers.size() > 0) {
            //resetowanie pola bledu
            eBillGroupMembers.setText("");
        } else {
            eBillGroupMembers.setText(getString(R.string.someone_have_to_pay));
        }
        return isFormValid == 0;
    }

    private boolean checkEditTextGreaterThanZero(EditText field) {
        try {
            double price = Double.valueOf(field.getText().toString());
            Log.d(TAG, getString(R.string.price) + price);
            if (price <= 0) {
                throw new NullPointerException("Zero price");
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return false;
        }
    }

    private void initVariables() {
        eBillTitle = findViewById(R.id.eBillTitle);
        eBillDescription = findViewById(R.id.eBillDescription);
        eBillTotalPrice = findViewById(R.id.eBillTotalPrice);
        eBillGroupMembers = findViewById(R.id.eBillGroupMembers);
        billTitle = findViewById(R.id.mTextBillTitle);
        billDescription = findViewById(R.id.mTextBillDescription);
        billTotalPrice = findViewById(R.id.mTextBillTotalPrice);
        saveBill = findViewById(R.id.mSaveBill);
        //Bill Total Price validation
        mBillTotalPrice = findViewById(R.id.mTextBillTotalPrice);
        mBillTotalPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        //Layout where group members were loaded
        mGroupMembersLinearLayout = (LinearLayout) findViewById(R.id.mGroupMembersLinearLayout);
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

    @Override
    public void finish() {
        Intent backToBillsFragment = new Intent(this, MainActivity.class);
        setResult(R.id.nav_bills, backToBillsFragment);
        super.finish();
    }
}