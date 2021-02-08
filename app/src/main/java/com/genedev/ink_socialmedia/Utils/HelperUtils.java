package com.genedev.ink_socialmedia.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import com.genedev.ink_socialmedia.PostUtils.ImageViewModel;
import com.genedev.ink_socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HelperUtils {

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser user = mAuth.getCurrentUser();
    public static FirebaseUser user_existing = mAuth.getCurrentUser();
    public static String Uid = user != null ? user.getUid() : null;
    public static String U_id = user_existing.getUid();

    public void setPersistence (FirebaseFirestore firebaseFirestore){
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
    }

    public void firebaseInstance(FirebaseAuth mAuth, FirebaseUser user, String Uid){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Uid = mAuth.getUid();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void progress(final ProgressBar progressBar, final int progressStatus, final Boolean visible){
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                int  progress = progressStatus;
                while (progress < 100) {
                    progress+=1;
                    final int progress_stat = progress;
                    handler.post(new Runnable() {
                        public void run() {

                            progressBar.setProgress(progress_stat);
                        }
                    });
                    try {

                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        if (!visible){
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    public static void startAlphaAnimation (View v, int visibility) {

        int duration = 300;
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public static void savePost(final DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("HelperUtils", "DocumentSnapshot successfully written!");
                                        //fromPath.delete()
                                                //.addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    //@Override
                                                    //public void onSuccess(Void aVoid) {
                                                        //Log.d("HelperUtils", "DocumentSnapshot successfully deleted!");
                                                    //}
                                                //})
                                                //.addOnFailureListener(new OnFailureListener() {
                                                    //@Override
                                                    //public void onFailure(@NonNull Exception e) {
                                                        //Log.w("HelperUtils", "Error deleting document", e);
                                                    //}
                                                //});
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("HelperUtils", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("HelperUtils", "No such document");
                    }
                } else {
                    Log.d("HelperUtils", "get failed with ", task.getException());
                }
            }
        });
    }

    public static void savePostImages(final CollectionReference fromPath, final CollectionReference toPath, final ArrayList<ImageViewModel> imageViewModelArrayList) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Images", imageViewModelArrayList);
                    if (document != null) {
                        toPath.add(map)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                    }
                                });

                    } else {
                        Log.d("HelperUtils", "No such document");
                    }
                } else {
                    Log.d("HelperUtils", "get failed with ", task.getException());
                }
            }
        });

    }
}
