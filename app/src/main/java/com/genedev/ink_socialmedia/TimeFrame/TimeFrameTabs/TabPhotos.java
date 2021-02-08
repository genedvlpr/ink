package com.genedev.ink_socialmedia.TimeFrame.TimeFrameTabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.genedev.ink_socialmedia.PostUtils.GalleryAdapter;
import com.genedev.ink_socialmedia.PostUtils.ImageViewModel;
import com.genedev.ink_socialmedia.PostUtils.PhotosAdapter;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class TabPhotos extends Fragment implements View.OnClickListener {

    private HelperUtils helperUtils;
    private ImageViewModel imageViewModel = new ImageViewModel();
    private ArrayList<ImageViewModel> imageViewModelArrayList = new ArrayList<>();

    private PhotosAdapter galleryAdapter;
    private StaggeredGridView gridview;

    private Chip chip_header_photos, chip_profile_images, chip_posts_images;
    private Context context = getActivity();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.time_fragment_photos, container, false);

        gridview = v.findViewById(R.id.grid_view);

        chip_header_photos = v.findViewById(R.id.header_photos);
        chip_profile_images = v.findViewById(R.id.profile_images);
        chip_posts_images = v.findViewById(R.id.post_images);

        chip_header_photos.setOnClickListener(this);
        chip_profile_images.setOnClickListener(this);
        chip_posts_images.setOnClickListener(this);


        return v;
    }

    private void loadCoverPhotos(){
        CollectionReference cover_photos = firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("photos")
                .document("img_cover")
                .collection("images");

        cover_photos.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()){
                    imageViewModel = document.toObject(ImageViewModel.class);
                    imageViewModelArrayList.add(imageViewModel);

                    String object = "cover";

                    galleryAdapter = new PhotosAdapter(getContext(), imageViewModelArrayList, object);
                    gridview.setAdapter(galleryAdapter);
                }
            }
        });
    }

    private void loadProfilePhotos(){
        CollectionReference profile_images = firestoreDB.collection("users")
                .document(mAuth.getUid())
                .collection("photos")
                .document("img_profile")
                .collection("images");

        profile_images.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        imageViewModel = document.toObject(ImageViewModel.class);
                        imageViewModelArrayList.add(imageViewModel);

                        String object = "profile";

                        galleryAdapter = new PhotosAdapter(getContext(), imageViewModelArrayList, object);
                        gridview.setAdapter(galleryAdapter);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.header_photos:
                imageViewModelArrayList.clear();
                loadCoverPhotos();
                break;
            case R.id.profile_images:
                imageViewModelArrayList.clear();
                loadProfilePhotos();
                break;
            case R.id.post_images:
                imageViewModelArrayList.clear();
                Toast.makeText(getContext(),"Clicked"+view.getId(),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onStart(){
        imageViewModelArrayList.clear();
        loadCoverPhotos();
        super.onStart();
    }

    @Override
    public void onResume(){
        imageViewModelArrayList.clear();
        loadCoverPhotos();
        super.onResume();
    }

    @Override
    public void onDestroy(){
        imageViewModelArrayList.clear();
        super.onDestroy();
    }
}