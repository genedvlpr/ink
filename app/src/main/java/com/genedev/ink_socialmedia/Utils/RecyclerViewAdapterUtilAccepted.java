package com.genedev.ink_socialmedia.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.genedev.ink_socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterUtilAccepted extends RecyclerView.Adapter<RecyclerViewHolderUtil> {

    private Context home;
    private ArrayList<FriendAcceptedModel> friendAcceptedModelArrayList;
    private FriendAcceptedModel friendAcceptedModel = new FriendAcceptedModel();
    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo = new AccountInfo();
    private RecyclerViewHolderUtil holderUtil;
    private ImageButton show_friend_requests;
    private String fname, lname, req_id, profile_img;

    private ArrayList<Friends> friendArrayList;
    private Friends friend = new Friends();

    private boolean isSelected = false;
    public RecyclerViewAdapterUtilAccepted(Context home, ArrayList<FriendAcceptedModel> friendAcceptedModels){
        this.home = home;
        this.friendAcceptedModelArrayList = friendAcceptedModels;
    }



    @NonNull
    @Override
    public RecyclerViewHolderUtil onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.friends_item_lists_accepted, parent, false);

        return new RecyclerViewHolderUtil(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolderUtil holder, final int position) {
        //holder.tv_name.setText(friendAcceptedModelArrayList.get(position).getFirstName()+" "+friendAcceptedModelArrayList.get(position).getLastName());
        //holder.btn_request.setText("Accept");

        holder.item_card.setVisibility(View.INVISIBLE);

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
        String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("accepted")
                .collection("users")
                .whereEqualTo(account_id+"(request)", "friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (final QueryDocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                                //Log.d("Ref ", querySnapshot.getId());
                                String accepted_friend = (String) querySnapshot.get("user_id");

                                Log.d("Friends: ", accepted_friend + " ");
                                final String locale = home.getResources().getConfiguration().locale.getCountry();
                                firestoreDB.collection("users")
                                        .document(accepted_friend)
                                        .collection("account_details")
                                        .whereEqualTo("user_id", accepted_friend)
                                        .whereEqualTo("country_code", locale)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @SuppressLint("RestrictedApi")
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        friendAcceptedModel = document.toObject(FriendAcceptedModel.class);
                                                        friendAcceptedModelArrayList.add(friendAcceptedModel);

                                                        firestoreDB.collection("users")
                                                                .document(U_id)
                                                                .collection("friends")
                                                                .document("following")
                                                                .collection("users")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                String friend_account = friendAcceptedModelArrayList.get(position).getUser_id();
                                                                                String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
                                                                                String followed_unfollwed = document.getString(account_id+"(request)");
                                                                                if (Objects.requireNonNull(followed_unfollwed).equals("friends")){
                                                                                    holder.btn_following.setText("Unfollow");
                                                                                    holder.btn_following.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View view) {
                                                                                            holder.btn_following.setText("Follow");
                                                                                            cancelUpdateInUsersCurrentAccountFollowing(holder.btn_following, position);
                                                                                            emptyFields(holder.btn_following, "Friend unfollowed");
                                                                                            isSelected = true;
                                                                                        }
                                                                                    });
                                                                                }
                                                                                if (followed_unfollwed.equals("false")){
                                                                                    holder.btn_following.setText("Follow");
                                                                                    holder.btn_following.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View view) {

                                                                                            holder.btn_following.setText("Unfollow");
                                                                                            addSendInUsersCurrentAccountFollowing(holder.btn_following, position);
                                                                                            emptyFields(holder.btn_following, "Friend followed");
                                                                                            isSelected = false;
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

        final HelperUtils helperUtils = new HelperUtils();

        final int TIME_OUT = 3000;
                try{
                    holder.tv_name.setText(friendAcceptedModelArrayList.get(position).getFirst_name()+" "+friendAcceptedModelArrayList.get(position).getLast_name());
                    try{
                        profile_img = friendAcceptedModelArrayList.get(position).getImg_profile();
                        Uri uri, uri1;

                        if (profile_img != null){
                            uri1 = Uri.parse(profile_img);
                            Picasso.get().load(uri1)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.img_profile, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            helperUtils.startAlphaAnimation(holder.item_card, View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                        }else{
                            helperUtils.startAlphaAnimation(holder.item_card, View.VISIBLE);
                        }
                    }catch (Exception ex){

                    }
                }catch (Exception e) {
                    holder.tv_name.setText(friendAcceptedModelArrayList.get(0).getFirst_name() + " " + friendAcceptedModelArrayList.get(0).getLast_name());
                    try {
                        profile_img = friendAcceptedModelArrayList.get(0).getImg_profile();
                        Uri uri, uri1;

                        if (profile_img != null) {
                            uri1 = Uri.parse(profile_img);
                            Picasso.get().load(uri1)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.img_profile, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            helperUtils.startAlphaAnimation(holder.item_card, View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                        } else {
                            helperUtils.startAlphaAnimation(holder.item_card, View.VISIBLE);
                        }
                    } catch (Exception ex) {

                    }
                }

            holder.btn_unfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isSelected) {
                        holder.btn_unfriend.setEnabled(false);
                        //friendRequestsArrayList.clear();
                        cancelUpdateInUsersCurrentAccount(holder.btn_unfriend, position);
                        cancelUpdateInUsersFriendAccount(holder.btn_unfriend, position);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.btn_unfriend.setText("Unfriended");
                                holder.btn_unfriend.setEnabled(false);
                            }
                        }, TIME_OUT);
                        emptyFields(holder.btn_unfriend, "Friend unfriended");
                        isSelected = true;
                    } else {
                        holder.btn_unfriend.setText("Accept");
                        addSendInUsersCurrentAccount(holder.btn_unfriend, position);
                        addSendInUsersFriendAccount(holder.btn_unfriend, position);
                        emptyFields(holder.btn_request, "Friend request sent");
                        isSelected = false;
                    }
                }
            });
    }

    private void addSendInUsersCurrentAccountFollowing(final View v, final int position){
        accountInfoArrayList = new ArrayList<>();
        String friend_account = friendAcceptedModelArrayList.get(position).getUserId();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", U_id);
        account_details.put("first_name", friendAcceptedModelArrayList.get(position).getFirstName());
        account_details.put("last_name", friendAcceptedModelArrayList.get(position).getLastName());
        account_details.put(account_id+"(request)","friends");
        account_details.put("user_id", friendAcceptedModelArrayList.get(position).getUserId());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("following")
                .collection("users")
                .document(friend_account+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void cancelUpdateInUsersCurrentAccountFollowing(final View v, final int position){
        String friend_account = friendAcceptedModelArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        //String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", friendAcceptedModelArrayList.get(position).getUser_id());
        account_details.put("first_name",  friendAcceptedModelArrayList.get(position).getFirst_name());
        account_details.put("last_name",  friendAcceptedModelArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", U_id);

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("following")
                .collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id()+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void addSendInUsersFriendAccount(final View v, final int position){
        accountInfoArrayList = new ArrayList<>();
        String account_id = U_id.substring(0, Math.min(U_id.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", accountInfo.getUser_id());
        account_details.put("first_name", accountInfo.getFirst_name());
        account_details.put("last_name", accountInfo.getLast_name());
        account_details.put(account_id+"(request)","true");
        account_details.put("user_id", friendAcceptedModelArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(U_id+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });


    }
    private void addSendInUsersCurrentAccount(final View v, final int position){
        accountInfoArrayList = new ArrayList<>();
        String friend_account = friendAcceptedModelArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", U_id);
        account_details.put("first_name", friendAcceptedModelArrayList.get(position).getFirst_name());
        account_details.put("last_name", friendAcceptedModelArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","true");
        account_details.put("user_id", friendAcceptedModelArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(friend_account+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void cancelUpdateInUsersFriendAccount(final View v, final int position){
        String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        String friend_account = friendAcceptedModelArrayList.get(position).getUser_id();
        //String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", friendAcceptedModelArrayList.get(position).getUser_id());
        account_details.put("first_name",  accountInfo.getFirst_name());
        account_details.put("last_name",  accountInfo.getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", U_id);

        firestoreDB.collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(U_id+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id())
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(U_id+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id())
                .collection("friends")
                .document("following")
                .collection("users")
                .document(U_id+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(friendAcceptedModelArrayList.get(position).getUser_id())
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(U_id+"_request")
                .delete();
    }

    private void cancelUpdateInUsersCurrentAccount(final View v, final int position){
        //String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        String friend_account = friendAcceptedModelArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", U_id);
        account_details.put("first_name",  friendAcceptedModelArrayList.get(position).getFirst_name());
        account_details.put("last_name",  friendAcceptedModelArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", friendAcceptedModelArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(friend_account+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(friend_account+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(friend_account+"_request")
                .delete();

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("following")
                .collection("users")
                .document(friend_account+"_request")
                .delete();
    }
    @Override
    public int getItemCount() {
        return friendAcceptedModelArrayList.size();
    }
    public void clear() {
        final int size = friendAcceptedModelArrayList.size();
        friendAcceptedModelArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }
}
