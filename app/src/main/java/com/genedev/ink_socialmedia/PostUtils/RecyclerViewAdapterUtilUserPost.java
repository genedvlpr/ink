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
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.genedev.ink_socialmedia.Utils.RecyclerViewHolderUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

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

public class RecyclerViewAdapterUtilUserPost extends RecyclerView.Adapter<RecyclerViewHolderPostUtil> {

    private Context home;

    private ArrayList<PostViewModel> postViewModelArrayList;
    private PostViewModel postViewModel = new PostViewModel();

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

    private ArrayList<ImageViewModel> imageViewModelArrayList = new ArrayList<>();
    private ImageViewModel imageViewModel = new ImageViewModel();

    private ArrayList<String> postImageArrays;
    private PostImageArray postImageArray = new PostImageArray();

    private RecyclerViewAdapterPostItemImages image_list;

    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();;
    private AccountInfo accountInfo = new AccountInfo();

    private RecyclerViewHolderUtil holderUtil;
    private ImageButton show_friend_requests;
    private String fname, lname, req_id;
    private String doc_name, profile;
    private boolean isSelected = false;
    private String time;

    public RecyclerViewAdapterUtilUserPost(Context home, ArrayList<PostViewModel> postViewModels){
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

        //holder.btn_request.setText("Accept");

        final HelperUtils helperUtils = new HelperUtils();

        @SuppressLint("WrongConstant") final LinearLayoutManager hs_linearLayout = new LinearLayoutManager(home, LinearLayoutManager.HORIZONTAL, false);
        hs_linearLayout.setItemPrefetchEnabled(true);

        final String doc_name = postViewModelArrayList.get(position).getDate()+ " ("+postViewModelArrayList.get(position).getTime()+")";

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

        countReactionAngry(mAuth.getUid(), doc_name,holder);
        countReactionHaha(mAuth.getUid(), doc_name,holder);
        countReactionCry(mAuth.getUid(), doc_name,holder);
        countReactionWow(mAuth.getUid(), doc_name,holder);
        countReactionHeart(mAuth.getUid(), doc_name,holder);
        countReactionLike(mAuth.getUid(), doc_name,holder);


        Picasso.get().load(postViewModelArrayList.get(position).getImg_profile()).fit()
                .centerCrop().into(holder.user_profile);

        //holder.post_item_recycler_view.setNestedScrollingEnabled(false);

        if (postViewModelArrayList.size() == 1){
            time = postViewModelArrayList.get(0).getTime();
        }
        if (postViewModelArrayList.size() > 1){
            time = postViewModelArrayList.get(position).getTime();
        }
        firestoreDB.collection("users")
                .document(U_id)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("image_list")
                .whereEqualTo("time", time)
                .whereEqualTo("date", doc_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            imageViewModelArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d("Ref ", querySnapshot.getId());

                                imageViewModel = document.toObject(ImageViewModel.class);
                                imageViewModelArrayList.add(imageViewModel);
                                image_list = new RecyclerViewAdapterPostItemImages(home,imageViewModelArrayList);
                                holder.post_item_recycler_view.setAdapter(image_list);

                                holder.post_item_recycler_view.setLayoutManager(hs_linearLayout);
                                holder.post_item_recycler_view.setHasFixedSize(true);
                                holder.post_item_recycler_view.setItemViewCacheSize(100);
                                holder.post_item_recycler_view.setDrawingCacheEnabled(true);
                                holder.post_item_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                            }
                            }

                    }
                });

        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home, Comments.class);
                i.putExtra("doc_name", doc_name);
                i.putExtra("user_id", String.valueOf(accountInfoArrayList.get(0).getUser_id()));
                Log.d("user", accountInfoArrayList.get(0).getUser_id());
                home.startActivity(i);
            }
        });

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

        holder.username.setText(postViewModelArrayList.get(position).getFirst_name()+" "+postViewModelArrayList.get(position).getLast_name());
        holder.date.setText(String.valueOf(postViewModelArrayList.get(position).getDate()));
        holder.time.setText(String.valueOf(postViewModelArrayList.get(position).getTime()));
        holder.caption.setText(postViewModelArrayList.get(position).getCaption());
        helperUtils.startAlphaAnimation(holder.username, View.VISIBLE);
        helperUtils.startAlphaAnimation(holder.date, View.VISIBLE);
        helperUtils.startAlphaAnimation(holder.time, View.VISIBLE);
        helperUtils.startAlphaAnimation(holder.caption, View.VISIBLE);

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
            holder.caption.setGravity(Gravity.START);
        }
        if (postViewModelArrayList.get(position).getInk_align().equals("alignment_center")){
            holder.caption.setGravity(Gravity.CENTER);
        }
        if (postViewModelArrayList.get(position).getInk_align().equals("alignment_right")){
            holder.caption.setGravity(Gravity.END);
        }
        if (postViewModelArrayList.get(position).getSharing_pref().equals("false")){
            holder.btn_share.setEnabled(false);
            holder.btn_share.setVisibility(View.GONE);
        }
        if (postViewModelArrayList.get(position).getSharing_pref().equals("true")){
            holder.btn_share.setEnabled(true);
        }
        if (postViewModelArrayList.get(position).getComments_pref().equals("false")){
            holder.btn_comment.setEnabled(false);
            holder.btn_comment.setVisibility(View.GONE);
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

        firestoreDB.collection("users")
                .document(accountInfoArrayList.get(0).getUser_id())
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("reactions")
                .document(mAuth.getUid())
                .set(reaction);

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
