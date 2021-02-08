package com.genedev.ink_socialmedia.PostUtils.CommentsUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterPostDetailedComments extends RecyclerView.Adapter<RecyclerViewAdapterPostDetailedComments.ViewHolder> {

    private final RecyclerView.RecycledViewPool viewPool;
    private Context context;
    private ArrayList<ListCommentsViewModel> listCommentsViewModelArrayList;
    private ListCommentsViewModel listCommentsViewModel;

    private AccountInfo accountInfo = new AccountInfo();
    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();

    private HelperUtils helperUtils = new HelperUtils();

    private ListCommentsReplyViewModel listCommentsReplyViewModel = new ListCommentsReplyViewModel();
    private ArrayList<ListCommentsReplyViewModel> listCommentsReplyViewModelArrayList = new ArrayList<>();

    private RecyclerViewAdapterPostCommentsReply adapterPostCommentsReply;

    private int mYear, mMonth, mDay;

    private SimpleDateFormat month_date, time;

    private Calendar c;
    private String doc_name, user_id, post_date_document, timeString, monthName, short_date;
    private LinearLayoutManager llm;
    private String comment_date;
    private String final_comment_date, specific_comment_date;

    public RecyclerViewAdapterPostDetailedComments(Context context, ArrayList<ListCommentsViewModel> listCommentsViewModelArrayList, String user_id, String doc_name, String post_date_document) {

        this.listCommentsViewModelArrayList = listCommentsViewModelArrayList;
        this.context = context;
        this.user_id = user_id;
        this.post_date_document = post_date_document;
        this.doc_name = doc_name;

        viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_comments, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        month_date = new SimpleDateFormat("MMMM");
        monthName = month_date.format(c.getTime());

        long date = System.currentTimeMillis();
        time = new SimpleDateFormat("hh:mm:ss a");
        timeString = time.format(date);

        comment_date = monthName + " " + mDay + ", " + mYear + " (" + timeString + ")";
        short_date =  monthName + " " + mDay + ", " + mYear;

        llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setItemPrefetchEnabled(true);

        if (listCommentsViewModelArrayList.size() == 1){
            final_comment_date = listCommentsViewModelArrayList.get(0).getDate()+" ("+listCommentsViewModelArrayList.get(0).getTime()+")";
        }
        if (listCommentsViewModelArrayList.size() > 1){
            final_comment_date = listCommentsViewModelArrayList.get(position).getDate()+" ("+listCommentsViewModelArrayList.get(position).getTime()+")";
        }

        holder.recycler_replies.setHasFixedSize(true);
        holder.recycler_replies.setLayoutManager(llm);
        holder.recycler_replies.setNestedScrollingEnabled(false);
        holder.recycler_replies.setItemViewCacheSize(100);
        holder.recycler_replies.setDrawingCacheEnabled(true);
        holder.recycler_replies.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

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

                                Uri uri = Uri.parse(accountInfoArrayList.get(0).getImg_profile());
                                Picasso.get().load(uri).centerCrop().resize(40,40).into(holder.profile_img_reply);
                                helperUtils.startAlphaAnimation(holder.profile_img_reply, View.VISIBLE);

                            }
                        }
                    }
                });

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.recycler_replies.setVisibility(View.VISIBLE);
                holder.comment_box.setVisibility(View.VISIBLE);


            }
        });
        try{

            firestoreDB.collection("users")
                    .document(user_id)
                    .collection("posts")
                    .document("published")
                    .collection("post")
                    .document(String.valueOf(doc_name))
                    .collection("comments")
                    .document(final_comment_date)
                    .collection("replies")
                    .whereEqualTo("date_time", final_comment_date)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    listCommentsReplyViewModel = document.toObject(ListCommentsReplyViewModel.class);
                                    listCommentsReplyViewModelArrayList.add(listCommentsReplyViewModel);


                                }
                                adapterPostCommentsReply = new RecyclerViewAdapterPostCommentsReply(context,listCommentsReplyViewModelArrayList);
                                holder.recycler_replies.setAdapter(adapterPostCommentsReply);
                            }

                        }
                    });


        }catch (Exception e){

        }


        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listCommentsViewModelArrayList.size() == 1){
                    holder.ed_reply.setText(Html.fromHtml("<b>"+"@"+listCommentsViewModelArrayList.get(0).getFirst_name()+ "</b>"+" "));
                    specific_comment_date = listCommentsViewModelArrayList.get(0).getDate()+ " ("+listCommentsViewModelArrayList.get(0).getTime()+")";
                    Log.d("Date", specific_comment_date+"");
                }
                if (listCommentsViewModelArrayList.size() > 1){
                    holder.ed_reply.setText(Html.fromHtml("<b>"+"@"+listCommentsViewModelArrayList.get(position).getFirst_name()+ "</b>"+" "));
                    specific_comment_date = listCommentsViewModelArrayList.get(position).getDate()+ " ("+listCommentsViewModelArrayList.get(position).getTime()+")";
                    Log.d("Date", specific_comment_date+"");
                }

            }
        });


        holder.btn_send_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progress_sent_reply.setVisibility(View.VISIBLE);
                holder.btn_send_reply.setEnabled(false);
                holder.btn_send_reply.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.state_list_btn_send_progressing)));


                Map<String, Object> comment_up = new HashMap<>();
                comment_up.put("comment", holder.ed_reply.getText().toString());
                comment_up.put("user_id", mAuth.getUid());
                comment_up.put("first_name", accountInfoArrayList.get(0).getFirst_name());
                comment_up.put("last_name", accountInfoArrayList.get(0).getLast_name());
                comment_up.put("img_profile", accountInfoArrayList.get(0).getImg_profile());
                comment_up.put("date", short_date);
                comment_up.put("time", timeString);
                comment_up.put("date_time", specific_comment_date);

                firestoreDB.collection("users")
                        .document(user_id)
                        .collection("posts")
                        .document("published")
                        .collection("post")
                        .document(String.valueOf(doc_name))
                        .collection("comments")
                        .document(specific_comment_date)
                        .collection("replies")
                        .document(comment_date)
                        .set(comment_up)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    holder.ed_reply.getText().clear();
                                    notifyDataSetChanged();
                                }

                            }
                        });
            }
        });

        if (listCommentsViewModelArrayList.size() > 1){
            Uri uri = Uri.parse(listCommentsViewModelArrayList.get(position).getImg_profile());

            Picasso.get().load(uri)
                    .fit()
                    .centerCrop()
                    .into(holder.profile_img);

            HelperUtils.startAlphaAnimation(holder.profile_img, View.VISIBLE);

            holder.full_name.setText(listCommentsViewModelArrayList.get(position).getFirst_name()+ " " +listCommentsViewModelArrayList.get(position).getLast_name());
            holder.full_date_time.setText(listCommentsViewModelArrayList.get(position).getDate()+ " ("+listCommentsViewModelArrayList.get(position).getTime()+")");
            holder.comment_text.setText(listCommentsViewModelArrayList.get(position).getComment());
        }
        if (listCommentsViewModelArrayList.size() == 1){
            Uri uri = Uri.parse(listCommentsViewModelArrayList.get(0).getImg_profile());

            Picasso.get().load(uri)
                    .fit()
                    .centerCrop()
                    .into(holder.profile_img);

            HelperUtils.startAlphaAnimation(holder.profile_img, View.VISIBLE);

            holder.full_name.setText(listCommentsViewModelArrayList.get(0).getFirst_name()+ " " +listCommentsViewModelArrayList.get(0).getLast_name());
            holder.full_date_time.setText(listCommentsViewModelArrayList.get(0).getDate()+ " ("+listCommentsViewModelArrayList.get(0).getTime()+")");
            holder.comment_text.setText(listCommentsViewModelArrayList.get(0).getComment());
        }
    }

    @Override
    public int getItemCount() {
        return listCommentsViewModelArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profile_img, profile_img_reply;
        public TextView full_name, full_date_time, comment_text;
        public RecyclerView recycler_replies;
        public EditText ed_reply;
        public ImageButton btn_send_reply, btn_reply, btn_more;
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
            profile_img_reply = itemView.findViewById(R.id.profile_img_reply);
        }


    }
}