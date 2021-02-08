package com.genedev.ink_socialmedia.StoriesUtil;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.ink_socialmedia.R;
import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHolderStoriesUtil extends RecyclerView.ViewHolder {

    public TextView username;
    public CircleImageView user_profile;
    public ImageView story_bg;

    public RecyclerViewHolderStoriesUtil(@NonNull View itemView) {
        super(itemView);

        user_profile = itemView.findViewById(R.id.story_profile_pic);
        username = itemView.findViewById(R.id.user_name);
        story_bg = itemView.findViewById(R.id.story_background);
    }

}
