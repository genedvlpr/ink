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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
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

public class RecyclerViewAdapterUtilAccept extends RecyclerView.Adapter<RecyclerViewHolderUtil> {

    Context home;
    ArrayList<FriendRequestModel> friendRequestsArrayList;
    FriendRequestModel friendRequests = new FriendRequestModel();

    ArrayList<FriendAcceptedModel> friendAcceptedModelArrayList;
    FriendAcceptedModel friendAcceptedModel = new FriendAcceptedModel();

    ArrayList<AccountInfo> accountInfoArrayList;
    AccountInfo accountInfo = new AccountInfo();
    RecyclerViewHolderUtil holderUtil;
    ImageButton show_friend_requests;
    String fname, lname, req_id, profile_img;

    ArrayList<Friends> friendArrayList;
    Friends friend = new Friends();

    private boolean isSelected = false;
    public RecyclerViewAdapterUtilAccept(Context home, ArrayList<FriendRequestModel> friendRequests){
        this.home = home;
        this.friendRequestsArrayList = friendRequests;
    }



    @NonNull
    @Override
    public RecyclerViewHolderUtil onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.friends_item_lists_accept, parent, false);

        return new RecyclerViewHolderUtil(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolderUtil holder, final int position) {
        //holder.tv_name.setText(friendRequestsArrayList.get(position).getFirstName()+" "+friendRequestsArrayList.get(position).getLastName());
        holder.btn_request.setText("Accept");

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
                .document("friend_requests")
                .collection("users")
                .whereEqualTo(account_id+"(request)", "true")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (final QueryDocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                                //Log.d("Ref ", querySnapshot.getId());
                                String requseted_users_alter = (String) querySnapshot.get("requester_id");

                                Log.d("Account not requested: ", requseted_users_alter + " ");
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

                                                        friendRequests = document.toObject(FriendRequestModel.class);
                                                        friendRequestsArrayList.add(friendRequests);

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
                    holder.tv_name.setText(friendRequestsArrayList.get(position).getFirst_name()+" "+friendRequestsArrayList.get(position).getLast_name());
                    try{
                        profile_img = friendRequestsArrayList.get(position).getImg_profile();
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
                }catch (Exception e){
                    holder.tv_name.setText(friendRequestsArrayList.get(0).getFirst_name()+" "+friendRequestsArrayList.get(0).getLast_name());
                    try{
                        profile_img = friendRequestsArrayList.get(0).getImg_profile();
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
                }


            holder.btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isSelected) {
                        holder.btn_request.setText("Ignore");
                        addSendInUsersFriendAccount(holder.btn_request, position);
                        addSendInUsersCurrentAccount(holder.btn_request, position);
                        updateSendInUsersCurrentAccount(holder.btn_request, position);
                        updateSendInUsersFriendAccount(holder.btn_request, position);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    holder.btn_request.setText("accepted");
                                    holder.btn_request.setEnabled(false);
                                }
                            }, TIME_OUT);
                        //friendRequestsArrayList.clear();
                        emptyFields(holder.btn_request, "Friend request accepted");
                        isSelected = true;
                    } else {
                        holder.btn_request.setText("Accept");
                        cancelUpdateInUsersFriendAccount(holder.btn_request, position);
                        cancelUpdateInUsersCurrentAccountAccepted(holder.btn_request, position);
                        cancelUpdateInUsersCurrentAccountRequests(holder.btn_request, position);
                        cancelUpdateInUsersFriendAccountAccepted(holder.btn_request, position);
                        emptyFields(holder.btn_request, "Friend request ignored");
                        isSelected = false;
                    }
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
        account_details.put(account_id+"(request)","friends");
        account_details.put("user_id", accountInfo.getUser_id());

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUserId())
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(U_id+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUserId())
                .collection("friends")
                .document("following")
                .collection("users")
                .document(U_id+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUserId())
                .collection("friends")
                .document("sent_requests")
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
        String friend_account = friendRequestsArrayList.get(position).getUserId();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", U_id);
        account_details.put("first_name", friendRequestsArrayList.get(position).getFirstName());
        account_details.put("last_name", friendRequestsArrayList.get(position).getLastName());
        account_details.put(account_id+"(request)","friends");
        account_details.put("user_id", friendRequestsArrayList.get(position).getUserId());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(friend_account+"_request")
                .set(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

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

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("friend_requests")
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
        String friend_account = friendRequestsArrayList.get(position).getUser_id();
        //String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", friendRequestsArrayList.get(position).getUser_id());
        account_details.put("first_name",  accountInfo.getFirst_name());
        account_details.put("last_name",  accountInfo.getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", U_id);

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUser_id())
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

    private void cancelUpdateInUsersCurrentAccountRequests(final View v, final int position){
        //String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        String friend_account = friendRequestsArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", U_id);
        account_details.put("first_name",  friendRequestsArrayList.get(position).getFirst_name());
        account_details.put("last_name",  friendRequestsArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","friends");
        account_details.put("requester_id", friendRequestsArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(friend_account+"_request")
                .delete();
    }

    private void cancelUpdateInUsersFriendAccountAccepted(final View v, final int position){
        String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        String friend_account = friendRequestsArrayList.get(position).getUser_id();
        //String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", friendRequestsArrayList.get(position).getUser_id());
        account_details.put("first_name",  accountInfo.getFirst_name());
        account_details.put("last_name",  accountInfo.getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", U_id);

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUser_id())
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(U_id+"_request")
                .delete();
    }

    private void cancelUpdateInUsersCurrentAccountAccepted(final View v, final int position){
        //String account_id = U_id.substring(0, Math.min(U_id.length(), 5));
        String friend_account = friendRequestsArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", U_id);
        account_details.put("first_name",  friendRequestsArrayList.get(position).getFirst_name());
        account_details.put("last_name",  friendRequestsArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","friends");
        account_details.put("requester_id", friendRequestsArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("accepted")
                .collection("users")
                .document(friend_account+"_request")
                .delete();
    }

    private void updateSendInUsersFriendAccount(final View v, final int position){
        //accountInfoArrayList = new ArrayList<>();
        String account_id = mAuth.getUid().substring(0, Math.min(mAuth.getUid().length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", accountInfo.getUser_id());
        account_details.put("first_name", accountInfo.getFirst_name());
        account_details.put("last_name", accountInfo.getLast_name());
        account_details.put(account_id+"(request)","friends");
        account_details.put("user_id", friendRequestsArrayList.get(position).getUserId());

        firestoreDB.collection("users")
                .document(friendRequestsArrayList.get(position).getUserId())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(mAuth.getUid()+"_request")
                .update(account_details);

    }
    private void updateSendInUsersCurrentAccount(final View v, final int position){
        //accountInfoArrayList = new ArrayList<>();
        String friend_account = friendRequestsArrayList.get(position).getUserId();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", mAuth.getUid());
        account_details.put("first_name", friendRequestsArrayList.get(position).getFirst_name());
        account_details.put("last_name", friendRequestsArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","friends");
        account_details.put("user_id", friendRequestsArrayList.get(position).getUserId());

        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(friend_account+"_request")
                .update(account_details);
    }

    @Override
    public int getItemCount() {
        return friendRequestsArrayList.size();
    }
    public void clear() {
        final int size = friendRequestsArrayList.size();
        friendRequestsArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }
    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }
}
