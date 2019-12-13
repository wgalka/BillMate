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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.billmate.itemsBean.Bill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.example.billmate.MainActivity.beginningGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateBill extends AppCompatActivity {

    private static final String TAG = CreateBill.class.getSimpleName();
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
    private IdDocForBills idDocForBills;
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
                    System.out.println("Wszystko po walidacji formularza !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
                    Toast.makeText(getApplicationContext(), "Tworzenie rachunku", Toast.LENGTH_SHORT).show();
//                    bill=new Bill();
//                    bill.setBillTitle(billTitle.getText().toString());
//                    bill.setBillOwner(user_google_information.getEmail());
//                    bill.setBillDescription(billDescription.getText().toString());
//                    bill.setBillTotal(billTotalPrice.getText().toString());
                    HashMap<String, Boolean> payers = new HashMap<>();
                    for (int i = 0; i < billPayers.size(); i++) {
                        if (billPayers.get(i).equals(user_google_information.getEmail())) {
                            payers.put(billPayers.get(i), true);
                            System.out.println(billPayers.get(i).equals(user_google_information.getEmail()) + " = " + billPayers.get(i) + " | " + user_google_information.getEmail());
                        } else {
                            payers.put(billPayers.get(i), false);
                            System.out.println(billPayers.get(i).equals(user_google_information.getEmail()) + " = " + billPayers.get(i) + " | " + user_google_information.getEmail());
                        }
                    }
//                    bill.setBillPayers(payers);
                    bill = new Bill(billTitle.getText().toString(), user_google_information.getEmail(), billDescription.getText().toString(), billTotalPrice.getText().toString(), payers, currentTime.milliseconds(), NOT_APPROVED);
                    //wysyłanie do bazy
                    collectionReference.add(bill).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Dane zostały zapisane");
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
                        }
                    });

                    System.out.println("nazwa grupy:" + beginningGroup.getNameOfGroup());
                } else {
                    Toast.makeText(getApplicationContext(), "Coś wymaga poprawy", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setGroupMembers() {
        ArrayList<String> groupMembers = beginningGroup.getMembers();
//        groupMembers.add("pafsda@gmail.com");
//        groupMembers.add("vbuwrbveyu@gmail.com");
//        groupMembers.add("bruwuivew@gmail.com");

        billPayers = beginningGroup.getMembers();

        for (int i = 0; i < groupMembers.size(); i++) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(groupMembers.get(i));
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        billPayers.add(checkBox.getText().toString());
                        System.out.println("Checkbox is true: " + billPayers);
                    } else {
                        billPayers.remove(checkBox.getText().toString());
                        System.out.println("Checkbox is false: " + billPayers);
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
//                    Toast.makeText(getApplicationContext(), "okej text jest wypełniany dla ciebie", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double price = Double.valueOf(billTotalPrice.getText().toString());
                        System.out.println("Price: " + price);
                        if (price <= 0) {
                            throw new NullPointerException("zero price");
                        }
                        eBillTotalPrice.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Tytuł rachunku musi zawierać minimum 3 znaki", Toast.LENGTH_SHORT).show();
//                        System.out.println("kolory: "+billTitle.getBackgroundTintList());
//                        billTitle.setBackgroundTintList(getResources().getColorStateList(R.color.outcome));
                        eBillTotalPrice.setText("Price value should be greater than 0");
//                        eBillTotalPrice.setTextColor(getResources().getColorStateList(R.color.outcome));
                        System.out.println("!!! Error: " + e);
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
//                    Toast.makeText(getApplicationContext(), "okej text jest wypełniany dla ciebie", Toast.LENGTH_SHORT).show();
                } else {
                    if (billDescription.getText().length() < 3) {
                        Toast.makeText(getApplicationContext(), "Description too short", Toast.LENGTH_SHORT).show();
//                        System.out.println("kolory: "+billTitle.getBackgroundTintList());
//                        billTitle.setBackgroundTintList(getResources().getColorStateList(R.color.outcome));
                        eBillDescription.setText("Description too short");
//                        eBillDescription.setTextColor(getResources().getColorStateList(R.color.outcome));
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
//                    Toast.makeText(getApplicationContext(), "okej text jest wypełniany dla ciebie", Toast.LENGTH_SHORT).show();
                } else {
                    if (billTitle.getText().length() < 3) {
                        Toast.makeText(getApplicationContext(), "Tytuł rachunku musi zawierać minimum 3 znaki", Toast.LENGTH_SHORT).show();
//                        System.out.println("kolory: "+billTitle.getBackgroundTintList());
//                        billTitle.setBackgroundTintList(getResources().getColorStateList(R.color.outcome));
                        eBillTitle.setText("Title too short");
//                        eBillTitle.setTextColor(getResources().getColorStateList(R.color.outcome));
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
            eBillTitle.setText("Title too short");
            ++isFormValid;
        }
        if (billDescription.getText().toString().length() > 2) {
            //resetowanie pola bledu
            eBillDescription.setText("");
        } else {
            eBillDescription.setText("Description too short");
            ++isFormValid;
        }
        if (checkEditTextGreaterThanZero(billTotalPrice)) {
            //resetowanie pola bledu
            eBillTotalPrice.setText("");
        } else {
            eBillTotalPrice.setText("Price should be greater than 0");
        }
        if (billPayers.size() > 0) {
            //resetowanie pola bledu
            eBillGroupMembers.setText("");
        } else {
            eBillGroupMembers.setText("Someone have to pay");
        }
        return isFormValid == 0;
    }

    private boolean checkEditTextGreaterThanZero(EditText field) {
        try {
            double price = Double.valueOf(field.getText().toString());
            System.out.println("Price: " + price);
            if (price <= 0) {
                throw new NullPointerException("zero price");
            }
            return true;
        } catch (Exception e) {

            System.out.println("!!! Error: " + e);
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

    public void showToastSaveBill() {
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

    @Override
    public void finish() {
        Intent backToBillsFragment = new Intent(this, MainActivity.class);
        setResult(R.id.nav_bills, backToBillsFragment);
        super.finish();
    }
}