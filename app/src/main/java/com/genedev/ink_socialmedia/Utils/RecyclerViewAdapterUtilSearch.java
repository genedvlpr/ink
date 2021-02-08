package com.genedev.ink_socialmedia.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.genedev.ink_socialmedia.Home;
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

public class RecyclerViewAdapterUtilSearch extends RecyclerView.Adapter<RecyclerViewHolderUtil> {

    private Context home;
    private ArrayList<FriendsSearch> friendsArrayList;
    private ArrayList<Friends> friendsArrayList1;
    private Friends friends = new Friends();
    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo = new AccountInfo();
    private RecyclerViewHolderUtil holderUtil;
    private ImageButton show_friend_requests;
    private String requested;
    private String profile_img;
    private boolean isSelected = false;

    public RecyclerViewAdapterUtilSearch(Context home, ArrayList<FriendsSearch> friendsArrayList){
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
        holder.item_card.setVisibility(View.INVISIBLE);
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

        final int TIME_OUT = 3000;

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
