package com.genedev.ink_socialmedia.PostUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.material.card.MaterialCardView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterPostItemImages extends RecyclerView.Adapter<RecyclerViewAdapterPostItemImages.ViewHolder> {

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

    public RecyclerViewAdapterPostItemImages(Context context, ArrayList<ImageViewModel> postViewModelArrayList) {

        this.postViewModelArrayList = postViewModelArrayList;

        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_images, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //postViewModelArrayList = new ArrayList<>();
        ImageLoader imageLoader = ImageLoader.getInstance();

        //Uri uri12 = Uri.parse(String.valueOf(postViewModelArrayList.get(0).getImages().get(0)));
        holder.post_item_image_loading.setVisibility(View.VISIBLE);
        size=postViewModelArrayList.size();
        //Log.d("TAG", uri+ "");
        Bitmap image;
        if (size > 1) {
            Uri uri = Uri.parse(postViewModelArrayList.get(position).getImages());

            holder.num_count.setText(holder.getAdapterPosition()+1+"/"+postViewModelArrayList.size());

                Picasso.get().load(uri).resize(500,450)
                        .centerInside()
                        .into(holder.imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.post_item_image_loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.post_item_image_loading.setVisibility(View.GONE);
                            }
                        });

            }
            if(size == 1) {

                Uri uri1 = Uri.parse(postViewModelArrayList.get(0).getImages());

                //holder.num_count.setText(holder.getAdapterPosition()+1+"/"+postViewModelArrayList.size());
                    holder.num_count_holder.setVisibility(View.INVISIBLE);
                    Picasso.get().load(uri1).resize(500,450)
                            .centerInside()
                            .into(holder.imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.post_item_image_loading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    holder.post_item_image_loading.setVisibility(View.GONE);
                                }
                            });


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