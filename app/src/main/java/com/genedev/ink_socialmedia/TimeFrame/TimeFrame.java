package com.genedev.ink_socialmedia.TimeFrame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.TimeFrame.ProfileUtils.GalleryProfileAdapter;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs.TabAdapter;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs.TabPhotos;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs.TabPost;
import com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs.TabVideos;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.*;

public class TimeFrame extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CircleImageView profile_pic;

    private CoordinatorLayout coordinatorLayout;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.4f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 300;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private TextView mTitle;
    private LinearLayout mTitleContainer;
    private HelperUtils helperUtils;
    private RelativeLayout profile_controls;

    private int PICK_PROFILE = 1;
    private int PICK_COVER = 2;
    private String imageEncoded;
    private List<String> imagesEncodedList;

    private StaggeredGridView img_post_image;
    private GalleryProfileAdapter galleryAdapter;
    private TwoWayView mTwvGrid;
    private Date currentTime;
    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

    private ArrayList<Uri> mArrayUri1 = new ArrayList<Uri>();
    private Uri filePath;

    private StorageReference storage;
    private StorageReference storageReference, ref;
    private UploadTask uploadTask;
    private Intent data_global;
    private Bitmap bitmap;
    private int totalItemsSelected;
    private StorageReference mStorage;
    private List<String> fileNameList;
    private List<String> fileDoneList;

    private ImageButton add_prof_pic, add_cover_photo;
    private ImageView cover_photo;

    private ArrayList<AccountInfo> accountInfoArrayList;
    private AccountInfo accountInfo;
    private TextView full_name, username;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(R.color.colorPrimaryDark);
        appBarLayout = findViewById(R.id.appBarLayout);

        helperUtils = new HelperUtils();

        mTitle = findViewById(R.id.main_textview_title);

        add_prof_pic = findViewById(R.id.app_profile_picture);

        coordinatorLayout = findViewById(R.id.coordinator);

        profile_controls = findViewById(R.id.profile_controls);

        full_name = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        profile_pic = findViewById(R.id.prof_pic);

        profile_pic.setVisibility(View.INVISIBLE);
        full_name.setVisibility(View.INVISIBLE);
        username.setVisibility(View.INVISIBLE);

        cover_photo = findViewById(R.id.cover_photo);

        add_cover_photo = findViewById(R.id.app_header_picture);

        mStorage = FirebaseStorage.getInstance().getReference();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.build();
        ImageLoader.getInstance().init(config);

        mTitleContainer = findViewById(R.id.main_linearlayout_title);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabPost(), "Time Frame");
        adapter.addFragment(new TabPhotos(), "Your Photos");
        //adapter.addFragment(new TabVideos(), "Your Videos");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        appBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        setDataToViews();

        add_prof_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_A, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PROFILE);
            }
        });

        add_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_A, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_COVER);
            }
        });


    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void setDataToViews(){
        firestoreDB.collection("users")
                .document(mAuth.getUid()).
                collection("account_details")
                .document("account_info")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final String fname, lname, cover, profile;


                        fname = (String) document.get("first_name");
                        lname = (String) document.get("last_name");
                        accountInfoArrayList = new ArrayList<>();
                        accountInfo = document.toObject(AccountInfo.class);
                        accountInfoArrayList.add(accountInfo);
                        try {
                            cover = (String) document.get("img_cover");
                            profile = (String) document.get("img_profile");
                            Uri uri, uri1;
                            if (cover != null){
                                uri = Uri.parse(cover);
                                Picasso.get()
                                        .load(uri)
                                        .resize(350,300)
                                        .centerCrop().into(cover_photo);
                            }else{
                                emptyFields("No cover photo has been set");
                                cover_photo.setVisibility(View.VISIBLE);
                            }
                            if (profile != null){
                                uri1 = Uri.parse(profile);
                                Picasso.get()
                                        .load(uri1)
                                        .resize(170,120)
                                        .centerCrop().into(profile_pic, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        startAlphaAnimation(profile_pic, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                                    }
                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            }else{
                                emptyFields("No profile photo has been set");
                                profile_pic.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e){
                            emptyFields("No profile and cover photo has been set");
                        }

                        helperUtils.setPersistence(firestoreDB);
                        full_name.setText(fname+" "+lname);
                        mTitle.setText(fname+" "+lname);
                        username.setText((String) document.get("email"));
                        startAlphaAnimation(full_name, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                        startAlphaAnimation(username, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
                mIsTheTitleVisible = true;
                profile_controls.setVisibility(View.INVISIBLE);
            }

        } else {

            if (mIsTheTitleVisible) {
                mTitle.setVisibility(View.INVISIBLE);
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                profile_controls.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(add_cover_photo, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(add_prof_pic, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(add_cover_photo, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(add_prof_pic, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            // When an Image is picked
            if (requestCode == PICK_PROFILE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    mArrayUri.clear();
                    data_global = data;
                    final Uri mImageUri = data.getData();
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    profile_pic.setImageURI(mArrayUri.get(0));

                    accountInfoArrayList = new ArrayList<>();

                    String cover_photo = "img_profile";

                    uploadPostImages(cover_photo);
                }
            }

            if(requestCode == PICK_COVER && resultCode == RESULT_OK
                    && null != data) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {
                    mArrayUri1.clear();
                    data_global = data;
                    final Uri mImageUri = data.getData();
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri1.add(mImageUri);
                    cover_photo.setImageURI(mArrayUri1.get(0));

                    String cover_photo = "img_cover";

                    uploadPostImages(cover_photo);
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPostImages(final String field) {
         if (data_global.getData() != null) {
            Uri fileUri = data_global.getData();
             final ProgressBar progressBar = findViewById(R.id.cover_progress);
             progressBar.setVisibility(View.VISIBLE);
            String fileName = getFileName(fileUri);

            final StorageReference fileToUpload = mStorage.child(mAuth.getUid()).child("post_images").child(fileName);

            uploadTask = fileToUpload.putFile(fileUri);

            Task<Uri> task;
            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return fileToUpload.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();

                        final Map<String, Object> account_details = new HashMap<>();

                        account_details.put(field, downloadUri.toString());

                        firestoreDB.collection("users")
                                .document(mAuth.getUid()).
                                collection("account_details")
                                .document("account_info")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        accountInfo = document.toObject(AccountInfo.class);
                                        accountInfoArrayList.add(accountInfo);



                                            firestoreDB.collection("users")
                                                    .document(mAuth.getUid())
                                                    .collection("account_details")
                                                    .document("account_info")
                                                    .update(account_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //emptyFields("Upload successful");
                                                        }
                                                    });

                                            firestoreDB.collection("users")
                                                    .document(mAuth.getUid())
                                                    .collection("photos")
                                                    .document(field)
                                                    .collection("images")
                                                    .document(String.valueOf(downloadUri.toString().replaceAll("/","_").replace(".","_")))
                                                    .set(account_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            emptyFields("Upload successful");

                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                        }
                                    }
                                }

                        });
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    emptyFields("Error");
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =  (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //progressBarFam1.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void emptyFields(String str){
        Snackbar.make(profile_pic, str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(TimeFrame.this, Home.class);
        startActivity(i);
        TimeFrame.this.finish();
        super.onBackPressed();
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
    public void onRestart(){
        helperUtils.setPersistence(firestoreDB);
        super.onRestart();
    }

    @Override
    public void onStart(){
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
