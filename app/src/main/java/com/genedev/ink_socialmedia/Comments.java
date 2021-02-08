package com.genedev.ink_socialmedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.ListCommentsViewModel;
import com.genedev.ink_socialmedia.PostUtils.CommentsUtils.RecyclerViewAdapterPostComments;
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
import java.util.Collections;
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

public class Comments extends Activity {

    private String doc_name, user_id, post_date_document, timeString, monthName, short_date;
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

    private LinearLayoutManager llm;

    private AccountInfo accountInfo = new AccountInfo();
    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();

    private ListCommentsViewModel listCommentsViewModel = new ListCommentsViewModel();
    private ArrayList<ListCommentsViewModel> listCommentsViewModelArrayList = new ArrayList<>();

    private RecyclerViewAdapterPostComments adapterPostComments;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_comments);

        helperUtils = new HelperUtils();

        Bundle bundle = getIntent().getExtras();
        doc_name = bundle.getString("doc_name");
        user_id = bundle.getString("user_id");

        btn_send = findViewById(R.id.btn_send_reply);
        profile_img = findViewById(R.id.profile_img);
        ed_comment = findViewById(R.id.ed_comment);
        sending_progress = findViewById(R.id.progress_sent_reply);
        recyclerViewComments = findViewById(R.id.recycler_replies);
        btn_back = findViewById(R.id.btn_back);
        toolbar_comment_size = findViewById(R.id.comment_size);
        toolbar_holder = findViewById(R.id.toolbar_holder);
        comment_box = findViewById(R.id.comment_box);

        helperUtils.startAlphaAnimation(toolbar_holder, View.VISIBLE);
        helperUtils.startAlphaAnimation(comment_box, View.VISIBLE);

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

                                Picasso.get().load(uri).centerCrop().resize(40,40).into(profile_img);
                                helperUtils.startAlphaAnimation(profile_img, View.VISIBLE);
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

        loadComments();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sending_progress.setVisibility(VISIBLE);
                btn_send.setEnabled(false);
                btn_send.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.state_list_btn_send_progressing)));
                createComment(doc_name,user_id, ed_comment.getText().toString(), post_date_document, short_date, timeString);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Comments.this, Home.class);
                startActivity(i);
                Comments.this.finish();
            }
        });
    }

    private void loadComments(){
        firestoreDB.collection("users")
                .document(user_id)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                listCommentsViewModel = document.toObject(ListCommentsViewModel.class);
                                listCommentsViewModelArrayList.add(listCommentsViewModel);

                                Collections.reverse(listCommentsViewModelArrayList);
                                toolbar_comment_size.setText(String.valueOf(listCommentsViewModelArrayList.size()) + " Comments");
                                helperUtils.startAlphaAnimation(toolbar_comment_size, View.VISIBLE);

                                if (listCommentsViewModelArrayList.size() ==0){
                                    toolbar_comment_size.setText("No one commented to this post");
                                    helperUtils.startAlphaAnimation(toolbar_comment_size, View.VISIBLE);
                                }
                                adapterPostComments = new RecyclerViewAdapterPostComments(getApplicationContext(),listCommentsViewModelArrayList, user_id, doc_name, post_date_document);
                                recyclerViewComments.setAdapter(adapterPostComments);

                                runLayoutAnimation(recyclerViewComments);
                            }
                        }
                    }
                });
    }

    private void createComment(String doc_name, String user_id, String comment, String date_doc, String date, String time){


        Map<String, Object> comment_up = new HashMap<>();
        comment_up.put("comment", comment);
        comment_up.put("user_id", mAuth.getUid());
        comment_up.put("first_name", accountInfoArrayList.get(0).getFirst_name());
        comment_up.put("last_name", accountInfoArrayList.get(0).getLast_name());
        comment_up.put("img_profile", accountInfoArrayList.get(0).getImg_profile());
        comment_up.put("date", date);
        comment_up.put("time", time);

        firestoreDB.collection("users")
                .document(user_id)
                .collection("posts")
                .document("published")
                .collection("post")
                .document(doc_name)
                .collection("comments")
                .document(date_doc)
                .set(comment_up)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ed_comment.getText().clear();
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
