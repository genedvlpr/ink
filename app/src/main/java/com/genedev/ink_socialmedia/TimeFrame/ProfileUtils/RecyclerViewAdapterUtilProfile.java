package com.genedev.ink_socialmedia.TimeFrame.ProfileUtils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterUtilProfile extends RecyclerView.Adapter<RecyclerViewAdapterUtilProfile.ViewHolder> {

    private Context home;

    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo = new AccountInfo();
    private Uri uri, image_uri;
    private String field;

    public RecyclerViewAdapterUtilProfile(Context home, ArrayList<AccountInfo> accountInfoArrayList, Uri image_uri, String field){
        this.home = home;
        this.accountInfoArrayList = accountInfoArrayList;
        this.image_uri = image_uri;
        this.field = field;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_time_frame, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterUtilProfile.ViewHolder holder, int position) {
        accountInfoArrayList = new ArrayList<>();
        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("account_details")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.exists()) {
                                    String url = String.valueOf(image_uri);
                                    if (!url.equals("")) {
                                        Map<String, Object> account_details = new HashMap<>();
                                        account_details.put(field, url);

                                        firestoreDB.collection("users")
                                                .document(mAuth.getUid())
                                                .collection("account_details")
                                                .document("account_info")
                                                .update(account_details);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return accountInfoArrayList.size();
    }

    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profile_image;
        public ImageView header_photo;
        public ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.prof_pic);
            header_photo = itemView.findViewById(R.id.cover_photo);
        }


    }
}
