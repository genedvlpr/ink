package com.genedev.ink_socialmedia.StoriesUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterUtilStories extends RecyclerView.Adapter<RecyclerViewHolderStoriesUtil> {

    private Context home;

    private ArrayList<StoriesModel> storiesModelArrayList;
    private StoriesModel storiesModel = new StoriesModel();

    private String profile_img, story_background, username;

    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();;
    private AccountInfo accountInfo = new AccountInfo();

    public RecyclerViewAdapterUtilStories(Context home, ArrayList<StoriesModel> storiesModels){
        this.home = home;
        this.storiesModelArrayList = storiesModels;

    }

    @NonNull
    @Override
    public RecyclerViewHolderStoriesUtil onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.story_item, parent, false);
        return new RecyclerViewHolderStoriesUtil(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolderStoriesUtil holder, final int position) {

        //holder.btn_request.setText("Accept");

        final HelperUtils helperUtils = new HelperUtils();

        try {
            firestoreDB.collection("users")
                    .document(mAuth.getUid())
                    .collection("account_details")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    accountInfo = document.toObject(AccountInfo.class);
                                    accountInfoArrayList.add(accountInfo);
                                }
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(home, "Error loading account details.", Toast.LENGTH_SHORT).show();

        }


        try {
            if (storiesModelArrayList.size() >= 2){
                username = storiesModelArrayList.get(position).getFirst_name();
                profile_img = storiesModelArrayList.get(position).getImg_profile();
                story_background = storiesModelArrayList.get(position).getMoments_photo();

                Uri story_bg_uri = Uri.parse(story_background);
                Uri profile_uri = Uri.parse(profile_img);
                Picasso.get().load(story_bg_uri).into(holder.story_bg);

                Picasso.get().load(profile_uri).resize(40,50).into(holder.user_profile);

                holder.story_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(home, ViewStories.class);
                        int pos = holder.getAdapterPosition();
                        i.putExtra("str_name", storiesModelArrayList.get(pos).getFirst_name());
                        i.putExtra("str_bg", storiesModelArrayList.get(pos).getMoments_photo());
                        i.putExtra("str_profile", storiesModelArrayList.get(pos).getImg_profile());
                        i.putExtra("str_caption", storiesModelArrayList.get(pos).getCaption());
                        //i.putExtra("str_date", String.valueOf(storiesModelArrayList.get(position).getTimestamp()));
                        home.startActivity(i);
                    }
                });
                holder.username.setText(username);
            }
            if (storiesModelArrayList.size() == 1){
                username = storiesModelArrayList.get(0).getFirst_name();
                profile_img = storiesModelArrayList.get(0).getImg_profile();
                story_background = storiesModelArrayList.get(0).getMoments_photo();

                Uri story_bg_uri = Uri.parse(story_background);
                Uri profile_uri = Uri.parse(profile_img);

                Picasso.get().load(story_bg_uri).into(holder.story_bg);

                Picasso.get().load(profile_uri).resize(40,50).into(holder.user_profile);

                holder.username.setText(username);

                holder.story_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        Intent i = new Intent(home, ViewStories.class);
                        i.putExtra("str_name", storiesModelArrayList.get(pos).getFirst_name());
                        i.putExtra("str_bg", storiesModelArrayList.get(pos).getMoments_photo());
                        i.putExtra("str_profile", storiesModelArrayList.get(pos).getImg_profile());
                        i.putExtra("str_caption", storiesModelArrayList.get(pos).getCaption());

                        //i.putExtra("str_date", String.valueOf(storiesModelArrayList.get(0).getTimestamp()));
                        home.startActivity(i);
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(home, "Error loading story details.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return storiesModelArrayList.size();
    }
    public void clear() {
        final int size = storiesModelArrayList.size();
        storiesModelArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }
}
