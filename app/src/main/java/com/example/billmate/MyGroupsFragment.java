package com.example.billmate;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.billmate.itemsBean.Bill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.example.billmate.MainActivity.beginningGroup;
import static com.example.billmate.itemsBean.Bill.round;


public class MyGroupsFragment extends Fragment {

    private static final String TAG = MyGroupsFragment.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private View goToFragmentBills;
    private TextView other_own_you, you_own_other, bilans;
    private ArrayList<String> idDocBills = new ArrayList<String>();
    private HashMap<String, Bill> bills = new HashMap<String, Bill>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        initVariables(mainView);
        swipeFragmentGroups(mainView);
        if (beginningGroup.getNameOfGroup() != null) {
            downloadListenerIdDocBills();
        }
        Snackbar.make(goToFragmentBills, "Make some dish!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        return mainView;
    }

    private void swipeFragmentGroups(View mainView) {
        final SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayoutGroups);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (beginningGroup.getNameOfGroup() != null) {
                    downloadListenerIdDocBills();
                }
                clearTextView();
                loadingObjectBillAgain();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initVariables(View mainView) {
        goToFragmentBills = mainView.findViewById(R.id.go_to_bills);
        other_own_you = mainView.findViewById(R.id.other_own_you);
        you_own_other = mainView.findViewById(R.id.you_own_other);
        bilans = mainView.findViewById(R.id.bilans);
        setGoToFragmentBills();
    }

    private void clearTextView() {
        other_own_you.setText("0");
        you_own_other.setText("0");
        bilans.setText("0");
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

    private void calculateBilans(String owes, HashMap<String, Boolean> paid, int size, String whoBuy) {
        if (whoBuy.equals(user_google_information.getEmail())) {
            double result = 0.0;
            for (String key : paid.keySet()) {
                if (paid.get(key) == false) {
                    result += Double.parseDouble(owes);
                }
            }
            String previousTEXT = (String) other_own_you.getText();
            double previous = Double.parseDouble(previousTEXT);
            bilans(true, result);
            result += previous;
            result = round(result, 2);
            other_own_you.setText(String.valueOf(result));
        } else {
            double result2 = 0.0;
            String previousTEXT = (String) you_own_other.getText();
            double previous = Double.parseDouble(previousTEXT);
            for (String key : paid.keySet()) {
                if (key.equals(user_google_information.getEmail()) && paid.get(key) == false) {
                    result2 += Double.parseDouble(owes);
                }
            }
            bilans(false, result2);
            result2 += previous;
            result2 = round(result2, 2);
            you_own_other.setText(String.valueOf(result2));
        }
    }

    private void bilans(Boolean payer, double amount) {
        String previous = (String) bilans.getText();
        double addressed = Double.parseDouble(previous);
        if (payer == true) {
            addressed += amount;
        } else {
            addressed -= amount;
        }
        addressed = round(addressed, 2);
        bilans.setText(String.valueOf(addressed));
    }

    private void downloadListenerIdDocBills() {
        idDocBills.clear();
        documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bookOfAccounts/" + user_google_information.getEmail());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ((e) != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Doc istnieje");
                    idDocBills.addAll((Collection<? extends String>) documentSnapshot.get("idDocs"));
                    Set<String> set = new HashSet<String>(idDocBills);
                    idDocBills = new ArrayList<String>(set);
                    loadingObjectBillAgain();
                } else {
                    Log.d(TAG, "Doc nie istnieje");
                    //Toast.makeText(getContext(), "Brawo! Nie masz zaległości", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadingObjectBillAgain() {
        if (!idDocBills.isEmpty()) {
            for (int i = 0; i < idDocBills.size(); i++) {
                documentReference = db.document("groups/" + beginningGroup.getIdDocFirebase() + "/bills/" + idDocBills.get(i));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Bill billLocal = documentSnapshot.toObject(Bill.class);
                        bills.put(documentSnapshot.getId(), billLocal);
                        calculateBilans(billLocal.getBillOwes(), billLocal.getBillPayers(), billLocal.getBillPayers().size(), billLocal.getBillOwner());
                        Log.d(TAG, "Dane zostały wczytane");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd wczytywania danych: " + e.toString());
                    }
                });
            }
        } else {
            //Toast.makeText(getContext(), "Brawo! Nie masz zaległości", Toast.LENGTH_LONG).show();
        }
    }
}
