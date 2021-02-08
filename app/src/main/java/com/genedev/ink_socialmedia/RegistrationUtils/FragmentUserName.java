package com.genedev.ink_socialmedia.RegistrationUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrame;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;


public class FragmentUserName extends Fragment {
    // Store instance variables
    private String title;
    private FloatingActionButton fab;
    private TextInputLayout userName,passWord,confirm_passWord;
    private int page;


    private ViewPager viewPager;
    private int nextFragment;
    private ProgressDialog progressDialog;

    private View view;

    private HelperUtils helperUtils;
    private FirebaseAuth auth;

    private final Context context = getActivity();
    private final Class aClass = RegistrationFragmentUtils.class;
    private static int TIME_OUT = 4000;

    private ProgressBar progress_bar_loading;

    // newInstance constructor for creating fragment with arguments
    public static FragmentUserName newInstance(int page, String title) {
        FragmentUserName fragmentFirst = new FragmentUserName();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        setRetainInstance(true);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reg_fragment_username, container, false);
        viewPager = (ViewPager) RegistrationFragmentUtils.mInstance.findViewById(R.id.vp_pager);
        nextFragment = viewPager.getCurrentItem() + 1;

        fab = view.findViewById(R.id.fab);
        userName = view.findViewById(R.id.reg_username);
        passWord = view.findViewById(R.id.reg_password);
        confirm_passWord = view.findViewById(R.id.confirm_password);

        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);
        //helperUtils = new HelperUtils();

        auth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(getActivity());
        setRetainInstance(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                setRetainInstance(true);
            }
        });

        return view;

    }

    private void emptyFields(String str){
        Snackbar.make(fab, str, Snackbar.LENGTH_SHORT).show();
    }

    private void registerUser(){
        final String conpassword = confirm_passWord.getEditText().getText().toString().trim();
        final String email = Objects.requireNonNull(userName.getEditText().getText()).toString();
        final String password = passWord.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            emptyFields("Email is required.");
            userName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emptyFields("Invalid email.");
            userName.requestFocus();
            return;
        }
        if (password.isEmpty()){
            emptyFields("Password is required.");
            passWord.requestFocus();
            return;
        }
        if (password.length()<=6){
            emptyFields("Password must be 6 characters.");
            passWord.requestFocus();
            return;
        }

        if (conpassword.isEmpty()){
            emptyFields("Password must not be empty.");
            passWord.requestFocus();
            return;
        }
        if (!conpassword.matches(password)){
            emptyFields("Password didn't matched.");
            passWord.requestFocus();
            return;
        }

        progress_bar_loading.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Create a new user with a first and last name
                            Map<String, Object> user = new HashMap<>();
                            Map<String, Object> account_details = new HashMap<>();
                            account_details.put("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
                            account_details.put("user_id", Objects.requireNonNull(auth.getUid()));
                            user.put("account_details", account_details);

                            firestoreDB.collection("users").document(mAuth.getUid())
                                    .collection("account_details").document("account_info").set(account_details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            viewPager.setCurrentItem(nextFragment);
                                        }
                                    });

                            firestoreDB.collection("users_lists").document(mAuth.getUid()).set(account_details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            viewPager.setCurrentItem(nextFragment);
                                        }
                                    });

                            emptyFields("Username and password configured successfully");

                            progress_bar_loading.setVisibility(View.INVISIBLE);
                            viewPager.setCurrentItem(nextFragment);
                        }
                        else {
                            emptyFields("Sign Up Failed");
                        }
                    }
                });
    }
}