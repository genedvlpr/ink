package com.genedev.ink_socialmedia.TimeFrame.ProfileUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genedev.ink_socialmedia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryProfileAdapter extends BaseAdapter {
    private final Random mRandom;
    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private ImageView placeholder_1;
    private CircleImageView ivGallery, placeholder;
    private ArrayList<Uri> mArrayUri;
    private TextView tv_position;
    private Bitmap bitmap;
    private List<String> fileNameList;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public GalleryProfileAdapter(Context ctx, ArrayList<Uri> mArrayUri) {
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

        View itemView = inflater.inflate(R.layout.activity_time_frame, parent, false);

        ivGallery = (CircleImageView) itemView.findViewById(R.id.prof_pic);
        //tv_position = itemView.findViewById(R.id.img_number);
        ivGallery.setImageURI(mArrayUri.get(position));

        ivGallery.setCropToPadding(true);

        //double positionHeight = getPositionRatio(position);
        //tv_position.setText("Image " + String.valueOf(position+1));

        //int backgroundIndex = position >= mArrayUri.size() ?
                //position % mArrayUri.size() : position;

        //ivGallery.setHeightRatio(positionHeight);
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