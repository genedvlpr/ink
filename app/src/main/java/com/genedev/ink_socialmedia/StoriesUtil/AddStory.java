package com.genedev.ink_socialmedia.StoriesUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.genedev.ink_socialmedia.AddPost;
import com.genedev.ink_socialmedia.Home;
import com.genedev.ink_socialmedia.PostUtils.GalleryAdapter;
import com.genedev.ink_socialmedia.PostUtils.SelectedImagesPreview;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.AccountInfo;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
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

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class AddStory extends AppCompatActivity {

    private CircleImageView img_profile;
    private MaterialProgressBar prg_profile, prg_upload;
    private EditText story_caption;
    private TextView user_name;
    private ImageButton add_capture, add_photo, story_upload;
    private String str_caption;
    private ImageView story_img;

    private int PICK_IMAGE_MULTIPLE = 1;

    private int PICK_PROFILE = 1;

    private String imageEncoded, monthName;
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

    private String fname, lname, profile_img;

    private ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();
    private AccountInfo accountInfo;

    private Calendar c;
    private int mYear, mMonth, mDay;

    @ServerTimestamp
    Date createdDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        img_profile = findViewById(R.id.img_profile);
        prg_profile = findViewById(R.id.progress_profile);
        prg_upload = findViewById(R.id.prg_upload);
        story_caption = findViewById(R.id.story_caption);
        user_name = findViewById(R.id.first_name);

        story_img = findViewById(R.id.story_img);

        add_capture = findViewById(R.id.add_capture);
        add_photo = findViewById(R.id.add_photo);
        story_upload = findViewById(R.id.add_upload);

        mStorage = FirebaseStorage.getInstance().getReference();



        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        story_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prg_upload.setVisibility(View.VISIBLE);
                story_upload.setEnabled(false);
                story_upload.setClickable(false);
                story_caption.setEnabled(false);
                add_capture.setEnabled(false);
                add_photo.setEnabled(false);
                upload_story();
            }
        });

        c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

        monthName = month_date.format(c.getTime());

        load_img_profile();
    }

    private void load_img_profile(){
        firestoreDB.collection("users").document(mAuth.getUid()).
                collection("account_details").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                final HelperUtils helperUtils = new HelperUtils();

                                fname = (String) document.get("first_name");
                                lname = (String)  document.get("last_name");

                                try{
                                    profile_img = (String) document.get("img_profile");
                                    Uri uri, uri1;

                                    user_name.setText(fname+" "+lname);
                                    if (profile_img != null){
                                        uri1 = Uri.parse(profile_img);
                                        Picasso.get().load(uri1)
                                                .resize( 150,100)
                                                .centerCrop()
                                                .into(img_profile);

                                        prg_profile.setVisibility(View.INVISIBLE);
                                    }else{
                                        emptyFields("No profile photo has been set");
                                        prg_profile.setVisibility(View.INVISIBLE);
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

    private void upload_story(){
        String cover_photo = "moments_photo";

        uploadPostImages(cover_photo);
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
                story_img.setImageURI(mArrayUri.get(0));

                //accountInfoArrayList = new ArrayList<>();


            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadPostImages(final String field) {
        if (data_global.getData() != null) {
            Uri fileUri = data_global.getData();
            //final ProgressBar progressBar = findViewById(R.id.cover_progress);
            //progressBar.setVisibility(View.VISIBLE);
            String fileName = getFileName(fileUri);

            final StorageReference fileToUpload = mStorage.child(mAuth.getUid()).child("moments").child(fileName);

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
                        account_details.put("timestamp", FieldValue.serverTimestamp());
                        account_details.put("user_id", mAuth.getUid());
                        account_details.put("first_name", fname);
                        account_details.put("last_name", lname);
                        account_details.put("img_profile", profile_img);

                        str_caption = story_caption.getText().toString();
                        if (!str_caption.equals("")){
                            account_details.put("caption", str_caption);
                        }

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
                                                .collection("moments")
                                                .document(field)
                                                .collection("images")
                                                .document(String.valueOf(downloadUri.toString().replaceAll("/","_").replace(".","_")))
                                                .set(account_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                emptyFields("Upload successful");
                                                Intent i = new Intent(AddStory.this, Home.class);
                                                startActivity(i);
                                                AddStory.this.finish();
                                                //progressBar.setVisibility(View.INVISIBLE);
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
        Snackbar.make(img_profile, str, Snackbar.LENGTH_SHORT).show();
    }
}
