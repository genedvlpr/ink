package com.genedev.ink_socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class PrivacyPolicy extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        lottieAnimationView = findViewById(R.id.animation_view);

        lottieAnimationView.setImageAssetsFolder("wow/");
        lottieAnimationView.playAnimation();
    }
}
