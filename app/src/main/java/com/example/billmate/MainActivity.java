package com.example.billmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.billmate.itemsBean.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAME_OF_GROUP = "NAME_OF_GROUP";
    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleSignInClient mGoogleSignInClient;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View headerView, floatingActionButton; //FloatingActionButton
    private ImageView avatar;
    private TextView name, email;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("groups");
    protected static User user = new User();
    protected static BeginningGroup beginningGroup = new BeginningGroup(); //if != null
    protected static HashMap<String, BeginningGroup> groups = new HashMap<String, BeginningGroup>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildLayout(savedInstanceState);
        downloadGroupListener();
    }

    private void downloadGroupListener() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                clearMenuItem();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BeginningGroup beginningGroupLocal = documentSnapshot.toObject(BeginningGroup.class);
                    for (int i = 0; i < beginningGroupLocal.getSize(); i++) {
                        if (beginningGroupLocal.getMembers().get(i).equals(user_google_information.getEmail())) {
                            beginningGroupLocal.setIdDocFirebase(documentSnapshot.getId());
                            groups.put(documentSnapshot.getId(), beginningGroupLocal);
                            createNewItem(navigationView, groups.get(documentSnapshot.getId()).getNameOfGroup(), documentSnapshot.getId());
                            beginningGroup = groups.get(documentSnapshot.getId());
                            setTitle(beginningGroup.getNameOfGroup());
                        }
                    }
                }
                if (!groups.isEmpty()) {
                    //zmienić XML na dodaj grupe
                }
            }
        });
    }

    private void buildLayout(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        avatar = headerView.findViewById(R.id.avatar);
        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MyGroupsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        if (user_google_information != null) {
            Log.d(TAG, user_google_information.getPhotoUrl().toString());
            Picasso.get().load(user_google_information.getPhotoUrl().toString()).into(avatar);
            name.setText(user_google_information.getDisplayName());
            email.setText(user_google_information.getEmail());
            user.setName(user_google_information.getDisplayName());
            user.setEmail(user_google_information.getEmail());
            user.setUid(user_google_information.getUid());
        }

        floatingActionButton = findViewById(R.id.floating_button);
        setFloatingActionButton();
    }

    private void setFloatingActionButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (navigationView.getCheckedItem().getItemId()) {
                    case R.id.nav_home:
                        createNewGroup();
                        break;
                    case R.id.nav_bills:
                        createBill();
                        break;
                    case R.id.nav_members:
                        if(beginningGroup.getMembers().get(0).equals(user_google_information.getEmail())){
                            addNewMember();
                        } else {
                            Toast.makeText(getApplicationContext(), "Tylko administrator może dodawać nowych userów", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.nav_notifications:
                        createNewItem(navigationView, "TestItem", "TestDescription");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), navigationView.getCheckedItem().toString(), Toast.LENGTH_LONG).show();
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // Management group
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyGroupsFragment()).commit();
                break;

            case R.id.nav_bills:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BillsFragment()).commit();
                break;

            case R.id.nav_members:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MembersFragment()).commit();
                break;

            case R.id.nav_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationFragment()).commit();
                break;


            // Actions group
            case R.id.nav_info:
                Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case 101:
                String title = (String) menuItem.getTitle();
                changeActualGroup((String) menuItem.getContentDescription());
                setTitle(title);
                refreshFragment();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeActualGroup(String Id) {
        beginningGroup = groups.get(Id);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(MainActivity.this, SignIn.class));
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void createNewGroup() {
        startActivity(new Intent(MainActivity.this, CreateNewGroup.class));
    }

    private void createBill() {
        startActivity(new Intent(MainActivity.this, CreateBill.class));
    }

    private void addNewMember(){
        Intent addMembers = new Intent(MainActivity.this, InviteActivity.class).putExtra(NAME_OF_GROUP,"UPDATE");
        startActivity(addMembers);
    }

    private void createNewItem(NavigationView navigationView, String nameOfGroup, String Id) {
        MenuItem menu = navigationView.getMenu().getItem(1);
        SubMenu subMenu = menu.getSubMenu();
        subMenu.add(R.id.group_flats, 101, Menu.NONE, nameOfGroup).setIcon(R.drawable.ic_firebase_logo).setContentDescription(Id);
    }

    protected void refreshFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    private void clearMenuItem() {
        MenuItem menu = navigationView.getMenu().getItem(1);
        menu.getSubMenu().clear();
    }
}