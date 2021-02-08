package com.genedev.ink_socialmedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.ListCommentsReplyViewModel;
import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.ListCommentsViewModel;
import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.RecyclerViewAdapterPostComments;
import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.RecyclerViewAdapterPostCommentsReply;
import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.RecyclerViewAdapterPostDetailedComments;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FieldPath;
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

import static android.view.View.VISIBLE;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class CommentsReply extends Activity {

    private String doc_name, user_id, post_date_document, timeString, monthName, short_date, specific_comment_date;
    private CircleImageView profile_img;
    private SimpleDateFormat month_date, time;
    private EditText ed_comment;
    private HelperUtils helperUtils;
    private Calendar c;
    private int mYear, mMonth, mDay;
    private ImageButton btn_send;
    private ProgressBar sending_progress;
    private RecyclerView recyclerViewComments;
    private Button btn_back;
    private TextView toolbar_comment_size;
    private RelativeLayout toolbar_holder;
    private MaterialCardView comment_box;

    private String commenter, comment_date, comment_time, comment_profile, comments;

    private LinearLayoutManager llm;

    private AccountInfo accountInfo = new AccountInfo();
    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();

    private ListCommentsViewModel listCommentsViewModel = new ListCommentsViewModel();
    private ArrayList<ListCommentsViewModel> listCommentsViewModelArrayList = new ArrayList<>();

    private RecyclerViewAdapterPostCommentsReply adapterPostCommentsReply;

    private CircleImageView commenter_profile_img, profile_img_reply;
    private TextView commenter_full_name, full_date_time, comment_text;
    private RecyclerView recycler_replies;
    private EditText ed_reply;
    private ImageButton btn_send_reply, btn_reply, btn_more;
    private ProgressBar progress_sent_reply;
    private MaterialCardView reply_comment_box;

    private ListCommentsReplyViewModel listCommentsReplyViewModel = new ListCommentsReplyViewModel();
    private ArrayList<ListCommentsReplyViewModel> listCommentsReplyViewModelArrayList = new ArrayList<>();

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_comments_detailed);

        helperUtils = new HelperUtils();

        Bundle bundle = getIntent().getExtras();
        doc_name = bundle.getString("doc_name");
        user_id = bundle.getString("user_id");
        specific_comment_date = bundle.getString("specific_comment_date");

        commenter = bundle.getString("commenter");
        comment_date = bundle.getString("date");
        comment_time = bundle.getString("time");
        comment_profile = bundle.getString("profile_img");
        comments = bundle.getString("comment");

        profile_img = findViewById(R.id.profile_img);
        ed_comment = findViewById(R.id.ed_comment);
        sending_progress = findViewById(R.id.progress_sent_reply);
        recyclerViewComments = findViewById(R.id.recycler_replies);
        btn_back = findViewById(R.id.btn_back);
        toolbar_comment_size = findViewById(R.id.comment_size);
        toolbar_holder = findViewById(R.id.toolbar_holder);
        comment_box = findViewById(R.id.comment_box);

        commenter_profile_img = findViewById(R.id.profile_img);
        commenter_full_name = findViewById(R.id.full_name);
        full_date_time = findViewById(R.id.date_time);
        comment_text = findViewById(R.id.comment_text);
        btn_more = findViewById(R.id.btn_more);
        ed_reply = findViewById(R.id.ed_comment);
        recycler_replies = findViewById(R.id.recycler_replies);
        btn_send_reply = findViewById(R.id.btn_send_reply);
        progress_sent_reply = findViewById(R.id.progress_sent_reply);
        comment_box = findViewById(R.id.comment_box);
        profile_img_reply = findViewById(R.id.profile_img_reply);

        commenter_full_name.setText(commenter);
        comment_text.setText(comments);
        full_date_time.setText(comment_date+" ("+comment_time+")");

        Uri uri_commenter = Uri.parse(comment_profile);

        Picasso.get().load(uri_commenter).centerCrop().resize(40,40).into(commenter_profile_img);
        HelperUtils.startAlphaAnimation(commenter_profile_img, View.VISIBLE);
        HelperUtils.startAlphaAnimation(toolbar_holder, View.VISIBLE);
        HelperUtils.startAlphaAnimation(comment_box, View.VISIBLE);

        Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        month_date = new SimpleDateFormat("MMMM");
        monthName = month_date.format(c.getTime());

        long date = System.currentTimeMillis();
        time = new SimpleDateFormat("hh:mm:ss a");
        timeString = time.format(date);

        post_date_document = monthName + " " + mDay + ", " + mYear + " (" + timeString + ")";
        short_date =  monthName + " " + mDay + ", " + mYear;

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

                                Picasso.get().load(uri).centerCrop().resize(40,40).into(profile_img_reply);
                                helperUtils.startAlphaAnimation(profile_img_reply, View.VISIBLE);
                            }
                        }
                    }
                });

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setItemPrefetchEnabled(true);

        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(llm);
        recyclerViewComments.setNestedScrollingEnabled(false);
        recyclerViewComments.setItemViewCacheSize(100);
        recyclerViewComments.setDrawingCacheEnabled(true);
        recyclerViewComments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        loadReplies();

        btn_send_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sending_progress.setVisibility(VISIBLE);
                btn_send_reply.setEnabled(false);
                btn_send_reply.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.state_list_btn_send_progressing)));
                createComment(doc_name,user_id, ed_comment.getText().toString(), post_date_document, short_date, timeString);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CommentsReply.this, Home.class);
                startActivity(i);
                CommentsReply.this.finish();
            }
        });


    }



    private void loadReplies(){
        firestoreDB.collection("users")
                .document(user_id)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(String.valueOf(doc_name))
                .collection("comments")
                .document(comment_date+" ("+comment_time+")")
                .collection("replies")
                .whereEqualTo("comment_time", comment_time)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listCommentsReplyViewModel = document.toObject(ListCommentsReplyViewModel.class);
                                listCommentsReplyViewModelArrayList.add(listCommentsReplyViewModel);

                                toolbar_comment_size.setText(listCommentsReplyViewModelArrayList.size()+" Replies");

                                if (listCommentsReplyViewModelArrayList.size() == 0){
                                    toolbar_comment_size.setText("No one replies to this comment");
                                    helperUtils.startAlphaAnimation(toolbar_comment_size, View.VISIBLE);
                                }

                                helperUtils.startAlphaAnimation(toolbar_comment_size, View.VISIBLE);
                                adapterPostCommentsReply = new RecyclerViewAdapterPostCommentsReply(getApplicationContext(), listCommentsReplyViewModelArrayList);
                                recycler_replies.setAdapter(adapterPostCommentsReply);
                                runLayoutAnimation(recycler_replies);
                            }
                        }
                    }
                });
    }

    private void createComment(String doc_name, String user_id, String comment, String date_doc, String date, String time){


        progress_sent_reply.setVisibility(View.VISIBLE);
        btn_send_reply.setEnabled(false);
        btn_send_reply.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.state_list_btn_send_progressing)));

        Map<String, Object> comment_up = new HashMap<>();
        comment_up.put("comment", ed_reply.getText().toString());
        comment_up.put("user_id", mAuth.getUid());
        comment_up.put("first_name", accountInfoArrayList.get(0).getFirst_name());
        comment_up.put("last_name", accountInfoArrayList.get(0).getLast_name());
        comment_up.put("img_profile", accountInfoArrayList.get(0).getImg_profile());
        comment_up.put("date", short_date);
        comment_up.put("time", timeString);
        comment_up.put("date_time", specific_comment_date);
        comment_up.put("comment_time", comment_time);

        firestoreDB.collection("users")
                .document(user_id)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(String.valueOf(doc_name))
                .collection("comments")
                .document(comment_date+" ("+comment_time+")")
                .collection("replies")
                .document(post_date_document)
                .set(comment_up)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ed_reply.getText().clear();
                            recreate();
                        }

                    }
                });
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
