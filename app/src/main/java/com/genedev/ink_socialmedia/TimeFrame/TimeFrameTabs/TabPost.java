package com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.genedev.ink_socialmedia.PostUtils.PostImageArray;
import com.genedev.ink_socialmedia.PostUtils.PostViewModel;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewAdapterPostItemImages;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewAdapterUtilUserPost;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewHolderPostUtil;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;

public class TabPost extends Fragment {

    private TextView tv_address, tv_status, tv_bday, tv_age;
    private HelperUtils helperUtils;
    private RecyclerViewHolderPostUtil recyclerViewHolderPostUtil;
    private PostViewModel postViewModel;
    private ArrayList<PostViewModel> postViewModelArrayList;
    private PostImageArray postImageArray;
    private ArrayList<PostImageArray> postImageArrayArrayList;
    private RecyclerViewAdapterUtilUserPost adapterUtil;

    private RecyclerViewAdapterPostItemImages adapterPostItemImages;

    private LinearLayoutManager llm;
    private RecyclerView recyclerView;
    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.time_fragment_post, container, false);

        helperUtils = new HelperUtils();
        helperUtils.setPersistence(firestoreDB);

        tv_address = v.findViewById(R.id.tv_address);
        tv_status = v.findViewById(R.id.tv_status);
        tv_bday = v.findViewById(R.id.tv_bday);
        tv_age = v.findViewById(R.id.tv_age);

        postViewModelArrayList = new ArrayList<>();
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        setRetainInstance(true);

        llm.setItemPrefetchEnabled(true);
        recyclerView = v.findViewById(R.id.user_post_list);



        setDataToViews();
        loadPostsData();
        return v;
    }

    private void setDataToViews(){

        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details").document("account_info")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String address, status, bday, age;

                        address = (String) document.get("address");
                        status = (String) document.get("status");
                        bday = (String) document.get("birthday");
                        age = (String) document.get("age");

                        helperUtils.setPersistence(firestoreDB);
                        tv_address.setText(address);
                        tv_status.setText(status);
                        tv_bday.setText("Born on "+bday);
                        tv_age.setText(age+" years of age");

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void loadPostsData(){

        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("posts")
                .document("published")
                .collection("post")
                .whereEqualTo("user_id", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                postViewModel = document.toObject(PostViewModel.class);
                                postViewModelArrayList.add(postViewModel);

                                //runLayoutAnimation(recyclerView);
                                //recyclerView.setVisibility(View.INVISIBLE);
                                Collections.reverse(postViewModelArrayList);
                                adapterUtil = new RecyclerViewAdapterUtilUserPost(getApplicationContext(),postViewModelArrayList);

                                recyclerView.setAdapter(adapterUtil);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(llm);
                                recyclerView.setNestedScrollingEnabled(false);
                                recyclerView.setItemViewCacheSize(100);
                                recyclerView.setDrawingCacheEnabled(true);
                                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                            }

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
    @Override
    public void onDestroy(){
        helperUtils.setPersistence(firestoreDB);
        super.onDestroy();
    }

    @Override
    public void onPause(){
        helperUtils.setPersistence(firestoreDB);
        super.onPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onResume(){
        super.onResume();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
}