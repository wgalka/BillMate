package com.example.billmate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.billmate.MainActivity.bills;
import static com.example.billmate.MainActivity.idDocBills;


public class MyGroupsFragment extends Fragment {

    private static final String TAG = MyGroupsFragment.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private View goToFragmentBills;
    private TextView other_own_you, you_own_other, bilans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        initVariables(mainView);
        calculateBilans();
        return mainView;
    }

    private void initVariables(View mainView) {
        goToFragmentBills = mainView.findViewById(R.id.go_to_bills);
        other_own_you = mainView.findViewById(R.id.other_own_you);
        you_own_other = mainView.findViewById(R.id.you_own_other);
        bilans = mainView.findViewById(R.id.bilans);
        setGoToFragmentBills();
    }

    private void setGoToFragmentBills() {
        goToFragmentBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BillsFragment()).commit();
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_bills);
            }
        });
    }

    private void calculateBilans() {
        //other_own_you.setText(bills.get(idDocBills.get(0)).getBillOwes());
        //ArrayList i HashMap observable
    }

}
