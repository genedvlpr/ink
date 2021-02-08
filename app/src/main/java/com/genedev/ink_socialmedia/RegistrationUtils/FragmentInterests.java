package com.genedev.ink_socialmedia.RegistrationUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;

public class FragmentInterests extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private ViewPager viewPager;
    private int nextFragment;

    private ArrayList<String> list = new ArrayList<String>();

    private ProgressDialog progressDialog;
    private String id, interests, clicked_chip;
    private View view, v;

    private HelperUtils helperUtils;

    private Chip chip1, chip2, chip3, chip4, chip5, chip6, chip7, chip8, chip9, chip10, chip11;

    private ChipGroup filterChipGroup;
    private Chip photography, music, movies, books, travel, blogs, foods, cooking, workout;

    private ProgressBar progress_bar_loading;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Index index;
    // newInstance constructor for creating fragment with arguments
    public static FragmentInterests newInstance(int page, String title) {
        FragmentInterests fragmentFifth  = new FragmentInterests();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFifth.setArguments(args);
        return fragmentFifth;
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
        view = inflater.inflate(R.layout.reg_fragment_interest, container, false);
        viewPager = (ViewPager) RegistrationFragmentUtils.mInstance.findViewById(R.id.vp_pager);

        FloatingActionButton fab = view.findViewById(R.id.fabFinal);

        progressDialog = new ProgressDialog(getActivity());

        progress_bar_loading = view.findViewById(R.id.progress_bar_loading);
        //helperUtils = new HelperUtils();

        filterChipGroup = view.findViewById(R.id.filter_chip_group);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

                setRetainInstance(true);
            }
        });
        Client client = new Client("9STP0WP04X", "e1942729ab88f8db92c34e7ded4bd925");
        index = client.getIndex("users");
        addChips();

        return view;
    }

    private void emptyFields(String str){
        Snackbar.make(viewPager, str, Snackbar.LENGTH_SHORT).show();
    }
    private void registerUser(){
        id = auth.getUid();


        progress_bar_loading.setVisibility(View.VISIBLE);
        // Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();
        Map<String, Object> account_details = new HashMap<>();
        account_details.put("interests", list);
        user.put("account_details", account_details);

        // Add a new document with a generated ID
        String idss = String.valueOf(getId());

        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details").document("account_info")
                .update(account_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String user_id, first_name, last_name;

                                user_id = document.getString("user_id");
                                first_name = document.getString("first_name");
                                last_name = document.getString("last_name");



                                List<JSONObject> accountDetails = new ArrayList<>();
                                try {
                                    accountDetails.add(new JSONObject().put("user_id", user_id).put("first_name", first_name).put("last_name",last_name));
                                    index.addObjectsAsync(new JSONArray(accountDetails), null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                emptyFields("Interest has been added");
                                Intent i = new Intent(getActivity(), Home.class);
                                startActivity(i);

                                progress_bar_loading.setVisibility(View.INVISIBLE);
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        }
                    }
                });
    }

    private void addChips(){
        chip1 = new Chip(Objects.requireNonNull(getContext()));
        chip2 = new Chip(Objects.requireNonNull(getContext()));
        chip3 = new Chip(Objects.requireNonNull(getContext()));
        chip4 = new Chip(Objects.requireNonNull(getContext()));
        chip5 = new Chip(Objects.requireNonNull(getContext()));
        chip6 = new Chip(Objects.requireNonNull(getContext()));
        chip7 = new Chip(Objects.requireNonNull(getContext()));
        chip8 = new Chip(Objects.requireNonNull(getContext()));
        chip9 = new Chip(Objects.requireNonNull(getContext()));
        chip10 = new Chip(Objects.requireNonNull(getContext()));
        chip11 = new Chip(Objects.requireNonNull(getContext()));

        chip1.setText("Photography");
        chip2.setText("Music");
        chip3.setText("Books");
        chip4.setText("Travel");
        chip5.setText("Arts");
        chip6.setText("Reading");
        chip7.setText("Writing");
        chip8.setText("Workout");
        chip9.setText("Sports");
        chip10.setText("Blogs");
        chip11.setText("Games");

        chip1.setCheckable(true);
        chip2.setCheckable(true);
        chip3.setCheckable(true);
        chip4.setCheckable(true);
        chip5.setCheckable(true);
        chip6.setCheckable(true);
        chip7.setCheckable(true);
        chip8.setCheckable(true);
        chip9.setCheckable(true);
        chip10.setCheckable(true);
        chip11.setCheckable(true);


        //chip1.setChipBackgroundColorResource(R.color.textColorPrimary);
        //chip1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //chip1.setElevation(4);

        filterChipGroup.addView(chip1);
        filterChipGroup.addView(chip2);
        filterChipGroup.addView(chip3);
        filterChipGroup.addView(chip4);
        filterChipGroup.addView(chip5);
        filterChipGroup.addView(chip6);
        filterChipGroup.addView(chip7);
        filterChipGroup.addView(chip8);
        filterChipGroup.addView(chip9);
        filterChipGroup.addView(chip10);
        filterChipGroup.addView(chip11);

        int id_1 = chip1.getId();

        chip1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip1.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip1.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip2.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip2.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip3.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip3.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip4.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip4.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip5.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip5.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip6.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip6.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip7.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip7.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip8.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip8.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip9.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip9.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip10.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip10.getText().toString();
                list.add(clicked_chip);
            }
        });

        chip11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getContext(), chip11.getText(), Toast.LENGTH_SHORT).show();
                clicked_chip = chip11.getText().toString();
                list.add(clicked_chip);
            }
        });


        try {
            for (String s : list) {
                Log.d("My array list content: ", s);
            }
        }
        catch (Exception e){
            Log.d("My array list content: ", "");
        }
    }
}
