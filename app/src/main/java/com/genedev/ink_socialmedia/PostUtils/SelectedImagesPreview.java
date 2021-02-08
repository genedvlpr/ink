package com.genedev.ink_socialmedia.PostUtils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.etsy.android.grid.StaggeredGridView;
import com.genedev.ink_socialmedia.R;

import java.util.ArrayList;

public class SelectedImagesPreview extends Activity {
    private GalleryAdapter galleryAdapter;

    private StaggeredGridView img_post_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_selected_images_preview);

        img_post_image = findViewById(R.id.grid_view);
        Intent i = new Intent();
        i.getBundleExtra("Images");

        //Log.d("ArrayImages", String.valueOf(i.getClipData().getItemCount()));

        if (i.getClipData() != null) {
            final ClipData mClipData = i.getClipData();
            final ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
            for (int s = 0; s < mClipData.getItemCount(); s++) {

                ClipData.Item item = mClipData.getItemAt(s);
                Uri uri_1 = item.getUri();
                mArrayUri.add(uri_1);

                galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);

                img_post_image.setAdapter(galleryAdapter);
            }
        }

    }
}
