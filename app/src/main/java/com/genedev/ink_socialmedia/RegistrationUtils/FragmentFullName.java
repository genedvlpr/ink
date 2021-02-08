package com.genedev.ink_socialmedia.RegistrationUtils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;
public class FragmentFullName extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private ViewPager viewPager;
    private View view;
    private int nextFragment;

    private ProgressDialog progressDialog;
    private String id;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private TextInputLayout fname, mname, lname;

    private HelperUtils helperUtils;
    private ProgressBar progress_bar_loading;
    // newInstance constructor for creating fragment with arguments
    public static FragmentFullName newInstance(int page, String title) {
        FragmentFullName fragmentSecond  = new FragmentFullName();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;
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
        view = inflater.inflate(R.layout.reg_fragment_full_name, container, false);

        viewPager = (ViewPager) RegistrationFragmentUtils.mInstance.findViewById(R.id.vp_pager);
        nextFragment = viewPager.getCurrentItem() + 2;

        FloatingActionButton fab = view.findViewById(R.id.fab);
        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);

        fname = view.findViewById(R.id.fname);
        mname = view.findViewById(R.id.mname);
        lname = view.findViewById(R.id.lname);

        progressDialog = new ProgressDialog(getActivity());

        //helperUtils = new HelperUtils();

        setRetainInstance(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        return view;
    }

    private void emptyFields(String str){
        Snackbar.make(getView(), str, Snackbar.LENGTH_SHORT).show();
    }
    private void registerUser(){
        id = auth.getUid();;
        final String f_name = fname.getEditText().getText().toString().trim();
        final String m_name = mname.getEditText().getText().toString().trim();
        final String l_name = lname.getEditText().getText().toString().trim();

        if (f_name.isEmpty()){
            emptyFields("First name is required.");
            fname.requestFocus();
            return;
        }
        if (m_name.isEmpty()){
            emptyFields("Middle name is required.");
            mname.requestFocus();
            return;
        }
        if (l_name.isEmpty()){
            emptyFields("Last name is required.");
            lname.requestFocus();
            return;
        }

        else{

            progress_bar_loading.setVisibility(View.VISIBLE);
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> account_details = new HashMap<>();
            account_details.put("first_name", f_name);
            account_details.put("middle_name", m_name);
            account_details.put("last_name", l_name);
            user.put("account_details", account_details);

            // Add a new document with a generated ID
            String idss = String.valueOf(getId());
            firestoreDB.collection("users").document(mAuth.getUid()).
                    collection("account_details").document("account_info")
                    .update(account_details)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progress_bar_loading.setVisibility(View.INVISIBLE);
                            viewPager.setCurrentItem(nextFragment);
                            emptyFields("First name, middle name and last name has been added");
                        }
                    });


        }

    }
}
