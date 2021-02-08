package com.genedev.ink_socialmedia.PostUtils.CommentsUtils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterPostCommentsReply extends RecyclerView.Adapter<RecyclerViewAdapterPostCommentsReply.ViewHolder> {

    private Context context;
    private ArrayList<ListCommentsViewModel> listCommentsViewModelArrayList;
    private ListCommentsViewModel listCommentsViewModel;
    private String doc_name, user_id, post_date_document, timeString, monthName, short_date;
    private AccountInfo accountInfo = new AccountInfo();
    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();

    private HelperUtils helperUtils = new HelperUtils();

    private ListCommentsReplyViewModel listCommentsReplyViewModel = new ListCommentsReplyViewModel();
    private ArrayList<ListCommentsReplyViewModel> listCommentsReplyViewModelArrayList;

    private String  final_comment_date;
    public RecyclerViewAdapterPostCommentsReply(Context context, ArrayList<ListCommentsReplyViewModel> listCommentsReplyViewModelArrayList) {

        this.listCommentsReplyViewModelArrayList = listCommentsReplyViewModelArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_comments_reply, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

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

        if (listCommentsReplyViewModelArrayList.size() > 1){
            Uri uri = Uri.parse(listCommentsReplyViewModelArrayList.get(position).getImg_profile());

            Picasso.get().load(uri)
                    .fit()
                    .centerCrop()
                    .into(holder.profile_img);

            HelperUtils.startAlphaAnimation(holder.profile_img, View.VISIBLE);

            holder.full_name.setText(listCommentsReplyViewModelArrayList.get(position).getFirst_name()+ " " +listCommentsReplyViewModelArrayList.get(position).getLast_name());
            holder.full_date_time.setText(listCommentsReplyViewModelArrayList.get(position).getDate()+ " ("+listCommentsReplyViewModelArrayList.get(position).getTime()+")");
            holder.comment_text.setText(listCommentsReplyViewModelArrayList.get(position).getComment());
        }
        if (listCommentsReplyViewModelArrayList.size() == 1){
            Uri uri = Uri.parse(listCommentsReplyViewModelArrayList.get(0).getImg_profile());

            Picasso.get().load(uri)
                    .fit()
                    .centerCrop()
                    .into(holder.profile_img);

            HelperUtils.startAlphaAnimation(holder.profile_img, View.VISIBLE);

            holder.full_name.setText(listCommentsReplyViewModelArrayList.get(0).getFirst_name()+ " " +listCommentsReplyViewModelArrayList.get(0).getLast_name());
            holder.full_date_time.setText(listCommentsReplyViewModelArrayList.get(0).getDate()+ " ("+listCommentsReplyViewModelArrayList.get(0).getTime()+")");
            holder.comment_text.setText(listCommentsReplyViewModelArrayList.get(0).getComment());
        }
    }

    @Override
    public int getItemCount() {
        return listCommentsReplyViewModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profile_img, btn_reply, btn_more, profile_img_reply;
        public TextView full_name, full_date_time, comment_text;
        public RecyclerView recycler_replies;
        public EditText ed_reply;
        public ImageButton btn_send_reply;
        public ProgressBar progress_sent_reply;
        public MaterialCardView comment_box;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_img = itemView.findViewById(R.id.profile_img);
            full_name = itemView.findViewById(R.id.full_name);
            full_date_time = itemView.findViewById(R.id.date_time);
            comment_text = itemView.findViewById(R.id.comment_text);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_reply = itemView.findViewById(R.id.btn_reply);
            ed_reply = itemView.findViewById(R.id.ed_comment);
            recycler_replies = itemView.findViewById(R.id.recycler_replies);
            btn_send_reply = itemView.findViewById(R.id.btn_send_reply);
            progress_sent_reply = itemView.findViewById(R.id.progress_sent_reply);
            comment_box = itemView.findViewById(R.id.comment_box);

        }


    }
}