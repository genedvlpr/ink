package com.genedev.ink_socialmedia.RegistrationUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.genedev.ink_socialmedia.Login;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Registration;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;

public class RegistrationFragmentUtils extends FragmentActivity {

    private ViewPager vpPager;
    private FragmentPagerAdapter adapterViewPager;
    public static RegistrationFragmentUtils mInstance = null;

    private TextInputLayout userName,passWord,confirm_passWord, fname, mname, lname, address, bday, age;
    private String relationship, interest;
    private RadioGroup status;
    private RadioButton single, married, in_a_relationship, complicated;
    private int status_selected;
    private static int TIME_OUT = 4000;

    private ChipGroup filterChipGroup;
    private Chip photography, music, movies, books, travel, blogs, foods, cooking, workout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FloatingActionButton fab;

    private ProgressDialog progressDialog;

    private ImageButton datePicker;

    private LayoutInflater inflater_1,inflater_2,inflater_3,inflater_4,inflater_5;

    private TextInputEditText bday_input_text,userName1;

    private View layout_1, layout_2, layout_3, layout_4, layout_5;

    private Context context = this;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_fragment_utils);
        vpPager = (ViewPager) findViewById(R.id.vp_pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        inflater_1 = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        layout_1 = inflater_1.inflate(R.layout.reg_fragment_username, null);

        inflater_2 = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        layout_2 = inflater_2.inflate(R.layout.reg_fragment_full_name, null);

        inflater_3 = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        layout_3 = inflater_3.inflate(R.layout.reg_fragment_add_bday_age, null);

        inflater_4 = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        layout_4 = inflater_4.inflate(R.layout.reg_fragment_status, null);

        inflater_5 = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        layout_5 = inflater_5.inflate(R.layout.reg_fragment_status, null);

        userName1 = layout_1.findViewById(R.id.username);
        passWord = layout_1.findViewById(R.id.reg_password);
        confirm_passWord = layout_1.findViewById(R.id.confirm_password);

        fname = layout_2.findViewById(R.id.fname);
        mname = layout_2.findViewById(R.id.mname);
        lname = layout_2.findViewById(R.id.lname);

        address = layout_3.findViewById(R.id.address);
        bday = layout_3.findViewById(R.id.bday);
        age = layout_3.findViewById(R.id.age);
        datePicker = layout_3.findViewById(R.id.datePicker);
        bday_input_text = layout_3.findViewById(R.id.input_text_bday);

        status = layout_4.findViewById(R.id.status_group);
        single = layout_4.findViewById(R.id.single);
        married = layout_4.findViewById(R.id.married);
        in_a_relationship = layout_4.findViewById(R.id.in_a_relationship);
        complicated = layout_4.findViewById(R.id.its_complicated);

        filterChipGroup = layout_5.findViewById(R.id.filter_chip_group);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        mInstance = this;

        builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

        vpPager.setOffscreenPageLimit(5);

        vpPager.beginFakeDrag();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentUserName.newInstance(0, "Page # 1");
                case 1:
                    return FragmentFullName.newInstance(1, "Page # 2");
                case 2:
                    return FragmentAddBdayAge.newInstance(2, "Page # 3");
                case 3:
                    return FragmentStatus.newInstance(3, "Page # 4");
                case 4:
                    return FragmentInterests.newInstance(4, "Page # 5");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void emptyFields(String str){
        Snackbar.make(vpPager, str, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed(){

        String message = "Registration isn't completed, all data that you entered will be deleted. Are you sure you want to exit?";
        String title = "Exit registration?";

        builder.setCancelable(true);

        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mAuth.getCurrentUser() != null){
                    firestoreDB.collection("users")
                            .document(mAuth.getUid())
                            .collection("account_details")
                            .document("account_info")
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firestoreDB.collection("users_lists")
                                            .document(mAuth.getUid())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        mAuth.getCurrentUser().delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Intent intent = new Intent(RegistrationFragmentUtils.this, Login.class);
                                                                        RegistrationFragmentUtils.this.startActivity(intent);
                                                                        RegistrationFragmentUtils.this.finish();
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog alert11 = builder.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        //alert11.setTitle(title);
        alert11.show();
    }


}
