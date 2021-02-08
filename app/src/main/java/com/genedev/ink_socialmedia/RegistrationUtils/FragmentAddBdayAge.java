package com.genedev.ink_socialmedia.RegistrationUtils;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;

public class FragmentAddBdayAge extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private TextInputLayout address, bday, age;

    private ProgressDialog progressDialog;
    private String id;

    private ViewPager viewPager;
    private int nextFragment;
    private View view;

    private ImageButton datePicker;
    private TextInputEditText bday_input_text;

    private HelperUtils helperUtils;
    private int mYear, mMonth, mDay;
    private FirebaseAuth auth;

    private ProgressBar progress_bar_loading;
    // newInstance constructor for creating fragment with arguments
    public static FragmentAddBdayAge newInstance(int page, String title) {
        FragmentAddBdayAge fragmentThird  = new FragmentAddBdayAge();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentThird.setArguments(args);
        return fragmentThird;
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
        view = inflater.inflate(R.layout.reg_fragment_add_bday_age, container, false);
        viewPager = RegistrationFragmentUtils.mInstance.findViewById(R.id.vp_pager);
        nextFragment = viewPager.getCurrentItem() + 3;

        FloatingActionButton fab = view.findViewById(R.id.fab);
        auth = FirebaseAuth.getInstance();
        address = view.findViewById(R.id.address);
        bday = view.findViewById(R.id.bday);
        age = view.findViewById(R.id.age);
        datePicker = view.findViewById(R.id.datePicker);
        bday_input_text = view.findViewById(R.id.input_text_bday);
        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);

        progressDialog = new ProgressDialog(getActivity());

        ImageButton datePicker = view.findViewById(R.id.datePicker);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        //helperUtils = new HelperUtils();
        auth = FirebaseAuth.getInstance();
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
        id = auth.getUid();
        final String add = address.getEditText().getText().toString();
        final String b_day = bday.getEditText().getText().toString();
        final String user_age = age.getEditText().getText().toString();

        if (add.isEmpty()){
            emptyFields("Address is required.");
            address.requestFocus();
            return;
        }
        if (b_day.isEmpty()){
            emptyFields("Birthday is required.");
            bday.requestFocus();
            return;
        }
        if (user_age.isEmpty()){
            emptyFields("Age is required.");
            age.requestFocus();
            return;
        }

        else{
            progress_bar_loading.setVisibility(View.VISIBLE);
            String locale = Objects.requireNonNull(getActivity()).getResources().getConfiguration().locale.getCountry();
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> account_details = new HashMap<>();
            account_details.put("address", add);
            account_details.put("birthday", b_day);
            account_details.put("age", user_age);
            account_details.put("country_code", locale);
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
                        }
                    });

            viewPager.setCurrentItem(nextFragment);

            emptyFields("Address, birthday and age has been added");

        }

    }

    public void showDatePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        bday_input_text.setText(date);
                        Objects.requireNonNull(bday.getEditText()).setText(date);


                        String final_age = String.valueOf(mYear - year);

                        Objects.requireNonNull(age.getEditText()).setText(final_age);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}
