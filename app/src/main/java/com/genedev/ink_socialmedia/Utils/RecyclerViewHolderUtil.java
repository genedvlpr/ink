package com.genedev.ink_socialmedia.Utils;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.genedev.ink_socialmedia.R;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHolderUtil extends RecyclerView.ViewHolder {

    public View view;
    public TextView tv_name;
    public CircleImageView img_profile;
    public Button btn_request, btn_following, btn_unfriend;
    public MaterialCardView item_card;
    public ProgressBar progressBar;

    public RecyclerViewHolderUtil(@NonNull View itemView) {
        super(itemView);

        tv_name = itemView.findViewById(R.id.tv_username);
        img_profile = itemView.findViewById(R.id.img_user_profile);
        btn_request = itemView.findViewById(R.id.btn_request);
        btn_following = itemView.findViewById(R.id.btn_following);
        btn_unfriend = itemView.findViewById(R.id.btn_unfriend);
        item_card = itemView.findViewById(R.id.item_card);
        progressBar = itemView.findViewById(R.id.progress);
    }
}
