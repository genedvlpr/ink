package com.genedev.ink_socialmedia.HomeFragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.genedev.ink_socialmedia.AddPost;
import com.genedev.ink_socialmedia.PostUtils.PostImageArray;
import com.genedev.ink_socialmedia.PostUtils.PostViewModel;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewAdapterPostItemImages;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewAdapterUtilFriendsPost;
import com.genedev.ink_socialmedia.PostUtils.RecyclerViewHolderPostUtil;
import com.genedev.ink_socialmedia.StoriesUtil.AddStory;
import com.genedev.ink_socialmedia.StoriesUtil.RecyclerViewAdapterUtilStories;
import com.genedev.ink_socialmedia.StoriesUtil.StoriesModel;
import com.genedev.ink_socialmedia.StoriesUtil.ViewStories;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.Friends;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.genedev.ink_socialmedia.StoriesUtil.Stories;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class FragmentFeeds extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View rootView;
    private HelperUtils helperUtils;
    private RecyclerViewHolderPostUtil recyclerViewHolderPostUtil;
    private PostViewModel postViewModel;
    private ArrayList<PostViewModel> postViewModelArrayList;
    private Stories stories;
    private ArrayList<Stories> storiesArrayList;
    private PostImageArray postImageArray;
    private ArrayList<PostImageArray> postImageArrayArrayList;
    private RecyclerViewAdapterUtilFriendsPost adapterUtil;

    private RecyclerViewAdapterUtilStories story_adapterUtil;

    private RecyclerViewAdapterPostItemImages adapterPostItemImages;

    private LinearLayoutManager llm, llm1;
    private RecyclerView recyclerView, recyclerView_stories;
    private Chip chip;
    private FragmentActivity myContext;
    private ArrayList<Friends> friendsArrayList;
    private Friends friends = new Friends();
    private String profile,final_users;

    private MaterialCardView card_story;

    private ImageButton strory_add;
    private ImageView add_story_background, story_background;
    private CircleImageView story_profile_pic;

    private RecyclerView friend_story_lists;
    private FloatingActionButton fab_post, fab_story, fab_edit_profile, fab_qr_code;

    private StoriesModel storiesModel;
    private ArrayList<StoriesModel> storiesModelArrayList;

    private AccountInfo accountInfo;
    private ArrayList<AccountInfo> accountInfoArrayList;

    public static FragmentFeeds newInstance(String param1, String param2) {
        FragmentFeeds fragment2 = new FragmentFeeds();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment2.setArguments(args);
        return fragment2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment_feeds, container, false);
        chip = rootView.findViewById(R.id.post);

        fab_post = rootView.findViewById(R.id.fab_post);
        fab_story = rootView.findViewById(R.id.fab_story);
        fab_edit_profile = rootView.findViewById(R.id.fab_edit_profile);
        fab_qr_code = rootView.findViewById(R.id.fab_qr_code);

        story_background = rootView.findViewById(R.id.story_background);
        story_profile_pic = rootView.findViewById(R.id.story_profile_pic);
        card_story = rootView.findViewById(R.id.card_add_story);
        //friend_story_lists = rootView.findViewById(R.id.friends_story_list);

        //dd_story_background.setClipToOutline(true);

        helperUtils = new HelperUtils();
        helperUtils.setPersistence(firestoreDB);

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddPost.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddPost.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        postViewModelArrayList = new ArrayList<>();
        storiesModelArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();

        accountInfoArrayList = new ArrayList<>();

        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm1 = new LinearLayoutManager(getActivity());
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);

        //storiesModelArrayList.clear();
        setRetainInstance(true);

        llm.setItemPrefetchEnabled(true);
        llm1.setItemPrefetchEnabled(true);

        recyclerView = rootView.findViewById(R.id.friends_post_list);
        recyclerView_stories = rootView.findViewById(R.id.friends_story_list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //recyclerView_stories.setHasFixedSize(true);
        recyclerView_stories.setLayoutManager(llm1);
        //recyclerView_stories.setNestedScrollingEnabled(false);
        recyclerView_stories.setItemViewCacheSize(100);
        recyclerView_stories.setDrawingCacheEnabled(true);
        recyclerView_stories.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        fab_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddStory.class);
                startActivity(i);
            }
        });

        try {
            loadOwnStory();
            loadStories();
        }catch (Exception e){

        }

        loadPostsData();
        return rootView;
    }
    private void loadStories(){
        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("friends")
                .document("accepted")
                .collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.exists()){
                                    recyclerView_stories.setVisibility(View.VISIBLE);
                                    friendsArrayList.clear();
                                    final String user_list_Uid =  (String) document.get("user_id");
                                    String locale = null;
                                    final_users = user_list_Uid.replace(U_id, " ");
                                    try{
                                        locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                    }catch (Exception e){

                                    }
                                    //Log.d("Account not requested: ",user_list_Uid+" ");
                                    //String locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                    firestoreDB.collection("users")
                                            .document(final_users)
                                            .collection("moments")
                                            .document("moments_photo")
                                            .collection("images")
                                            .whereEqualTo("user_id", final_users)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                                            storiesModel = document.toObject(StoriesModel.class);
                                                            storiesModelArrayList.add(storiesModel);
                                                            //Collections.reverse(storiesModelArrayList);
                                                            //runLayoutAnimation(recyclerView);
                                                            //recyclerView.setVisibility(View.INVISIBLE);

                                                        }
                                                        story_adapterUtil = new RecyclerViewAdapterUtilStories(getApplicationContext(),storiesModelArrayList);
                                                        story_adapterUtil.setHasStableIds(false);
                                                        recyclerView_stories.setAdapter(story_adapterUtil);
                                                    }
                                                }
                                            });

                                }

                            }
                        }
                    }
                });
    }
    private void loadOwnStory(){

        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("moments")
                .document("moments_photo")
                .collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                          for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                              if (documentSnapshot.exists()){
                                  card_story.setVisibility(View.VISIBLE);

                                  final String story_own_bg = documentSnapshot.getString("moments_photo");

                                  Uri story_bg_uri = Uri.parse(story_own_bg);

                                  final String story_own_profile = documentSnapshot.getString("img_profile");

                                  final String caption = documentSnapshot.getString("caption");
                                  Uri story_profile_uri = Uri.parse(story_own_profile);

                                  try {
                                      Picasso.get().load(story_bg_uri).resize(100,200).centerCrop().into(story_background);
                                      Picasso.get().load(story_profile_uri).resize(100,150).centerCrop().into(story_profile_pic);

                                  }catch (Exception e){
                                      Toast.makeText(getContext(), "Error loading story details.", Toast.LENGTH_SHORT).show();

                                  }

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

                                                          story_background.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  Intent i = new Intent(getActivity(), ViewStories.class);
                                                                  //int pos = holder.getAdapterPosition();
                                                                  i.putExtra("str_name", accountInfoArrayList.get(0).getFirst_name());
                                                                  i.putExtra("str_bg", story_own_bg);
                                                                  i.putExtra("str_profile", story_own_profile);
                                                                  i.putExtra("str_caption", caption);
                                                                  startActivity(i);
                                                                  getActivity().finish();
                                                              }
                                                          });
                                                      }
                                                  }
                                              }
                                          });


                              }

                          }
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                card_story.setVisibility(View.GONE);
            }
        });
    }

    private void loadPostsData(){
                firestoreDB.collection("users")
                        .document(mAuth.getUid())
                        .collection("friends")
                        .document("accepted")
                        .collection("users")
                        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                friendsArrayList.clear();
                                final String user_list_Uid =  (String) document.get("user_id");
                                String locale = null;
                                final_users = user_list_Uid.replace(U_id, " ");
                                try{
                                    locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                }catch (Exception e){

                                }
                                //Log.d("Account not requested: ",user_list_Uid+" ");
                                //String locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                firestoreDB.collection("users")
                                        .document(user_list_Uid)
                                        .collection("posts")
                                        .document("published")
                                        .collection("post")
                                        .whereEqualTo("user_id", user_list_Uid)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @SuppressLint("RestrictedApi")
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        postViewModel = document.toObject(PostViewModel.class);
                                                        postViewModelArrayList.add(postViewModel);
                                                        Collections.reverse(postViewModelArrayList);
                                                        adapterUtil = new RecyclerViewAdapterUtilFriendsPost(getApplicationContext(),postViewModelArrayList);
                                                        adapterUtil.setHasStableIds(false);
                                                        recyclerView.setAdapter(adapterUtil);
                                                        //runLayoutAnimation(recyclerView);
                                                        //recyclerView.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            }
                                        });
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
    public void onStart(){
        super.onStart();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onResume(){
        super.onResume();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
}