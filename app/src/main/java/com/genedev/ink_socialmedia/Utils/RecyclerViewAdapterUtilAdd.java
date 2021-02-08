package com.genedev.ink_socialmedia.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Splashscreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterUtilAdd extends RecyclerView.Adapter<RecyclerViewHolderUtil> {

    private Context home;
    private ArrayList<Friends> friendsArrayList;
    private ArrayList<Friends> friendsArrayList1;
    private Friends friends = new Friends();
    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo = new AccountInfo();
    private RecyclerViewHolderUtil holderUtil;
    private ImageButton show_friend_requests;
    private String requested;
    private String profile_img;
    private boolean isSelected = false;

    public RecyclerViewAdapterUtilAdd(Context home, ArrayList<Friends> friendsArrayList){
        this.home = home;
        this.friendsArrayList = friendsArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolderUtil onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.friends_item_lists_suggested, parent, false);

        return new RecyclerViewHolderUtil(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolderUtil holder, final int position) {
        holder.progressBar.setIndeterminate(false);
        final HelperUtils helperUtils = new HelperUtils();

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

        firestoreDB.collection("users")
                .document(friendsArrayList.get(position).getUser_id())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(U_id+"_request")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            try{
                                String requested = String.valueOf(Objects.requireNonNull(task.getResult()).getData().values());
                                Log.d("Requested", requested+"");
                                if (requested.contains("true")){
                                    holder.btn_request.setText("Cancel");
                                    holder.btn_request.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            cancelUpdateInUsersCurrentAccount(holder.btn_request, position);
                                            cancelUpdateInUsersFriendAccount(holder.btn_request, position);
                                            holder.btn_request.setText("Request");
                                            emptyFields(holder.btn_request, "Friend request canceled");
                                            isSelected = !isSelected;

                                            emptyFields(holder.btn_request,"Refreshing...");
                                            Context c = home;
                                            Intent i = new Intent(c, Home.class);
                                            c.startActivity(i);
                                        }
                                    });
                                }
                                if (requested.contains("friends")){
                                    holder.btn_request.setText("Friends");
                                    holder.btn_request.setEnabled(false);
                                    //friendsArrayList.remove(position).getUser_id();
                                }
                            }
                            catch (Exception e){
                                Log.d("Tag", "error");
                            }
                        }
                    }
                });

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(friendsArrayList.get(position).getUser_id()+"_request")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            try{
                                String requested = String.valueOf(Objects.requireNonNull(task.getResult()).getData().values());
                                Log.d("Requested", requested+"");
                                if (requested.contains("true")){
                                    holder.btn_request.setText("Requested You");
                                    holder.btn_request.setEnabled(false);
                                }if (requested.contains("friends")){
                                    holder.btn_request.setText("Friends");
                                    holder.btn_request.setEnabled(false);
                                    //friendsArrayList.remove(position).getUser_id();
                                }
                            }
                            catch (Exception e){
                                Log.d("Tag", "error");
                            }
                        }
                    }
                });

        final int TIME_OUT = 3000;
                try{

                    holder.tv_name.setText(friendsArrayList.get(position).getFirst_name()+" "+friendsArrayList.get(position).getLast_name());
                    try{
                        profile_img = friendsArrayList.get(position).getImg_profile();
                        Uri uri1;

                        if (profile_img != null){
                            uri1 = Uri.parse(profile_img);
                            Picasso.get().load(uri1)
                                    .placeholder(R.drawable.circular_bg)
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
                    holder.tv_name.setText(friendsArrayList.get(0).getFirst_name()+" "+friendsArrayList.get(0).getLast_name());
                    try{
                        profile_img = friendsArrayList.get(0).getImg_profile();
                        Uri uri1;

                        if (profile_img != null){
                            uri1 = Uri.parse(profile_img);
                            Picasso.get().load(uri1)
                                    .placeholder(R.drawable.circular_bg)
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
                        Log.d("Tag", "error");
                    }
                }


        holder.btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setIndeterminate(true);
                if (!isSelected) {
                    addSendInUsersCurrentAccount(holder.btn_request, position);
                    addSendInUsersFriendAccount(holder.btn_request, position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.progressBar.setIndeterminate(false);
                            holder.btn_request.setText("Cancel");
                            emptyFields(holder.btn_request, "Friend request sent");
                            }
                        }, TIME_OUT);
                    isSelected = true;
                } else {
                    cancelUpdateInUsersCurrentAccount(holder.btn_request, position);
                    cancelUpdateInUsersFriendAccount(holder.btn_request, position);new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.progressBar.setIndeterminate(false);
                            holder.btn_request.setText("Request");
                            emptyFields(holder.btn_request, "Friend request canceled");
                        }
                    }, TIME_OUT);
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
        account_details.put(account_id+"(request)","true");
        account_details.put("user_id", friendsArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(friendsArrayList.get(position).getUser_id())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(U_id+"_request")
                .set(account_details);


    }
    private void addSendInUsersCurrentAccount(final View v, final int position){
        accountInfoArrayList = new ArrayList<>();
        String friend_account = friendsArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));

        final Map<String, Object> account_details = new HashMap<>();
        account_details.put("requester_id", U_id);
        account_details.put("first_name", friendsArrayList.get(position).getFirst_name());
        account_details.put("last_name", friendsArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","true");
        account_details.put("user_id", friendsArrayList.get(position).getUser_id());

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
        accountInfoArrayList = new ArrayList<>();
        String account_id = U_id.substring(0, Math.min(U_id.length(), 5));

        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", friendsArrayList.get(position).getUser_id());
        account_details.put("first_name",  accountInfo.getFirst_name());
        account_details.put("last_name",  accountInfo.getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", U_id);

        firestoreDB.collection("users")
                .document(friendsArrayList.get(position).getUser_id())
                .collection("friends")
                .document("friend_requests")
                .collection("users")
                .document(U_id+"_request")
                .set(account_details);
    }

    private void cancelUpdateInUsersCurrentAccount(final View v, final int position){

        accountInfoArrayList = new ArrayList<>();
        String friend_account = friendsArrayList.get(position).getUser_id();
        String account_id = friend_account.substring(0, Math.min(friend_account.length(), 5));
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("user_id", U_id);
        account_details.put("first_name",  friendsArrayList.get(position).getFirst_name());
        account_details.put("last_name",  friendsArrayList.get(position).getLast_name());
        account_details.put(account_id+"(request)","false");
        account_details.put("requester_id", friendsArrayList.get(position).getUser_id());

        firestoreDB.collection("users")
                .document(U_id)
                .collection("friends")
                .document("sent_requests")
                .collection("users")
                .document(friend_account+"_request")
                .set(account_details);
    }

    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }

    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }
}
