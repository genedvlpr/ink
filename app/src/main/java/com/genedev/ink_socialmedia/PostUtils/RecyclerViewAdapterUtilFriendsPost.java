package com.genedev.ink_socialmedia.PostUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.genedev.ink_socialmedia.Comments;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionAngry;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionCry;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionHaha;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionHeart;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionLike;
import com.genedev.ink_socialmedia.PostUtils.ReactionUtils.ReactionWow;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.FriendAcceptedModel;
import com.genedev.ink_socialmedia.Utils.Friends;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterUtilFriendsPost extends RecyclerView.Adapter<RecyclerViewHolderPostUtil>  {

    private Context home;

    private ArrayList<PostViewModel> postViewModelArrayList;
    private PostViewModel postViewModel = new PostViewModel();

    private ArrayList<ImageViewModel> imageViewModelArrayList = new ArrayList<>();
    private ImageViewModel imageViewModel = new ImageViewModel();

    private ArrayList<Friends> friendsArrayList = new ArrayList<>();
    private Friends friends = new Friends();

    private ArrayList<ReactionAngry> reactionAngryArrayList = new ArrayList<>();
    private ReactionAngry reactionAngry = new ReactionAngry();

    private ArrayList<ReactionCry> reactionCryArrayList = new ArrayList<>();
    private ReactionCry reactionCry = new ReactionCry();

    private ArrayList<ReactionHaha> reactionHahaArrayList = new ArrayList<>();
    private ReactionHaha reactionHaha = new ReactionHaha();

    private ArrayList<ReactionHeart> reactionHeartArrayList = new ArrayList<>();
    private ReactionHeart reactionHeart = new ReactionHeart();

    private ArrayList<ReactionLike> reactionLikeArrayList = new ArrayList<>();
    private ReactionLike reactionLike = new ReactionLike();

    private ArrayList<ReactionWow> reactionWowArrayList = new ArrayList<>();
    private ReactionWow reactionWow = new ReactionWow();

    private ArrayList<ReactionCountModel> reactionCountModelArrayList;
    private ReactionCountModel reactionCountModel = new ReactionCountModel();

    private RecyclerViewAdapterPostItemImages image_list;

    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo = new AccountInfo();

    private RecyclerViewHolderPostUtil holderUtil;
    private ImageButton show_friend_requests;
    private String fname, lname, req_id;
    private String doc_name, profile;
    private boolean isSelected = false;

    private ArrayList<FriendAcceptedModel> friendAcceptedModelArrayList;
    private FriendAcceptedModel friendAcceptedModel = new FriendAcceptedModel();

    private StringBuilder all_reaction_summary = new StringBuilder();

    private String u_id, profile_url;
    public RecyclerViewAdapterUtilFriendsPost(Context home, ArrayList<PostViewModel> postViewModels){
        this.home = home;
        this.postViewModelArrayList = postViewModels;

    }

    @NonNull
    @Override
    public RecyclerViewHolderPostUtil onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.post_item, parent, false);

        return new RecyclerViewHolderPostUtil(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolderPostUtil holder, final int position) {
        holderUtil = holder;
        final HelperUtils helperUtils = new HelperUtils();

        @SuppressLint("WrongConstant")
        LinearLayoutManager hs_linearLayout = new LinearLayoutManager(home, LinearLayoutManager.HORIZONTAL, false);
        hs_linearLayout.setItemPrefetchEnabled(true);
        holder.post_item_recycler_view.setLayoutManager(hs_linearLayout);
        holder.post_item_recycler_view.setHasFixedSize(true);
        holder.post_item_recycler_view.setItemViewCacheSize(100);
        holder.post_item_recycler_view.setDrawingCacheEnabled(true);
        holder.post_item_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        holder.username.setText(postViewModelArrayList.get(position).getFirst_name()+" "+postViewModelArrayList.get(position).getLast_name());
        holder.date.setText(String.valueOf(postViewModelArrayList.get(position).getDate()));
        holder.time.setText(String.valueOf(postViewModelArrayList.get(position).getTime()));
        holder.caption.setText(postViewModelArrayList.get(position).getCaption());

        Picasso.get().load(postViewModelArrayList.get(position).getImg_profile()).fit()
                .centerCrop().into(holder.user_profile);

        reactionCountModelArrayList = new ArrayList<>();

        accountInfoArrayList = new ArrayList<>();
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

        try {
            friendAcceptedModelArrayList = new ArrayList<>();
            firestoreDB.collection("users")
                    .document(U_id)
                    .collection("friends")
                    .document("accepted")
                    .collection("users")
                    //.whereEqualTo("requester_id", mAuth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                                    String requseted_users_alter = (String) querySnapshot.get("user_id");

                                    //Log.d("Account not requested: ", requseted_users_alter + " ");

                                    final String locale = home.getResources().getConfiguration().locale.getCountry();
                                    firestoreDB.collection("users")
                                            .document(requseted_users_alter)
                                            .collection("account_details")
                                            .whereEqualTo("user_id", requseted_users_alter)
                                            .whereEqualTo("country_code", locale)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                                            friendAcceptedModelArrayList = new ArrayList<>();
                                                            friendAcceptedModel = document.toObject(FriendAcceptedModel.class);

                                                            friendAcceptedModelArrayList.add(friendAcceptedModel);

                                                            if (friendAcceptedModelArrayList.size() == 1){
                                                                u_id = friendAcceptedModelArrayList.get(0).getUser_id();
                                                            }
                                                            else {
                                                                u_id = friendAcceptedModelArrayList.get(position).getUser_id();
                                                            }
                                                            firestoreDB.collection("users")
                                                                    .document(u_id)
                                                                    .collection("account_details")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                    friendsArrayList = new ArrayList<>();
                                                                                    friends = document.toObject(Friends.class);
                                                                                    friendsArrayList.add(friends);
                                                                                    profile = (String) document.get("img_profile");

                                                                                    if (friendsArrayList.size() == 1){
                                                                                        profile_url = friendsArrayList.get(0).getImg_profile();
                                                                                    }else {
                                                                                        profile_url = friendsArrayList.get(position).getImg_profile();
                                                                                    }


                                                                                    helperUtils.startAlphaAnimation(holder.user_profile, View.VISIBLE);
                                                                                    helperUtils.startAlphaAnimation(holder.username, View.VISIBLE);
                                                                                    helperUtils.startAlphaAnimation(holder.date, View.VISIBLE);
                                                                                    helperUtils.startAlphaAnimation(holder.time, View.VISIBLE);
                                                                                    helperUtils.startAlphaAnimation(holder.caption, View.VISIBLE);

                                                                                    if (friendsArrayList.size() > 1) {
                                                                                        final String doc_name = postViewModelArrayList.get(position).getDate() + " (" + postViewModelArrayList.get(position).getTime() + ")";

                                                                                        countReactionAngry(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        countReactionHaha(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        countReactionCry(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        countReactionWow(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        countReactionHeart(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        countReactionLike(friendsArrayList.get(position).getUser_id(), doc_name,holder);
                                                                                        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {
                                                                                                Intent i = new Intent(home, Comments.class);
                                                                                                i.putExtra("doc_name", doc_name);
                                                                                                i.putExtra("user_id", String.valueOf(friendsArrayList.get(position).getUser_id()));
                                                                                                Log.d("user", friendsArrayList.get(position).getUser_id());
                                                                                                home.startActivity(i);
                                                                                            }
                                                                                        });
                                                                                        firestoreDB.collection("users")
                                                                                                .document(friendsArrayList.get(position).getUser_id())
                                                                                                .collection("posts")
                                                                                                .document("published")
                                                                                                .collection("post")
                                                                                                .document(doc_name)
                                                                                                .collection("image_list")
                                                                                                .get()
                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            imageViewModelArrayList = new ArrayList<>();
                                                                                                            for (final QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                                                imageViewModel = document.toObject(ImageViewModel.class);
                                                                                                                imageViewModelArrayList.add(imageViewModel);

                                                                                                                image_list = new RecyclerViewAdapterPostItemImages(home, imageViewModelArrayList);
                                                                                                                //image_list.setHasStableIds(false);
                                                                                                                holder.post_item_recycler_view.setAdapter(image_list);

                                                                                                                CollectionReference fromDoc1 = firestoreDB.collection("users")
                                                                                                                        .document(friendsArrayList.get(position).getUser_id())
                                                                                                                        .collection("posts")
                                                                                                                        .document("published")
                                                                                                                        .collection("post")
                                                                                                                        .document(doc_name)
                                                                                                                        .collection("image_list");

                                                                                                                CollectionReference toDoc1 = firestoreDB.collection("users")
                                                                                                                        .document(mAuth.getUid())
                                                                                                                        .collection("saved")
                                                                                                                        .document("published")
                                                                                                                        .collection("post")
                                                                                                                        .document(doc_name)
                                                                                                                        .collection("image_list");

                                                                                                                HelperUtils.savePostImages(fromDoc1,toDoc1, imageViewModelArrayList);
                                                                                                                }
                                                                                                            }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                    if (friendsArrayList.size() == 1) {
                                                                                        final String doc_name = postViewModelArrayList.get(position).getDate() + " (" + postViewModelArrayList.get(position).getTime() + ")";

                                                                                        countReactionAngry(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        countReactionHaha(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        countReactionCry(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        countReactionWow(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        countReactionHeart(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        countReactionLike(friendsArrayList.get(0).getUser_id(), doc_name,holder);
                                                                                        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {
                                                                                                Intent i = new Intent(home, Comments.class);
                                                                                                i.putExtra("doc_name", doc_name);
                                                                                                i.putExtra("user_id",String.valueOf(friendsArrayList.get(0).getUser_id()) );
                                                                                                Log.d("user", friendsArrayList.get(0).getUser_id());
                                                                                                home.startActivity(i);
                                                                                            }
                                                                                        });
                                                                                        firestoreDB.collection("users")
                                                                                                .document(friendsArrayList.get(0).getUser_id())
                                                                                                .collection("posts")
                                                                                                .document("published")
                                                                                                .collection("post")
                                                                                                .document(doc_name)
                                                                                                .collection("image_list")
                                                                                                .get()
                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            imageViewModelArrayList = new ArrayList<>();
                                                                                                            for (final QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                                                imageViewModel = document.toObject(ImageViewModel.class);
                                                                                                                imageViewModelArrayList.add(imageViewModel);

                                                                                                                image_list = new RecyclerViewAdapterPostItemImages(home, imageViewModelArrayList);
                                                                                                                holder.post_item_recycler_view.setAdapter(image_list);

                                                                                                                CollectionReference fromDoc1 = firestoreDB.collection("users")
                                                                                                                        .document(friendsArrayList.get(0).getUser_id())
                                                                                                                        .collection("posts")
                                                                                                                        .document("published")
                                                                                                                        .collection("post")
                                                                                                                        .document(doc_name)
                                                                                                                        .collection("image_list");

                                                                                                                CollectionReference toDoc1 = firestoreDB.collection("users")
                                                                                                                        .document(mAuth.getUid())
                                                                                                                        .collection("saved")
                                                                                                                        .document("published")
                                                                                                                        .collection("post")
                                                                                                                        .document(doc_name)
                                                                                                                        .collection("image_list");

                                                                                                                HelperUtils.savePostImages(fromDoc1,toDoc1, imageViewModelArrayList);
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                    }

                                                                                    if (friendsArrayList.size() == 1) {
                                                                                        firestoreDB.collection("users")
                                                                                                .document(friendsArrayList.get(0).getUser_id())
                                                                                                .collection("posts")
                                                                                                .document("published")
                                                                                                .collection("post")
                                                                                                .document(postViewModelArrayList.get(0).getDate() + " (" + postViewModelArrayList.get(0).getTime() + ")")
                                                                                                .collection("reactions")
                                                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                                    reactionCountModel = document.toObject(ReactionCountModel.class);
                                                                                                    reactionCountModelArrayList.add(reactionCountModel);
                                                                                                    holder.btn_save.setOnClickListener(new View.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(View view) {
                                                                                                            final String doc_name = postViewModelArrayList.get(position).getDate() + " (" + postViewModelArrayList.get(position).getTime() + ")";

                                                                                                            DocumentReference fromDoc = firestoreDB.collection("users")
                                                                                                                    .document(friendsArrayList.get(0).getUser_id())
                                                                                                                    .collection("posts")
                                                                                                                    .document("published")
                                                                                                                    .collection("post")
                                                                                                                    .document(doc_name);
                                                                                                            DocumentReference toDoc = firestoreDB.collection("users")
                                                                                                                    .document(mAuth.getUid())
                                                                                                                    .collection("saved")
                                                                                                                    .document("published")
                                                                                                                    .collection("post")
                                                                                                                    .document(doc_name);
                                                                                                            HelperUtils.savePost(fromDoc,toDoc);
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }

                                                                                    if (friendsArrayList.size() > 1) {
                                                                                        firestoreDB.collection("users")
                                                                                                .document(friendsArrayList.get(position).getUser_id())
                                                                                                .collection("posts")
                                                                                                .document("published")
                                                                                                .collection("post")
                                                                                                .document(postViewModelArrayList.get(position).getDate() + " (" + postViewModelArrayList.get(position).getTime() + ")")
                                                                                                .collection("reactions")
                                                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                                    reactionCountModel = document.toObject(ReactionCountModel.class);
                                                                                                    reactionCountModelArrayList.add(reactionCountModel);
                                                                                                    holder.btn_save.setOnClickListener(new View.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(View view) {
                                                                                                            final String doc_name = postViewModelArrayList.get(position).getDate() + " (" + postViewModelArrayList.get(position).getTime() + ")";

                                                                                                            DocumentReference fromDoc = firestoreDB.collection("users")
                                                                                                                    .document(friendsArrayList.get(position).getUser_id())
                                                                                                                    .collection("posts")
                                                                                                                    .document("published")
                                                                                                                    .collection("post")
                                                                                                                    .document(doc_name);
                                                                                                            DocumentReference toDoc = firestoreDB.collection("users")
                                                                                                                    .document(mAuth.getUid())
                                                                                                                    .collection("saved")
                                                                                                                    .document("published")
                                                                                                                    .collection("post")
                                                                                                                    .document(doc_name);
                                                                                                            HelperUtils.savePost(fromDoc,toDoc);


                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }catch (Exception e){

        }



        holder.react_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.post_reactions.setVisibility(View.VISIBLE);
                holder.action_haha.playAnimation();
                holder.action_angry.playAnimation();
                holder.action_cry.playAnimation();
                holder.action_heart.playAnimation();
                holder.action_like.playAnimation();
                holder.action_wow.playAnimation();
            }
        });

        holder.action_haha.useHardwareAcceleration(true);
        holder.action_angry.useHardwareAcceleration(true);
        holder.action_cry.useHardwareAcceleration(true);
        holder.action_heart.useHardwareAcceleration(true);
        holder.action_like.useHardwareAcceleration(true);
        holder.action_wow.useHardwareAcceleration(true);

        holder.action_haha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("haha_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });
        holder.action_angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("angry_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });
        holder.action_cry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("cry_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });
        holder.action_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("heart_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });
        holder.action_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("like_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });
        holder.action_wow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                react("wow_reaction",position);
                holder.post_reactions.setVisibility(View.GONE);
            }
        });

        if (postViewModelArrayList.get(position).getCaption().isEmpty()) {
            holder.caption.setVisibility(View.GONE);
        }

        if (postViewModelArrayList.get(position).getInk_color().equals("default_ink_outline")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.colorPrimaryDark));
            holder.caption.setTextColor(home.getResources().getColor(R.color.colorPrimaryDark));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("default_ink_filled")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.colorPrimaryDark));
            holder.post_card.setCardBackgroundColor(home.getResources().getColor(R.color.colorPrimaryDark));
            holder.caption.setTextColor(home.getResources().getColor(R.color.colorAccent));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_primary));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_primary_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("green_outlined")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.green_outlined));
            holder.caption.setTextColor(home.getResources().getColor(R.color.green_outlined));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("green_filled")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.green_outlined));
            holder.post_card.setCardBackgroundColor(home.getResources().getColor(R.color.green_outlined));
            holder.caption.setTextColor(home.getResources().getColor(R.color.colorAccent));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_green));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_green_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("amber_outlined")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.amber_outlined));
            holder.caption.setTextColor(home.getResources().getColor(R.color.amber_outlined));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("amber_filled")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.amber_outlined));
            holder.post_card.setCardBackgroundColor(home.getResources().getColor(R.color.amber_outlined));
            holder.caption.setTextColor(home.getResources().getColor(R.color.colorAccent));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_amber));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_amber_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("purple_outlined")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.purple_outline));
            holder.caption.setTextColor(home.getResources().getColor(R.color.purple_outline));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_color().equals("purple_filled")){
            holder.post_card.setStrokeColor(home.getResources().getColor(R.color.purple_outline));
            holder.post_card.setCardBackgroundColor(home.getResources().getColor(R.color.purple_outline));
            holder.caption.setTextColor(home.getResources().getColor(R.color.colorAccent));
            holder.btn_comment.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));
            holder.btn_share.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));
            holder.btn_save.setBackgroundTintList(home.getResources().getColorStateList(R.color.state_list_purple));

            holder.btn_comment.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
            holder.btn_share.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
            holder.btn_save.setImageTintList(home.getResources().getColorStateList(R.color.state_list_purple_foreground));
        }
        if (postViewModelArrayList.get(position).getInk_align().equals("alignment_left")){
            holder.caption.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
        if (postViewModelArrayList.get(position).getInk_align().equals("alignment_center")){
            holder.caption.setGravity(View.TEXT_ALIGNMENT_CENTER);
        }
        if (postViewModelArrayList.get(position).getInk_align().equals("alignment_right")){
            holder.caption.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
        }
        if (postViewModelArrayList.get(position).getSharing_pref().equals("false")){
            holder.btn_share.setEnabled(false);
            holder.btn_share.setImageResource(R.drawable.ic_outline_cancel_24px);
        }
        if (postViewModelArrayList.get(position).getSharing_pref().equals("true")){
            holder.btn_share.setEnabled(true);
        }
        if (postViewModelArrayList.get(position).getComments_pref().equals("false")){
            holder.btn_comment.setEnabled(false);
            holder.btn_comment.setImageResource(R.drawable.ic_outline_cancel_24px);
        }
        if (postViewModelArrayList.get(position).getComments_pref().equals("true")){
            holder.btn_comment.setEnabled(true);
        }

    }

    private void countReactionAngry(String userID,String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","angry_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            reactionAngry = document.toObject(ReactionAngry.class);
                            reactionAngryArrayList.add(reactionAngry);
                            if (reactionAngryArrayList.size() == 0) {
                                holder.angry_reaction.setVisibility(View.GONE);
                            }if (reactionAngryArrayList.size() >= 1){
                                holder.angry_reaction.setVisibility(View.VISIBLE);
                            }
                            holder.reactions_summary.setVisibility(View.VISIBLE);
                            holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionAngryArrayList.size()));
                        }
                    }
                });
    }

    private void countReactionCry(String userID,String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","cry_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    reactionCry = document.toObject(ReactionCry.class);
                    reactionCryArrayList.add(reactionCry);
                    if (reactionCryArrayList.size() == 0) {
                        holder.cry_reaction.setVisibility(View.GONE);
                    }if (reactionCryArrayList.size() >= 1){
                        holder.cry_reaction.setVisibility(View.VISIBLE);
                    }
                    holder.reactions_summary.setVisibility(View.VISIBLE);
                    holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionCryArrayList.size()));
                }
            }
        });
    }
    private void countReactionHaha(String userID,String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","haha_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    reactionHaha = document.toObject(ReactionHaha.class);
                    reactionHahaArrayList.add(reactionHaha);
                    if (reactionHahaArrayList.size() == 0) {
                        holder.haha_reaction.setVisibility(View.GONE);
                    }if (reactionHahaArrayList.size() >= 1){
                        holder.haha_reaction.setVisibility(View.VISIBLE);
                    }
                    holder.reactions_summary.setVisibility(View.VISIBLE);
                    holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionHahaArrayList.size()));
                }
            }
        });
    }
    private void countReactionHeart(String userID,String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","heart_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    reactionHeart = document.toObject(ReactionHeart.class);
                    reactionHeartArrayList.add(reactionHeart);
                    Log.d("HEART SIZE", reactionHeartArrayList.size()+"");
                    if (reactionHeartArrayList.size() == 0) {
                        holder.heart_reaction.setVisibility(View.GONE);
                    }if (reactionHeartArrayList.size() >= 1){
                        holder.heart_reaction.setVisibility(View.VISIBLE);
                    }
                    holder.reactions_summary.setVisibility(View.VISIBLE);
                    holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionHeartArrayList.size()));
                }
            }
        });
    }
    private void countReactionLike(String userID, String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","like_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    reactionLike = document.toObject(ReactionLike.class);
                    reactionLikeArrayList.add(reactionLike);
                    if (reactionLikeArrayList.size() == 0) {
                        holder.like_reaction.setVisibility(View.INVISIBLE);
                    }if (reactionLikeArrayList.size() >= 1){
                        holder.like_reaction.setVisibility(View.VISIBLE);
                    }
                    holder.reactions_summary.setVisibility(View.VISIBLE);
                    holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionLikeArrayList.size()));
                }
            }
        });
    }
    private void countReactionWow(String userID,String doc_name, final RecyclerViewHolderPostUtil holder){
        firestoreDB.collection("users")
                .document(userID)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .whereEqualTo("reaction","wow_reaction")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    reactionWow = document.toObject(ReactionWow.class);
                    reactionWowArrayList.add(reactionWow);
                    if (reactionWowArrayList.size() == 0) {
                        holder.wow_reaction.setVisibility(View.GONE);
                    }if (reactionWowArrayList.size() >= 1){
                        holder.wow_reaction.setVisibility(View.VISIBLE);
                    }
                    holder.reactions_summary.setVisibility(View.VISIBLE);
                    holder.reactions_summary.setText(Integer.toString(Integer.parseInt(holder.reactions_summary.getText().toString())+reactionWowArrayList.size()));
                }
            }
        });
    }
    private void react(String user_reaction, int position){
        Map<String, Object> reaction = new HashMap<>();
        reaction.put("reaction", user_reaction);
        reaction.put("user_id", mAuth.getUid());
        reaction.put("first_name", accountInfoArrayList.get(0).getFirst_name());
        reaction.put("last_name", accountInfoArrayList.get(0).getLast_name());

        String doc_name;
        if (postViewModelArrayList.size() == 1){
            doc_name = postViewModelArrayList.get(0).getDate()+" ("+postViewModelArrayList.get(0).getTime()+")";
        }
        else {
            doc_name = postViewModelArrayList.get(position).getDate()+" ("+postViewModelArrayList.get(position).getTime()+")";
        }

        if (friendsArrayList.size() == 1) {
            firestoreDB.collection("users")
                    .document(friendsArrayList.get(0).getUser_id())
                    .collection("posts")
                    .document("published")
                    .collection("post")
                    .document(doc_name)
                    .collection("reactions")
                    .document(mAuth.getUid())
                    .set(reaction);


        }
        else{
            firestoreDB.collection("users")
                    .document(friendsArrayList.get(position).getUser_id())
                    .collection("posts")
                    .document("published")
                    .collection("post")
                    .document(doc_name)
                    .collection("reactions")
                    .document(mAuth.getUid())
                    .set(reaction);
        }

        HelperUtils helperUtils = new HelperUtils();
        helperUtils.setPersistence(firestoreDB);
    }

    private void save(ArrayList<PostViewModel> postViewModelArrayList,ArrayList<ImageViewModel> imageViewModelArrayList, int position){
        String first_name = postViewModelArrayList.get(position).getFirst_name();
        String last_name = postViewModelArrayList.get(position).getLast_name();
        String date = postViewModelArrayList.get(position).getDate();
        String time = postViewModelArrayList.get(position).getTime();
        String user_id = postViewModelArrayList.get(position).getUser_id();
        String caption = postViewModelArrayList.get(position).getCaption();
        String img_profile = postViewModelArrayList.get(position).getImg_profile();
        String comments_pref = postViewModelArrayList.get(position).getComments_pref();
        String ink_align = postViewModelArrayList.get(position).getInk_align();
        String ink_color = postViewModelArrayList.get(position).getInk_color();
        String sharing_pref = postViewModelArrayList.get(position).getSharing_pref();

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("first_name", first_name);
        account_details.put("last_name", last_name);
        account_details.put("ink_color", ink_color);
        account_details.put("caption", caption);
        account_details.put("ink_align", ink_align);
        account_details.put("sharing_pref", sharing_pref);
        account_details.put("comments_pref", comments_pref);
        account_details.put("timestamp", FieldValue.serverTimestamp());
        account_details.put("img_profile", img_profile);
        account_details.put("user_id", user_id);
        account_details.put("date", date);
        account_details.put("time", time);

        firestoreDB.collection("users")
                        .document(mAuth.getUid())
                        .collection("saved")
                        .document("published")
                        .collection("post")
                        .document(date+" ("+time+")")
                        .set(account_details);
    }
    @Override
    public int getItemCount() {
        return postViewModelArrayList.size();
    }
    public void clear() {
        final int size = postViewModelArrayList.size();
        postViewModelArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }
}
