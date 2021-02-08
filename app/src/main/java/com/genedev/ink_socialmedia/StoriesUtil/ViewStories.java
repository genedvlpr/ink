package com.genedev.ink_socialmedia.StoriesUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class ViewStories extends AppCompatActivity {

    private TextView caption, date, username;
    private ImageView story_img;
    private CircleImageView profile_img;
    private Uri story_bg_uri, profile_img_uri;
    private String str_name, str_bg, str_profile, str_date, str_caption, final_users;
    private RecyclerView recyclerview_friend_profile;

    private StoriesModel storiesModel;
    private ArrayList<StoriesModel> storiesModelArrayList;

    private ArrayList<Friends> friendsArrayList;
    private Friends friends = new Friends();

    private RecyclerViewAdapterUtilStoryProfiles recyclerViewAdapterUtilStoryProfiles;

    private LinearLayoutManager llm;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stroies);

        caption = findViewById(R.id.story_caption);
        username = findViewById(R.id.first_name);
        date = findViewById(R.id.story_date);

        story_img = findViewById(R.id.story_img);
        profile_img = findViewById(R.id.img_profile);

        recyclerview_friend_profile = findViewById(R.id.recyclerview_friend_profile);
        //str_date = i.getStringExtra("str_date");

        storiesModelArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();


        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        llm.setItemPrefetchEnabled(true);

        recyclerview_friend_profile.setHasFixedSize(true);
        recyclerview_friend_profile.setLayoutManager(llm);
        recyclerview_friend_profile.setNestedScrollingEnabled(false);
        recyclerview_friend_profile.setItemViewCacheSize(100);
        recyclerview_friend_profile.setDrawingCacheEnabled(true);
        recyclerview_friend_profile.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        try {
            Intent i = getIntent();
            str_name = i.getExtras().getString("str_name");
            str_bg = i.getExtras().getString("str_bg");
            str_profile = i.getExtras().getString("str_profile");
            str_caption = i.getExtras().getString("str_caption");

            Log.d("profile", str_profile+"");

            caption.setText(str_caption);
            username.setText(str_name);
            //date.setText(str_date);

            profile_img_uri = Uri.parse(str_profile);
            story_bg_uri = Uri.parse(str_bg);

            loadStories();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error loading story.", Toast.LENGTH_SHORT).show();
        }
        try {
            Picasso.get().load(str_profile).resize(40,50).into(profile_img);
            Picasso.get().load(str_bg).into(story_img);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error loading story.", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadStories(){
        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("friends")
                .document("accepted")
                .collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.exists()){
                                    //recyclerview_friend_profile.setVisibility(View.VISIBLE);

                                    friendsArrayList.clear();
                                    final String user_list_Uid =  (String) document.get("user_id");
                                    String locale = null;
                                    final_users = user_list_Uid.replace(U_id, " ");
                                    try{
                                        //locale = this.getResources().getConfiguration().locale.getCountry();
                                        //Log.d("Account not requested: ",user_list_Uid+" ");
                                        //String locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                        firestoreDB.collection("users")
                                                .document(user_list_Uid)
                                                .collection("moments")
                                                .document("moments_photo")
                                                .collection("images")
                                                .whereEqualTo("user_id", user_list_Uid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @SuppressLint("RestrictedApi")
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                                                storiesModelArrayList.clear();
                                                                storiesModel = document.toObject(StoriesModel.class);
                                                                storiesModelArrayList.add(storiesModel);
                                                                //Collections.reverse(storiesModelArrayList);
                                                                recyclerViewAdapterUtilStoryProfiles = new RecyclerViewAdapterUtilStoryProfiles(getApplicationContext(),storiesModelArrayList);
                                                                recyclerViewAdapterUtilStoryProfiles.setHasStableIds(false);
                                                                recyclerview_friend_profile.setAdapter(recyclerViewAdapterUtilStoryProfiles);
                                                                //runLayoutAnimation(recyclerView);
                                                                //recyclerView.setVisibility(View.INVISIBLE);

                                                            }
                                                        }
                                                    }
                                                });
                                    }catch (Exception e){
                                        Toast.makeText(getApplicationContext(), "Error loading story.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }
                    }
                });
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(ViewStories.this, Home.class);
        startActivity(i);
        ViewStories.this.finish();
    }
}
