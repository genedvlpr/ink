package com.genedev.ink_socialmedia.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.genedev.ink_socialmedia.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList) {

        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_add_image_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);

        String fileDone = fileDoneList.get(position);

        if (fileDone.equals("uploading")) {

            holder.fileDoneView.setImageResource(R.drawable.ic_outline_add_to_photos_24px);

        } else {

            holder.fileDoneView.setImageResource(R.drawable.ic_round_check_circle_24px);

        }

    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView fileNameView;
        public DynamicHeightImageView fileDoneView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            fileNameView = (TextView) mView.findViewById(R.id.img_number);
            fileDoneView = (DynamicHeightImageView) mView.findViewById(R.id.post_item_dynamic);


        }

    }
}