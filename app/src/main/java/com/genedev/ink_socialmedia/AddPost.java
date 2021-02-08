package com.genedev.ink_socialmedia;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.genedev.ink_socialmedia.PostUtils.GalleryAdapter;
import com.genedev.ink_socialmedia.PostUtils.SelectedImagesPreview;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class AddPost extends Activity implements View.OnClickListener {

    private String full_name, date, time, ink_color, caption, monthName, alignment_left, alignment_center, alignment_right, ink_align, sharing_pref, comments_pref, draft_pref, document_date;
    private Uri post_image, account_image;
    private AccountInfo accountInfo;
    private ArrayList<AccountInfo> accountInfoArrayList;

    private String sharing, comments;

    private ImageButton post_now, align_left, align_center, align_right, btn_share;
    private EditText ed_caption;

    private View rel_anim;
    private Switch switch_disable_sharing, switch_disable_comments, switch_save_as_draft;

    private MaterialCardView post_card;
    private Button btn_default_ink_outline, btn_default_ink_filled, btn_green_outlined, btn_green_filled, btn_amber_outlined, btn_amber_filled, btn_purple_outlined, btn_purple_filled, btn_review;
    //private String default_ink_outline, default_ink_filled, green_outlined, green_filled, amber_outlined, amber_filled, purple_outlined, purple_filled;
    private Button btn_add_photo, btn_add_moments;
    private TextView tv_name, tv_caption, tv_date, tv_time;
    private Calendar c;
    private int mYear, mMonth, mDay;

    private int PICK_IMAGE_MULTIPLE = 1;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private StaggeredGridView img_post_image;
    private GalleryAdapter galleryAdapter;
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
    private ProgressBar progressBar;
    private CircleImageView add_user_profile;

    @ServerTimestamp
    Date createdDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_add);

        accountInfoArrayList = new ArrayList<>();

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        mStorage = FirebaseStorage.getInstance().getReference();

        c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

        tv_name = findViewById(R.id.add_tv_username);
        tv_date = findViewById(R.id.add_tv_date);
        ed_caption = findViewById(R.id.add_post_caption);
        img_post_image = findViewById(R.id.grid_view);
        add_user_profile = findViewById(R.id.add_img_user_profile);

        add_user_profile.setVisibility(View.INVISIBLE);

        tv_name.setVisibility(View.INVISIBLE);
        tv_date.setVisibility(View.INVISIBLE);

        btn_default_ink_outline = findViewById(R.id.ink_default);
        btn_default_ink_filled = findViewById(R.id.ink_default_filled);
        btn_green_outlined = findViewById(R.id.ink_green_light);
        btn_green_filled = findViewById(R.id.ink_green_dark);
        btn_amber_outlined = findViewById(R.id.ink_amber_light);
        btn_amber_filled = findViewById(R.id.ink_amber_dark);
        btn_purple_outlined = findViewById(R.id.ink_purple_light);
        btn_purple_filled = findViewById(R.id.ink_purple_dark);
        btn_add_photo = findViewById(R.id.btn_add_photo);
        btn_add_moments = findViewById(R.id.btn_capture);
        btn_share = findViewById(R.id.btn_share);

        switch_disable_sharing = findViewById(R.id.switch_enable_sharing);
        switch_disable_comments = findViewById(R.id.switch_enable_comment);
        switch_save_as_draft = findViewById(R.id.switch_enable_save_as_draft);

        post_card = findViewById(R.id.post_card);

        btn_default_ink_outline.setOnClickListener(this);
        btn_default_ink_filled.setOnClickListener(this);
        btn_green_outlined.setOnClickListener(this);
        btn_green_filled.setOnClickListener(this);
        btn_amber_outlined.setOnClickListener(this);
        btn_amber_filled.setOnClickListener(this);
        btn_purple_outlined.setOnClickListener(this);
        btn_purple_filled.setOnClickListener(this);

        align_left = findViewById(R.id.add_format_left);
        align_center = findViewById(R.id.add_format_center);
        align_right = findViewById(R.id.add_format_right);

        progressBar = findViewById(R.id.share_progress);

        align_left.setOnClickListener(this);
        align_center.setOnClickListener(this);
        align_right.setOnClickListener(this);

        ink_color = "default_ink_outline";
        ink_align = "alignment_left";
        currentTime = Calendar.getInstance().getTime();

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_share.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.state_list_btn_send_progressing)));
                progressBar.setVisibility(View.VISIBLE);
                sharePost();
                try {
                    uploadPostImages();
                }catch (Exception e){
                    Intent i = new Intent(AddPost.this,Home.class);
                    startActivity(i);
                    AddPost.this.finish();
                }
            }
        });

        monthName = month_date.format(c.getTime());
        setUserDetails();
    }

    private void setUserDetails() {

        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String fname, lname, profile;
                                final HelperUtils helperUtils = new HelperUtils();

                                accountInfo = document.toObject(AccountInfo.class);
                                accountInfoArrayList.add(accountInfo);
                                fname = (String) accountInfo.getFirst_name();
                                lname = (String) accountInfo.getLast_name();

                                tv_name.setText(fname + " " + lname);
                                tv_date.setText(monthName + " " + mDay + ", " + mYear);

                                helperUtils.startAlphaAnimation(tv_name, View.VISIBLE);
                                helperUtils.startAlphaAnimation(tv_date, View.VISIBLE);

                                try{
                                    profile = (String) document.get("img_profile");
                                    Uri uri, uri1;

                                    if (profile != null){
                                        uri1 = Uri.parse(profile);
                                        Picasso.get().load(uri1).placeholder(R.drawable.circular_bg).into(add_user_profile, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                helperUtils.startAlphaAnimation(add_user_profile, View.VISIBLE);
                                            }
                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                                    }else{
                                        emptyFields("No profile photo has been set");
                                    }
                                }catch (Exception e){
                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ink_default:
                ink_color = "default_ink_outline";
                post_card.setStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
                ed_caption.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ed_caption.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                setCardAnimation(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.ink_default_filled:
                ink_color = "default_ink_filled";
                post_card.setStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
                ed_caption.setHintTextColor(getResources().getColor(R.color.colorAccent));
                ed_caption.setTextColor(getResources().getColor(R.color.colorAccent));
                setCardAnimation(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case R.id.ink_green_light:
                ink_color = "green_outlined";
                post_card.setStrokeColor(getResources().getColor(R.color.green_outlined));
                ed_caption.setHintTextColor(getResources().getColor(R.color.green_outlined));
                ed_caption.setTextColor(getResources().getColor(R.color.green_outlined));
                setCardAnimation(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.ink_green_dark:
                ink_color = "green_filled";
                post_card.setStrokeColor(getResources().getColor(R.color.green_outlined));
                ed_caption.setHintTextColor(getResources().getColor(R.color.colorAccent));
                ed_caption.setTextColor(getResources().getColor(R.color.colorAccent));
                setCardAnimation(getResources().getColor(R.color.green_outlined));
                break;
            case R.id.ink_amber_light:
                ink_color = "amber_outlined";
                post_card.setStrokeColor(getResources().getColor(R.color.amber_outlined));
                ed_caption.setHintTextColor(getResources().getColor(R.color.amber_outlined));
                ed_caption.setTextColor(getResources().getColor(R.color.amber_outlined));
                setCardAnimation(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.ink_amber_dark:
                ink_color = "amber_filled";
                post_card.setStrokeColor(getResources().getColor(R.color.amber_outlined));
                ed_caption.setHintTextColor(getResources().getColor(R.color.colorAccent));
                ed_caption.setTextColor(getResources().getColor(R.color.colorAccent));
                setCardAnimation(getResources().getColor(R.color.amber_outlined));
                break;
            case R.id.ink_purple_light:
                ink_color = "purple_outlined";
                post_card.setStrokeColor(getResources().getColor(R.color.purple_outline));
                ed_caption.setHintTextColor(getResources().getColor(R.color.purple_outline));
                ed_caption.setTextColor(getResources().getColor(R.color.purple_outline));
                setCardAnimation(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.ink_purple_dark:
                ink_color = "purple_filled";
                post_card.setStrokeColor(getResources().getColor(R.color.purple_outline));
                ed_caption.setHintTextColor(getResources().getColor(R.color.colorAccent));
                ed_caption.setTextColor(getResources().getColor(R.color.colorAccent));
                setCardAnimation(getResources().getColor(R.color.purple_outline));
                break;

            case R.id.add_format_left:
                ink_align = "alignment_left";
                ed_caption.setGravity(Gravity.START);
                break;

            case R.id.add_format_center:
                ink_align = "alignment_center";
                ed_caption.setGravity(Gravity.CENTER);
                break;

            case R.id.add_format_right:
                ink_align = "alignment_right";
                ed_caption.setGravity(Gravity.END);
                break;
        }
    }

    private void setCardAnimation(int color) {
        int centerX = (post_card.getLeft() + post_card.getRight()) / 2;
        int centerY = (post_card.getTop() + post_card.getBottom()) / 2;

        int startRadius = 0;

        int endRadius = Math.max(post_card.getWidth(), post_card.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(post_card, centerX, centerY, startRadius, endRadius);

        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());

        post_card.setVisibility(View.VISIBLE);
        post_card.setCardBackgroundColor(color);
        anim.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    data_global = data;
                    final Uri mImageUri = data.getData();
                    img_post_image.setColumnCount(1);
                    ViewGroup.LayoutParams layoutParams = img_post_image.getLayoutParams();
                    layoutParams.height = 870;
                    img_post_image.setLayoutParams(layoutParams);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);

                    mArrayUri1 = mArrayUri;
                    img_post_image.setAdapter(galleryAdapter);

                    //img_post_image.setVerticalSpacing(img_post_image.getHorizontalSpacing());
                    //ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) img_post_image
                    //.getLayoutParams();
                    //mlp.setMargins(0, img_post_image.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {

                        data_global = data;
                        totalItemsSelected = data.getClipData().getItemCount();
                        final ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);

                            if (mArrayUri.size() == 2) {
                                img_post_image.setColumnCount(2);
                                ViewGroup.LayoutParams layoutParams = img_post_image.getLayoutParams();
                                layoutParams.height = 400;
                                img_post_image.setLayoutParams(layoutParams);
                            }
                            if (mArrayUri.size() == 3) {
                                img_post_image.setColumnCount(2);
                                ViewGroup.LayoutParams layoutParams = img_post_image.getLayoutParams();
                                layoutParams.height = 550;
                                img_post_image.setLayoutParams(layoutParams);
                            }
                            if (mArrayUri.size() > 3) {
                                ViewGroup.LayoutParams layoutParams = img_post_image.getLayoutParams();
                                layoutParams.height = 750;
                                img_post_image.setLayoutParams(layoutParams);
                            }

                            mArrayUri1 = mArrayUri;
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                            img_post_image.setAdapter(galleryAdapter);


                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sharePost() {
        //String full_name, date, time, ink_color, caption, monthName, alignment_left, alignment_center, alignment_right, ink_align;


        firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("account_details")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                final Map<String, Object> account_details = new HashMap<>();
                                String first_name = (String) document.get("first_name");
                                String last_name = (String) document.get("last_name");
                                String post_caption = ed_caption.getText().toString();
                                String post_date = monthName + " " + mDay + ", " + mYear;

                                Calendar c = Calendar.getInstance();
                                long date1 = System.currentTimeMillis();
                                SimpleDateFormat time2 = new SimpleDateFormat("hh:mm:ss a");
                                String timeString = time2.format(date1);

                                SimpleDateFormat time3 = new SimpleDateFormat("hhmmss");
                                String timeString3 = time3.format(date1);

                                int hour = c.get(Calendar.HOUR_OF_DAY);
                                int minutes = c.get(Calendar.MINUTE);

                                String current_time_short = timeString;
                                String current_time = timeString3;

                                time = current_time_short;

                                String date_num = String.valueOf(mYear+mMonth+mDay);

                                String post_date_document = monthName + " " + mDay + ", " + mYear + " (" + current_time_short + ")";

                                document_date = post_date_document;

                                boolean switch_disable_sharing_boolean = switch_disable_sharing.isChecked();
                                boolean switch_disable_comments_boolean = switch_disable_sharing.isChecked();
                                boolean switch_disable_save_as_draft = switch_save_as_draft.isChecked();

                                if (switch_disable_sharing_boolean) {
                                    sharing_pref = "false";
                                } else {
                                    sharing_pref = "true";
                                }

                                if (switch_disable_comments_boolean) {
                                    comments_pref = "false";
                                } else {
                                    comments_pref = "true";
                                }

                                if (switch_disable_save_as_draft) {
                                    draft_pref = "drafts";
                                } else {
                                    draft_pref = "published";
                                }

                                account_details.put("user_id", mAuth.getUid());
                                account_details.put("first_name", first_name);
                                account_details.put("last_name", last_name);
                                account_details.put("date", post_date);
                                account_details.put("time", current_time_short);
                                account_details.put("ink_color", ink_color);
                                account_details.put("caption", post_caption);
                                account_details.put("ink_align", ink_align);
                                account_details.put("sharing_pref", sharing_pref);
                                account_details.put("comments_pref", comments_pref);
                                account_details.put("date_num", "p_"+String.valueOf(date_num));
                                account_details.put("timestamp", FieldValue.serverTimestamp());
                                account_details.put("img_profile", accountInfoArrayList.get(0).getImg_profile());

                                final WriteBatch writeBatch = firestoreDB.batch();
                                //account_details.put("images", mArrayUri);
                                final DocumentReference nycRef = firestoreDB.collection("users").document(mAuth.getUid()).
                                        collection("posts").document(draft_pref).collection("post").document(post_date_document);
                                date = post_date;
                                //ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

                                nycRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Log.d("Document", "exists");
                                        } else {
                                            writeBatch.set(nycRef, account_details);

                                            writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // ...


                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void uploadPostImages() {
        if (data_global.getClipData() != null) {
            for (int i = 0; i < totalItemsSelected; i++) {

                Uri fileUri = data_global.getClipData().getItemAt(i).getUri();

                final String fileName = getFileName(fileUri);

                fileNameList.add(fileName);
                fileDoneList.add("uploading");


                final StorageReference fileToUpload = mStorage.child(mAuth.getUid()).child("post_images").child(fileName);

                final int finalI = i;
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
                                    Uri downloadUri = task.getResult();
                                    fileDoneList.remove(finalI);
                                    fileDoneList.add(finalI, "done");
                                    //mStorage.child(mAuth.getUid()).child("post_images").child(document_date).child(fileName).getDownloadUrl();


                                    final ArrayList<String> list = new ArrayList<>();
                                    for (int i = 0; i < totalItemsSelected; i++) {
                                        mStorage.child(mAuth.getUid()+"/"+"post_images/"+ fileNameList.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String path_to_download = uri.toString();
                                                String path_id = "";

                                                path_id = path_to_download.substring(path_to_download.length() - 4);


                                                final Map<String, Object> account_details = new HashMap<>();
                                                account_details.put("images", path_to_download);
                                                account_details.put("date", document_date);
                                                account_details.put("time", time);

                                                firestoreDB.collection("users")
                                                        .document(mAuth.getUid())
                                                        .collection("posts").document(draft_pref).collection("post")
                                                        .document(document_date)
                                                        .collection("image_list")
                                                        .document(path_id)
                                                        .set(account_details);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                emptyFields("Finished Uploading");
                                                intentNew(Home.class);
                                            }
                                        });
                                    }

                                    //Log.d("Array Size ",mStorage.child(mAuth.getUid()).child("post_images").child(fileName).getDownloadUrl().toString());


                                } else {
                                    emptyFields("Error");
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

        } else if (data_global.getData() != null) {
            Uri fileUri = data_global.getData();

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
                                Uri downloadUri = task.getResult();
                                String path_to_download = downloadUri.toString();
                                String path_id = "";

                                path_id = path_to_download.substring(path_to_download.length() - 4);

                                final Map<String, Object> account_details = new HashMap<>();
                                account_details.put("images", path_to_download);
                                account_details.put("date", document_date);
                                account_details.put("time", time);

                                firestoreDB.collection("users")
                                        .document(mAuth.getUid())
                                        .collection("posts").document(draft_pref).collection("post")
                                        .document(document_date)
                                        .collection("image_list")
                                        .document(path_id)
                                        .set(account_details);
                                    emptyFields("Finished Uploading");
                                    intentNew(Home.class);
                            } else {
                                emptyFields("Error");
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
        Snackbar.make(post_card, str, Snackbar.LENGTH_SHORT).show();
    }

    private void intentNew(Class c){
        Intent i = new Intent(AddPost.this, c);
        startActivity(i);
        AddPost.this.finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(AddPost.this, Home.class);
        startActivity(i);
        AddPost.this.finish();
    }
}
