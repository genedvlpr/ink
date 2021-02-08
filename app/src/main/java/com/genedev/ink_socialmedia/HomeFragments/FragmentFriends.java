package com.genedev.ink_socialmedia.HomeFragments;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.FriendAcceptedModel;
import com.genedev.ink_socialmedia.Utils.FriendRequestModel;
import com.genedev.ink_socialmedia.Utils.Friends;
import com.genedev.ink_socialmedia.Utils.FriendsSearch;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.genedev.ink_socialmedia.Utils.RecyclerViewAdapterUtilAccept;
import com.genedev.ink_socialmedia.Utils.RecyclerViewAdapterUtilAccepted;
import com.genedev.ink_socialmedia.Utils.RecyclerViewAdapterUtilAdd;
import com.genedev.ink_socialmedia.Utils.RecyclerViewAdapterUtilSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.Fragment;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.U_id;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;

public class FragmentFriends extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private boolean isSelected = false;
    private View rootView;
    private Chip chip;
    private String chip_text;
    private HelperUtils helperUtils;
    private RecyclerView recyclerView;
    private Friends friends;
    private FriendRequestModel friendRequests;
    private ArrayList<Friends> friendsArrayList;
    private FriendAcceptedModel friendAcceptedModel;
    private ArrayList<FriendsSearch> friendsSearchArrayList;
    private FriendsSearch friendsSearch;
    private ArrayList<FriendAcceptedModel> friendAcceptedModelArrayList;
    private ArrayList<FriendRequestModel> friendRequestsArrayList;
    private RecyclerViewAdapterUtilSearch adapterUtilSearch;
    private RecyclerViewAdapterUtilAdd adapterUtil;
    private RecyclerViewAdapterUtilAccept adapterUtil_accept;

    private RecyclerViewAdapterUtilAccepted adapterUtil_accepted;
    private ProgressBar contentLoadingProgressBar;
    private String requseted_users_alter;
    private LinearLayoutManager llm;
    private String final_users;
    private TextView username, friend_requests_number;
    private ImageButton show_friend_requests;
    private MaterialCardView crd_restart;
    private Button btn_restart;

    private EditText search_bar;

    private SharedPreferences Prefs = null;
    private String data = null, keyData = null;
    private String search_query;
    public static FragmentFriends newInstance(String param1, String param2) {
        FragmentFriends fragment3 = new FragmentFriends();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment3.setArguments(args);
        return fragment3;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment_friends, container, false);
        chip = rootView.findViewById(R.id.search_in_friends);
        show_friend_requests = rootView.findViewById(R.id.btn_show_friend_request);
        crd_restart = rootView.findViewById(R.id.crd_restart);
        btn_restart = rootView.findViewById(R.id.btn_restart);

        helperUtils = new HelperUtils();
        //helperUtils.setPersistence(firestoreDB);

        contentLoadingProgressBar = rootView.findViewById(R.id.content_loading);
        contentLoadingProgressBar.isIndeterminate();



        Prefs = getActivity().getSharedPreferences("com.genedev.ink_socialmedia", MODE_PRIVATE);

        friend_requests_number = rootView.findViewById(R.id.friend_request_number);
        search_bar = rootView.findViewById(R.id.search_bar);


        chip.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    if(!isSelected){
                        friendAcceptedModelArrayList.clear();
                        friendsArrayList.clear();
                        showAccepted();
                        chip.setText("Find Friends");
                        isSelected=true;
                    }else{
                        friendAcceptedModelArrayList.clear();
                        friendsArrayList.clear();
                        chip.setText("Search in Friends");
                        loadDataToView();
                        isSelected=false;
                    }
                }
            });

        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
                    friendsArrayList.clear();
                    search_query = search_bar.getText().toString();
                    Log.d("name", search_query+"");
                    loadSearches();
                    return true;
                }
                return false;
            }
        });

        friendsArrayList = new ArrayList<>();
        friendRequestsArrayList = new ArrayList<>();
        friendAcceptedModelArrayList = new ArrayList<>();

        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        setRetainInstance(true);

        search_bar.setTypeface(null, Typeface.NORMAL);
        friend_requests_number.setTypeface(null, Typeface.NORMAL);
        //llm.setItemPrefetchEnabled(true);
        recyclerView = rootView.findViewById(R.id.friend_list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        loadDataToView();
        notifyRequests();
        return rootView;
    }
    private void emptyFields(View v, String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }


    private void loadDataToView(){

        helperUtils.progress(contentLoadingProgressBar,50, true);
        //friendsArrayList.clear();
            CollectionReference usersCollectionRef =
            firestoreDB.collection("users_lists");
            usersCollectionRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    //friendsArrayList.clear();
                                    final String user_list_Uid =  (String) document.get("user_id");
                                    String locale = null;
                                    final_users = user_list_Uid.replace(U_id, " ");
                                    helperUtils.progress(contentLoadingProgressBar,0, false);
                                    try{
                                        locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                    }catch (Exception e){
                                        Log.d("Error", "cant detect locality");
                                    }
                                    firestoreDB.collection("users")
                                            .document(final_users)
                                            .collection("account_details")
                                            .whereEqualTo("user_id", final_users)
                                            .whereEqualTo("country_code", locale)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                            friends = document.toObject(Friends.class);
                                                            friendsArrayList.add(friends);

                                                            adapterUtil = new RecyclerViewAdapterUtilAdd(getApplicationContext(),friendsArrayList);
                                                            adapterUtil.setHasStableIds(false);
                                                            recyclerView.setAdapter(adapterUtil);
                                                            helperUtils.runLayoutAnimation(recyclerView);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }else{
                                Log.d("Other Users", "Empty");
                            }
                        }
                    });
        helperUtils.progress(contentLoadingProgressBar,0, false);
    }

    private void showRequests(){

        recyclerView.setAdapter(null);

        CollectionReference usersCollectionRef =
        firestoreDB.collection("users_lists");
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                final String user_list_Uid =  (String) document.get("user_id");

                                final_users = user_list_Uid.replace(U_id, " ");

                                String account_id = final_users.substring(0, Math.min(final_users.length(), 5));
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
                                                        requseted_users_alter = (String) querySnapshot.get("requester_id");
                                                        //Log.d("Account not requested: ", requseted_users_alter + " ");
                                                        final String locale = getActivity().getResources().getConfiguration().locale.getCountry();
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

                                                                                // = new RecyclerViewAdapterUtilAdd(getApplicationContext(), friendsArrayList);
                                                                                adapterUtil_accept = new RecyclerViewAdapterUtilAccept(getApplicationContext(), friendRequestsArrayList);
                                                                                //adapterUtil_accept.clear();
                                                                                recyclerView.setAdapter(adapterUtil_accept);
                                                                                helperUtils.runLayoutAnimation(recyclerView);

                                                                                if (friendRequestsArrayList.size() >= 1){
                                                                                    emptyFields(rootView, "People who requested you");
                                                                                    helperUtils.progress(contentLoadingProgressBar, 0, false);
                                                                                }
                                                                            }
                                                                        }
                                                                        else {
                                                                            emptyFields(rootView, "No friend request/s");
                                                                            helperUtils.progress(contentLoadingProgressBar, 50, false);
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                                else {
                                                    show_friend_requests.setEnabled(false);
                                                    friend_requests_number.setText("0");
                                                    helperUtils.progress(contentLoadingProgressBar,0, false);
                                                    friendRequestsArrayList.clear();
                                                }

                                            }
                                        });
                            }
                        }else {
                            show_friend_requests.setEnabled(false);
                            friend_requests_number.setText("0");
                            helperUtils.progress(contentLoadingProgressBar,0, false);
                        }
                    }
                });
    }

    private void showAccepted(){

        helperUtils.progress(contentLoadingProgressBar,50, true);

        CollectionReference usersCollectionRef =
                firestoreDB.collection("users_lists");
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                final String user_list_Uid =  (String) document.get("user_id");

                                final_users = user_list_Uid.replace(U_id, " ");

                                String account_id = final_users.substring(0, Math.min(final_users.length(), 5));
                                firestoreDB.collection("users")
                                        .document(U_id)
                                        .collection("friends")
                                        .document("accepted")
                                        .collection("users")
                                        .whereEqualTo(account_id+"(request)", "friends")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){

                                                    for (final QueryDocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                                                        //Log.d("Ref ", querySnapshot.getId());
                                                        requseted_users_alter = (String) querySnapshot.get("user_id");

                                                        Log.d("Account not requested: ", requseted_users_alter + " ");
                                                        final String locale = getActivity().getResources().getConfiguration().locale.getCountry();
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

                                                                                friendAcceptedModel = document.toObject(FriendAcceptedModel.class);
                                                                                friendAcceptedModelArrayList.add(friendAcceptedModel);

                                                                                // = new RecyclerViewAdapterUtilAdd(getApplicationContext(), friendsArrayList);
                                                                                adapterUtil_accepted = new RecyclerViewAdapterUtilAccepted(getApplicationContext(), friendAcceptedModelArrayList);
                                                                                //adapterUtil_accept.clear();
                                                                                recyclerView.setAdapter(adapterUtil_accepted);
                                                                                helperUtils.runLayoutAnimation(recyclerView);

                                                                                if (friendAcceptedModelArrayList.size() == 0){
                                                                                    emptyFields(rootView, "You dont have any friends");
                                                                                    helperUtils.progress(contentLoadingProgressBar, 0, false);
                                                                                }
                                                                                if (friendAcceptedModelArrayList.size() >= 1){
                                                                                    emptyFields(rootView, "Your connections");
                                                                                    helperUtils.progress(contentLoadingProgressBar, 0, false);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }

                                            }
                                        });

                            }
                        }
                    }
                });
    }

    private void notifyRequests(){

        helperUtils.progress(contentLoadingProgressBar,50, true);

        CollectionReference usersCollectionRef =
                firestoreDB.collection("users_lists");
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                final String user_list_Uid =  (String) document.get("user_id");

                                final_users = user_list_Uid.replace(U_id, " ");

                                if (final_users.equals("")){
                                    show_friend_requests.setEnabled(false);
                                    show_friend_requests.setVisibility(View.INVISIBLE);
                                    friend_requests_number.setText("0");
                                    helperUtils.progress(contentLoadingProgressBar,50, false);
                                }
                                if (!final_users.equals("")){
                                    String account_id = final_users.substring(0, Math.min(final_users.length(), 5));
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

                                                            requseted_users_alter = (String) querySnapshot.get("user_id");
                                                            //helperUtils.progress(contentLoadingProgressBar,0, false);
                                                            //Log.d("Account not requested: ", requseted_users_alter + " ");
                                                            final String locale = getActivity().getResources().getConfiguration().locale.getCountry();
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

                                                                                    if (friendRequestsArrayList.size() == 0){
                                                                                        show_friend_requests.setVisibility(View.INVISIBLE);
                                                                                        friend_requests_number.setText("0");
                                                                                        show_friend_requests.setEnabled(false);
                                                                                        helperUtils.progress(contentLoadingProgressBar,50, false);
                                                                                    }
                                                                                    if (friendRequestsArrayList.size() >= 1){
                                                                                        friend_requests_number.setText(friendRequestsArrayList.size()+"");
                                                                                        show_friend_requests.setEnabled(true);
                                                                                        show_friend_requests.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {
                                                                                                if(!isSelected) {
                                                                                                    try{
                                                                                                        friendsArrayList.clear();
                                                                                                        friendRequestsArrayList.clear();
                                                                                                        showRequests();
                                                                                                        emptyFields(rootView, "Friend requests");
                                                                                                        isSelected = true;
                                                                                                    }catch (Exception e){

                                                                                                    }
                                                                                                }
                                                                                                else{
                                                                                                    try{
                                                                                                        friendRequestsArrayList.clear();
                                                                                                        friendsArrayList.clear();
                                                                                                        loadDataToView();
                                                                                                        emptyFields(rootView, "Suggested friends");
                                                                                                        isSelected=false;
                                                                                                    }catch (Exception e){

                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                        helperUtils.progress(contentLoadingProgressBar,0, false);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            show_friend_requests.setEnabled(false);
                                            friend_requests_number.setText("0");
                                            show_friend_requests.setVisibility(View.INVISIBLE);
                                            helperUtils.progress(contentLoadingProgressBar,0, false);
                                        }
                                    });
                                }

                            }
                        }
                    }
                });
    }

    private boolean promptTutorial() {
        // Check fo saved value in Shared preference for key: keyTutorial return "NullTutorial" if nothing found
        keyData = Prefs.getString("keyData", "NullData");
        // Log what we found in shared preference
        Log.d(TAG, "Shared Pref read: [keyData: " + keyData + "]");

        if (keyData.contains("NullData")){
            // if nothing found save a new value "PROMPTED" for the key: keyTutorial
            // to save it in shared prefs just call our saveKey function
            saveKey("keyData", "PROMPTED");
            return true;
        }
        // if some value was found for this key we already propted this window some time in the past
        // no need to prompt it again
        return false;
    }

    private void loadSearches(){
        helperUtils.progress(contentLoadingProgressBar,50, true);
        //friendsArrayList.clear();
        CollectionReference usersCollectionRef =
                firestoreDB.collection("users_lists");
        usersCollectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                final String user_list_Uid =  (String) document.get("user_id");
                                String locale = null;
                                final_users = user_list_Uid.replace(U_id, " ");
                                helperUtils.progress(contentLoadingProgressBar,0, false);
                                try{
                                    locale = getActivity().getResources().getConfiguration().locale.getCountry();
                                }catch (Exception e){
                                    Log.d("Error", "cant detect locality");
                                }

                                    firestoreDB.collection("users")
                                            .document(final_users)
                                            .collection("account_details")
                                            .whereEqualTo("user_id", final_users)
                                            .whereEqualTo("first_name", search_query)
                                            .whereEqualTo("country_code", locale)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                            friendsSearchArrayList = new ArrayList<>();
                                                            friendsSearch = document.toObject(FriendsSearch.class);
                                                            friendsSearchArrayList.add(friendsSearch);

                                                            adapterUtilSearch = new RecyclerViewAdapterUtilSearch(getApplicationContext(),friendsSearchArrayList);

                                                            recyclerView.setAdapter(adapterUtilSearch);
                                                            helperUtils.runLayoutAnimation(recyclerView);
                                                        }
                                                    }
                                                }
                                            });

                            }
                        }else{
                            Log.d("Other Users", "Empty");
                        }
                    }
                });
    }

    private void saveKey(String key, String value) {
        SharedPreferences.Editor editor = Prefs.edit();
        // Log what are we saving in the shared Prefs
        Log.d(TAG, "Shared Prefs Write ["+ key + ":" +value + "]");
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void onStart(){
        super.onStart();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //helperUtils.setPersistence(firestoreDB);
    }
    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //helperUtils.setPersistence(firestoreDB);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }
}