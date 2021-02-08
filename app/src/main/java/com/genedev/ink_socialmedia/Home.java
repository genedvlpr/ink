package com.genedev.ink_socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.genedev.ink_socialmedia.HomeFragments.FragmentFeeds;
import com.genedev.ink_socialmedia.HomeFragments.FragmentFriends;
import com.genedev.ink_socialmedia.HomeFragments.FragmentPages;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrame;
import com.genedev.ink_socialmedia.Utils.Friends;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.genedev.ink_socialmedia.Utils.RecyclerViewAdapterUtilAdd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;

public class Home extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    public Toolbar toolbar;
    private ActionBarDrawerToggle t;
    private View v;
    private TextView username, full_name;
    private HelperUtils helperUtils;
    private View view;
    private NavigationView nv;
    private Friends friends;
    private ArrayList<Friends> friendsArrayList;
    private RecyclerViewAdapterUtilAdd adapterUtil;
    private CollectionReference collectionReference;
    private CircleImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);

        ButterKnife.bind(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_feeds);

        helperUtils = new HelperUtils();
        helperUtils.setPersistence(firestoreDB);
        collectionReference = firestoreDB.collection("users");
        DrawerLayout dl = findViewById(R.id.drawer_layout);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        dl.addDrawerListener(t);
        nv = findViewById(R.id.navigationView);
        v = nv;

        friendsArrayList = new ArrayList<>();
        navigationDrawerListener();
        Log.d("Account", auth.getUid());

        setDataToViews();

        final LayoutInflater factory = getLayoutInflater();

    }

    private void emptyFields(String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }

    private void intentPass(Class c){
        Intent i = new Intent(Home.this, c);
        startActivity(i);
        Home.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {


            //case R.id.navigation_pages:
                //fragment = new FragmentPages();
                //break;

            case R.id.navigation_feeds:
                fragment = new FragmentFeeds();
                break;

            case R.id.navigation_friends:
                fragment = new FragmentFriends();
                break;

        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(android.app.Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void setDataToViews(){

        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String fname, lname, profile_img;
                                final HelperUtils helperUtils = new HelperUtils();

                                profile = findViewById(R.id.profile_image_1);
                                full_name = findViewById(R.id.full_name_profile);
                                username = findViewById(R.id.username_profile);

                                fname = (String) document.get("first_name");
                                lname = (String)  document.get("last_name");


                                try{
                                    profile_img = (String) document.get("img_profile");
                                    Uri uri, uri1;
                                    username.setText((String) document.get("email"));
                                    full_name.setText(fname+" "+lname);
                                    if (profile_img != null){
                                        uri1 = Uri.parse(profile_img);
                                        Picasso.get().load(uri1)
                                                .placeholder(R.drawable.circular_bg)
                                                .resize( 150,100)
                                                .centerCrop()
                                                .into(profile);
                                    }else{
                                        emptyFields("No profile photo has been set");
                                    }
                                }catch (Exception e){

                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void navigationDrawerListener(){
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account_profile:
                        intentPass(TimeFrame.class);
                        break;
                    case R.id.saved:
                        emptyFields("Saved: coming soon...");
                        break;
                    case R.id.groups:
                        emptyFields("Groups: coming soon...");
                        break;
                    case R.id.recent_actions:
                        emptyFields("Recent Actions: coming soon...");
                        break;
                    case R.id.nearby:
                        emptyFields("Nearby: coming soon...");
                        break;
                    case R.id.weather:
                        emptyFields("Weather: coming soon...");
                        break;
                    case R.id.help_center:
                        emptyFields("Help Center: coming soon...");
                        break;
                    case R.id.report:
                        emptyFields("Report: coming soon...");
                        break;
                    case R.id.privacy_policy:
                        emptyFields("Privacy Policy: coming soon...");
                        break;
                    case R.id.app_settings:
                        emptyFields("App Settings: coming soon...");
                        break;
                    case R.id.logout:
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        deleteCache(Home.this);
                        emptyFields("Youv'e been logged out");

                        Intent i = new Intent(getApplicationContext(), Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        Home.this.finish();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy(){
        helperUtils.setPersistence(firestoreDB);
        super.onDestroy();
    }

    @Override
    public void onPause(){
        helperUtils.setPersistence(firestoreDB);
        super.onPause();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        setDataToViews();
        helperUtils.setPersistence(firestoreDB);
    }

    @Override
    public void onResume(){
        super.onResume();
        setDataToViews();
        helperUtils.setPersistence(firestoreDB);
    }
}
