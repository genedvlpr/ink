package com.genedev.ink_socialmedia.PostUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.genedev.ink_socialmedia.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GalleryAdapter extends BaseAdapter {
    private final Random mRandom;
    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private ImageView placeholder_1;
    private DynamicHeightImageView ivGallery, placeholder;
    private ArrayList<Uri> mArrayUri;
    private TextView tv_position;
    private Bitmap bitmap;
    public List<String> fileNameList;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {
        mRandom = new Random();
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }

    @Override
    public int getCount() {
        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        pos = position;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.post_add_image_item, parent, false);

        ivGallery = (DynamicHeightImageView) itemView.findViewById(R.id.post_item_dynamic);
        tv_position = itemView.findViewById(R.id.img_number);

        ivGallery.setImageURI(mArrayUri.get(position));

        ivGallery.setCropToPadding(true);

        double positionHeight = getPositionRatio(position);
        tv_position.setText("Image " + String.valueOf(position+1));

        int backgroundIndex = position >= mArrayUri.size() ?
                position % mArrayUri.size() : position;

        ivGallery.setHeightRatio(positionHeight);
        return itemView;
    }
    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            //Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }

}