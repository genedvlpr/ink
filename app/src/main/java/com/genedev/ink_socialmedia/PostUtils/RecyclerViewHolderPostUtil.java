package com.genedev.ink_socialmedia.PostUtils;

import android.view.View;
import android.view.ViewGroup;
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

public class RecyclerViewHolderPostUtil extends RecyclerView.ViewHolder {

    public TextView username, date, time, reactions_summary, caption, comments_summary, image_count;
    public CircleImageView user_profile;
    public MaterialCardView post_card;
    public ImageButton btn_comment, btn_share, btn_save;
    public RecyclerView post_item_recycler_view, post_item_recycler_view1;
    public LottieAnimationView action_haha,action_angry,action_heart,action_like,action_wow,action_cry;
    public Button react_btn;
    public RelativeLayout post_reactions;
    public CircleImageView like_reaction, haha_reaction, cry_reaction, heart_reaction, angry_reaction, wow_reaction;
    public FloatingActionButton fab_scroll_next_image;

    private LinearLayout layout;

    public RecyclerViewHolderPostUtil(@NonNull View itemView) {
        super(itemView);

        user_profile = itemView.findViewById(R.id.img_user_profile);
        username = itemView.findViewById(R.id.tv_username);
        date = itemView.findViewById(R.id.tv_date);
        time = itemView.findViewById(R.id.tv_time);
        caption = itemView.findViewById(R.id.post_caption);
        reactions_summary = itemView.findViewById(R.id.reaction_summary);
        comments_summary = itemView.findViewById(R.id.comments_summary);
        post_card = itemView.findViewById(R.id.post_card_holder);
        btn_comment = itemView.findViewById(R.id.post_item_comment);
        btn_share = itemView.findViewById(R.id.post_item_share);
        btn_save = itemView.findViewById(R.id.post_item_save);
        post_item_recycler_view = itemView.findViewById(R.id.post_item_recycler_view);
        post_item_recycler_view1 = itemView.findViewById(R.id.post_item_recycler_view1);

        action_haha = itemView.findViewById(R.id.action_haha);
        action_angry = itemView.findViewById(R.id.action_angry);
        action_cry = itemView.findViewById(R.id.action_cry);
        action_heart = itemView.findViewById(R.id.action_heart);
        action_like = itemView.findViewById(R.id.action_like);
        action_wow = itemView.findViewById(R.id.action_wow);

        like_reaction = itemView.findViewById(R.id.react_like);
        heart_reaction = itemView.findViewById(R.id.react_love);
        angry_reaction = itemView.findViewById(R.id.react_angry);
        wow_reaction = itemView.findViewById(R.id.react_wow);
        haha_reaction = itemView.findViewById(R.id.react_haha);
        cry_reaction = itemView.findViewById(R.id.react_like);


        post_reactions = itemView.findViewById(R.id.post_reactions);

        react_btn = itemView.findViewById(R.id.btn_react);

        final HelperUtils helperUtils = new HelperUtils();

        helperUtils.startAlphaAnimation(user_profile, View.VISIBLE);
        helperUtils.startAlphaAnimation(username, View.VISIBLE);
        helperUtils.startAlphaAnimation(date, View.VISIBLE);
        helperUtils.startAlphaAnimation(time, View.VISIBLE);
        helperUtils.startAlphaAnimation(caption, View.VISIBLE);

    }

}
