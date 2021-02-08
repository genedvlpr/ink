package com.genedev.ink_socialmedia.RegistrationUtils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;
public class FragmentStatus extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private String relationship, interest;
    private RadioGroup status;
    private RadioButton stat;
    private int status_selected;
    private ViewPager viewPager;
    private int nextFragment;
    private View view;

    private ProgressDialog progressDialog;
    private String id;
    private String selection;;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private HelperUtils helperUtils;

    private ProgressBar progress_bar_loading;
    // newInstance constructor for creating fragment with arguments
    public static FragmentStatus newInstance(int page, String title) {
        FragmentStatus fragmentFourth  = new FragmentStatus();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFourth.setArguments(args);
        return fragmentFourth;
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
        view = inflater.inflate(R.layout.reg_fragment_status, container, false);

        viewPager = (ViewPager) RegistrationFragmentUtils.mInstance.findViewById(R.id.vp_pager);
        nextFragment = viewPager.getCurrentItem() + 4;

        progressDialog = new ProgressDialog(getActivity());

        status = view.findViewById(R.id.status_group);


        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);
        //helperUtils = new HelperUtils();

        FloatingActionButton fab = view.findViewById(R.id.fab);
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
        Snackbar.make(viewPager, str, Snackbar.LENGTH_SHORT).show();
    }
    private void registerUser(){

        progress_bar_loading.setVisibility(View.VISIBLE);
        id = auth.getUid();
        if(status.getCheckedRadioButtonId()!=-1){
            int ids= status.getCheckedRadioButtonId();
            View radioButton = status.findViewById(ids);
            int radioId = status.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) status.getChildAt(radioId);
            selection = (String) btn.getText();
            Log.d("rel",selection);
            final HashMap<String, Object> result = new HashMap<>();
            result.put("status", selection);

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> account_details = new HashMap<>();
            account_details.put("status", selection);
            user.put("account_details", account_details);

            // Add a new document with a generated ID
            firestoreDB.collection("users").document(mAuth.getUid()).
                    collection("account_details").document("account_info")
                    .update(account_details)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progress_bar_loading.setVisibility(View.INVISIBLE);
                        }
                    });

            viewPager.setCurrentItem(nextFragment);
        }

    }

}
