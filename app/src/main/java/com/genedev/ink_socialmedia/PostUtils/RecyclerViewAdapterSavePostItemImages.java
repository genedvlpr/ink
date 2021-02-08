package com.genedev.ink_socialmedia.PostUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.genedev.ink_socialmedia.R;
import com.google.android.material.card.MaterialCardView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

import static com.genedev.ink_socialmedia.Utils.HelperUtils.firestoreDB;
import static com.genedev.ink_socialmedia.Utils.HelperUtils.mAuth;

public class RecyclerViewAdapterSavePostItemImages extends RecyclerView.Adapter<RecyclerViewAdapterSavePostItemImages.ViewHolder> {

    private Context context;
    private List<ImageViewModel> imageArrayList;
    private PostImageArray post_images;

    private int size;

    private ArrayList<ImageViewModel> imageViewModelArrayList;
    private ImageViewModel imageViewModel = new ImageViewModel();

    private PostViewModel postViewModel = new PostViewModel();

    private ArrayList<ImageViewModel> postViewModelArrayList;
    private String url;
    private Picasso picasso;

    int query_size;
    private int count;

    private static int TIME_OUT = 1000;

    private String doc_name, user;

    public RecyclerViewAdapterSavePostItemImages(Context context, ArrayList<ImageViewModel> postViewModelArrayList, String doc_name, String user) {

        this.postViewModelArrayList = postViewModelArrayList;

        this.context = context;

        this.doc_name = doc_name;
        this.user = user;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_images, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.d("Document", postViewModelArrayList.get(position).getDate());
        try{
            Map<String, Object> images = new HashMap<>();
            images.put("images", postViewModelArrayList.get(position).getImages());
            images.put("date", postViewModelArrayList.get(position).getDate());
            images.put("time", postViewModelArrayList.get(position).getTime());

            firestoreDB.collection("users")
                    .document(mAuth.getUid())
                    .collection("saved")
                    .document("published")
                    .collection("post")
                    .document(doc_name)
                    .collection("image_list")
                    .document(doc_name)
                    .update(images);

        }catch (Exception e){
            Map<String, Object> images = new HashMap<>();
            images.put("images", postViewModelArrayList.get(0).getImages());
            images.put("date", postViewModelArrayList.get(0).getDate());
            images.put("time", postViewModelArrayList.get(0).getTime());

            firestoreDB.collection("users")
                    .document(mAuth.getUid())
                    .collection("saved")
                    .document("published")
                    .collection("post")
                    .document(doc_name)
                    .collection("image_list")
                    .document(doc_name)
                    .update(images);


            Log.d("Document", doc_name);

        }

    }

    @Override
    public int getItemCount() {
        return postViewModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public DynamicHeightImageView imageView;
        public MaterialCardView post_item_image_loading, num_count_holder;
        public TextView num_count;
        public ViewHolder(View itemView) {
            super(itemView);

            num_count = itemView.findViewById(R.id.num_count);
            num_count_holder = itemView.findViewById(R.id.num_count_holder);
            imageView = (DynamicHeightImageView) itemView.findViewById(R.id.post_item_imageView_item);
            post_item_image_loading = itemView.findViewById(R.id.post_item_image_loading);
        }


    }
}